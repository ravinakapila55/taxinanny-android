package com.taxi.nanny.views.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.driver.DriverHome;
import com.taxi.nanny.views.home.ParentHome;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends BaseActivity implements Callback<ResponseBody> {

    @BindViews({R.id.tvChange,R.id.tvEmergency})
    List<TextView> txtList;

    SharedPrefUtil sharedPrefUtil;

    @BindView(R.id.viwww1)
    View viwww1;

    @BindView(R.id.swNoti)
    Switch swNoti;

    @BindView(R.id.swSms)
    Switch swSms;

    String sms="",noti="";


    String token;

    String apiCall="0";


    @Override
    protected int getContentId()
    {
        return R.layout.settings;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Settings");
        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");

        if (sharedPrefUtil.getString(SharedPrefUtil.USERTYPE).equalsIgnoreCase("driver"))
        {
            txtList.get(1).setVisibility(View.GONE);
            viwww1.setVisibility(View.GONE);
        }
        else
        {
            txtList.get(1).setVisibility(View.VISIBLE);
            viwww1.setVisibility(View.VISIBLE);
        }

        if (sharedPrefUtil.getString(SharedPrefUtil.NOTIFICATION_SETTING).equalsIgnoreCase("1"))
        {
           swNoti.setChecked(true);
           noti="1";
        }
        else {
            swNoti.setChecked(false);
            noti="0";
        }

        if (sharedPrefUtil.getString(SharedPrefUtil.SMS_SETTINGS).equalsIgnoreCase("1"))
        {
           swSms.setChecked(true);
           sms="1";
        }
        else {
            swSms.setChecked(false);
            sms="0";
        }



        swNoti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    noti="1";
                    apiCall="1";
                    callNotiSettings();
                }
                else {
                    noti="0";
                    apiCall="1";
                    callNotiSettings();
                }

            }
        });

        swSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    sms="1";
                    apiCall="2";
                    callSmsSettings();
                }
                else
                {
                    sms="0";
                    apiCall="2";
                    callSmsSettings();
                }
            }
        });
    }

    @OnClick({R.id.tvEmergency,R.id.tvChange,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvChange:

                Intent  intent=new Intent(this,ChangePassword.class);
                startActivity(intent);
                break;

            case R.id.img_back_btn:
                finish();
                break;

            case R.id.tvEmergency:

                Intent  intent1=new Intent(this,EmergencyContacts.class);
                startActivity(intent1);

                break;

        }
    }

    private void callNotiSettings()
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("status", noti);
        Log.e("callNotification ",param.toString() );
        api(param,this,token,33);
    }

    private void callSmsSettings()
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("status", sms);
        param.put("user_type",sharedPrefUtil.getString(SharedPrefUtil.USERTYPE,""));
        Log.e("callNotification ",param.toString() );
        api(param,this,token,34);
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e("GeneralBooking<< ",generalResponse+"");

        try {

            if (apiCall.equalsIgnoreCase("1"))
            {
                JSONObject jsonObject = generalResponse.getResponse_object();

                Log.e("JsonNoti ",jsonObject+"");

                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {

                    if (jsonObject.getString("is_enable_push_notifications").equalsIgnoreCase("1"))
                    {
                        Toast.makeText(this,"Push Notifications are ON now." , Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(this,"Push Notifications are OFF now." , Toast.LENGTH_SHORT).show();

                    }
                    sharedPrefUtil.saveString(SharedPrefUtil.NOTIFICATION_SETTING,jsonObject.getString("is_enable_push_notifications"));
                }
                else
                {
                    Toast.makeText(this,"Not Successful ,Please try again" , Toast.LENGTH_SHORT).show();
                }
            }
            else if (apiCall.equalsIgnoreCase("2"))
            {
                JSONObject jsonObject = generalResponse.getResponse_object();

                Log.e("JsonSms ",jsonObject+"");

                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {
                    if (jsonObject.getString("is_enable_text_message_alert").equalsIgnoreCase("1"))
                    {
                        Toast.makeText(this,"Text Message Alerts are ON now." , Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(this,"Text Message Alerts are OFF now." , Toast.LENGTH_SHORT).show();

                    }

                    sharedPrefUtil.saveString(SharedPrefUtil.SMS_SETTINGS,jsonObject.getString("is_enable_text_message_alert"));
                }
                else
                {
                    Toast.makeText(this,"Not Successful ,Please try again" , Toast.LENGTH_SHORT).show();

                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

    }


}
