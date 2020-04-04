package com.s.youtubeapi.Interface;

import com.s.youtubeapi.Model.ResultadoVideos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YouTubeService {

    /*
    https://www.googleapis.com/youtube/v3/
    search
    ?part=snippet
    &order=date
    &maxResults=20
    &key=AIzaSyD6i2Kjfj2hOjBqMJSEkj_CqoRAtSfINrk
    &channeId=UC5wM6nmwErq3D_3k72Q5nFg
    &q=desenvolvimento+android
     * */

    @GET("search")
    Call<ResultadoVideos> recuperarVideos(@Query("part") String part,
                                          @Query("order") String order,
                                          @Query("maxResult") String maxResult,
                                          @Query("key") String key,
                                          @Query("channelId") String channelId,
                                          @Query("q") String q


    );
}
