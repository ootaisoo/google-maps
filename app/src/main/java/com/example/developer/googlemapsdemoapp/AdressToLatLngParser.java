package com.example.developer.googlemapsdemoapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class AdressToLatLngParser {

    private static final String LOG_TAG  = AdressToLatLngParser.class.getSimpleName();

    public String parse(String result){
        String placeId = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(result);
            placeId = jsonObject.getJSONArray("predictions").getJSONObject(0).getString("place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(LOG_TAG, placeId);
        return placeId;
    }
}
