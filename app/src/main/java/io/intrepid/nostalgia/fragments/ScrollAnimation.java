package io.intrepid.nostalgia.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

import io.intrepid.nostalgia.R;


public class ScrollAnimation extends Fragment {
public static final String TAG = "ScrollFragment";
Timer timer = new Timer();
    public static ScrollAnimation newInstance() {
        ScrollAnimation fragment = new ScrollAnimation();
        return fragment;
    }

    public ScrollAnimation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scroll_animation, container, false);
    }


    public void deleteAfterAnimation() {
        if (getActivity().getSupportFragmentManager()!=null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                }
            }, 4000);
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
