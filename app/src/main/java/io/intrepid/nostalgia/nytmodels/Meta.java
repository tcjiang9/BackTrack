package io.intrepid.nostalgia.nytmodels;

import com.google.gson.annotations.Expose;

public class Meta {
    @Expose
    private Integer hits;
    @Expose
    private Integer time;
    @Expose
    private Integer offset;

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
