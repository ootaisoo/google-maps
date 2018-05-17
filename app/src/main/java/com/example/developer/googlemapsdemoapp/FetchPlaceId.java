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
import java.util.List;

public class FetchPlaceId extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG  = FetchPlaceId.class.getSimpleName();

    FetchLatLng.OnLatLngGetListener latLngGetListener;

    public interface OnPredictionsGetListener{
        void OnPredictionsGet(List<String> predictions);
    }

    public FetchPlaceId(FetchLatLng.OnLatLngGetListener latLngGetListener) {
        this.latLngGetListener = latLngGetListener;
    }

    @Override
    protected String doInBackground(String... url) {
        String data = "";

        try {
            data = UtilsUrl.downloadUrl(url[0]);
        } catch (Exception e) {
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.e(LOG_TAG, result);

        AdressToLatLngParser parser = new AdressToLatLngParser();
        String plaseId = parser.parse(result);
        FetchLatLng fetchLatLng = new FetchLatLng(latLngGetListener);
        fetchLatLng.execute(getPlaceIdUrl(plaseId));
    }

    private String getPlaceIdUrl(String plaseId){
        String baseurl = "https://maps.googleapis.com/maps/api/place/details/json?";
        return baseurl + "placeid=" + plaseId + "&key=" + "AIzaSyDy7eOHAFMIv7k4eDWns5w9fMcmCJ1XaVo" + "&language=RU";
    }
}
