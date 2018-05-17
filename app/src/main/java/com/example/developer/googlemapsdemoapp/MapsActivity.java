package com.example.developer.googlemapsdemoapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, FetchAdress.OnAdressGetListener, FetchLatLng.OnLatLngGetListener {

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

        from.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getCoordinates(from.getText().toString());
                    return true;
                }
                return false;
            }
        });

        to.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getCoordinates(to.getText().toString());
                    return true;
                }
                return false;
            }
        });
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
        getAdress(latLng);
        handleLatLng(latLng);
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
        return baseurl + latitude + "," + longitude + "&key=" + "AIzaSyDy7eOHAFMIv7k4eDWns5w9fMcmCJ1XaVo" + "&language=RU";
    }

    private String getreverseGeoCodingUrl(String input){
        String baseUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
        return baseUrl + "input=" + input + "&key=" + "AIzaSyDy7eOHAFMIv7k4eDWns5w9fMcmCJ1XaVo" + "&language=RU";
    }

    public void getAdress(LatLng latLng){
        FetchAdress fetchAdress = new FetchAdress(this);
        fetchAdress.execute(getGeoCodingUrl(latLng));
    }

    public void getCoordinates(String input){
        FetchPlaceId fetchLatLng = new FetchPlaceId(this);
        fetchLatLng.execute(getreverseGeoCodingUrl(input));
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

    @Override
    public void onLatLngGet(LatLng latLng) {
        Log.e(LOG_TAG, "onLatLngGet()");
        handleLatLng(latLng);
    }

    private void handleLatLng(LatLng latLng){
        if (markerpoints.size() > 1){
            markerpoints.clear();
            map.clear();
            from.setText("");
            to.setText("");
        }
        map.addMarker(new MarkerOptions().position(latLng));
        markerpoints.add(latLng);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

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


}
