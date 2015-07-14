package io.intrepid.nostalgia.models.itunesmodels;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class ItunesResults {

    @Expose
    private Integer resultCount;
    @Expose
    private List<ItunesSong> results = new ArrayList<ItunesSong>();

    /**
     *
     * @return
     * The resultCount
     */
    public Integer getResultCount() {
        return resultCount;
    }

    /**
     *
     * @param resultCount
     * The resultCount
     */
    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    /**
     *
     * @return
     * The results
     */
    public List<ItunesSong> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<ItunesSong> results) {
        this.results = results;
    }

}