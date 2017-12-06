package com.afterrabble.silentnight3;

import android.os.AsyncTask;

/**
 * Created by AaronR on 12/4/17.
 */

public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

    private Runnable runner;

    public MyAsyncTask(Runnable runner) {
        this.runner = runner;
    }

    @Override
    public Void doInBackground(Void... voids) {
        runner.run();
        return null;
    }

}
