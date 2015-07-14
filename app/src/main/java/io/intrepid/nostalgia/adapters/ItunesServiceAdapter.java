package io.intrepid.nostalgia.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.intrepid.nostalgia.services.ItunesService;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class ItunesServiceAdapter {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String ITUNES_ENDPOINT = "https://itunes.apple.com";
    private static RestAdapter serviceAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(ITUNES_ENDPOINT)
            .setConverter(new GsonConverter(GSON))
            .build();

    private static ItunesService itunesService;

    public static ItunesService getItunesServiceInstance() {
        if (itunesService == null) {
            itunesService = serviceAdapter.create(ItunesService.class);
        }
        return itunesService;
    }
}
