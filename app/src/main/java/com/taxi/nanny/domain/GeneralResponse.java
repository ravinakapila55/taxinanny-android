package com.taxi.nanny.domain;

import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.taxi.nanny.views.login_section.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class GeneralResponse {

    public static final String TAG = GeneralResponse.class.getSimpleName();
    private JSONObject response;
    ResponseBody responseBody;
    JSONArray response_array;
    JSONObject response_object;

    public GeneralResponse(Response<ResponseBody> rb) {
        this.responseBody = (rb.body() != null) ? rb.body() : rb.errorBody();

        //  Log.e("Data",responseBody.toString());
        try {
            this.response = new JSONObject(responseBody.string());
        } catch (Exception je) {
            je.printStackTrace();
        }
    }

    public boolean getResponseObject(Activity activity) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());
        Log.e("VVVVV",response+"");
        if (response.getString("status").equalsIgnoreCase("success")|| response.getString("status").equalsIgnoreCase("true")) {
            return true;
        } else {

            return false;
        }

    }
    public JSONObject getResponse_object() throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());
        return  response;
    }

    public GeneralResponse(JSONArray response) {
        try {
            this.response_array = response;
        } catch (Exception je) {
            je.printStackTrace();
        }
    }



    public GeneralResponse(JSONObject response) {
        try {
            this.response_object = response;
        } catch (Exception je) {
            je.printStackTrace();
        }
    }

    JsonObject jsonObject;

    public GeneralResponse(JsonObject body) {
        this.jsonObject = body;
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonObject_ = new JSONObject(jsonObject.toString());
        return jsonObject_;
    }

    public boolean checkResponse(CoordinatorLayout coordinatorLayout) throws JSONException {
        JSONObject respnseCheck = new JSONObject(jsonObject.toString());
        if (respnseCheck.getString("ResponseCode").equals("1")) {
            return true;
        } else {
            return false;
        }
    }


    public JSONObject getResponse() {
        return response;
    }

    public <T> List<T> getDataAsList(String key, Class<T> classOFT) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());
        JSONArray ja = response.getJSONArray(key);
        int len = ja.length();
        Gson gson = new Gson();
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < ja.length(); i++) {
            list.add(gson.fromJson(ja.getString(i), classOFT));
        }
        return list;
    }

    public JSONArray getResponse_array(String key) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());
        JSONArray ja = response.getJSONArray(key);
        return ja;
    }

    public <T> List<T> getJSONArrayAsList(Class<T> classOFT) throws JSONException {
        JSONArray ja = response_array;
        int len = ja.length();
        Gson gson = new Gson();
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < ja.length(); i++) {
            list.add(gson.fromJson(ja.getString(i), classOFT));
        }
        return list;
    }

    public <T> T getJSONObjectAs(Class<T> classOFT) throws JSONException {
        JSONObject ja = response_object;
        Gson gson = new Gson();
        return gson.fromJson(ja.toString(), (Type) classOFT);

    }

    public <T> T getData(String key, Class<T> dataOFSET) throws JSONException {

        if (response == null) response = new JSONObject(responseBody.toString());


        Gson gson = new Gson();

        return gson.fromJson(response.getString(key), (Type) dataOFSET);
    }


    public JSONObject getResponseObject(String s) throws JSONException {
        if (response == null) response = new JSONObject(responseBody.toString());
        return response.getJSONObject(s);
    }

    public <T> List<T> getJSonArray(JSONArray ja, Class<T> dataOFSET) throws JSONException {
        Gson gson = new Gson();
        int len = ja.length();
        List<T> list = new ArrayList<>(len);
        for (int i = 0; i < ja.length(); i++) {
            list.add(gson.fromJson(ja.getString(i), dataOFSET));
        }
        return list;
    }
}
