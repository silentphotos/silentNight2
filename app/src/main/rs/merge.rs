#pragma version(1)
#pragma rs java_package_name(com.example.android.hdrviewfinder)
#pragma rs_fp_relaxed


rs_allocation gCurrentFrame;
rs_allocation gPrevFrame;


int gCutPointX = 0;
int gGain = 0;
int gDoMerge = 1;
int gFrameCount = 1;


uchar4 __attribute__((kernel)) mergeFrames(uchar val, uint32_t x) {
    // Read in pixel values from latest frame - YUV color space

    uchar val2 = rsGetElementAt_uchar(gCurrentFrame, x);

    uchar newval = val;

    if (gDoMerge == 1 || ((gCutPointX > 0) && (!(x < gCutPointX)))) {
        //Complex fusion technique
        newval = val2/2 + val/2;
    }
    // Store current pixel for next frame
    rsSetElementAt_uchar(gPrevFrame, newval, x);
    rsDebug("Returned val: ", newval);
    return val;
}