package com.example.developer.googlemapsdemoapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchAdress extends AsyncTask<String, Void, String> {

    private OnAdressGetListener onAdressGetListener;

    FetchAdress(OnAdressGetListener onAdressGetListener) {
        this.onAdressGetListener = onAdressGetListener;
    }

    public interface OnAdressGetListener{
        void onAdressGet(String adress);
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

        AdressJsonParser adressJsonParser = new AdressJsonParser();
        String adress = adressJsonParser.parse(result);
        if (onAdressGetListener != null) {
            onAdressGetListener.onAdressGet(adress);
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
