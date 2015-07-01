package io.intrepid.nostalgia.Facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import io.intrepid.nostalgia.R;


public class FacebookPostsFragment extends Fragment {

    public static final String YEAR_KEY = "YEAR_KEY";
    public static final int MILLISECOND_PER_SECOND = 1000;
    @InjectView(R.id.fb_name)
    TextView name;
    @InjectView(R.id.fb_name_2)
    TextView nameFor2;
    @InjectView(R.id.fb_name_3)
    TextView nameFor3;
    @InjectViews({R.id.image_shared, R.id.image_shared_2,R.id.image_shared_3})
    List<ImageView> loadImages;

    CallbackManager callbackManager;
    private int currentYear;
    JSONObject completeDatafromFb;

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

    @OnClick(R.id.image_shared)
    void openFullScreenActivity() {
        Intent intent = new Intent(getActivity(), FacebookDetailsActivity.class);
        Bundle bundle = new Bundle();
        if (completeDatafromFb != null) {
            bundle.putString(FacebookConstants.JSON_OBJECT, completeDatafromFb.toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    private void getUserPosts() {
        if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(),
                    FacebookConstants.ME_POSTS, setUserSelectedDate(currentYear), HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {
                            processFacebookResponse(graphResponse);
                        }
                    }
            ).executeAsync();
        }
    }

    private void processFacebookResponse(GraphResponse graphResponse) {
        completeDatafromFb = graphResponse.getJSONObject();
        try {
            JSONArray specificData = (JSONArray) completeDatafromFb.get(FacebookConstants.DATA);
            String responseStr = specificData.toString();
            Log.e("response str", responseStr);
            if (specificData.length() == 0) {
                name.setText(getString(R.string.no_activity_msg));
            }
            for (int i = 0; i < specificData.length(); i++) {
                if (specificData.getJSONObject(i).toString().contains(FacebookConstants.PICTURE)) {
                    String imageUrl = specificData.getJSONObject(i).get(FacebookConstants.PICTURE).toString();
                    loadImageFromPost(specificData.getJSONObject(i), loadImages.get(i));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromPost(JSONObject specificData, ImageView image) throws JSONException {
        String imageUrl = specificData.get(FacebookConstants.PICTURE).toString();
        image.setVisibility(View.VISIBLE);
        Picasso.with(getActivity()).
                load(imageUrl).fit().into(image);
    }

    private Bundle setUserSelectedDate(int year) {
        Bundle parameters = new Bundle();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        long initialTime = cal.getTimeInMillis() / MILLISECOND_PER_SECOND;
        parameters.putString("since", "" + initialTime);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        long limitTime = cal.getTimeInMillis() / MILLISECOND_PER_SECOND;
        parameters.putString("until", "" + limitTime);
        parameters.putString("limit", "4");
        return parameters;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
