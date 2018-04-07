package com.example.harsimar.hospi;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HospiMaps extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    public static JSONArray jsonArray;
    static public final double currentLatitude=21.167353;
    static public final double currentLongitude=72.785095;
    public static final String HOSPI_NAME ="name";
    public static final String JSON_ARRAY ="json_array";
    private Button hospiDetails;
    private Marker currentMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospi_maps);
        hospiDetails = (Button)findViewById(R.id.hospi_details);
        if (googleApiAvailability()) {
            Log.d("harsimarSingh", "Google Api is Available");
            initMaps();
        }

        hospiDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentMarker == null){
                    Toast.makeText(HospiMaps.this,"Please select a marker",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("harsimarSingh","Launching HospiDetails with "+currentMarker.getTitle());
                Intent hospiDetails = new Intent(HospiMaps.this,HospiDetails.class);
                hospiDetails.putExtra(HOSPI_NAME,currentMarker.getTitle());
                startActivity(hospiDetails);
            }
        });

    }

    public void initMaps(){
        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.maps_fragment);
        fragment.getMapAsync(this);
    }
    public boolean googleApiAvailability() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) return true;
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();

        } else
            Toast.makeText(HospiMaps.this, "Cant connect to the network", Toast.LENGTH_LONG).show();
        return false;
    }

    public List<Address> gpsConverter(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        return addresses;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Log.d("harsimarSingh","Map Ready");
        Log.d("harsimarSingh","lat "+currentLatitude);
        Log.d("harsimarSingh","long "+currentLongitude);
        goToLocationZoomed(currentLatitude,currentLongitude,12);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        jsonArray = JsonLoader.loadJSONFromAsset(getApplicationContext());
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject hospJosn = jsonArray.getJSONObject(i);
                String coordinate = hospJosn.getString("Location_Coordinates");
                List<String> coordinateList = Arrays.asList(coordinate.split(","));
                String hospName = hospJosn.getString("Hospital_Name");
                if(coordinateList.get(0) != null && coordinateList.get(0).matches("[-+]?\\d*\\.?\\d+"))
                    try {
                        double longJson = Double.parseDouble(coordinateList.get(1));
                        double latJson = Double.parseDouble(coordinateList.get(0));
                        if(distance(currentLatitude,currentLongitude,latJson,longJson)<10) {
                            MarkerOptions options = new MarkerOptions().title(hospName).position(new LatLng(latJson, longJson));
                            mGoogleMap.addMarker(options);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                hospiDetails.setVisibility(View.VISIBLE);
                marker.setTag(marker.getTitle());
                currentMarker = marker;
                return true;
            }
        });
        this.mGoogleMap.setMyLocationEnabled(true);

    }
    private void goToLocationZoomed(double latitude, double longitude,int zoom) {
        LatLng ll=new LatLng(latitude,longitude);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(ll,zoom);
        mGoogleMap.animateCamera(update);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

}
