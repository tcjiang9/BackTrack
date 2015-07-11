package io.intrepid.nostalgia;

import io.intrepid.nostalgia.models.itunesmodels.ItunesResults;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.Callback;
import retrofit.http.Query;


//https://itunes.apple.com/search?term=NeverGonnaGiveYouUp&country=US&entity=song&term=RickAstley
// gives me the results I wan't
public interface ItunesService {
    @GET("/search")
    void listSongInfo(@Query("term") String term, @Query("country") String country, @Query("entity") String entity, Callback<ItunesResults> stringCallback);
}
