package io.intrepid.nostalgia;

import retrofit.http.GET;
import retrofit.http.Path;

public interface NytService {

    @GET("/svc/search/v2/articlesearch.json?api-key=b79d2983dd0cbf2ef9c649921710f267:6:72359923&begin_date={date}&sort=oldest&end_date={date}")
    void getNytArticle(@Path(value= "date", encode = false) String date);
}
