package com.example.developer.googlemapsdemoapp;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchData extends AsyncTask<String, Void, String> {

    private GoogleMap map;

    public FetchData(GoogleMap map) {
        this.map = map;
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

        String[] directionsList;
        DataParser dataParser = new DataParser();
        directionsList = dataParser.parse(result);
        displayDirection(directionsList);
    }

    public void displayDirection(String[] directionsList) {
        if (directionsList != null){
            int count = directionsList.length;
            for(int i = 0;i<count;i++) {
                PolylineOptions options = new PolylineOptions();
                options.color(Color.RED);
                options.width(10);
                options.addAll(PolyUtil.decode(directionsList[i]));

                map.addPolyline(options);
            }
        }
    }
}
