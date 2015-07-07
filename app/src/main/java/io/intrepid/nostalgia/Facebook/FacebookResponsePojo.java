package io.intrepid.nostalgia.facebook;

import java.util.ArrayList;
import java.util.List;

public class FacebookResponsePojo {
    public class Action {


        private String name;

        private String link;

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         * The link
         */
        public String getLink() {
            return link;
        }

        /**
         *
         * @param link
         * The link
         */
        public void setLink(String link) {
            this.link = link;
        }

    }
    public class Comments {


        private List<Datum__> data = new ArrayList<Datum__>();


        /**
         *
         * @return
         * The data
         */
        public List<Datum__> getData() {
            return data;
        }

        /**
         *
         * @param data
         * The data
         */
        public void setData(List<Datum__> data) {
            this.data = data;
        }



    }
    public class Datum {

        private String id;
        private From from;
        private String story;
        private String picture;
        private String link;
        private String icon;
        private List<Action> actions = new ArrayList<Action>();
        private String type;
        private String statusType;
        private String createdTime;
        private String updatedTime;
        private Boolean isHidden;
        private Boolean subscribed;
        private Boolean isExpired;
        private Likes likes;
        private Comments comments;

        /**
         *
         * @return
         * The id
         */
        public String getId() {
            return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         *
         * @return
         * The from
         */
        public From getFrom() {
            return from;
        }

        /**
         *
         * @param from
         * The from
         */
        public void setFrom(From from) {
            this.from = from;
        }

        /**
         *
         * @return
         * The story
         */
        public String getStory() {
            return story;
        }

        /**
         *
         * @param story
         * The story
         */
        public void setStory(String story) {
            this.story = story;
        }


        /**
         *
         * @return
         * The picture
         */
        public String getPicture() {
            return picture;
        }

        /**
         *
         * @param picture
         * The picture
         */
        public void setPicture(String picture) {
            this.picture = picture;
        }

        /**
         *
         * @return
         * The link
         */
        public String getLink() {
            return link;
        }

        /**
         *
         * @param link
         * The link
         */
        public void setLink(String link) {
            this.link = link;
        }

        /**
         *
         * @return
         * The icon
         */
        public String getIcon() {
            return icon;
        }

        /**
         *
         * @param icon
         * The icon
         */
        public void setIcon(String icon) {
            this.icon = icon;
        }

        /**
         *
         * @return
         * The actions
         */
        public List<Action> getActions() {
            return actions;
        }

        /**
         *
         * @param actions
         * The actions
         */
        public void setActions(List<Action> actions) {
            this.actions = actions;
        }



        /**
         *
         * @return
         * The statusType
         */
        public String getStatusType() {
            return statusType;
        }

        /**
         *
         * @param statusType
         * The status_type
         */
        public void setStatusType(String statusType) {
            this.statusType = statusType;
        }

        /**
         *
         * @return
         * The createdTime
         */
        public String getCreatedTime() {
            return createdTime;
        }

        public Likes getLikes() {
            return likes;
        }



        /**
         *
         * @return
         * The comments
         */
        public Comments getComments() {
            return comments;
        }


    }
    public class Datum_ {


        private String id;

        private String name;


        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

    }
    public class Datum__ {

        private String id;
        private From_ from;
        private String message;
        private Boolean canRemove;
        private String createdTime;
        private Integer likeCount;
        private Boolean userLikes;


        /**
         *
         * @return
         * The from
         */
        public From_ getFrom() {
            return from;
        }


        /**
         *
         * @return
         * The message
         */
        public String getMessage() {
            return message;
        }


        /**
         *
         * @return
         * The createdTime
         */
        public String getCreatedTime() {
            return createdTime;
        }


        /**
         *
         * @return
         * The likeCount
         */
        public Integer getLikeCount() {
            return likeCount;
        }

    }
    public class From {

        private String id;
        private String name;


        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

    }
    public class From_ {

        private String id;
        private String name;


        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }


    }
    public class Likes {

        private List<Datum_> data = new ArrayList<Datum_>();

        /**
         *
         * @return
         * The data
         */
        public List<Datum_> getData() {
            return data;
        }


    }

}
