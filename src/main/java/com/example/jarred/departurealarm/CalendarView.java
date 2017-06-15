package com.example.jarred.departurealarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A view of events
 *
 * @author Jarred
 * @version 11/2/2016
 */
public class CalendarView extends Fragment implements View.OnClickListener {

    private static String packageName="com.example.jarred.departurealarm";

    private InteractionListener mListener;

    private android.widget.CalendarView calendar;

    public CalendarView() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_view, container, false);
        calendar=(android.widget.CalendarView)view.findViewById(R.id.calendar_view);
        calendar.setOnClickListener(this);
        calendar.setDate(System.currentTimeMillis()/1000L);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(v==calendar) {
            Intent intent=new Intent(getActivity(), ShowEventListViewActivity.class);
            intent.putExtra(packageName+".startTime", calendar.getDate()+"");
            intent.putExtra(packageName+".endTime", ""+(calendar.getDate()+86400));
            startActivity(intent);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface InteractionListener {
        void viewEvent(String name);
    }
}
