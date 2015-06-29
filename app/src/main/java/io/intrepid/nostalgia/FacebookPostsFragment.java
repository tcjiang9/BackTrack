package io.intrepid.nostalgia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class FacebookPostsFragment extends Fragment {
    public static final String YEAR_KEY = "YEAR_KEY";

    @InjectView(R.id.fb_name)
    TextView name;
    @InjectView(R.id.fb_status)
    TextView status;
    @InjectView(R.id.image_shared)
    ImageView fbImage;

    CallbackManager callbackManager;
    private int currentYear;

    public static FacebookPostsFragment getInstance(int currentYear) {
        FacebookPostsFragment fragment = new FacebookPostsFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR_KEY, currentYear);
        fragment.setArguments(args);
        return fragment;
    }

    public FacebookPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_facebook_posts, container, false);
        ButterKnife.inject(this, rootView);
        currentYear = getArguments().getInt(YEAR_KEY);
        callbackManager = CallbackManager.Factory.create();
        getUserPosts();
        return rootView;
    }

    private void getUserPosts() {
        if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(),
                    "/me/posts/", setUserSelectedDate(currentYear), HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {
                            processFacebookResponse(graphResponse);
                        }
                    }).executeAsync();
        }
    }
    private void processFacebookResponse(GraphResponse graphResponse) {
        JSONObject completeDatafromFb;
        completeDatafromFb = graphResponse.getJSONObject();
        try {
            JSONArray specificData = (JSONArray) completeDatafromFb.get("data");
            if (specificData.length() == 0) {
                name.setText(getString(R.string.no_activity_msg));
                status.setVisibility(View.GONE);
            } else {
                completeDatafromFb = (JSONObject) specificData.getJSONObject(0).get("from");
                status.setText(specificData.getJSONObject(0).get("message").toString());
                name.setText(completeDatafromFb.get("name").toString());
                if (specificData.toString().contains("picture")) {
                    loadImagefromPost(specificData);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadImagefromPost(JSONArray specificData) throws JSONException {
        String imageUrl = specificData.getJSONObject(0).get("picture").toString();
        fbImage.setVisibility(View.VISIBLE);
        Picasso.with(getActivity()).
                load(imageUrl).into(fbImage);
    }

    private Bundle setUserSelectedDate(int year) {
        Bundle parameters = new Bundle();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        long initialTime = cal.getTimeInMillis() / 1000;
        parameters.putString("since", "" + initialTime);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        long limitTime = cal.getTimeInMillis() / 1000;
        parameters.putString("until", "" + limitTime);
        parameters.putString("limit", " 1");
        return parameters;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
