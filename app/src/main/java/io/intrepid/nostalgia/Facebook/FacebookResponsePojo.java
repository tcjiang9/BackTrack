package io.intrepid.nostalgia.facebook;

import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.intrepid.nostalgia.R;

public class FacebookResponsePojo {
    private JSONArray commentData = new JSONArray();

    private int likeCount = 0;
    private int commentCount = 0;
    private String[] likeNames;
    private String pictureUrl;
    private JSONObject parseJson = new JSONObject();
    private JSONArray likesData = new JSONArray();
    private String status;
    private String createdTime;
    String name;

    public String getName() {
        try {
            JSONObject tempJson = parseJson.getJSONObject(FacebookConstants.FROM);
            name = tempJson.getString(FacebookConstants.NAME);
            return name;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getCreatedTime() {
        try {
            createdTime = parseJson.getString(FacebookConstants.CREATED_TIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return createdTime;
    }

    public String getStatus() {
        try {
            status = (parseJson.getString(FacebookConstants.MESSAGE));
            Log.e("in pojo 2", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }

    public int getCommentCount() {
            try {
                JSONObject commentObj = (JSONObject) parseJson.get(FacebookConstants.COMMENTS);
                commentData = commentObj.getJSONArray(FacebookConstants.DATA);
                commentCount = commentData.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return commentCount;
    }

    public int getLikeCount() {
            try {
                JSONObject likesObj = (JSONObject) parseJson.get(FacebookConstants.LIKES);
                likesData = likesObj.getJSONArray(FacebookConstants.DATA);
                likeCount = likesData.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return likeCount;
    }

    public String[] getLikeNames() {
        return likeNames;
    }

    public String getPictureUrl() {
            try {
                pictureUrl = parseJson.get(FacebookConstants.PICTURE).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return pictureUrl;
    }


    public FacebookResponsePojo(JSONObject onePostDetails) {
        this.parseJson = onePostDetails;
    }


    public JSONArray getCommentData() {


        return commentData;
    }


}