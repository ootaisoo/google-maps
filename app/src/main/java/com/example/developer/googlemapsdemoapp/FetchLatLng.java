package com.example.developer.googlemapsdemoapp;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class FetchLatLng extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG  = FetchLatLng.class.getSimpleName();

    private OnLatLngGetListener onLatLngGetListener;

    public FetchLatLng(OnLatLngGetListener onLatLngGetListener) {
        this.onLatLngGetListener = onLatLngGetListener;
    }

    public interface OnLatLngGetListener{
        void onLatLngGet(LatLng latLng);
    }

    @Override
    protected String doInBackground(String... url) {
        String data = "";

        try {
            data = UtilsUrl.downloadUrl(url[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e(LOG_TAG, result);

        PlaceIdParcer placeIdParcer = new PlaceIdParcer();
        LatLng latLng = placeIdParcer.parse(result);
        if (onLatLngGetListener != null) {
            onLatLngGetListener.onLatLngGet(latLng);
        }
    }
}
