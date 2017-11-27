package com.afterrabble.silentnight3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.RenderScript;

import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.android.hdrviewfinder.ScriptC_merge;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;


public class MainActivity extends Activity {


    final int MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE = 123;
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
    int captureMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.camera_view);

        mCameraButton = findViewById(R.id.imageButton);

        mHorizSlider = findViewById(R.id.h_seekbar);
        mHorizSlider.setAlpha((float) 0.25);

        mVertSlider = findViewById(R.id.v_seekbar);
        mVertSlider.setAlpha(0.25f);
        mVertSlider.setProgress(50);

        mLibraryButton = findViewById(R.id.libraryButton);

        setUIHandlers();

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                onCapture(picture);
            }
        });

        checkPerms();


    }

    private void checkPerms(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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

    private void setUIHandlers(){
        mLibraryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent libraryActivity = new Intent(MainActivity.this, PhotosActivity.class);
                startActivity(libraryActivity);
            }
        });

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
                    captureImage();
                }
                return true;
            }
        });

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

    private void captureImage(){
        cameraView.capturePicture();
        // if frame count == 1
        //     take and save image
        // else
        // Concurrently:
        // 1) capture number of frames
        //    place each frame in a buffer
        //
        // 2) compute image from frames
        // 3) save each frame to apps file System with generated name
        // 2b) save composite image to filesystem with generated name
        // 2c) add frame id's to frame db table with pk being generated name, captureID that is the same for all frames,and image location
        // 2d) add composite id to db table with pk being the generated name, captureID to reference composite images, and image location

    }


    private void onCapture(byte[] picture){
        switch (captureMode) {
            case CaptureMode.SINGLE_FRAME:
                String imageName = new SavePhotoTask().doInBackground(picture);
                // Add to DB image location: imageName
                break;
            case CaptureMode.LOWLIGHT_COMPOSIT:
                Runnable runner = new Runnable() {
                    byte[] frame;
                    @Override
                    public void run() {
                        // done
                        // do something with frame
                    }
                };
                break;
            case CaptureMode.SUBJECT_COMPOSIT:
                break;
        }
    }

    private void onCaptureModeButtonTapped(){
        captureMode++;
        captureMode = captureMode%3;
    }

    private void saveImage(byte[] frame){

    }

}
