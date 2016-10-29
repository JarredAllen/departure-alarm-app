package com.example.jarred.departurealarm;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.IllegalFormatException;

/**
 * A class that represents events that the user may have
 *
 * @author Jarred
 * @version 10/27/2016
 */

public class UserEvent implements Comparable<UserEvent> {

    private long time;//number of seconds after Jan. 1, 1970
    private String name;
    private Place location;
    private ArrayList<EventNotification> notifications;

    /**
     * Completely and properly instantiates a UserEvent object
     *
     * @param time The number of seconds after midnight on Jan. 1, 1970
     * @param name The name of this event
     * @param location The location of this event, as a string (anything that Google Maps will take)
     * @param notificationTimes The number of seconds before departure for every notification
     */
    public UserEvent(long time, String name, Place location, ArrayList<EventNotification>notificationTimes) {
        this.time=time;
        this.name=name;
        this.location=location;
        this.notifications =notificationTimes;
    }


    /**
     * Completely and properly instantiates a UserEvent object
     *
     * @param time The number of seconds after midnight on Jan. 1, 1970
     * @param name The name of this event
     * @param location The location of this event, as a string (anything that Google Maps will take)
     */
    public UserEvent(long time, String name, Place location) {
        this(time, name, location, new ArrayList<EventNotification>());
    }

    /**
     * Builds a new <code>UserEvent</code> object from a String. Note that the
     *
     * @param str A string that was a result of a <code>UserEvent.toString()</code> call
     * @return A UserEvent equal to the one that first returned this string
     * @throws IllegalFormatException If the string does not match the format this class uses
     */
    public static UserEvent userEventFromString(@NonNull String str) {
        String[] items = str.split("\uFDD0");
        long time = Long.parseLong(items[0]);
        String name = items[1];
        final UserEvent ue;
        String notificationsString = items[3];
        notificationsString = notificationsString.substring(1, notificationsString.length() - 1);
        if (!notificationsString.equals("")) {
            String[] notifications = notificationsString.split(", ");
            ArrayList<EventNotification> newEvents = new ArrayList<>(notifications.length);
            for (String item : notifications) {
                newEvents.add(new EventNotification(Integer.parseInt(item)));
            }
            ue = new UserEvent(time, name, null, newEvents);
        } else {
            ue = new UserEvent(time, name, null);
        }
        Places.GeoDataApi.getPlaceById(new GoogleApiClient.Builder(new Application()).build(), items[2]).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                    ue.setLocation(places.get(0));
                }
            }
        });
        return ue;
    }

    @Override
    /**
     * Compares this UserEvent to another UserEvent, ordering them by time, then by name
     *
     * @param ue The UserEvent to compare this object to
     * @return A value as specified by Comparable.compareTo(Object obj) and the natural ordering of UserEvents
     */
    public int compareTo(@NonNull UserEvent ue) {
        if(time!=ue.time) {
            return (int)Math.signum(time-ue.time);
        }
        return name.compareTo(ue.name);
    }

    @Override
    /**
     * Compare an object to this object
     *
     * @param obj The object to compare this object to
     * @return true if obj is a <code>UserEvent</code> at the same time, with the same name, location, and notification times.
     */
    public boolean equals(Object obj) {
        if(obj instanceof UserEvent) {
            UserEvent ue =(UserEvent)obj;
            return (ue.time==time) && ue.name.equals(name) && ue.location.equals(location) && ue.notifications.equals(notifications);
        }
        return false;
    }

    @Override
    /**
     * Overridden <code>hashCode()</code> method so that it does not break data structures relying on the <code>hashCode()</code> method.
     *
     * @return The sum of the hash codes of its private variables
     */
    public int hashCode() {
        return ((int)time)+name.hashCode()+location.hashCode()+ notifications.hashCode();
    }

    @Override
    /**
     * Turns this <code>UserEvent</code> into a string that it can be turned back into a userEvent from.</br>
     * See (@Link UserEvent.userEventFromString) for stuff
     *
     * @return A string representation of this object
     */
    public String toString() {
        return time+""+'\uFDD0'+name+'\uFDD0'+location.getId()+'\uFDD0'+notifications.toString();
    }

    //Getters and setters

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        if(name==null) {
            name="";
        }
        this.name = name;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation(Place location) {
        this.location = location;
    }
}

class EventNotification implements Cloneable {

    private int minutes;

    /**
     * Creates a new object representing a notification to be sounded a set amount of time before the user needs to leave.
     *
     * @param minutes The number of minutes before departure that this needs to be sounded
     */
    public EventNotification(int minutes) {
        this.minutes=minutes;
    }

    @Override
    /**
     * Returns a hashcode for this object
     *
     * @return A hash code
     */
    public int hashCode() {
        return minutes;
    }

    @Override
    /**
     * Check if this object equals another (the other is an <code>EventNotification</code> with equal time before the event).
     *
     * @param obj The object to be compared to
     * @return true if and only if obj is equal to this object
     */
    public boolean equals(Object obj) {
        if(obj instanceof EventNotification) {
            return ((EventNotification)obj).minutes==minutes;
        }
        return false;
    }

    @Override
    /**
     * Returns an exact copy of this object.
     */
    protected Object clone() {
        return new EventNotification(minutes);
    }

    @Override
    /**
     * Returns a string containing the number of minutes before the event
     */
    public String toString() {
        return ""+minutes;
    }
}
