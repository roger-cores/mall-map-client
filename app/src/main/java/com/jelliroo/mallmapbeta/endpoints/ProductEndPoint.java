package com.jelliroo.mallmapbeta.endpoints;

import com.jelliroo.mallmapbeta.bean.Link;
import com.jelliroo.mallmapbeta.bean.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by roger on 2/21/2017.
 */

public interface ProductEndPoint {

    @POST("product")
    Call<Product> createLink(@Body Link link);

    @GET("product")
    Call<List<Product>> getAllLinks();

    @GET("product/{classlabel}")
    Call<List<Product>> getAllLinksForClassLabel(@Path("classlabel") String classLabel);

    @DELETE("product/{id}")
    Call<Object> deleteLinks(@Path("id") Integer id);

}
