package io.intrepid.nostalgia.Facebook;

import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.GraphResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.nostalgia.R;


public class FacebookDetailsActivity extends AppCompatActivity {


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

    JSONObject completeDatafromFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_details);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        try {
            completeDatafromFb = new JSONObject(intent.getExtras().getString(FacebookConstants.JSON_OBJECT));
            processFacebookResponse();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processFacebookResponse() {
        try {
            JSONArray specificData = (JSONArray) completeDatafromFb.get(FacebookConstants.DATA);
            String responseStr = specificData.toString();
            Log.e("specific data", specificData.toString());
            if (specificData.length() == 0) {
                name.setText(getString(R.string.no_activity_msg));
                status.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < specificData.length(); i++) {
                    String response = specificData.getJSONObject(i).toString();
                    Log.e("value of i", "" + i);
                    completeDatafromFb = specificData.getJSONObject(i);
                    name.setText(completeDatafromFb.getJSONObject(FacebookConstants.FROM)
                            .get(FacebookConstants.NAME).toString());
                    String createdTime = completeDatafromFb.getString(FacebookConstants.CREATED_TIME);
                    if (response.contains(FacebookConstants.MESSAGE)) {
                        status.setText(completeDatafromFb.get(FacebookConstants.MESSAGE).toString());
                    }
                    if (response.contains(FacebookConstants.PICTURE)) {
                        loadImageFromPost(completeDatafromFb);
                    }
                    if (response.contains(FacebookConstants.LIKES)) {
                        getLikesCount(completeDatafromFb);
                        Log.e("value of i", "likes");
                    }
                    if (specificData.toString().contains(FacebookConstants.COMMENTS)) {
                        getComments(completeDatafromFb);
                        Log.e("value of i", "comments");
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
        likes.setText(getString(R.string.likes) + " " + String.valueOf(likesArr.length()));

    }

    private void getComments(JSONObject specificData) throws JSONException {
        JSONObject likesObj = (JSONObject) specificData.get(FacebookConstants.COMMENTS);
        JSONArray likesArr = likesObj.getJSONArray(FacebookConstants.DATA);
        likes.setText(getString(R.string.comments) + " " + String.valueOf(likesArr.length()));
    }

    private void loadImageFromPost(JSONObject specificData) throws JSONException {
        String imageUrl = specificData.get(FacebookConstants.PICTURE).toString();
        fbImage.setVisibility(View.VISIBLE);
        Log.e("in picture", imageUrl);
        Picasso.with(this).
                load(imageUrl).fit().into(fbImage);
    }


}
