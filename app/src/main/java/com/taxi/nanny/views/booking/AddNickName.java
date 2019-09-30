package com.taxi.nanny.views.booking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNickName extends BaseActivity implements Callback<ResponseBody> {


    @BindView(R.id.tvSave)
    TextView tvSave;

    @BindView(R.id.etLoc)
    EditText etLoc;

    @BindView(R.id.etNickName)
    EditText etNickName;

    @BindView(R.id.etNote)
    EditText etNote;



    String key="";
    String key_final_pick="";
    int position;
    ArrayList<RiderListModel> riderList;
    public static String TAG=EnterPickUp.class.getSimpleName();

    @Override
    protected int getContentId()
    {
        return R.layout.add_nick_name;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Enter Nick Name/Note");

        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");

        if (getIntent().hasExtra("key"))
        {
            key=getIntent().getExtras().getString("key");
            key_final_pick=getIntent().getExtras().getString("key_final");
            position=getIntent().getExtras().getInt("position");
            if (key.equalsIgnoreCase("start_single"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e(TAG, "onCreate: RiderListSize "+riderList.size());

                Log.e("PickUpValue<<",riderList.get(position).getPickup());
                Log.e("PickUpValue1<<",riderList.get(0).getPickup());
                etLoc.setText(riderList.get(position).getPickup());
            }
            if (key.equalsIgnoreCase("start_multiple"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e(TAG, "onCreate: RiderListSize "+riderList.size());
                etLoc.setText(riderList.get(position).getPickup());
            }
            if (key.equalsIgnoreCase("start_multiple_different"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e(TAG, "onCreate: RiderListSize "+riderList.size());
                etLoc.setText(riderList.get(position).getPickup());
            }
        }
    }

    @OnClick({R.id.tvSave,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvSave:

                Constant.hideKeyboard(AddNickName.this,view);


                if (checkValidation())
                {
                    callService();
                }


/*
                if (key.equalsIgnoreCase("start_single"))
                {
                    if (key_final_pick.equalsIgnoreCase("yes"))
                    {
                        //key_final_pick="yes";
                        Intent intent=new Intent(AddNickName.this,PickDropConfirmation.class);
                        intent.putExtra("key",key);
                        intent.putExtra("key_final",key_final_pick);
                        intent.putExtra("position",position);
                        intent.putExtra("rider_list",(Serializable) riderList);
                        startActivity(intent);
                    }
                    else
                    {
                        //key_final_pick="no";
                        Intent intent=new Intent(AddNickName.this,EnterDropLocation.class);
                        intent.putExtra("key",key);
                        intent.putExtra("key_final",key_final_pick);
                        intent.putExtra("rider_list",(Serializable) riderList);
                        intent.putExtra("position",position);
                        startActivity(intent);
                    }

                }
                else if (key.equalsIgnoreCase("start_multiple"))
                {
                    if (key_final_pick.equalsIgnoreCase("yes"))
                    {
                        Intent intent=new Intent(AddNickName.this,PickDropConfirmation.class);
                        intent.putExtra("key",key);
                        intent.putExtra("key_final",key_final_pick);
                        intent.putExtra("rider_list",(Serializable) riderList);
                        intent.putExtra("position",position);
                        startActivity(intent);
                    }
                    else {
                        callAlert();
                    }

                } else if (key.equalsIgnoreCase("start_multiple_different"))
                {
                    callAlert();
                }*/


                break;

            case R.id.img_back_btn:
                finish();
                break;
        }
    }

    String token = "";
    SharedPrefUtil sharedPrefUtil;

    public void callService()
    {

        HashMap<String, String> param = new HashMap<>();
        param.put("rider_id",sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
        param.put("location_name",etLoc.getText().toString().trim());
        param.put("nick_name",etNickName.getText().toString().trim());
        param.put("description",etNote.getText().toString().trim());
        param.put("lattitude",riderList.get(position).getPickLat());
        param.put("longitude",riderList.get(position).getPicklng());
        Log.e("AddPickLocationParama ",param.toString());
        api(param,this,token,10);
    }


    public boolean checkValidation()
    {
        if (etNickName.getText().toString().trim().length() == 0)
        {
            etNickName.setError("Please enter nick name");
            etNickName.requestFocus();
            return false;
        }

        return true;
    }

    public void callAlert(String location,String latt,String longitude,String id)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvMsg.setText("Will all rider's drop off location will be same?");

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Constant.hideKeyboard(AddNickName.this,view);
                alertDialog.dismiss();

                if (key_final_pick.equalsIgnoreCase("no"))
                {

                    for (int i = 0; i < riderList.size(); i++)
                    {
                        riderList.get(i).setPickup(location);
                        riderList.get(i).setPickLat(latt);
                        riderList.get(i).setPicklng(longitude);
                        riderList.get(i).setPick_saved_id(id);
                    }
                }else {
                    riderList.get(position).setPickup(location);
                    riderList.get(position).setPickLat(latt);
                    riderList.get(position).setPicklng(longitude);
                    riderList.get(position).setPick_saved_id(id);
                }

                Intent intent=new Intent(AddNickName.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("position",position);
                startActivity(intent);
                alertDialog.dismiss();

            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Constant.hideKeyboard(AddNickName.this,view);


                if (key_final_pick.equalsIgnoreCase("no"))
                {

                    for (int i = 0; i < riderList.size(); i++)
                    {
                        riderList.get(i).setPickup(location);
                        riderList.get(i).setPickLat(latt);
                        riderList.get(i).setPicklng(longitude);
                        riderList.get(i).setPick_saved_id(id);
                    }
                }else {
                    riderList.get(position).setPickup(location);
                    riderList.get(position).setPickLat(latt);
                    riderList.get(position).setPicklng(longitude);
                    riderList.get(position).setPick_saved_id(id);
                }

                Intent intent=new Intent(AddNickName.this,EnterDropLocation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("position",position);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        Log.e(TAG, "onResponse:AddRiderResponse "+response );
        progressDialog.dismiss();

        GeneralResponse generalResponse=new GeneralResponse(response);

        try {
            JSONObject jsonObject=generalResponse.getResponse_object();

            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
//                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                JSONObject data=jsonObject.getJSONObject("data");

                riderList.get(position).setPick_saved_id(data.getString("id"));
                riderList.get(position).setPickup(data.getString("location_name"));
                riderList.get(position).setPickLat(data.getString("lattitude"));
                riderList.get(position).setPicklng(data.getString("longitude"));

                replaceActivity(data.getString("location_name"),data.getString("lattitude"),data.getString("longitude"),
                        data.getString("id"));


            }
            else {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                replaceActivity();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void replaceActivity(String location,String lattitude,String longitude,String id)
    {
        if (key.equalsIgnoreCase("start_single"))
        {
            riderList.get(position).setPickup(location);
            riderList.get(position).setPickLat(lattitude);
            riderList.get(position).setPicklng(longitude);
            riderList.get(position).setPick_saved_id(id);


             if (key_final_pick.equalsIgnoreCase("yes"))
            {
                Intent intent=new Intent(AddNickName.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("position",position);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
            }
            else
            {
                Intent intent=new Intent(AddNickName.this,EnterDropLocation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("position",position);
                startActivity(intent);
            }

        }

        //when first time coming from Lis of children in case of multiple riders by clicking yes option
        else if (key.equalsIgnoreCase("start_multiple"))
        {
            Log.e(TAG, "replaceActivity: start_multiple "+key_final_pick );

            if (key_final_pick.equalsIgnoreCase("yes"))
            {
                riderList.get(position).setPickup(location);
                riderList.get(position).setPickLat(lattitude);
                riderList.get(position).setPicklng(longitude);
                riderList.get(position).setPick_saved_id(id);

                Intent intent=new Intent(AddNickName.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("position",position);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
            }
            else
            {
                callAlert(location,lattitude,longitude,id);
            }
        }

        else if (key.equalsIgnoreCase("start_multiple_different"))
        {

            Log.e(TAG, "replaceActivity: start_multiple_different "+key);

            for (int i = 0; i <riderList.size() ; i++) {

                if (riderList.get(i).getDropup().equalsIgnoreCase(""))
                {
                    Log.e(TAG, "replaceActivity: start_multiple_different "+"IF");
                    callAlert(location,lattitude,longitude,id);
                }
                else {
                    Log.e(TAG, "replaceActivity: start_multiple_different "+"else");

                    riderList.get(position).setPickup(location);
                    riderList.get(position).setPickLat(lattitude);
                    riderList.get(position).setPicklng(longitude);
                    riderList.get(position).setPick_saved_id(id);

                    Intent intent=new Intent(AddNickName.this,PickDropConfirmation.class);
                    intent.putExtra("key",key);
                    intent.putExtra("key_final",key_final_pick);
                    intent.putExtra("rider_list",(Serializable) riderList);
                    intent.putExtra("position",position);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

    }
}
