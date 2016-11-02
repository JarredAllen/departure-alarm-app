package com.example.jarred.departurealarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

/**
 * An activity to handle displaying user settings and letting the user pick new settings
 *
 * @author Jarred
 * @version 10/31/2016
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner travelModePicker=(Spinner)findViewById(R.id.travel_mode_picker);
        ArrayAdapter<CharSequence>aa=ArrayAdapter.createFromResource(this, R.array.modes, R.layout.support_simple_spinner_dropdown_item);
        travelModePicker.setAdapter(aa);

        Button selectNewLocation=(Button)findViewById(R.id.departure_location_set_button);
        selectNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDepartureAddress();
            }
        });

        String defLoc=DatabaseRetriever.getSettings(DatabaseRetriever.DEPARTURE_LOCATION);
        TextView defLocDisplay=(TextView)findViewById(R.id.current_default_location);
        defLocDisplay.setText(defLoc);

        String travelMode=DatabaseRetriever.getSettings(DatabaseRetriever.TRAVEL_MODE);
        for (int i=0;i<travelModePicker.getCount();i++) {
            if (travelModePicker.getItemAtPosition(i).toString().equalsIgnoreCase(travelMode)) {
                travelModePicker.setSelection(i);
                break;
            }
        }
    }

    private void selectDepartureAddress() {
        PlacePicker.IntentBuilder placePicker= new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(placePicker.build(this), 1);
        }
        catch (Exception e) {
            assert true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Place p=PlacePicker.getPlace(this, data);
        LatLng ll=p.getLatLng();
        String loc=ll.latitude+","+ll.longitude;
        DatabaseRetriever.changeSettings(DatabaseRetriever.DEPARTURE_LOCATION, loc);
    }
}
