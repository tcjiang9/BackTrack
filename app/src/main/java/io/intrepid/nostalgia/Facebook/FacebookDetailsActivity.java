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
            completeDatafromFb = new JSONObject (intent.getExtras().getString(FacebookConstants.JSON_OBJECT));
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
            } else if (responseStr.contains(FacebookConstants.STORY)) {
                completeDatafromFb = (JSONObject) specificData.getJSONObject(0).get(FacebookConstants.FROM);
                status.setText(specificData.getJSONObject(0).get(FacebookConstants.STORY).toString());
                name.setText(completeDatafromFb.get(FacebookConstants.NAME).toString());
            } else if (responseStr.contains(FacebookConstants.STATUS)) {
                status.setText(specificData.getJSONObject(0).get(FacebookConstants.STATUS).toString());
                name.setText(completeDatafromFb.get(FacebookConstants.NAME).toString());
            }
            if (specificData.toString().contains(FacebookConstants.PICTURE)) {
                loadImageFromPost(specificData);
            }
            if (specificData.toString().contains(FacebookConstants.LIKES)){
                getLikesCount(specificData);
            } if (specificData.toString().contains(FacebookConstants.COMMENTS)){
                JSONObject likesObj = (JSONObject)specificData.getJSONObject(0).get(FacebookConstants.COMMENTS);
                JSONArray likesArr = likesObj.getJSONArray(FacebookConstants.DATA);
                Log.e("comments", likesArr.toString());
                comments.setText(getString(R.string.comments) + " " + String.valueOf(likesArr.length()));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLikesCount(JSONArray specificData) throws JSONException {
        JSONObject likesObj = (JSONObject)specificData.getJSONObject(0).get(FacebookConstants.LIKES);
        JSONArray likesArr = likesObj.getJSONArray(FacebookConstants.DATA);
        likes.setText(getString(R.string.likes) + " " + String.valueOf(likesArr.length()));
    }

    private void loadImageFromPost(JSONArray specificData) throws JSONException {
        String imageUrl = specificData.getJSONObject(0).get(FacebookConstants.PICTURE).toString();
        fbImage.setVisibility(View.VISIBLE);
        Log.e("in picture", imageUrl);
        Picasso.with(this).
                load(imageUrl).fit().into(fbImage);
    }


}
