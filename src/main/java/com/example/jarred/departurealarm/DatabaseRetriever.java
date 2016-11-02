package com.example.jarred.departurealarm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Handles loading in everything from cloud storage, storing everything, and passing on the info.
 *
 * @author Jarred
 * @version 10/31/2016
 */
public final class DatabaseRetriever {

    private static final int NUM_SETTINGS=1;
    /**
     * Flag to use to retrieve the travel mode setting from the arraylist
     */
    public static final int TRAVEL_MODE=0;
    /**
     * Flag to use to retrieve the user's location from which to depart, stored as latitude and longitude formatted <code>"LAT,LONG"</code>.
     */
    public static final int DEPARTURE_LOCATION=1;

    private static ArrayList<DatabaseRetriever.EventsUpdateListener>listeners;



    private DatabaseRetriever(){}

    private static NavigableSet<UserEvent> events;
    private static FirebaseStorage storage;

    private static ArrayList<String> settings;

    private static String lastLoadTime;

    private static SimpleDateFormat sdf;

    private static boolean isBuildingSettings;
    private static boolean isBuildingEvents;
    private static boolean isBuildingLastLoad;
    private static boolean areEventsLoaded;
    private static boolean areSettingsLoaded;
    private static boolean areLastLoadTimeBuilt;

