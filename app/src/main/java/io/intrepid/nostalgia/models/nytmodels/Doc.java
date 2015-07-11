package io.intrepid.nostalgia.models.nytmodels;

import com.google.gson.annotations.SerializedName;

public class Doc {

    @SerializedName("web_url")
    private String webUrl;

    private String snippet;

    private Headline headline;

    private Byline byline;

    @SerializedName("pub_date")
    private String pubDate;

    public String getWebUrl() {
        return webUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public Headline getHeadline() {
        return headline;
    }

    public String getPubDate() {
        return pubDate;
    }

    public Byline getByline() {
        return byline;
    }
}
