package ppla5.handymanworkerapp;

import android.os.AsyncTask;

/**
 * Created by Ari on 5/2/2016.
 */
public class BackgroundTask extends AsyncTask <Void,Void,Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
