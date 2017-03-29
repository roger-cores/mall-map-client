package com.jelliroo.mallmapbeta.endpoints;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by roger on 2/20/2017.
 */

public interface RouteEndPoint {

    @GET("route/{source}/to/{destination}")
    Call<List<String>> getShortestPath(@Path("source") String source, @Path("destination") String destination);

}
