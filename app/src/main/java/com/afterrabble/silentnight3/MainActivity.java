package com.afterrabble.silentnight3;

import android.app.Activity;
import android.content.Intent;

import android.renderscript.RenderScript;

import android.os.Bundle;

import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.android.hdrviewfinder.ScriptC_merge;
import com.otaliastudios.cameraview.CameraView;


public class MainActivity extends Activity {

    CameraView cameraView;

    RenderScript mRS;
    ScriptC_merge mMergeScript;

    private int frameCount;
    private Button mCameraButton;
    private SeekBar mHorizSlider;
    private SeekBar mVertSlider;
    private Button mLibraryButton;
    long lastDown;
    long lastDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.camera_view);


        mLibraryButton = (Button) findViewById(R.id.libraryButton);
        mLibraryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent libraryActivity = new Intent(MainActivity.this, PhotosActivity.class);
                startActivity(libraryActivity);
            }
        });


        mCameraButton = (Button) findViewById(R.id.imageButton);
        mCameraButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastDown = System.currentTimeMillis();
                    view.playSoundEffect(SoundEffectConstants.CLICK);
                    mCameraButton.setBackgroundResource(R.drawable.camera_button_image_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    lastDuration = System.currentTimeMillis() - lastDown;
                    mCameraButton.setBackgroundResource(R.drawable.camera_button_image);
                }
                return true;
            }
        });


        mHorizSlider = (SeekBar) findViewById(R.id.h_seekbar);
        mHorizSlider.setAlpha((float) 0.25);
        mHorizSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mHorizSlider.setAlpha(1);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mHorizSlider.setAlpha((float) 0.25);
                        Toast.makeText(getApplicationContext(), "Frames: " + mHorizSlider.getProgress()/10, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //cameraView.setRotation(90); // UnComment this if the preview is rotated 90 degrees.
        mVertSlider = findViewById(R.id.v_seekbar);
        mVertSlider.setAlpha(0.25f);
        mVertSlider.setProgress(50);
        mVertSlider.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        // Camera returns min and max exposure values this maps them to the slider
                        float min = cameraView.getCameraOptions().getExposureCorrectionMinValue();
                        float max = cameraView.getCameraOptions().getExposureCorrectionMaxValue();
                        cameraView.setExposureCorrection((Math.abs(min - max)/100.0f * seekBar.getProgress()) - (max - min)/2);
                        System.out.println(cameraView.getExposureCorrection());

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mVertSlider.setAlpha(1);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mVertSlider.setAlpha((float) 0.25);
                        // ISO Might not bee the correct term here
                        Toast.makeText(getApplicationContext(), "ISO: " + calcISO(mVertSlider.getProgress()), Toast.LENGTH_SHORT).show();

                    }

                    public String calcISO(int in){
                        int mapped = (in / 20) + 1;
                        return ((int)(25 * Math.pow(2, 1 + mapped))) + "";
                    }
                }
        );
    }


    private void onCapture(){
        //TODO: Implement This
        // Check out AndroidArsenal CameraView for the Documentation on how to take images

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }
}
