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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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

        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (markerpoints.size() > 1){
            markerpoints.clear();
            map.clear();
        }
        map.addMarker(new MarkerOptions().position(latLng));
        markerpoints.add(latLng);

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

    private String getDirectionsUrl(LatLng from,LatLng to){

        String str_origin = "origin="+from.latitude+","+from.longitude;
        String str_dest = "destination="+to.latitude+","+to.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String url = "https://maps.googleapis.com/maps/api/directions/json?"+parameters;

        return url;
    }
}
