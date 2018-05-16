package com.example.developer.googlemapsdemoapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, FetchAdress.OnAdressGetListener {

    private static final String LOG_TAG  = FragmentActivity.class.getSimpleName();

    EditText from;
    EditText to;
    private GoogleMap map;
    private UiSettings uiSettings;
    private List<LatLng> markerpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        uiSettings = map.getUiSettings();

        uiSettings.setZoomControlsEnabled(true);

        map.setOnMapClickListener(this);

        markerpoints = new ArrayList<>();

        LatLng sydney = new LatLng(47, 39);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (markerpoints.size() > 1){
            markerpoints.clear();
            map.clear();
            from.setText("");
            to.setText("");
        }
        map.addMarker(new MarkerOptions().position(latLng));
        markerpoints.add(latLng);

        getAdress(latLng);

        if (markerpoints.size() == 2) {
            LatLng origin = markerpoints.get(0);
            LatLng destination = markerpoints.get(1);

            String directionsUrl = getDirectionsUrl(origin, destination);
            FetchData fetchData = new FetchData(map);
            fetchData.execute(directionsUrl);

            map.moveCamera(CameraUpdateFactory.newLatLng(origin));
            map.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }

    private String getDirectionsUrl(LatLng from, LatLng to){

        String str_origin = "origin="+from.latitude+","+from.longitude;
        String str_dest = "destination="+to.latitude+","+to.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String url = "https://maps.googleapis.com/maps/api/directions/json?"+parameters;

        return url;
    }

    private String getGeoCodingUrl(LatLng latLng){
        String baseurl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
        String latitude = String.valueOf(latLng.latitude);
        String longitude = String.valueOf(latLng.longitude);

        String geoCodingUrl = baseurl + latitude + "," + longitude + "&key=" + "AIzaSyDy7eOHAFMIv7k4eDWns5w9fMcmCJ1XaVo";
        Log.e(LOG_TAG, geoCodingUrl);
        return geoCodingUrl;
    }

    public void getAdress(LatLng latLng){
        FetchAdress fetchAdress = new FetchAdress(this);
        fetchAdress.execute(getGeoCodingUrl(latLng));
    }

    @Override
    public void onAdressGet(String adress) {
        Log.e(LOG_TAG, "onAdressGet()");
        if (from.getText().toString().equals("")){
            from.setText(adress);
        } else {
            to.setText(adress);
        }
    }
}
