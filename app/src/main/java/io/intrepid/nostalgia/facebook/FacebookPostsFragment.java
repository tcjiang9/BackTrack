package io.intrepid.nostalgia.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectViews;
import butterknife.OnClick;
import io.intrepid.nostalgia.DateFormatter;
import io.intrepid.nostalgia.R;


public class FacebookPostsFragment extends Fragment {

    public static final String YEAR_KEY = "YEAR_KEY";
    public static final int MILLISECOND_PER_SECOND = 1000;
    public static final String IMAGE_URL = "image_url";

    @InjectViews({R.id.likes_cnt, R.id.likes_cnt_2, R.id.likes_cnt_3})
    List<TextView> likesCount;
    @InjectViews({R.id.comments_cnt, R.id.comments_cnt_2, R.id.comments_cnt_3})
    List<TextView> commentsCount;
    @InjectViews({R.id.status_1, R.id.status_2, R.id.status_3})
    List<TextView> status;
    @InjectViews({R.id.time_post_1, R.id.time_post_2, R.id.time_post_3})
    List<TextView> timeStamp;
    @InjectViews({R.id.post_1, R.id.post_2, R.id.post_3})
    List<RelativeLayout> postLayout;
    @InjectViews({R.id.image_container, R.id.image_container_2, R.id.image_container_3})
    List<FrameLayout> imageLayout;
    @InjectViews({R.id.image_shared, R.id.image_shared_2, R.id.image_shared_3})
    List<ImageView> loadImages;

    CallbackManager callbackManager;
    private int currentYear;
    JSONObject completeDataFromFb;
    String[] imageUrl = new String[3];

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

    @OnClick({R.id.image_shared, R.id.image_shared_2, R.id.image_shared_3})
    void openScreenForActivity(View view) {
      openPhotoDetails(view.getId());
    }

    @OnClick({R.id.status_1, R.id.status_2, R.id.status_3})
    void statusUpdate(View view) {
        openPhotoDetails(view.getId());
    }

    private void openPhotoDetails(int viewType) {
        Intent intent = new Intent(getActivity(), FacebookPostDetailsActivity.class);
        Bundle bundle = new Bundle();
        if (completeDataFromFb != null) {
            try {
                JSONArray array = completeDataFromFb.getJSONArray(FacebookConstants.DATA);
                bundle.putString(FacebookConstants.JSON_OBJECT, array.getJSONObject(viewType).toString());
                bundle.putString(IMAGE_URL, imageUrl[viewType]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void getUserPosts() {
        if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(),
                    FacebookConstants.ME_POSTS, DateFormatter.makeFacebookDate(currentYear), HttpMethod.GET,
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
        completeDataFromFb = graphResponse.getJSONObject();
        try {
            JSONArray specificData = (JSONArray) completeDataFromFb.get(FacebookConstants.DATA);
            for (int i = 0; i < specificData.length(); i++) {
                FacebookResponse facebookResponse = new FacebookResponse(specificData.getJSONObject(i));
                if (specificData.getJSONObject(i).length() == 0) {
                    likesCount.get(i).setText(getString(R.string.no_activity_msg));
                }
                if (specificData.getJSONObject(i).toString().contains(FacebookConstants.PICTURE)) {
                    postLayout.get(i).setVisibility(View.VISIBLE);
                    imageLayout.get(i).setVisibility(View.VISIBLE);
                    timeStamp.get(i).setText(String.valueOf(facebookResponse.getCreatedTime()));
                    likesCount.get(i).setText(String.valueOf(facebookResponse.getLikeCount()));
                    commentsCount.get(i).setText(String.valueOf(facebookResponse.getCommentCount()));
                    loadImageFromPost(specificData.getJSONObject(i), loadImages.get(i), i);
                    /*if (!specificData.getJSONObject(i).has(FacebookConstants.MESSAGE)){
                        status.get(i).setVisibility(View.GONE);
                    } else {
                        status.get(i).setText(facebookResponse.getStatus());
                    }*/
                } else {
                    postLayout.get(i).setVisibility(View.VISIBLE);
                    status.get(i).setId(i);
                    timeStamp.get(i).setText(String.valueOf(facebookResponse.getCreatedTime()));
                    RelativeLayout.LayoutParams currentLayoutParams = (RelativeLayout.LayoutParams) status.get(i).getLayoutParams();
                    currentLayoutParams.addRule(RelativeLayout.BELOW, timeStamp.get(i).getId());
                    status.get(i).setText(specificData.getJSONObject(i).get(FacebookConstants.MESSAGE).toString());
                    likesCount.get(i).setText(String.valueOf(facebookResponse.getLikeCount()));
                    commentsCount.get(i).setText(String.valueOf(facebookResponse.getCommentCount()));

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromPost(JSONObject specificData, final ImageView image, final int imageId) throws JSONException {
        String obj = "/" + specificData.get(FacebookConstants.OBJECT_ID).toString();
        if (AccessToken.getCurrentAccessToken() != null) {
            getFullImage(image, imageId, obj);
        }

    }

    private void getFullImage(final ImageView image, final int imageId, String obj) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                obj,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject arr = response.getJSONObject();
                        try {
                            JSONArray jsonArr = arr.getJSONArray(FacebookConstants.IMAGES);
                            imageUrl[imageId] = jsonArr.getJSONObject(0).get(FacebookConstants.SOURCE).toString();
                            loadImage(imageId, image);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private void loadImage(int imageId, ImageView image) {
        image.setVisibility(View.VISIBLE);
        /*image.setId(imageId);
        if (imageUrl[0] != null) {
            Picasso.with(getActivity()).
                    load(imageUrl[0]).fit()
                    .into(image);
        }
        if (imageUrl[1] != null) {
            Picasso.with(getActivity()).
                    load(imageUrl[1]).fit()
                    .into(image);
        }
        if (imageUrl[2] != null) {
            Picasso.with(getActivity()).
                    load(imageUrl[2]).fit()
                    .into(image);
        }*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
