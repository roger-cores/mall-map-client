package com.jelliroo.mallmapbeta.endpoints;

import com.jelliroo.mallmapbeta.bean.ClassCountResponse;
import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.bean.RequestObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by roger on 2/13/2017.
 */

public interface KNNEndPoint {

    @POST("knn/train")
    Call<Object> postTrainingData(@Body RequestObject requestObject);

    @POST("knn/classify/{k}")
    Call<ClassRecord> classifyData(@Body RequestObject requestObject, @Path("k") int k);

    @GET("knn/count")
    Call<List<ClassCountResponse>> getClassCount();

    @DELETE("knn")
    Call<Object> deleteClasses();

    @DELETE("knn/{classlabel}")
    Call<Object> deleteClasses(@Path("classlabel") String classLabel);

}
