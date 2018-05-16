package com.example.developer.googlemapsdemoapp;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class AdressJsonParser {

    private static final String LOG_TAG  = FragmentActivity.class.getSimpleName();

    public String parse(String result){
        String adress = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(result);
            adress = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(LOG_TAG, adress);
        return adress;
    }
}
