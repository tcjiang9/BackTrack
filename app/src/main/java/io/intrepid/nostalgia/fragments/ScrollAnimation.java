package io.intrepid.nostalgia.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Timer;

import io.intrepid.nostalgia.R;


public class ScrollAnimation extends Fragment {
    public static final String TAG = "ScrollFragment";
    Animation ufoSlideUpAnimation, lightAnimation, cloudAnimation;
    Animation endAnimation;
    ImageView ufo, light, clouds;
    LinearLayout container;
    Timer timer = new Timer();

    public static ScrollAnimation newInstance() {
        ScrollAnimation fragment = new ScrollAnimation();
        return fragment;
    }

    public ScrollAnimation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup cont,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_scroll_animation, container, false);
        ufo = (ImageView) view.findViewById(R.id.ufo);
        light = (ImageView) view.findViewById(R.id.light);
        clouds = (ImageView) view.findViewById(R.id.clouds);
        container = (LinearLayout) view.findViewById(R.id.container);
        ufoSlideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_ufo);
        lightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.light_animation);
        cloudAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.cloud_animation);
        endAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.end_animation);
        ufoSlideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clouds.setVisibility(View.VISIBLE);
                clouds.startAnimation(cloudAnimation);
                light.setVisibility(View.VISIBLE);
                light.startAnimation(lightAnimation);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cloudAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                container.startAnimation(endAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        endAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ufo.setVisibility(View.GONE);
                light.setVisibility(View.GONE);
                clouds.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager().beginTransaction().remove(ScrollAnimation.this).commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // deleteAfterAnimation();
        return view;
    }

    public void deleteAfterAnimation() {
        // if (getActivity().getSupportFragmentManager() != null) {
        ufo.setVisibility(View.VISIBLE);
        ufo.startAnimation(ufoSlideUpAnimation);
        //}
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
