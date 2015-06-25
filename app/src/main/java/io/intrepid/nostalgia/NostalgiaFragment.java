package io.intrepid.nostalgia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NostalgiaFragment extends Fragment{
    public static final String YEAR = "Display Year";

    @InjectView(R.id.facebook_view)
    RelativeLayout facebookView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int currentYear = getArguments().getInt(YEAR);

        View rootView = inflater.inflate(R.layout.fragment_year, container, false);
        ButterKnife.inject(this, rootView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences.getString("accessToken", null) == null){
            facebookView.setVisibility(View.GONE);
        }
        return rootView;
    }
}
