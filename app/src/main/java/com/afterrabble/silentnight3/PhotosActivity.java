package com.afterrabble.silentnight3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.afterrabble.silentnight3.db.ImageDbHelper;

import java.io.File;
import java.util.Comparator;
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
        List<String> imageDates = dbHelper.getAllImageDates();

        // If the file was deleted remove it from the list so it's not shown
        // However this doenst seem to be working
        for (int i = 0; i < imagesList.size(); i++) {
            if(!(new File(imagesList.get(i)).exists())){

                imagesList.remove(i);
                imageDates.remove(i);
            }
        }


        String[] images = imagesList.toArray(new String[imagesList.size()]);
        String[] dates = imageDates.toArray(new String[imageDates.size()]);


        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ImagesAdapter(this, images, dates));
    }
}
