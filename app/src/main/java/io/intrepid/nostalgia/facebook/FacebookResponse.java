package io.intrepid.nostalgia.facebook;

import android.util.Log;
import android.util.Pair;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class FacebookResponse {
    private JSONArray commentData = new JSONArray();

    private int likeCount = 0;
    private int commentCount = 0;
    private ArrayList<String> likeNames = new ArrayList<>();
    private String pictureUrl;
    private JSONObject parseJson = new JSONObject();
    private JSONArray likesData = new JSONArray();
    private String status;
    private String createdTime;
    private String name;
    ArrayList<Comments> data = new ArrayList<>();


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
            createdTime = createdTime.substring(createdTime.indexOf("T")+1, createdTime.lastIndexOf("+"));
            DateFormat militaryTime = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
            try {
                Date time = militaryTime.parse(createdTime);
                DateFormat date = new SimpleDateFormat("h:mma", Locale.US);
                createdTime = date.format(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return createdTime;
    }

    public String getStatus() {
        try {
            status = (parseJson.getString(FacebookConstants.MESSAGE));
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

    public String getLikeNames() {
        String result = "";
        try {
            for (int i = 0; i < getLikeCount(); i++) {
                likeNames.add(i, likesData.getJSONObject(i).getString(FacebookConstants.NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String likeName : likeNames) {
            result += likeName + ", ";
        }
        return result.substring(0,result.lastIndexOf(","));
    }

    public String getPictureUrl() {
        try {
            pictureUrl = parseJson.get(FacebookConstants.PICTURE).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return pictureUrl;
    }


    public FacebookResponse(JSONObject onePostDetails) {
        this.parseJson = onePostDetails;
    }


    public String getCommentData() {
        String stringBuilder = "";
        try {
            for (int i = 0; i < getCommentCount(); i++) {
                JSONObject temp = (JSONObject) commentData.getJSONObject(i).get(FacebookConstants.FROM);
                data.add(i, new Comments(temp.getString(FacebookConstants.NAME),
                        commentData.getJSONObject(i).getString(FacebookConstants.MESSAGE)));
            }
            for (int i = 0; i < data.size(); i++) {
                stringBuilder += (data.get(i).name + ": " + data.get(i).comment + "\n");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stringBuilder;
    }


}