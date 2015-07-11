package io.intrepid.nostalgia.models.nytmodels;

import java.util.ArrayList;
import java.util.List;

public class Response {
    
    private List<Doc> docs = new ArrayList<Doc>();

    public List<Doc> getDocs() {
        return docs;
    }
}
