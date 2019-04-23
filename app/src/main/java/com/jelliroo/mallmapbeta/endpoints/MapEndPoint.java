package com.jelliroo.mallmapbeta.endpoints;

import android.database.Observable;

import com.jelliroo.mallmapbeta.bean.Map;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MapEndPoint {

    @GET("map_image")
    Call<List<Map>> getAllMaps();

    @DELETE("map_image/{label}")
    Call<Object> deleteMap(@Path("label") String label);

    @Multipart
    @POST("map_image/post")
    Call<Map> createMapImage(@Part("label") RequestBody label, @Part MultipartBody.Part image);

}