    static {
        storage=FirebaseStorage.getInstance();
        areEventsLoaded=false;
        areSettingsLoaded=false;
        isBuildingSettings=false;
        isBuildingEvents =false;
        isBuildingLastLoad=false;
        areLastLoadTimeBuilt=false;
        listeners=new ArrayList<>();
        sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US);
    }

    /**
     * Retrieve an unmodifiable view of the list of events, building it if necessary.
     *
     * @return The events of the current user
     */
    public static SortedSet<UserEvent> getEvents() {
        if(events==null) {
            isBuildingEvents =true;
            buildEvents();
        }
        return Collections.unmodifiableSortedSet(events);
    }

    /**
     * Retrieve an unmodifiable view of the list of settings, building it if necessary
     *
     * @return The current user's settings
     */
    public static List<String> getSettings() {
        if(settings==null) {
            isBuildingSettings=true;
            buildSettings();
        }
        return Collections.unmodifiableList(settings);
    }

    /**
     * Retrieves the request setting, formatted as a String
     *
     * @param flag The value to return, see aforemented constants
     */
    public static String getSettings(int flag) {
        if(!isBuildingSettings) {
            isBuildingSettings=true;
            buildSettings();
        }
        while(!areSettingsLoaded) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ie) {
                assert true;
            }
        }
        return settings.get(flag);
    }

    /**
     * Change one of the settings
     *
     * @param flag The setting to change, see the aforemented constants
     * @param newSetting The new value to change the setting to.
     */
    public static void changeSettings(int flag, String newSetting) {
        if(!isBuildingSettings) {
            isBuildingSettings=true;
            buildSettings();
        }
        while(!areSettingsLoaded) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ie) {
                assert true;
            }
        }
        settings.set(flag,newSetting);
    }

    /**
     * Retrieve the needed events and turn them into UserEvent classes
     */
    private static void buildEvents() {
        if(events==null) {
            FirebaseUser fu = FirebaseAuth.getInstance().getCurrentUser();
            if (fu == null) {
                throw new IllegalStateException("The user must be logged in.");
            }
            else {
                events = new TreeSet<>();
                StorageReference ref = storage.getReferenceFromUrl("gs://departurealarm-7445e.appspot.com/" + fu.getUid() + "/events.txt");
                ref.getBytes(1000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        char[] chars = new char[bytes.length];
                        for (int i = 0; i < bytes.length; i++) {
                            chars[i] = (char) bytes[i];
                        }
                        String text = new String(chars);
                        for (String event : text.split("\uFDD1")) {
                            events.add(UserEvent.userEventFromString(event));
                        }
                        areEventsLoaded = true;
                    }
                });
            }
        }
    }

    private static void buildSettings() {
        if(settings==null) {
            FirebaseUser fu=FirebaseAuth.getInstance().getCurrentUser();
            if(fu==null) {
                throw new IllegalStateException("The user must be logged in.");
            }
            else {
                settings=new ArrayList<>();
                StorageReference file=storage.getReferenceFromUrl("gs://departurealarm-7445e.appspot.com/" + fu.getUid() + "/settings.txt");
                file.getBytes(1000000L).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        char[] chars = new char[bytes.length];
                        for (int i = 0; i < bytes.length; i++) {
                            chars[i] = (char) bytes[i];
                        }
                        String text = new String(chars);
                        text=text.substring(1,text.length()-1);
                        settings.clear();
                        settings.addAll(Arrays.asList(text.split(",")));
                        while(settings.size()<NUM_SETTINGS) {
                            settings.add("");
                        }
                        areSettingsLoaded=true;
                    }
                });
            }
        }
    }

    /**
     * Removes an event from the list of events. This class shares a copy of its underlying data structure with any client, so it should automatically update.
     *
     * @param ue The event that the user has deleted
     */
    public static void removeEvent(UserEvent ue) {
        if(!isBuildingEvents) {
            isBuildingEvents =true;
            buildEvents();
        }
        while(!areEventsLoaded) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ie) {
                assert true;
            }
        }
        if(events.remove(ue)) {
            updateListeners(ue);
        }
    }

    /**
     * Add the given event to its list of events
     *
     * @param ue The event that the user just created
     */
    public static void addEvent(UserEvent ue) {
        if(events==null) {
            buildEvents();
        }
        events.add(ue);
        updateListeners(ue);
    }

    /**
     * Builds a string of all events that it can turn back into detailed info on all of the events.
     * @return The string that has been built, with one event on each line
     */
    public static String turnEventsToString() {
        String toReturn="";
        for(UserEvent ue:events) {
            toReturn+=ue.toString()+'\uFDD1';
        }
        return toReturn.substring(0,toReturn.length()-1);
    }

    public static void writeToFirebase() throws IllegalStateException {
        FirebaseUser fu=FirebaseAuth.getInstance().getCurrentUser();
        if(fu==null) {
            throw new IllegalStateException("The user must be logged in.");
        }
        else {
            StorageReference toWrite;
            if(areEventsLoaded) {
                toWrite = storage.getReference("gs://departurealarm-7445e.appspot.com/" + fu.getUid() + "/events.txt");
                toWrite.putBytes(turnEventsToString().getBytes());
            }
            if(areSettingsLoaded) {
                toWrite = storage.getReference("gs://departurealarm-7445e.appspot.com/" + fu.getUid() + "/settings.txt");
                toWrite.putBytes(settings.toString().getBytes());
            }
            if(areLastLoadTimeBuilt) {
                toWrite = storage.getReference("gs://departurealarm-7445e.appspot.com/" + fu.getUid() + "/lastWrite.txt");
                toWrite.putBytes(lastLoadTime.getBytes());
            }
        }
    }

    /**
     * Finds the first <code>UserEvent</code> with the given name.
     *
     * @param name The name to search for
     * @return The first <code>UserEvent</code> found with that name, or null if no such <code>UserEvent</code> exists.
     */
    public static @Nullable UserEvent findEventByName(String name) {
        for(UserEvent ue:events) {
            if(ue.getName().equals(name)) {
                return ue;
            }
        }
        return null;
    }

    /**
     * Places the event back into the appropriate spot of the list of events, a location from which it may have been moved. If the event is not already in its list of events, it returns false.
     *
     * @param ue The event to update
     * @return <code>true</code> if and only if its data set was updated.
     */
    public static boolean updateEvent(@NonNull UserEvent ue) {
        if(!isBuildingEvents) {
            isBuildingEvents=true;
            buildEvents();
        }
        while(!areEventsLoaded) {
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException ie) {
                assert true;
            }
        }
        if(events.remove(ue)) {
            events.add(ue);
            updateListeners(ue);
            return true;
        }
        return false;
    }

    private static void buildLastLoad() {
        if(lastLoadTime==null) {
            FirebaseUser fu=FirebaseAuth.getInstance().getCurrentUser();
            if(fu==null) {
                throw new IllegalStateException("The user must be logged in.");
            }
            else {
                StorageReference file=storage.getReferenceFromUrl("gs://departurealarm-7445e.appspot.com/" + fu.getUid() + "/lastWrite.txt");
                file.getBytes(1000000L).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        char[] chars = new char[bytes.length];
                        for (int i = 0; i < bytes.length; i++) {
                            chars[i] = (char) bytes[i];
                        }
                        lastLoadTime=new String(chars);
                        areLastLoadTimeBuilt=true;
                    }
                });
            }
        }
    }

    public static String getLastLoadTime() {
        if(!isBuildingLastLoad) {
            isBuildingLastLoad=true;
            buildLastLoad();
        }
        while(!areLastLoadTimeBuilt) {
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException ie) {
                assert true;
            }
        }
        return lastLoadTime;
    }

    public static void setLastLoadTime(String lastLoadTime) {
        isBuildingLastLoad=true;
        areLastLoadTimeBuilt=true;
        DatabaseRetriever.lastLoadTime = lastLoadTime;
    }

    /**
     * Query google maps to figure out the travel time to get to the given UserEvent
     *
     * @param ue The event to figure out travel time to
     * @return The number of seconds it is expected to take.
     */
    public static int getTravelTime(UserEvent ue) {
        LatLng loc=ue.getLocation().getLatLng();
        String destination=loc.longitude+","+loc.latitude;
        String urlAdd="https://maps.googleapis.com/maps/api/directions/json?";
        urlAdd+="origin=";//Calculate the origin
        urlAdd+="&destination="+destination;//Calculate the target
        urlAdd+="&arrival_time="+ue.getTime();
        if(!getSettings(TRAVEL_MODE).equalsIgnoreCase("driving")) {
            urlAdd+="&mode="+getSettings(TRAVEL_MODE).toLowerCase();
        }
        URL url;
        int time=-1;
        try {
            url=new URL(urlAdd);
            BufferedReader input=new BufferedReader(new InputStreamReader(url.openStream()));
            boolean hasTime=false;
            String line;
            while(!hasTime) {
                line=input.readLine();
                if(line.contains("\"duration\"")) {
                    input.readLine();
                    line=input.readLine();
                    line=line.split(":")[1];
                    time=Integer.parseInt(line);
                    hasTime=true;
                }
            }
        }
        catch (Exception e){
            assert true;
        }
        return time;
    }

    /**
     * Method to handle creating everything for all of the users.
     * Only to be called while logged in as a newly created user and before anything else is done.
     */
    public static void doOnCreateUser() {
        FirebaseUser fu=FirebaseAuth.getInstance().getCurrentUser();
        if(fu!=null) {
            StorageReference toWrite = storage.getReference("gs://departurealarm-7445e.appspot.com/" + fu.getUid() + "/events.txt");
            toWrite.putBytes("".getBytes());
            toWrite = storage.getReference("gs://departurealarm-7445e.appspot.com/" + fu.getUid() + "/settings.txt");
            toWrite.putBytes("[,,]".getBytes());
            toWrite = storage.getReference("gs://departurealarm-7445e.appspot.com/" + fu.getUid() + "/lastWrite.txt");
            toWrite.putBytes("0".getBytes());
            buildSettings();
            buildEvents();
            buildLastLoad();
        }
    }

    public interface EventsUpdateListener {
        void onEventsUpdate(UserEvent ue);
    }

    /**
     * Add the listener, to be notified upon any change to the list of events
     *
     * @param eul The listener to be added
     */
    public static void addListener(EventsUpdateListener eul) {
        listeners.add(eul);
    }

    private static void updateListeners(UserEvent ue) {
        for (EventsUpdateListener eul:listeners) {
            eul.onEventsUpdate(ue);
        }
    }

    /**
     * Remove the listener from its list of listeners
     *
     * @param eul The listener to be removed
     * @return True if the underlying collection was changed.
     */
    public static boolean removeListener(EventsUpdateListener eul) {
        return listeners.remove(eul);
    }

    public static SimpleDateFormat getSdf() {
        return sdf;
    }
}
