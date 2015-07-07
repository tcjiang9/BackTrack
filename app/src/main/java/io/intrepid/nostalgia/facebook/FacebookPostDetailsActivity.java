package io.intrepid.nostalgia.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.nostalgia.R;


public class FacebookPostDetailsActivity extends AppCompatActivity {


    @InjectView(R.id.like_cnt)
    TextView name;
    @InjectView(R.id.fb_status)
    TextView status;
    @InjectView(R.id.full_picture)
    ImageView fbImage;
    @InjectView(R.id.likes_details)
    TextView likes;
    @InjectView(R.id.comments)
    TextView comments;

    JSONObject onePostFromResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_details_post);
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
            FacebookResponse facebookResponse = new FacebookResponse(onePostFromResponse);
            if (onePostFromResponse.length() == 0) {
                name.setText(getString(R.string.no_activity_msg));
                status.setVisibility(View.GONE);
            } else {
                name.setText(facebookResponse.getName());
                String response = onePostFromResponse.toString();
                if (response.contains(FacebookConstants.MESSAGE)) {
                    status.setText(facebookResponse.getStatus());
                } else {
                    status.setText(getString(R.string.status_alternative));
                }
                if (response.contains(FacebookConstants.PICTURE)) {
                    loadImageFromPost(facebookResponse);
                } else {
                    fbImage.setVisibility(View.GONE);
                }
                if (response.contains(FacebookConstants.LIKES)) {
                    getLikesCount(facebookResponse);
                }
                if (onePostFromResponse.toString().contains(FacebookConstants.COMMENTS)) {
                    getComments(facebookResponse);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLikesCount(FacebookResponse responsePojo) throws JSONException {
        likes.setText(getString(R.string.likes, responsePojo.getLikeNames()));

    }

    private void getComments(FacebookResponse facebookResponse) throws JSONException {
        comments.setText(getString(R.string.comments, facebookResponse.getCommentData()));
    }

    private void loadImageFromPost(FacebookResponse facebookResponse) throws JSONException {
        String imageUrl = facebookResponse.getPictureUrl();
        fbImage.setVisibility(View.VISIBLE);
        Picasso.with(this).
                load(imageUrl).fit().into(fbImage);
    }


}
