package com.example.l18domin.tp501;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity /*FragmentActivity*/ implements OnMapReadyCallback {

    public GoogleMap mMap;
    public String pointLatStr ="";
    public String pointLongStr="";
    public LatLng userMarker;
    public ArrayList<LatLng> pointUser = new ArrayList<LatLng>();
    public int mapType = 1;
    public ArrayList<String> savedPointStr = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("lifecycle", "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            Log.i("lifecycle", "if saved instance");
            mapType = savedInstanceState.getInt("Map_Type");
            Log.i("lifecycle","len lista puntos string "+savedInstanceState.size());
            for (int i=0; i<= savedInstanceState.size();i++) {
                if (savedInstanceState.containsKey("Latitude"+i))
                    pointLatStr = savedInstanceState.getString("Latitude"+i);
                else pointLatStr = null;
                if (savedInstanceState.containsKey("Longitude"+i))
                    pointLongStr = savedInstanceState.getString("Longitude"+i);
                else pointLongStr = null;
                if (pointLatStr != null && pointLongStr != null) {
                    pointUser.add(new LatLng(Double.parseDouble(pointLatStr), Double.parseDouble(pointLongStr)));
                    Log.i("lifecycle", "1, pList: " + pointUser);
                }
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(mapType);

        // Add a marker in IMT and move the camera
        LatLng imt = new LatLng(48.359285, -4.569933); // Arg : lat:-34 long:-64
        mMap.addMarker(new MarkerOptions().position(imt).title("Marker in IMT"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(imt));

        //Permission request, the user must allow or deny it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions (this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }, 1) ;
        //
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Move map to your click and show position on toast
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Toast.makeText(MapsActivity.this, "Lat et Long :"+point.toString(), Toast.LENGTH_SHORT).show();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
            }
        });

        //Add marker on long click
        //It could be many markers
        mMap.setOnMapLongClickListener((new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                userMarker = point;
                mMap.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("Position: "));
                String pointStr = point.toString();
                String[] stringAux = pointStr.split(",");
                pointLatStr = stringAux[0].substring(10); //10.28
                pointLongStr = stringAux[1].substring(0,18); //0.18
                savedPointStr.add(pointLatStr);
                savedPointStr.add(pointLongStr);
                Log.i("lifecycle", "point list "+savedPointStr);
            }
        }));
        //
        if (pointUser != null)
            savedPointStr.clear();
            for (int i=1; i<=pointUser.size();i++) {
                mMap.addMarker(new MarkerOptions().position(pointUser.get(i-1)).title("Position: "));
                String pointStr = pointUser.get(i-1).toString();
                String[] stringAux = pointStr.split(",");
                pointLatStr = stringAux[0].substring(10); //10.28
                pointLongStr = stringAux[1].substring(0,18); //0.18
                savedPointStr.add(pointLatStr);
                savedPointStr.add(pointLongStr);
            }
    }


    //Creating the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }
    //

    //Menu items
    public boolean typeNormal (MenuItem item){
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        return true;
    }
    public boolean typeHybrid (MenuItem item){
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        return true;
    }
    public boolean typeSatellite (MenuItem item){
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        return true;
    }
    //

    //Save markers and map type
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("lifecycle", "saved instance");
        if (savedPointStr.size() != 0) {
            for (int i = 0;i<=savedPointStr.size()-1;i=i+2){
                outState.putString("Latitude"+i, savedPointStr.get(i));
                outState.putString("Longitude"+i, savedPointStr.get(i+1));
            }
        }
        outState.putInt("Map_Type", mMap.getMapType());
        super.onSaveInstanceState(outState);
    }
}
