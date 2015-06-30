package io.intrepid.nostalgia.nytmodels;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Response {
    @Expose
    private Meta meta;
    @Expose
    private List<Doc> docs = new ArrayList<Doc>();

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Doc> getDocs() {
        return docs;
    }

    public void setDocs(List<Doc> docs) {
        this.docs = docs;
    }
}
