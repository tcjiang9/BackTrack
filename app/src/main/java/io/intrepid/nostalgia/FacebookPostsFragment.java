package io.intrepid.nostalgia;

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
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class FacebookPostsFragment extends Fragment {
    CallbackManager callbackManager;
    TextView name, status;
    ImageView fbImage;

    public FacebookPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("???????????????", String.valueOf(Constants.currentYear) + "'s facebook post created");
        // Inflate the layout for this fragment
        callbackManager = CallbackManager.Factory.create();

        getUserPosts();
        return inflater.inflate(R.layout.fragment_facebook_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = (TextView) view.findViewById(R.id.fb_name);
        status = (TextView) view.findViewById(R.id.fb_status);
        fbImage = (ImageView) view.findViewById(R.id.image_shared);
    }

    private void getUserPosts() {
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me/posts/", setUserSelectedDate(2015), HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        JSONObject jsonObject;
                        jsonObject = graphResponse.getJSONObject();
                        try {
                            JSONArray json = (JSONArray) jsonObject.get("data");
                            status.setText(json.getJSONObject(0).get("message").toString());
                            jsonObject = (JSONObject) json.getJSONObject(0).get("from");
                            name.setText(jsonObject.get("name").toString());
                            String str = json.toString();

                            if (str.contains("picture")) {
                                Log.e("str", str);
                                String imageUrl = json.getJSONObject(0).get("picture").toString();
                               // imageUrl = imageUrl.replace("\\","");
                             //   Log.e("image url",imageUrl);
                                fbImage.setVisibility(View.VISIBLE);
                                Picasso.with(getActivity()).
                                        load(imageUrl).into(fbImage);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();
    }

    private Bundle setUserSelectedDate(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        long initialTime = cal.getTimeInMillis() / 1000;
        Bundle parameters = new Bundle();
        parameters.putString("since", "" + initialTime);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        long limitTime = cal.getTimeInMillis() / 1000;
        parameters.putString("until", "" + limitTime);
        parameters.putString("limit", " 1");
       // Log.e("since", "" + initialTime);
        //Log.e("until", "" + limitTime);
        return parameters;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
