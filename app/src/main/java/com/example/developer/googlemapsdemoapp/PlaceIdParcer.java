package com.example.developer.googlemapsdemoapp;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceIdParcer {

    private static final String LOG_TAG  = PlaceIdParcer.class.getSimpleName();

    public LatLng parse(String result){
        String latitude = null;
        String longitude = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(result);
            latitude = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getString("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        return latLng;
    }
}
