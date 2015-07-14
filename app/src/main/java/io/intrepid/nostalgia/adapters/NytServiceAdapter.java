package io.intrepid.nostalgia.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.intrepid.nostalgia.services.NytService;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class NytServiceAdapter {

    private static final Gson GSON = new GsonBuilder().create();
    private static final String NYT_ENDPOINT = "http://api.nytimes.com";
    private static RestAdapter serviceAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(NYT_ENDPOINT)
            .setConverter(new GsonConverter(GSON))
            .build();
    private static NytService nytServiceInstance;

    public static NytService getNytServiceInstance() {
        if (nytServiceInstance == null) {
            nytServiceInstance = serviceAdapter.create(NytService.class);
        }
        return nytServiceInstance;
    }

}
