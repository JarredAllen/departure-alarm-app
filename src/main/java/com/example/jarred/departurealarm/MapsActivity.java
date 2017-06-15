package com.example.jarred.departurealarm;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * An activity that shows the user directions to their event
 *
 * @author Jarred
 * @version 10/30/2016
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private UserEvent displayedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Displays the location of the event to the end user.
     *
     * @param googleMap The map to draw on.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(displayedEvent.getLocation().getLatLng()).title("Destination"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(displayedEvent.getLocation().getLatLng()));
    }
}
