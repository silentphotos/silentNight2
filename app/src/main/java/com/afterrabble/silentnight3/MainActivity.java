package com.afterrabble.silentnight3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afterrabble.silentnight3.db.ImageDbHelper;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MainActivity extends Activity {

    private ImageDbHelper dbHelper;

    final String  TAG = "MAIN ACTIVITY";
    private final int maxFrameCountAllowed = 5;

    final int MY_PERMISSIONS_REQUEST_EXTERNAL_WRITE = 123;
    CameraView cameraView;

    private int frameCount;
    private Button mCameraButton;
    private SeekBar mVertSlider;
    private Button mLibraryButton;
    private Button mCaptureMode;
    private CompositeBuilder builder;
    private TextView mBrightnessLabel;
    private Button frameCountButton;


    long lastDown;
    long lastDuration;
    int captureMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameCount = 2;
        // FindViewByID's and setAlpha's
        initIUComponents();
        setUIHandlers();

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                onCapture(picture);
            }
        });

        checkPerms();


        dbHelper = ImageDbHelper.getInstance(this);
        dbHelper.open();


    }

    private void initIUComponents(){
        cameraView = findViewById(R.id.camera_view);

        mCameraButton = findViewById(R.id.imageButton);


        mVertSlider = findViewById(R.id.v_seekbar);
        mVertSlider.setAlpha(0.25f);
        mVertSlider.setProgress(50);

        mLibraryButton = findViewById(R.id.libraryButton);

        mCaptureMode = findViewById(R.id.captureMode);
        mBrightnessLabel = findViewById(R.id.brightnessLabel);
        frameCountButton = findViewById(R.id.frameCountButton);
        frameCountButton.setAlpha(0);
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

                    // permission was granted, yay!


                } else {

                    // permission denied, boo!
                }
                return;
            }

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

        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER); // Tap to focus!
        cameraView.mapGesture(Gesture.LONG_TAP, GestureAction.CAPTURE); // Long tap to shoot!


        mLibraryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent libraryActivity = new Intent(MainActivity.this, PhotosActivity.class);
                startActivity(libraryActivity);
            }
        });

        mCaptureMode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(captureMode < 2){
                    captureMode++;
                } else {
                    captureMode = 0;
                }

                switch(captureMode){
                    case SessionInfo.SINGLE_FRAME:
                        mCaptureMode.setBackgroundResource(R.drawable.single_mode);
                        frameCountButton.setEnabled(false);
                        frameCountButton.setAlpha(0);
                        break;
                    case SessionInfo.LOWLIGHT_COMPOSIT:
                        mCaptureMode.setBackgroundResource(R.drawable.composite_mode);
                        frameCountButton.setEnabled(true);
                        frameCountButton.setAlpha(1);
                        break;
                    case SessionInfo.SUBJECT_COMPOSIT:
                        mCaptureMode.setBackgroundResource(R.drawable.subject_mode);
                        break;
                    default:
                        break;
                }
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

        frameCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameCount++;
                if(frameCount > maxFrameCountAllowed){
                    frameCount = 2;
                }

                frameCountButton.setText(String.valueOf(frameCount));
            }
        });

        //cameraView.setRotation(90); // UnComment this if the preview is rotated 90 degrees.

        mVertSlider.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        // Camera returns min and max exposure values this maps them to the slider
                        float min = cameraView.getCameraOptions().getExposureCorrectionMinValue();
                        float max = cameraView.getCameraOptions().getExposureCorrectionMaxValue();
                        cameraView.setExposureCorrection((Math.abs(min - max)/100.0f * seekBar.getProgress()) - (max - min)/2);
                        mBrightnessLabel.setText((mVertSlider.getProgress()-50)*2 + "%");

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mVertSlider.setAlpha(1);
                        mBrightnessLabel.setAlpha(1);

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mVertSlider.setAlpha((float) 0.25);
                        mBrightnessLabel.setAlpha((float) 0.25);
                        // ISO Might not bee the correct term here
                        mBrightnessLabel.setText((mVertSlider.getProgress()-50)*2 + "%");

                    }




                }
        );
    }

    private void captureImage(){
        //cameraView.capturePicture();
        cameraView.captureSnapshot();

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

//        HEY THIS IS THE NAME THAT GETS ADDED TO THE DB

        switch (captureMode) {

            case SessionInfo.SINGLE_FRAME:

                String imagePath = new SavePhotoTask().doInBackground(picture);
                saveImageToDb(imagePath);
                updateSessionId();
                notifyMediaStore(imagePath);


                // Add to DB image location: imageName
                break;
            case SessionInfo.LOWLIGHT_COMPOSIT:

                if(builder == null || builder.isFinished()){

                    int w = cameraView.getPreviewSize().getWidth();
                    int h = cameraView.getPreviewSize().getHeight();

                    builder = new CompositeBuilder(this, frameCount,w,h, new Runnable(){
                        @Override
                        public void run() {
                            stepFinished();
                        }
                    });
                }

                builder.addImage(picture);
                if (!builder.isFull()){
                    cameraView.captureSnapshot();
                }else{
                    Toast.makeText(this,"Processing...",Toast.LENGTH_SHORT).show();
                    HandlerThread ht = new HandlerThread("CompositBuilder");
                    ht.start();
                    Looper looper = ht.getLooper();
                    while (looper == null){
                        System.out.println("waiting");
                    }
                    Handler handler = new Handler(looper);
                    builder.setHandlerThread(ht);
                    handler.post(builder);

                }

                break;
            case SessionInfo.SUBJECT_COMPOSIT:
                break;
        }
    }

    private void saveImageToDb(String pathToImage){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        dbHelper.createImage(pathToImage, dateFormat.format(date).toString(), SessionInfo.currentSessionID);
    }

    private void updateSessionId(){
        switch (captureMode){
            case SessionInfo.LOWLIGHT_COMPOSIT: break;
            case SessionInfo.SINGLE_FRAME: SessionInfo.resetSessionID(); break;
            case SessionInfo.SUBJECT_COMPOSIT: break;
            default: break;
        }
    }

    private void onCaptureModeButtonTapped(){
        captureMode++;
        captureMode = captureMode%3;
    }

    private void stepFinished(){

        if (builder.isFinished()) {
            final HandlerThread ht = new HandlerThread("saver");
            ht.start();
            final Looper looper = ht.getLooper();
            Handler handler = new Handler(looper);

            final String id = builder.getID();
            final Bitmap bmp = builder.getComposite();
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File dir = new File(file_path);
                    if (!dir.exists())
                        dir.mkdirs();
                    File file = new File(dir, "compositStep" + id + ((new Date().getTime()) + ".png"));


                    try {
                        FileOutputStream fOut = new FileOutputStream(file);
                        bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                        notifyMediaStore(file.getPath());

                        saveImageToDb(file.getAbsolutePath());

                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        Log.i(TAG, "stepFinished: Failed to saved Image to " + file_path);

                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            killBilder();
                            ht.quit();
                        }
                    });
                }
            };

            handler.post(runner);

            //new MyAsyncTask(runner).doInBackground();

        }

    }

    private  void notifyMediaStore(String imLocation){
        try{
            MediaStore.Images.Media.insertImage(getContentResolver(), imLocation, "A Silent Photo", "Captured by Silent Night");
        }catch(Exception e){
            System.out.println(e);
        }
    }
    private void killBilder(){
        Toast.makeText(this,"Done!",Toast.LENGTH_SHORT).show();
        builder = null;
    }
}
