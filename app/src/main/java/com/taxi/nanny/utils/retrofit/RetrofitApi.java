package com.taxi.nanny.utils.retrofit;

import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface RetrofitApi {

    @GET
    Call<ResponseBody> callGet(@Url String url);

    @POST
    Call<ResponseBody> callPost(@Url String url, @Body RequestBody body);

/*    @POST
    Call<ResponseBody> callPost(@Url String url, @PartMap HashMap<String, RequestBody> map);*/

    @Multipart
    @POST
    Call<ResponseBody> callPostNew(@Url String url, @PartMap HashMap<String, RequestBody> map);

    @Multipart
    @POST
    Call<ResponseBody> callMultipart(@Url String url, @PartMap HashMap<String, RequestBody> map, @Part MultipartBody.Part part);

    @POST
    @Multipart
    Call<ResponseBody> callMultipartList(@Url String url, @PartMap HashMap<String, RequestBody> map, @Part ArrayList<MultipartBody.Part> parts);

}


