package com.afterrabble.silentnight3;

import android.renderscript.Allocation;

import com.otaliastudios.cameraview.CameraView;

/**
 * Created by AaronR on 11/17/17.
 */

public class CompositBuilder {

    private int totalFrameCount;
    private int currFrameCount;
    private byte[][] frames;
    private byte[] composit;
    private long width;
    private long height;
    private Allocation inAllocation;
    private Allocation outAllocation;
    private Runnable done;

    // Maybe
    private int framesInQueue;


    public CompositBuilder(int frameCount, long width, long height, CameraView camera, Runnable done){

        this.totalFrameCount = frameCount;
        this.frames = new byte[frameCount][];
        this.width = width;
        this.height = height;
        this.framesInQueue = 0;
        this.currFrameCount = 0;
        this.done = done;

        initRS();

    }

    public void addFrame(byte[] frame){

        frames[currFrameCount] = frame;

        if(currFrameCount == 0){
            composit = frame;
        }

        currFrameCount++;
        framesInQueue++;
        build();
    }

    private void build(){
        // process each frame as it's added
        // TODO: IMPLEMENT THIS;

        framesInQueue -=1;
    }

    private void initRS(){
        // init allocations
    }
    
}
