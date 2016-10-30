package com.example.jarred.departurealarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * The activity to see an overview of all upcoming events
 *
 * @author Jarred
 * @version 10/30/2016
 */
public class EventViewActivity extends AppCompatActivity implements CalendarView.InteractionListener {

    private static String packageName="com.example.jarred.departurealarm";

    private Switch eventViewType;

    private CalendarView calendar;

    private EventListView list;

    private Button createNewEventButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        eventViewType=(Switch)findViewById(R.id.event_view_type);
        eventViewType.setOnCheckedChangeListener(new EventViewChangeListener());
        if(savedInstanceState==null) {
            buildEventCalendar();
        }
        createNewEventButton=(Button)findViewById(R.id.create_event_button);
        createNewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createANewEvent();
            }
        });
        settingsButton=(Button)findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });
    }

    private void buildEventCalendar() {
        if(calendar==null) {
            calendar=new CalendarView();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.event_view_list, calendar).commit();
    }

    private void buildEventList() {
        if(list==null) {
            list=new EventListView();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.event_view_list, list).commit();
    }

    public void viewEvent(String name) {
        Intent intent=new Intent(this, DetailedEventViewActivity.class);
        intent.putExtra(packageName+".eventName", name);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DatabaseRetriever.writeToFirebase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseRetriever.writeToFirebase();
    }

    private class EventViewChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton v, boolean isChecked) {
            if (v.equals(eventViewType)) {
                if (isChecked) {
                    eventViewType.setText(R.string.event_calendar);
                    buildEventCalendar();
                } else {
                    eventViewType.setText(R.string.event_list);
                    buildEventList();
                }
            }
        }
    }

    /**
     * Launches an activity for creating a new event.
     */
    private void createANewEvent() {
        Intent intent=new Intent(this, EventEditorActivity.class);
        intent.putExtra(packageName+".eventEditorAction", "create");
        startActivity(intent);
    }

    private void showSettings(){
        //TODO: Implement this
    }
}
