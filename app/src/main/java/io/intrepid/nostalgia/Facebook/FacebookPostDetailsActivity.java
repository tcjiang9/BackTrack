package io.intrepid.nostalgia.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.nostalgia.R;


public class FacebookPostDetailsActivity extends AppCompatActivity {


    @InjectView(R.id.fb_name)
    TextView name;
    @InjectView(R.id.fb_status)
    TextView status;
    @InjectView(R.id.full_picture)
    ImageView fbImage;
    @InjectView(R.id.likes)
    TextView likes;
    @InjectView(R.id.comments)
    TextView comments;

    JSONObject onePostFromResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_first);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        try {
            onePostFromResponse = new JSONObject(intent.getExtras().getString(FacebookConstants.JSON_OBJECT));
            Log.e("budle value", onePostFromResponse.toString());
            processFacebookResponse();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processFacebookResponse() {
        try {
            String responseStr = onePostFromResponse.toString();
            if (onePostFromResponse.length() == 0) {
                name.setText(getString(R.string.no_activity_msg));
                status.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < onePostFromResponse.length(); i++) {
                    name.setText(onePostFromResponse.getJSONObject(FacebookConstants.FROM)
                            .get(FacebookConstants.NAME).toString());
                    String createdTime = onePostFromResponse.getString(FacebookConstants.CREATED_TIME);
                    String response = onePostFromResponse.toString();
                    if (response.contains(FacebookConstants.MESSAGE)) {
                        status.setText(onePostFromResponse.get(FacebookConstants.MESSAGE).toString());
                    } else{
                        status.setText(getString(R.string.status_alternative));
                    }
                    if (response.contains(FacebookConstants.PICTURE)) {
                        loadImageFromPost(onePostFromResponse);
                    } else {
                        fbImage.setVisibility(View.GONE);
                    }
                    if (response.contains(FacebookConstants.LIKES)) {
                        getLikesCount(onePostFromResponse);
                    }
                    if (onePostFromResponse.toString().contains(FacebookConstants.COMMENTS)) {
                        getComments(onePostFromResponse);
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLikesCount(JSONObject specificData) throws JSONException {
        JSONObject likesObj = (JSONObject) specificData.get(FacebookConstants.LIKES);
        JSONArray likesArr = likesObj.getJSONArray(FacebookConstants.DATA);
        likes.setText(getString(R.string.likes, String.valueOf(likesArr.length())));

    }

    private void getComments(JSONObject specificData) throws JSONException {
        JSONObject likesObj = (JSONObject) specificData.get(FacebookConstants.COMMENTS);
        JSONArray likesArr = likesObj.getJSONArray(FacebookConstants.DATA);
        comments.setText(getString(R.string.comments, String.valueOf(likesArr.length())));
    }

    private void loadImageFromPost(JSONObject specificData) throws JSONException {
        String imageUrl = specificData.get(FacebookConstants.PICTURE).toString();
        fbImage.setVisibility(View.VISIBLE);
        Picasso.with(this).
                load(imageUrl).fit().into(fbImage);
    }


}
