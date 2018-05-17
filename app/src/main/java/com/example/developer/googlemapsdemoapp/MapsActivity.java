package com.example.developer.googlemapsdemoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, FetchAdress.OnAdressGetListener, FetchLatLng.OnLatLngGetListener{

    private static final String LOG_TAG  = FragmentActivity.class.getSimpleName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private TextView from;
    private TextView to;
    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private UiSettings uiSettings;
    private List<LatLng> markerpoints;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        googleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */,
//                        this /* OnConnectionFailedListener */)
//                .addConnectionCallbacks(this)
//                .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .build();
//        googleApiClient.connect();

        from = findViewById(R.id.from);
        to = findViewById(R.id.to);

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
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

        updateLocationUI();
        getDeviceLocation();

        map.addMarker(new MarkerOptions().position(currentLocation).title("Marker in Rostov"));
        markerpoints.add(currentLocation);
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
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
        return "https://maps.googleapis.com/maps/api/directions/json?"+parameters;
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                handleLatLng(place.getLatLng());
                onAdressGet(place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(LOG_TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // A step later in the tutorial adds the code to get the device location.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}
