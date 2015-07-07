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
            processFacebookResponse();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processFacebookResponse() {
        try {
    FacebookResponsePojo responsePojo = new FacebookResponsePojo (onePostFromResponse);
            String responseStr = onePostFromResponse.toString();
            if (onePostFromResponse.length() == 0) {
                name.setText(getString(R.string.no_activity_msg));
                status.setVisibility(View.GONE);
            } else {
                name.setText( responsePojo.getName());
                String response = onePostFromResponse.toString();
                if (response.contains(FacebookConstants.MESSAGE)) {
                    status.setText(responsePojo.getStatus());
                } else {
                    status.setText(getString(R.string.status_alternative));
                }
                if (response.contains(FacebookConstants.PICTURE)) {
                    loadImageFromPost(responsePojo);
                } else {
                    fbImage.setVisibility(View.GONE);
                }
                if (response.contains(FacebookConstants.LIKES)) {
                    getLikesCount(responsePojo);
                }
                if (onePostFromResponse.toString().contains(FacebookConstants.COMMENTS)) {
                    getComments(responsePojo);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLikesCount(FacebookResponsePojo responsePojo) throws JSONException {
        likes.setText(getString(R.string.likes, responsePojo.getLikeCount()));

    }

    private void getComments(FacebookResponsePojo responsePojo) throws JSONException {
        comments.setText(getString(R.string.comments, responsePojo.getCommentCount()));
    }

    private void loadImageFromPost(FacebookResponsePojo responsePojo) throws JSONException {
        String imageUrl = responsePojo.getPictureUrl();
        fbImage.setVisibility(View.VISIBLE);
        Picasso.with(this).
                load(imageUrl).fit().into(fbImage);
    }


}
