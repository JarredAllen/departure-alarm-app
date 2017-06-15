package com.example.jarred.departurealarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * A class that just views the <code>EventListView</code> fragment and passes to the fragment the arguments given to its Intent.
 *
 * @author Jarred
 * @version 11/2/2016
 */
public class ShowEventListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event_list_view);

        EventListView elv=new EventListView();
        elv.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_show_event_list_view, elv).commit();
    }
}
