package io.intrepid.nostalgia.services;

import io.intrepid.nostalgia.models.itunesmodels.ItunesResults;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.Callback;
import retrofit.http.Query;


//https://itunes.apple.com/search?term=Never+Gonna+Give+You+Up+Rick+Astley&country=US&entity=song&limit=x
// gives me the results I wan't
public interface ItunesService {
    @GET("/search")
    void listSongInfo(@Query("term") String term,
                      @Query("country") String country,
                      @Query("entity") String entity,
                      @Query("limit") String limit,
                      Callback<ItunesResults> stringCallback);
}
