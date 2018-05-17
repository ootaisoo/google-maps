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

public class FetchPlaceId extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG  = FetchPlaceId.class.getSimpleName();

    FetchLatLng.OnLatLngGetListener latLngGetListener;

    public FetchPlaceId(FetchLatLng.OnLatLngGetListener latLngGetListener) {
        this.latLngGetListener = latLngGetListener;
    }

    @Override
    protected String doInBackground(String... url) {
        String data = "";

        try {
            data = downloadUrl(url[0]);
        } catch (Exception e) {
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        AdressToLatLngParser parser = new AdressToLatLngParser();
        String placeId = parser.parse(result);

        FetchLatLng fetchLatLng = new FetchLatLng(latLngGetListener);
        fetchLatLng.execute(getPlaceDetailsUrl(placeId));
    }

    private String downloadUrl(String strUrl) throws IOException {
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

    private String getPlaceDetailsUrl(String plaseId){
        String baseurl = "https://maps.googleapis.com/maps/api/place/details/json?";
        return baseurl + "placeid=" + plaseId + "&key=" + "AIzaSyDy7eOHAFMIv7k4eDWns5w9fMcmCJ1XaVo" + "&language=RU";
    }
}
