package io.intrepid.nostalgia.models.itunesmodels;

public class ItunesSong {

    private String artistName;

    private String collectionName;

    private String trackName;

    private String collectionCensoredName;

    private String artistViewUrl;

    private String collectionViewUrl;

    private String trackViewUrl;

    private String previewUrl;

    private String artworkUrl60;

    private String artworkUrl100;

    private Integer trackTimeMillis;


    public String getArtistName() {
        return artistName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getCollectionCensoredName() {
        return collectionCensoredName;
    }

    public void setCollectionCensoredName(String collectionCensoredName) {
        this.collectionCensoredName = collectionCensoredName;
    }

    public String getArtistViewUrl() {
        return artistViewUrl;
    }

    public String getCollectionViewUrl() {
        return collectionViewUrl;
    }

    public String getTrackViewUrl() {
        return trackViewUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getArtworkUrl60() {
        return artworkUrl60;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public Integer getTrackTimeMillis() {
        return trackTimeMillis;
    }
}