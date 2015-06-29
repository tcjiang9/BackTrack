package io.intrepid.nostalgia;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class YearFragment extends Fragment {
    public static final String YEAR = "Display Year";
    private PrevYearButtonListener prevYearButtonListener;

    @InjectView(R.id.facebook_view)
    RelativeLayout facebookView;

    public interface PrevYearButtonListener {
        void onPrevYearButtonClicked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            prevYearButtonListener = (PrevYearButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PrevYearButtonListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Constants.currentYear = getArguments().getInt(YEAR); //the current year, for future use.

        Log.i("!!!!!!!!!!!!!!!!!!!!", String.valueOf(Constants.currentYear));

        View rootView = inflater.inflate(R.layout.fragment_year, container, false);
        getFragmentManager().beginTransaction()
                .add(R.id.facebook_view, new FacebookPostsFragment())
                .commit();

        Button prevYearButton = (Button) rootView.findViewById(R.id.previous_year_button);
        prevYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevYearButtonListener.onPrevYearButtonClicked();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences.getString(Constants.SHARED_PREFS_ACCESS_TOKEN, null) == null) {
            facebookView.setVisibility(View.GONE);
        }
        return rootView;
    }
}
