package com.taxi.nanny.domain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import com.taxi.nanny.utils.Constant;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIRequest {
    public static ApiInterface apiInterface(final String authToken){
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(Constant.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Authorization","Bearer "+ authToken)
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Log.e("Message", message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = httpClient
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS).addInterceptor(interceptor)
                .build();
       Retrofit retrofit = builder.client(client).build();
        return retrofit.create(ApiInterface.class);
    }

    public static ProgressDialog getProgress(Activity activity)
    {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Waiting...");
             progressDialog.setCancelable(false);
            progressDialog.show();
        return progressDialog;
    }

}
