package com.jelliroo.mallmapbeta.endpoints;

import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.bean.Link;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by roger on 2/19/2017.
 */

public interface LinkEndPoint {

    @POST("link")
    Call<Link> createLink(@Body Link link);

    @GET("link")
    Call<List<Link>> getAllLinks();

    @DELETE("link/{id}")
    Call<Object> deleteLinks(@Path("id") Integer id);

}
