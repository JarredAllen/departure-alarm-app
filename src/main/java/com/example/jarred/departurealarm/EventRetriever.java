package com.example.jarred.departurealarm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handles loading in the events and storing them
 *
 * @author Jarred
 * @version 10/30/2016
 */
public final class EventRetriever {
    private EventRetriever(){}

    private static Collection<UserEvent> events;
    private static FirebaseStorage storage;

    private static boolean isLoaded;

    static {
        storage=FirebaseStorage.getInstance();
        isLoaded=false;
    }

    /**
     * Retrieve the list of events, building it if necessary. It returns its own data structure, so do not modify it or things will break.
     *
     * @return The events of the current user
     */
    public static Collection<UserEvent> getEvents() {
        if(events==null) {
            buildEvents();
        }
        return events;
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
                //TODO: Retrieve the text from the user's file
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
                        isLoaded = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO: Handle being unable to acquire the user's data from the server
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
        while(!isLoaded) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ie) {
                assert true;
            }
        }
        events.remove(ue);
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
        try {
            StorageReference toWrite = storage.getReference("gs://departurealarm-7445e.appspot.com/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/events.txt");
            toWrite.putBytes(turnEventsToString().getBytes());
        }
        catch(NullPointerException npe) {
            throw new IllegalStateException("The user must be logged in", npe);
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
        if(events.remove(ue)) {
            events.add(ue);
            return true;
        }
        return false;
    }
}
