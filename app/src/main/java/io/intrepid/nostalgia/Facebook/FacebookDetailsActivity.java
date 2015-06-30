package io.intrepid.nostalgia.Facebook;

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


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromPost(JSONArray specificData) throws JSONException {
        String imageUrl = specificData.getJSONObject(0).get(FacebookConstants.PICTURE).toString();
        fbImage.setVisibility(View.VISIBLE);
        Picasso.with(this).
                load(imageUrl).fit().into(fbImage);
    }


}
