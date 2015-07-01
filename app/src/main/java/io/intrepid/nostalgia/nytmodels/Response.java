package io.intrepid.nostalgia.nytmodels;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Response {
    
    private List<Doc> docs = new ArrayList<Doc>();

    public List<Doc> getDocs() {
        return docs;
    }
}
