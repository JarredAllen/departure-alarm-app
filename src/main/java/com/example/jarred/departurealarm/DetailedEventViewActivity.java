package com.example.jarred.departurealarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
    }

    private void editEvent() {
        Intent intent=new Intent(this, EventEditorActivity.class);
        intent.putExtra(packageName+".eventEditorAction", "edit");
        intent.putExtra(packageName+".nameOfEvent", ue.getName());
        startActivity(intent);
    }
}
