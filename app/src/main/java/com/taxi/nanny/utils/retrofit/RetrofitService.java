package com.taxi.nanny.utils.retrofit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taxi.nanny.R;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import org.json.JSONObject;
import java.io.IOException;
import  java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.taxi.nanny.utils.Constant.TOKEN;


public class RetrofitService
{
    private Context mcontext;
    private String mUrl;
    private int mValue,mRequestCode;
    private HashMap<String,RequestBody> map;
    private RetrofitResponse mResponse;
    private Call<ResponseBody> mCall;
    private MultipartBody.Part mpart;
    private JSONObject mJsonObject;
    private ProgressDialog dialog;
    private ArrayList<MultipartBody.Part> mPartList;
    private Dialog pd;
    String apiCallTYpe="";

    //For Get Request
    public RetrofitService(Context mcontext, RetrofitResponse mResponse, String mUrl,
                           int mRequestCode, int mValue,String apiCallTYpe)
    {
        this.mcontext = mcontext;
        this.mResponse = mResponse;
        this.mUrl = mUrl;
        this.mRequestCode = mRequestCode;
        this.mValue = mValue;
        this.apiCallTYpe=apiCallTYpe;
    }

    //For Post Request
    public RetrofitService(Context mcontext, RetrofitResponse mResponse,
                           String mUrl, JSONObject postparam, int mRequestCode, int mValue,String apiCallTYpe)
    {
        this.mcontext = mcontext;
        this.mResponse = mResponse;
        this.mUrl = mUrl;
        this.mJsonObject = postparam;
        this.mRequestCode = mRequestCode;
        this.mValue = mValue;
        this.apiCallTYpe=apiCallTYpe;
      /*  dialog=new ProgressDialog(mcontext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading...");*/
    }

    //For Post Request
    public RetrofitService(Context mcontext, RetrofitResponse mResponse, String mUrl,
                           HashMap<String,RequestBody> postparam,
                           int mRequestCode, int mValue) {
        this.mcontext = mcontext;
        this.mResponse = mResponse;
        this.mUrl = mUrl;
        this.map = postparam;
        this.mRequestCode = mRequestCode;
        this.mValue = mValue;
        dialog=new ProgressDialog(mcontext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading...");
    }

    //For Multipart Request
    public RetrofitService(Context mcontext, RetrofitResponse mResponse,
                           String mUrl,HashMap<String, RequestBody> map, MultipartBody.Part mpart,
                           int mRequestCode, int mValue)
    {
        this.mcontext = mcontext;
        this.mResponse = mResponse;
        this.mUrl = mUrl;
        this.mpart = mpart;
        this.map = map;
        this.mRequestCode = mRequestCode;
        this.mValue = mValue;
    }

        //For multipart multiple files
    public RetrofitService(Context mcontext,RetrofitResponse response,String url,
                           int requestCode,int value,HashMap<String,RequestBody> map,
                           ArrayList<MultipartBody.Part> files)
    {
        this.mcontext = mcontext;
        mResponse = response;
        mUrl = url;
        mRequestCode = requestCode;
        mValue = value;
        this.map = map;
        mPartList = files;

    }

    public void callService(boolean ProgressDialog)
    {
        try {
            /*dialog = new ProgressDialog(mcontext);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Loading....");

            if (ProgressDialog) {
                dialog.show();
            }*/


            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

//            RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(4, TimeUnit.MINUTES)
                    .connectTimeout(4, TimeUnit.MINUTES);

            SharedPrefUtil prefUtil=SharedPrefUtil.getInstance();

            String token=prefUtil.getString(TOKEN,"");
            Log.e("Token  ",token);
            String value="Bearer "+ token;
            Log.e("value  ", value);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder().
                            addHeader("Authorization",value).build();
                    return chain.proceed(request);
                }
            });
            Retrofit retrofit;
            if (apiCallTYpe.equalsIgnoreCase("1"))
            {

                System.setProperty("http.keepAlive", "false");

                retrofit = new Retrofit.Builder()
                        .baseUrl(Constant.BASE_URL)
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            else {

                System.setProperty("http.keepAlive", "false");
                retrofit = new Retrofit.Builder()
                        .baseUrl(Constant.SOCKET_URL)
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }



            RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

            if (mValue == 1) {
                Log.e("Url<<<: ",mUrl );
                mCall = retrofitApi.callGet(mUrl);
            } else if (mValue == 2) {
                Log.e("Url<<<: ",mUrl );
                mCall = retrofitApi.callPost(mUrl, RequestBody.create(MediaType.parse("application/json"),
                        mJsonObject.toString()));
            }/* else if (mValue == 3) {
                Log.e("Url<<<: ",mUrl );
                mCall = retrofitApi.callMultipart(mUrl, map, mpart);
            } else if (mValue == 4) {
                Log.e("Url<<<: ",mUrl );
                mCall = retrofitApi.callMultipartList(mUrl, map, mPartList);
            }else if (mValue == 6) {
                Log.e("Url<<<: ",mUrl );
                mCall = retrofitApi.callPostNew(mUrl, map);
            }*/

            mCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String webRes = "";
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (response.isSuccessful()) {
                        try {
                            String res = response.body().string();
                            Log.e("ResponseRetrofitStatus  ",res+"");
                            mResponse.onResponse(mRequestCode, res);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
               /*  if (dialog.isShowing()) {
                        try {
                            dialog.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                  /*  if (dialog.isShowing()) {
                        dialog.cancel();
                    }*/
                    t.printStackTrace();

                    Log.e( "onResponse: ListOfChildrenREsponse ","Failue " );


                    alertOnTimeOut(mCall, this, mcontext.getString(R.string.connection_time_out));


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertOnTimeOut(final Call<ResponseBody> call, final Callback<ResponseBody> callback, String message)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mcontext);
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton(mcontext.getResources().getString(R.string.retry), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog1, int which)
            {
//                dialog .show();

                call.clone().enqueue(callback);
            }
        });

        alertDialog.setNegativeButton(mcontext.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener()
                {
            @Override
            public void onClick(DialogInterface dialog1, int which)
            {
                dialog1.dismiss();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
