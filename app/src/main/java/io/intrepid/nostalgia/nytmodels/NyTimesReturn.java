package io.intrepid.nostalgia.nytmodels;

import com.google.gson.annotations.Expose;

public class NyTimesReturn {

    @Expose
    private Response response;
    @Expose
    private String status;
    @Expose
    private String copyright;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

}
