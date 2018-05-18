package com.example.developer.googlemapsdemoapp;

import android.os.AsyncTask;

public class FetchAdress extends AsyncTask<String, Void, String> {

    private OnAdressGetListener onAdressGetListener;

    FetchAdress(OnAdressGetListener onAdressGetListener) {
        this.onAdressGetListener = onAdressGetListener;
    }

    public interface OnAdressGetListener{
        void onAdressGet(String adress);
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

        AdressJsonParser adressJsonParser = new AdressJsonParser();
        String adress = adressJsonParser.parse(result);
        if (onAdressGetListener != null) {
            onAdressGetListener.onAdressGet(adress);
        }
    }
}
