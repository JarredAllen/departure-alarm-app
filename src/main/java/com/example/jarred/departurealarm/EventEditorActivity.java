package com.example.jarred.departurealarm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * The activity that the user can use to edit an existing event or create a new event.
 *
 * @author Jarred
 * @version 11/2/2016
 */
public class EventEditorActivity extends AppCompatActivity {

    private static String packageName="com.example.jarred.departurealarm";

    private String mode;

    private String eventNameToEdit;//Can be null if it is creating a new event. If a new event is being created, this variable is never looked at.

    private Button cancelButton, confirmButton, addNotificationButton, eventLocation;

    private EditText eventTime, eventName;

    private LinearLayout[]notificationEditors;
    private EditText[]notificationTexts;
    private Button[]notificationDeleteButtons;

    private int visibleEvents=1;

    private Date currentDate;

    private Place currentPlace;

    private ArrayList<TextView>errors;

    private ArrayList<SimpleDateFormat>dateFormats;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);
        dateFormats=new ArrayList<>();
        dateFormats.add(new SimpleDateFormat("dd.MM.yy HH:mm.ss", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd.MM.yy HH:mm", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd/MM/yy HH:mm", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US));
        dateFormats.add(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US));
        mode=getIntent().getStringExtra(packageName+".eventEditorAction");
        cancelButton=(Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirmButton =(Button)findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
        addNotificationButton=(Button)findViewById(R.id.add_new_notification_button);
        addNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNotification();
            }
        });
        if(mode.equals("edit")) {
            eventNameToEdit=getIntent().getStringExtra(packageName+".nameOfEvent");
            UserEvent ue=DatabaseRetriever.findEventByName(eventNameToEdit);
            if(ue==null) {
                eventNameToEdit=null;
                mode="create";
            }
            else {
                cancelButton.setText("Cancel Changes");
                confirmButton.setText("Save Changes");
                eventName.setText(eventNameToEdit);
                eventTime.setText(dateFormats.get(11).format(new Date(ue.getTime())));

            }
        }
        eventName=(EditText)findViewById(R.id.event_name);
        eventName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkEvent();
                return true;
            }
        });
        eventTime=(EditText)findViewById(R.id.event_time);
        eventTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setTime();
                return true;
            }
        });
        notificationEditors=new LinearLayout[5];
        notificationEditors[0]=(LinearLayout)findViewById(R.id.notification1);
        notificationEditors[1]=(LinearLayout)findViewById(R.id.notification2);
        notificationEditors[2]=(LinearLayout)findViewById(R.id.notification3);
        notificationEditors[3]=(LinearLayout)findViewById(R.id.notification4);
        notificationEditors[4]=(LinearLayout)findViewById(R.id.notification5);
        notificationTexts=new EditText[5];
        notificationTexts[0]=(EditText)findViewById(R.id.notification_time1);
        notificationTexts[1]=(EditText)findViewById(R.id.notification_time2);
        notificationTexts[2]=(EditText)findViewById(R.id.notification_time3);
        notificationTexts[3]=(EditText)findViewById(R.id.notification_time4);
        notificationTexts[4]=(EditText)findViewById(R.id.notification_time5);
        OnNotificationInAdvanceChanged oniac=new OnNotificationInAdvanceChanged();
        for(EditText et:notificationTexts) {
            et.setOnEditorActionListener(oniac);
        }
        notificationDeleteButtons=new Button[5];
        notificationDeleteButtons[0]=(Button)findViewById(R.id.delete_button1);
        notificationDeleteButtons[1]=(Button)findViewById(R.id.delete_button2);
        notificationDeleteButtons[2]=(Button)findViewById(R.id.delete_button3);
        notificationDeleteButtons[3]=(Button)findViewById(R.id.delete_button4);
        notificationDeleteButtons[4]=(Button)findViewById(R.id.delete_button5);
        OnNotificationRemoved onr=new OnNotificationRemoved();
        for(Button b:notificationDeleteButtons) {
            b.setOnClickListener(onr);
        }
        Button del=(Button)findViewById(R.id.delete_event_button);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisEvent();
            }
        });
        errors=new ArrayList<>();
        eventLocation=(Button)findViewById(R.id.pick_location_button);
        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPlace();
            }
        });
    }

    private void deleteThisEvent() {
        DatabaseRetriever.removeEvent(DatabaseRetriever.findEventByName(eventNameToEdit));
        finish();
    }

    private void setTime() {
        Date eventDate=null;
        for(SimpleDateFormat sdf:dateFormats) {
            try {
                Date d = sdf.parse(eventTime.getText().toString());
                if(d.getYear()>new Date().getYear()) {
                    if(d.getMonth()<12) {
                        eventDate=d;
                    }
                }
            }
            catch (ParseException pe) {
                assert true;
            }
        }
        if(eventDate==null) {
            eventTime.setError("This is not a recognized format. Recognized formats include:\ndd.MM.yy HH:mm:ss, dd.MM.yy HH:mm, dd.MM.yyyy HH:mm:ss, dd.MM.yyyy HH:mm");
            eventTime.requestFocus();
            errors.add(eventTime);
        }
        else {
            errors.remove(eventTime);
            currentDate=eventDate;
        }
    }

    private void selectPlace() {
        PlacePicker.IntentBuilder placePicker= new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(placePicker.build(this), 1);
        }
        catch (Exception e) {
            assert true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent extraData) {
        currentPlace=PlacePicker.getPlace(this, extraData);
        if(currentPlace==null) {
            errors.add(eventLocation);
        }
        else {
            errors.remove(eventLocation);
        }
    }

    private void addNotification() {
        notificationEditors[visibleEvents].setVisibility(View.VISIBLE);
        visibleEvents++;
    }

    private void checkEvent() {
        String name=eventName.getText().toString();
        if(DatabaseRetriever.findEventByName(name)!=null) {
            if(mode.equals("create")||!name.equals(eventNameToEdit)) {
                eventName.setError("You can't have two events with the same name.");
                eventName.requestFocus();
                errors.add(eventName);
            }
        }
    }

    private void removeNotification(int notification) {
        if(notification>=visibleEvents) {
            return;
        }
        for(int i=visibleEvents-1;i>=notification;i--) {
            notificationTexts[i].setText(notificationTexts[i+1].getText());
        }
        notificationTexts[visibleEvents-1].setText("");
        notificationEditors[visibleEvents-1].setVisibility(View.GONE);
    }

    private void saveChanges() {
        if(errors.isEmpty()) {
            switch (mode) {
                case "create":
                    createEvent();
                    break;

                case "edit":
                    UserEvent ue = DatabaseRetriever.findEventByName(eventNameToEdit);
                    if (ue == null) {
                        createEvent();
                        break;
                    }
                    editEvent(ue);
                    break;

                default:
                    throw new RuntimeException();//This line should not run, but I will leave it here just in case
            }
        }
        else {
            for(TextView v:errors) {
                v.setError("This field doesn't seem right.");
            }
        }
    }

    private void createEvent() {
        if(errors.isEmpty()) {
            ArrayList<EventNotification>enl=new ArrayList<>(5);
            for(int i=0;i<visibleEvents;i++) {
                String str=notificationTexts[i].getText().toString();
                int t=getMinutes(str);
                enl.add(new EventNotification(t));
            }
            UserEvent ue=new UserEvent(currentDate.getTime(),eventName.getText().toString(),currentPlace, enl);
            DatabaseRetriever.addEvent(ue);
        }
        else {
            for(TextView v:errors) {
                v.setError("This field doesn't seem right.");
            }
        }
    }

    private void editEvent(@NonNull UserEvent ue) {
        if(errors.isEmpty()) {
            ue.setName(eventName.getText().toString());
            ue.setTime(currentDate.getTime());
            ue.setLocation(currentPlace);
            ue.clearNotifications();
            for (int i=0;i<visibleEvents;i++) {
                String str=notificationTexts[i].getText().toString();
                int t=getMinutes(str);
                ue.addNotification(new EventNotification(t));
            }
        DatabaseRetriever.updateEvent(ue);
        }
        else {
            for(TextView v:errors) {
                v.setError("This field doesn't seem right.");
            }
        }
    }

    /**
     * Calculate the number of minutes from the number of hours and minutes
     *
     * @param str Time formatted as HH:MM, H:MM,MM
     * @return The number of minutes
     */
    private int getMinutes(String str) {
        int t=0;
        try {
            t=Integer.parseInt(str);
        }
        catch (NumberFormatException nfe) {
            String[] parts = str.split(":");
            t += Integer.parseInt(parts[1]);
            t += Integer.parseInt(parts[0]) * 60;
        }
        return t;
    }

    class OnNotificationRemoved implements View.OnClickListener {
        public void onClick(View v) {
            for(int i=0; i<notificationDeleteButtons.length; i++) {
                if(v==notificationDeleteButtons[i]) {
                    removeNotification(i);
                }
            }
        }
    }

    class OnNotificationInAdvanceChanged implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId==EditorInfo.IME_NULL) {
                String[] vals=v.getText().toString().split(":");
                if(!(vals.length==0)) {
                    v.setError("Not a valid time. Accepted formats: H:MM, HH:MM");
                    v.requestFocus();
                    errors.add(v);
                }
                try {
                    byte h=Byte.parseByte(vals[0]);
                    byte m=Byte.parseByte(vals[1]);
                    if(h>24||h<0||m>60||m<0||vals.length>2) {
                        v.setError("Not a valid time. Accepted formats: H:MM, HH:MM");
                        v.requestFocus();
                        errors.add(v);
                    }
                    else {
                        errors.remove(v);
                    }
                }
                catch (NumberFormatException nfe) {
                    v.setError("Not a valid time. Accepted format: HH:MM");
                    v.requestFocus();
                    errors.add(v);
                }
                catch (ArrayIndexOutOfBoundsException aioobe) {
                    try {
                        Byte.parseByte(v.getText().toString());
                    }
                    catch (NumberFormatException nfe) {
                        v.setError("Not a valid time. Accepted format: HH:MM");
                        v.requestFocus();
                        errors.add(v);
                    }
                }
            }
            return true;
        }
    }
}
