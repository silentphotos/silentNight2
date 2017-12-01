package com.afterrabble.silentnight3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.afterrabble.silentnight3.db.ImageDbHelper;

import java.util.ArrayList;
import java.util.List;

public class PhotosActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        dbHelper = ImageDbHelper.getInstance(this);
        dbHelper.open();

        List<String> imagesList = dbHelper.getAllImagePaths();
        String[] images = imagesList.toArray(new String[imagesList.size()]);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ImagesAdapter(this, images));
    }
}
