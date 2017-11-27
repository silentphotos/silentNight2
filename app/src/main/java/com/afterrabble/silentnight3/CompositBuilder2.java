package com.afterrabble.silentnight3;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.Log;

import com.example.android.hdrviewfinder.ScriptC_merge;
import com.otaliastudios.cameraview.CameraUtils;

import java.util.Date;
import java.util.UUID;

/**
 * Created by AaronR on 11/27/17.
 */

public class CompositBuilder2 extends AsyncTask<Void, Void, Bitmap> {

    private String compositID ;
    private int totalFrameCount;
    private int currFrameCount;

    private int width;
    private int height;
    private int decodedFrames;
    private int decodingFrames;

    private Allocation prevAllocation;
    private Allocation currentAllocation;
    private Allocation outAllocation;
    private Bitmap composit;
    private Runnable done;


    private RenderScript rS;
    ScriptC_merge mergeScript;

    // Maybe

    private int framesInQueue;

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        done.run();
    }

    public CompositBuilder2(Context context, int frameCount, int width, int height, Runnable done) {
        super();

        compositID = UUID.randomUUID().toString();
        this.totalFrameCount = frameCount;
        this.width = width;
        this.height = height;
        this.framesInQueue = 0;
        this.currFrameCount = 0;
        this.done = done;

        rS = RenderScript.create(context);

    }

    public void decodeJpegByteArray(byte[] jpg){
        decodingFrames ++;
        CameraUtils.decodeBitmap(jpg, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                decodedFrames++;
                // send bitmap to CompositeBuilder
                addFrame(bitmap);
            }
        });
    }

    public String getID(){
        return this.compositID;
    }

    public boolean isFull(){
        return decodingFrames == totalFrameCount;
    }


    public void addFrame(Bitmap frame){

        currFrameCount++;
        if(currFrameCount > 1){
            framesInQueue++;
            build(frame);
        }else{
            composit = frame;
            initRS();
        }
    }

    private void build(Bitmap frame){
        // process each frame as it's added
        // TODO: IMPLEMENT THIS;
        currentAllocation = Allocation.createFromBitmap(rS, frame);
        mergeScript.set_gCurrentFrame(currentAllocation);
        mergeScript.set_gPrevFrame(prevAllocation);
        long startTime =  new Date().getTime();
        mergeScript.forEach_mergeFrames(prevAllocation, outAllocation);
        Log.i("COMPOSITBUILDER", "build: mergeDuraiton " + (new Date().getTime() - startTime));
        outAllocation.copyTo(composit);
        Log.i("COMPOSITBUILDER", "build: copyToDuration " + (new Date().getTime() - startTime));
        // callback and tell view to update with new composite
        updateView();
        framesInQueue -=1;
    }

    private void initRS(){
        // init allocations
        prevAllocation = Allocation.createFromBitmap(rS, composit);
        outAllocation = Allocation.createTyped(rS, prevAllocation.getType());
        currentAllocation = Allocation.createTyped(rS, prevAllocation.getType());
        mergeScript = new ScriptC_merge(rS);
    }

    public Bitmap getComposite(){
        return composit;
    }

    private void updateView(){
        done.run();
    }
}
