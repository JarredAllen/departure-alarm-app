package com.example.jarred.departurealarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * A class that runs code for showing a detailed view of all of the events in an activity
 *
 * @author Jarred
 * @version 10/29/2016
 */
public class DetailedEventViewActivity extends AppCompatActivity {

    private static String packageName="com.example.jarred.departurealarm";

    private UserEvent ue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_event_view);
        findViewById(R.id.edit_event_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEvent();
            }
        });
        ue= DatabaseRetriever.findEventByName(getIntent().getStringExtra(packageName+".eventName"));
        TextView eventName=(TextView)findViewById(R.id.event_name_display);
        eventName.setText(ue.getName());
        TextView eventLocation=(TextView)findViewById(R.id.event_location_display);
        eventLocation.setText(ue.getLocation().getAddress());

    }

    private void editEvent() {
        Intent intent=new Intent(this, EventEditorActivity.class);
        intent.putExtra(packageName+".eventEditorAction", "edit");
        intent.putExtra(packageName+".nameOfEvent", ue.getName());
        startActivity(intent);
    }
}
