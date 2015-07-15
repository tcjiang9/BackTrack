package io.intrepid.nostalgia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.constants.FacebookConstants;
import io.intrepid.nostalgia.fragments.FacebookPostsFragment;
import io.intrepid.nostalgia.models.facebook.FacebookResponse;


public class FacebookPostDetailsActivity extends AppCompatActivity {


    @InjectView(R.id.fb_status)
    TextView status;
    @InjectView(R.id.full_picture)
    ImageView fbImage;
    @InjectView(R.id.likes_details)
    TextView likes;
    @InjectView(R.id.comments)
    TextView comments;
    String url;
    JSONObject onePostFromResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_facebook_details_post);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        try {
            url = intent.getExtras().getString(FacebookPostsFragment.IMAGE_URL);
            onePostFromResponse = new JSONObject(intent.getExtras().getString(FacebookConstants.JSON_OBJECT));
            processFacebookResponse();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processFacebookResponse() {
        try {
            FacebookResponse facebookResponse = new FacebookResponse(onePostFromResponse);

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
        if (url != null) {
            fbImage.setVisibility(View.VISIBLE);
            Picasso.with(this).
                    load(url).fit().into(fbImage);
        }
    }


}
