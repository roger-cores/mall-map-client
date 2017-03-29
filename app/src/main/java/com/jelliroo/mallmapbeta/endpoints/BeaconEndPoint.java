package com.jelliroo.mallmapbeta.endpoints;

import com.jelliroo.mallmapbeta.bean.Beacon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by roger on 2/14/2017.
 */

public interface BeaconEndPoint {

    @POST("beacon")
    Call<Beacon> createBeacon(@Body Beacon beacon);

    @GET("beacon")
    Call<List<Beacon>> getAllBeacons();

    @DELETE("beacon/{name}")
    Call<Object> deleteBeacon(@Path("name") String name);

}
