package com.example.developer.googlemapsdemoapp;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
    protected String doInBackground(String... strings) {
        String data = "";

        try {
            data = downloadJson(strings[0]);
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

    private String downloadJson(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
}
