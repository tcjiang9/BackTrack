package io.intrepid.nostalgia.services;

import io.intrepid.nostalgia.models.nytmodels.NyTimesReturn;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface NytService {

    @GET("/svc/search/v2/articlesearch.json?api-key=b79d2983dd0cbf2ef9c649921710f267:6:72359923&sort=oldest&fq=news_desk('Foreign')")
    void getNytArticle(@Query("begin_date") String begin, @Query("end_date") String end, Callback<NyTimesReturn> callback);
}
