package com.afterrabble.silentnight3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.Log;

import com.example.android.hdrviewfinder.ScriptC_merge;
import com.otaliastudios.cameraview.CameraUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by AaronR on 11/27/17.
 */

public class CompositeBuilder extends Thread {
    private static String TAG = "COMPOSITEBUILDER";
    private String compositID ;
    private int totalFrameCount;
    private int currFrameCount;
    private int finishedFrameCount;
    private int width;
    private int height;



    private Allocation prevAllocation;
    private Allocation currentAllocation;
    private Allocation outAllocation;
    private Bitmap composite;
    private Runnable done;
    private List<byte[]> jpgs;
    private Context mContext;

    private RenderScript rS;
    ScriptC_merge mergeScript;

    // Maybe


    public CompositeBuilder(Context context, int frameCount, int width, int height, Runnable done) {
        super();
        compositID = UUID.randomUUID().toString();
        this.totalFrameCount = frameCount;
        this.width = width;
        this.height = height;

        this.currFrameCount = 0;
        this.finishedFrameCount = 0;
        this.done = done;
        this.jpgs = jpgs;
        this.mContext = context;
        rS = RenderScript.create(mContext);
        jpgs = new ArrayList();


    }

    @Override
    public void start(){
        super.start();
        System.out.println("Thread Is Running");
        for (int i = 0; i < jpgs.size(); i++) {
            decodeJpegByteArray(jpgs.get(i));
        }

    }

    public void addImage(byte[] frame){
        jpgs.add(frame);
    }

    private void decodeJpegByteArray(byte[] jpg){


        CameraUtils.decodeBitmap(jpg, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {

                // send bitmap to CompositeBuilder
                addFrame(bitmap);
            }
        });

    }

    public String getID(){
        return this.compositID;
    }

    public boolean isFull(){
        return jpgs.size() == totalFrameCount;
    }

    public void addFrame(Bitmap frame){

        currFrameCount++;
        if(currFrameCount > 1){

            build(frame);
        }else{
            composite = frame;
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
        Log.i(TAG, "build: mergeDuraiton " + (new Date().getTime() - startTime));
        outAllocation.copyTo(composite);
        Log.i(TAG, "build: copyToDuration " + (new Date().getTime() - startTime));
        // callback and tell view to update with new composite
        finishedFrameCount ++;
        updateView();

    }

    private void initRS(){
        // init allocations
        prevAllocation = Allocation.createFromBitmap(rS, composite);
        outAllocation = Allocation.createTyped(rS, prevAllocation.getType());
        currentAllocation = Allocation.createTyped(rS, prevAllocation.getType());
        mergeScript = new ScriptC_merge(rS);

    }

    public Bitmap getComposite(){
        return composite;
    }

    private void updateView(){
        ((MainActivity) mContext).runOnUiThread(done);
        System.out.println("Finished");
        if (isFinished()){
            System.out.println("+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
            System.out.println("Finished");
            System.out.println("+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        }
        //
    }

    public boolean isFinished(){
        return finishedFrameCount ==  totalFrameCount-1;

    }
}
