package com.example.jarred.departurealarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A class that runs code for showing a detailed view of all of the events in an activity
 *
 * @author Jarred
 * @version 11/2/2016
 */
public class DetailedEventViewActivity extends AppCompatActivity implements DatabaseRetriever.EventsUpdateListener {

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
        if(ue==null) {
            finish();
        }
        else {
            TextView eventName = (TextView) findViewById(R.id.event_name_display);
            eventName.setText(ue.getName());
            TextView eventLocation = (TextView) findViewById(R.id.event_location_display);
            eventLocation.setText(ue.getLocation().getAddress());
            TextView eventTime = (TextView) findViewById(R.id.event_time_display);
            eventTime.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US).format(new Date(ue.getTime())));
            List<EventNotification> nots =ue.getNotifications();
            TableRow[]notsDisplay=new TableRow[5];
            notsDisplay[0]=(TableRow)findViewById(R.id.first_notification);
            notsDisplay[1]=(TableRow)findViewById(R.id.first_notification);
            notsDisplay[2]=(TableRow)findViewById(R.id.first_notification);
            notsDisplay[3]=(TableRow)findViewById(R.id.first_notification);
            notsDisplay[4]=(TableRow)findViewById(R.id.first_notification);
            for(int i=0; i<nots.size(); i++) {
                int minutes=nots.get(i).getMinutes();
                ((TextView)notsDisplay[i].getChildAt(0)).setText(String.format(Locale.US, "Notification at %d02:%d02 before departure time.", minutes/60, minutes%60));
            }
            TextView departureTime=(TextView)findViewById(R.id.departure_time_display);
            departureTime.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US).format(new Date(ue.getTime()-DatabaseRetriever.getTravelTime(ue))));
        }
    }

    private void editEvent() {
        Intent intent=new Intent(this, EventEditorActivity.class);
        intent.putExtra(packageName+".eventEditorAction", "edit");
        intent.putExtra(packageName+".nameOfEvent", ue.getName());
        startActivity(intent);
    }

    @Override
    public void onEventsUpdate(UserEvent ue) {
        if(ue==this.ue) {
            finish();
            if(DatabaseRetriever.getEvents().contains(this.ue)) {
                Intent intent=new Intent(this, DetailedEventViewActivity.class);
                intent.putExtra(packageName+".eventName", ue.getName());
                startActivity(intent);
            }
        }
    }
}
