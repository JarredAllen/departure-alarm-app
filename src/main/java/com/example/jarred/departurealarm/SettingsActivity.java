package com.example.jarred.departurealarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner travelModePicker=(Spinner)findViewById(R.id.travel_mode_picker);
        ArrayAdapter<CharSequence>aa=ArrayAdapter.createFromResource(this, R.array.modes, R.layout.support_simple_spinner_dropdown_item);
        travelModePicker.setAdapter(aa);
    }
}
