package io.intrepid.nostalgia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.nostalgia.CustomListAdapter;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.constants.FacebookConstants;
import io.intrepid.nostalgia.fragments.FacebookPostsFragment;
import io.intrepid.nostalgia.models.facebook.Comment;
import io.intrepid.nostalgia.models.facebook.FacebookResponse;


public class FacebookPostDetailsActivity extends AppCompatActivity {


    TextView status;
    ImageView fbImage;
    TextView likes;
    String url;
    @InjectView(R.id.display_comments)
    ListView comments;
    JSONObject onePostFromResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_details_post);
        ButterKnife.inject(this);
        View header = getLayoutInflater().inflate(R.layout.header_listview, comments, false);
        status = (TextView) header.findViewById(R.id.fb_status);
        fbImage = (ImageView) header.findViewById(R.id.full_picture);
        likes = (TextView) header.findViewById(R.id.likes_details);
        comments.addHeaderView(header);
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
        String likeNames = responsePojo.getLikeNames();
        if (responsePojo.getLikeCount() > 0) {
            likes.setText(getString(R.string.likes, likeNames));
        }

    }

    private void getComments(FacebookResponse facebookResponse) throws JSONException {
        
        List<Comment> commentData = facebookResponse.getCommentData();
        if (commentData != null) {
            CustomListAdapter listAdapter = new CustomListAdapter(this, commentData);
            comments.setAdapter(listAdapter);
        }
    }

    private void loadImageFromPost(FacebookResponse facebookResponse) throws JSONException {
        if (url != null) {
            fbImage.setVisibility(View.VISIBLE);
            Picasso.with(this).
                    load(url).fit().into(fbImage);
        }
    }


}
