package com.example.jarred.departurealarm;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarView.InteractionListener} interface
 * to handle interaction events.
 */
public class EventListView extends Fragment {

    private String packageName="com.example.jarred.departurealarm";

    private CalendarView.InteractionListener mListener;

    private Date startDate;
    private Date endDate;

    private SortedSet<UserEvent>events;

    private Map<TextView, String>eventNames;

    public EventListView() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_list_view, container, false);
        try {
            startDate = DatabaseRetriever.getSdf().parse(getArguments().getString(packageName + ".startTime", "This should break the sdf"));
        }
        catch (ParseException pe) {
            startDate = new Date();
        }
        try {
            endDate = DatabaseRetriever.getSdf().parse(getArguments().getString(packageName + ".endTime", "This should break the sdf"));
        }
        catch (ParseException pe) {
            endDate = new Date(startDate.getTime()+2678400L);//Displays a month after the start date if no end date is given to it
        }
        events=DatabaseRetriever.getEvents().tailSet(new UserEvent(startDate.getTime()-1,"",null)).headSet(new UserEvent(endDate.getTime(),"\uFFFF", null));
        eventNames=new HashMap<>();
        LinearLayout layout=(LinearLayout)v.findViewById(R.id.list);
        for(UserEvent ue:events) {
            TextView item=new TextView(getActivity());
            item.setText(ue.toUserFriendlyString());
            layout.addView(item);
            eventNames.put(item, ue.getName());
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CalendarView.InteractionListener) {
            mListener = (CalendarView.InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //It uses CalendarView's interaction listener
    //so it does not have one of its own

    public class ListenerForEventSelection implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String name=eventNames.get(v);
            if(name==null) {
                throw new RuntimeException("Null event name in EventListView.ListenerForEventSelection");
            }
            else {
                mListener.viewEvent(name);
            }
        }
    }
}
