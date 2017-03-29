package com.jelliroo.mallmapbeta.endpoints;

import com.jelliroo.mallmapbeta.bean.ClassRecord;

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

public interface ClassEndPoint {

    @POST("class")
    Call<ClassRecord> createClassRecord(@Body ClassRecord classRecord);

    @GET("class")
    Call<List<ClassRecord>> getAllClassRecords();

    @GET("class/{classType}")
    Call<List<ClassRecord>> getAllClassRecordsByType(@Path("classType") String classType);

    @DELETE("class/{label}")
    Call<Object> deleteClassRecord(@Path("label") String label);

}
