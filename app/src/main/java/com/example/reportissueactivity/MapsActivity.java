package com.example.reportissueactivity;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    Double lat, lon;
    String place;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);
        place = getIntent().getStringExtra("place");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(location).title(place));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

            LatLng position = marker.getPosition();
            double latitude = position.latitude;
            double longitude = position.longitude;

            // Create an Intent to open Google Maps
            // Option 1: Show the location on the map
            // String uri = "geo:" + latitude + "," + longitude;

            // Option 2: Show the location on the map with a specific zoom level
            // String uri = "geo:" + latitude + "," + longitude + "?z=15"; // z is the zoom level

            // Option 3: Search for the location by its coordinates
            String uri = "geo:0,0?q=" + latitude + "," + longitude + "(" + marker.getTitle() + ")";

            // Option 4: Search for a specific place by name (less precise with coordinates)
            // String uri = "geo:0,0?q=" + marker.getTitle();

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps"); // Specify Google Maps package

            // Verify if Google Maps is installed on the device
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Handle the case where Google Maps is not installed
                // You could show a message to the user
                Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
            }

            // Consume the click event so that the default behavior (showing the info window) is not triggered
            return true;
        }

}