package io.intrepid.nostalgia.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectViews;
import butterknife.OnClick;
import io.intrepid.nostalgia.DateFormatter;
import io.intrepid.nostalgia.R;


public class FacebookPostsFragment extends Fragment {

    public static final String YEAR_KEY = "YEAR_KEY";
    public static final int MILLISECOND_PER_SECOND = 1000;
    @InjectViews({R.id.likes_cnt, R.id.likes_cnt_2, R.id.likes_cnt_3})
    List<TextView> likesCount;
    @InjectViews({R.id.comments_cnt, R.id.comments_cnt_2, R.id.comments_cnt_3})
    List<TextView> commentsCount;
    @InjectViews({R.id.time_post_1, R.id.time_post_2, R.id.time_post_3})
    List<TextView> timeStamp;
    @InjectViews({R.id.post_1, R.id.post_2, R.id.post_3})
    List<RelativeLayout> postLayout;
    @InjectViews({R.id.image_shared, R.id.image_shared_2, R.id.image_shared_3})
    List<ImageView> loadImages;

    CallbackManager callbackManager;
    private int currentYear;
    JSONObject completeDataFromFb;

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
    void openScreenForActivity3(View view) {
        if (view.getId() == loadImages.get(0).getId()) {
            openPhotoDetails(loadImages.get(0).getId());
        }
        if (view.getId() == loadImages.get(1).getId()) {
            openPhotoDetails(loadImages.get(1).getId());
        }
        if (view.getId() == loadImages.get(2).getId()) {
            openPhotoDetails(loadImages.get(2).getId());
        }
    }

    @OnClick({R.id.likes_cnt, R.id.likes_cnt_2, R.id.likes_cnt_3})
    void statusUpdate(View view) {
        if (view.getId() == likesCount.get(0).getId()) {
            openPhotoDetails(likesCount.get(0).getId());
        } else if (view.getId() == likesCount.get(1).getId()) {
            openPhotoDetails(likesCount.get(1).getId());
        } else if (view.getId() == likesCount.get(2).getId()) {
            openPhotoDetails(likesCount.get(2).getId());
        }

    }

    private void openPhotoDetails(int viewType) {
        Intent intent = new Intent(getActivity(), FacebookPostDetailsActivity.class);
        Bundle bundle = new Bundle();
        if (completeDataFromFb != null) {
            try {
                JSONArray array = completeDataFromFb.getJSONArray(FacebookConstants.DATA);
                bundle.putString(FacebookConstants.JSON_OBJECT, array.getJSONObject(viewType).toString());
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
                    FacebookConstants.ME_POSTS, DateFormatter.makeFacebookDate(2015), HttpMethod.GET,
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
            String responseStr = specificData.toString();

            for (int i = 0; i < specificData.length(); i++) {
                FacebookResponse facebookResponse = new FacebookResponse(specificData.getJSONObject(i));
                int likesCnt = facebookResponse.getLikeCount();
                if (specificData.getJSONObject(i).length() == 0) {
                    likesCount.get(i).setText(getString(R.string.no_activity_msg));
                }
                if (specificData.getJSONObject(i).toString().contains(FacebookConstants.PICTURE)) {
                    String imageUrl = specificData.getJSONObject(i).get(FacebookConstants.PICTURE).toString();
                    postLayout.get(i).setVisibility(View.VISIBLE);
                    likesCount.get(i).setText(String.valueOf(facebookResponse.getLikeCount()));
                    commentsCount.get(i).setText(String.valueOf(facebookResponse.getCommentCount()));
                    loadImageFromPost(specificData.getJSONObject(i), loadImages.get(i), i);
                } else {
                    likesCount.get(i).setText(specificData.getJSONObject(i).get(FacebookConstants.MESSAGE).toString());
                    likesCount.get(i).setId(i);

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromPost(JSONObject specificData, ImageView image, int imageId) throws JSONException {
        String imageUrl = specificData.get(FacebookConstants.PICTURE).toString();
        image.setVisibility(View.VISIBLE);

        image.setId(imageId);
        Picasso.with(getActivity()).
                load(imageUrl).fit().into(image);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
