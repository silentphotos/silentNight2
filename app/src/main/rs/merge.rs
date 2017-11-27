#pragma version(1)
#pragma rs java_package_name(com.example.android.hdrviewfinder)
#pragma rs_fp_relaxed


rs_allocation gCurrentFrame;
rs_allocation gPrevFrame;

int gGain = 0;
int gFrameCount = 1;

uchar4 __attribute__((kernel)) mergeFrames(uchar4 in, uint32_t x, uint32_t y) {
    // Read in pixel values from latest frame - YUV color space

    uchar4 val2 = rsGetElementAt_uchar4(gCurrentFrame, x, y);

    uchar4 newval = in;

    //Complex fusion technique
    newval = val2/2 + in/2;

    // Store current pixel for next frame
    rsSetElementAt_uchar4(gPrevFrame, newval, x, y);
    rsDebug("Returned val: ", newval);
    return newval;
}