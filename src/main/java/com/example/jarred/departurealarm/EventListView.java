package com.example.jarred.departurealarm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarView.InteractionListener} interface
 * to handle interaction events.
 */
public class EventListView extends Fragment {

    private CalendarView.InteractionListener mListener;

    public EventListView() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list_view, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CalendarView.InteractionListener) {
            mListener = (CalendarView.InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public void onClick(View v) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //It uses CalendarView's interaction listener
}
