package com.example.developer.googlemapsdemoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, FetchAdress.OnAdressGetListener, FetchLatLng.OnLatLngGetListener, LocationListener{

    private static final String LOG_TAG  = FragmentActivity.class.getSimpleName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;

    private TextView from;
    private TextView to;
    private GoogleMap map;
    private List<LatLng> markerpoints;
    private boolean mLocationPermissionGranted;
    private LatLng currentLocation;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        currentLocation = getCurrentLocation();

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
        UiSettings uiSettings = map.getUiSettings();

        uiSettings.setZoomControlsEnabled(true);

        map.setOnMapClickListener(this);

        markerpoints = new ArrayList<>();

        getDevicePermission();
        updateLocationUI();

        map.addMarker(new MarkerOptions().position(currentLocation));
        markerpoints.add(currentLocation);
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));
        getAdress(currentLocation);
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

    @Override
    public void onLatLngGet(LatLng latLng) {
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

    private void getDevicePermission() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {

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
                getDevicePermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "onLocationChanged()");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    public LatLng getCurrentLocation() {
        Location location = null;

        try {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            }
                        }
                    }
                }
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

        return currentLocation;
    }


}
