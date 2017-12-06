package com.afterrabble.silentnight3;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class ImageViewActivity extends AppCompatActivity {
    public static String currentImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView currentImage = (ImageView) findViewById(R.id.currentImage);
        currentImage.setImageURI(Uri.fromFile(new File(currentImagePath)));
    }

}
