package io.intrepid.nostalgia.models.facebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.intrepid.nostalgia.constants.FacebookConstants;


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
    List<Comment> data = new ArrayList<>();


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
                DateFormat date = new SimpleDateFormat("h:mma");
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
        if (getLikeCount() > 0) {
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
            return result.substring(0, result.lastIndexOf(","));
        } else
        return null;
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


    public List getCommentData() {
        try {
            for (int i = 0; i < getCommentCount(); i++) {
                JSONObject temp = (JSONObject) commentData.getJSONObject(i).get(FacebookConstants.FROM);
                data.add(i, new Comment(temp.getString(FacebookConstants.NAME),
                        commentData.getJSONObject(i).getString(FacebookConstants.MESSAGE)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }


}