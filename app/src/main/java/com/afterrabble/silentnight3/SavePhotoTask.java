package com.afterrabble.silentnight3;

import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.afterrabble.silentnight3.db.ImageDbHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by AaronR on 11/15/17.
 */

class SavePhotoTask extends AsyncTask<byte[], String, String> {

    private ImageDbHelper dbHelper = null;
    String imageName;

    @Override
    protected String doInBackground(byte[]... jpeg) {


        imageName = UUID.randomUUID().toString();

        imageName = imageName +".jpg";

        File photo = new File(Environment.getExternalStorageDirectory(), imageName);

        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());

            fos.write(jpeg[0]);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.i("SAVEPHOTOTASK", "Exception in photoCallback", e);
            imageName = null;
        }

        return(photo.getPath());
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(imageName);
    }

}