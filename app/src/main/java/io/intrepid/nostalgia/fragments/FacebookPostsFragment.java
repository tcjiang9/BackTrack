package io.intrepid.nostalgia.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import io.intrepid.nostalgia.DateFormatter;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.activities.FacebookPostDetailsActivity;
import io.intrepid.nostalgia.constants.Constants;
import io.intrepid.nostalgia.constants.FacebookConstants;
import io.intrepid.nostalgia.models.facebook.FacebookResponse;


public class FacebookPostsFragment extends Fragment {

    public static final String YEAR_KEY = "YEAR_KEY";
    public static final int MILLISECOND_PER_SECOND = 1000;
    public static final String IMAGE_URL = "image_url";
    public static final String TEXT_PLAIN = "text/plain";
    String sharingHeader;
    CallbackManager callbackManager;
    private int currentYear;
    JSONObject completeDataFromFb;
    String[] imageUrl = new String[3];

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
    @InjectViews({R.id.status_container, R.id.status_container_2, R.id.status_container_3})
    List<RelativeLayout> statusContainer;
    @InjectViews({R.id.image_container, R.id.image_container_2, R.id.image_container_3})
    List<FrameLayout> imageLayout;
    @InjectViews({R.id.image_shared, R.id.image_shared_2, R.id.image_shared_3})
    List<ImageView> loadImages;
    @InjectViews({R.id.share_post_1, R.id.share_post_2, R.id.share_post_3})
    List<ImageButton> sharePosts;
    @InjectView(R.id.no_facebook_content)
    TextView noFbMessage;
    @InjectView(R.id.no_fb_message)
    LinearLayout noFb;


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
        FacebookSdk.sdkInitialize(getActivity());
        ButterKnife.inject(this, rootView);
        currentYear = getArguments().getInt(YEAR_KEY);
        callbackManager = CallbackManager.Factory.create();
        getUserPosts();
        sharingHeader = getString(R.string.share_via);
        return rootView;
    }

    @OnClick(R.id.share_post_1)
    void onShareOne() {
        shareFacebookPost(0);
    }

    @OnClick(R.id.share_post_2)
    void onShareTwo() {
        shareFacebookPost(1);
    }

    @OnClick(R.id.share_post_3)
    void onShareThree() {
        shareFacebookPost(2);
    }

    @OnClick({R.id.image_shared, R.id.image_shared_2, R.id.image_shared_3})
    void onClickLayout(View view) {
        openPhotoDetails(view.getId());

    }

    @OnClick({R.id.status_1, R.id.status_2, R.id.status_3})
    void onClickStatus(View view) {
        openPhotoDetails(view.getId());

    }

    private void shareFacebookPost(int index) {
        if (imageUrl[index] != null) {
            sharePhoto(index);
        } else {
            shareText(index);
        }

    }

    private void sharePhoto(int index) {
        loadImages.get(index).setDrawingCacheEnabled(true);

        Bitmap bitmap = loadImages.get(index).getDrawingCache();
        File root = Environment.getExternalStorageDirectory();
        File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/image.jpg");
        try {
            cachePath.createNewFile();
            FileOutputStream ostream = new FileOutputStream(cachePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
        startActivity(Intent.createChooser(share, sharingHeader));
    }

    private void shareText(int index) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(TEXT_PLAIN);
        i.putExtra(Intent.EXTRA_TEXT, status.get(index).getText().toString());
        startActivity(Intent.createChooser(i, sharingHeader));
    }

    private void openPhotoDetails(int index) {
        Intent intent = new Intent(getActivity(), FacebookPostDetailsActivity.class);
        Bundle bundle = new Bundle();
        if (completeDataFromFb != null) {
            try {
                JSONArray array = completeDataFromFb.getJSONArray(FacebookConstants.DATA);
                bundle.putString(FacebookConstants.JSON_OBJECT, array.getJSONObject(index).toString());
                if (completeDataFromFb.toString().contains(FacebookConstants.PICTURE))
                    bundle.putString(IMAGE_URL, imageUrl[index]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void getUserPosts() {
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String access = editor.getString(Constants.SHARED_PREFS_ACCESS_TOKEN, null);
        if (access == null) {
            noFb.setVisibility(View.VISIBLE);
            noFbMessage.setText(getString(R.string.no_facebook_content));
        } else {
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
        if (isAdded()) {
            if (completeDataFromFb == null || completeDataFromFb.length() == 1) {
                noFb.setVisibility(View.VISIBLE);
                noFbMessage.setText(getString(R.string.no_activity_msg));
            } else {
                try {
                    JSONArray specificData = (JSONArray) completeDataFromFb.get(FacebookConstants.DATA);
                    //int size = specificData.length() -1;
                    for (int i = 0; i < specificData.length(); i++) {
                        FacebookResponse facebookResponse = new FacebookResponse(specificData.getJSONObject( i));
                        if (specificData.getJSONObject(i).get(FacebookConstants.TYPE).toString().equals(FacebookConstants.ADDED_PHOTOS)) {
                            postLayout.get(i).setVisibility(View.VISIBLE);
                            imageLayout.get(i).setVisibility(View.VISIBLE);
                            status.get(i).setId(i);
                            timeStamp.get(i).setText(String.valueOf(facebookResponse.getCreatedTime()));
                            likesCount.get(i).setText(String.valueOf(facebookResponse.getLikeCount()));
                            commentsCount.get(i).setText(String.valueOf(facebookResponse.getCommentCount()));
                            loadImageFromPost(specificData.getJSONObject(i), loadImages.get(i), i);
                            if (specificData.getJSONObject(i).has(FacebookConstants.MESSAGE)) {
                                status.get(i).setVisibility(View.VISIBLE);
                                status.get(i).setText(facebookResponse.getStatus());
                            }
                        } else if (specificData.getJSONObject(i).get(FacebookConstants.TYPE).toString().equals(FacebookConstants.STATUS)) {
                            postLayout.get(i).setVisibility(View.VISIBLE);
                            status.get(i).setVisibility(View.VISIBLE);
                            status.get(i).setId(i);
                            timeStamp.get(i).setText(String.valueOf(facebookResponse.getCreatedTime()));
                            status.get(i).setText(specificData.getJSONObject( i).get(FacebookConstants.MESSAGE).toString());
                            likesCount.get(i).setText(String.valueOf(facebookResponse.getLikeCount()));
                            commentsCount.get(i).setText(String.valueOf(facebookResponse.getCommentCount()));
                        } else {
                            noFb.setVisibility(View.VISIBLE);
                            noFbMessage.setText(getString(R.string.no_activity_msg));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
                        if (arr != null) {
                            try {
                                JSONArray jsonArr = arr.getJSONArray(FacebookConstants.IMAGES);
                                imageUrl[imageId] = jsonArr.getJSONObject(0).get(FacebookConstants.SOURCE).toString();
                                loadImage(imageId, image);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAsync();
    }

    private void loadImage(int imageId, ImageView image) {
        image.setVisibility(View.VISIBLE);
        postLayout.get(imageId).setId(imageId);
        image.setId(imageId);
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
