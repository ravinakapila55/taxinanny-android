package com.taxi.nanny.views.driver;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.Drawer;
import com.taxi.nanny.views.driver.documents.DriverDocuments;
import com.taxi.nanny.views.driver.rating.DriverRating;
import com.taxi.nanny.views.driver.vehicles.VehicleDetails;
import com.taxi.nanny.views.driver.waybill.WayBillDriver;
import com.taxi.nanny.views.login_section.WelcomeActivity;
import com.taxi.nanny.views.login_section.login.LoginActivity;
import com.taxi.nanny.views.profile.EditProfile;
import com.taxi.nanny.views.settings.SettingsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class DriverAccount extends BaseActivity implements RetrofitResponse
{
    String token = "";
    SharedPrefUtil sharedPrefUtil;

    @BindView(R.id.linearWAY)
    LinearLayout linearWAY;

    @BindView(R.id.linearDocuments)
    LinearLayout linearDocuments;

    @BindView(R.id.linearSettings)
    LinearLayout linearSettings;

    @BindView(R.id.linearRatings)
    LinearLayout linearRatings;

    @BindView(R.id.linaerABout)
    LinearLayout linaerABout;

    @BindView(R.id.linearHelp)
    LinearLayout linearHelp;

    @BindView(R.id.linearLogout)
    LinearLayout linearLogout;

    @BindView(R.id.ivUser)
    ImageView ivUser;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvEditProfile)
    TextView tvEditProfile;

    @BindView(R.id.tvEditCar)
    TextView tvEditCar;

    @BindView(R.id.ivCar)
    ImageView ivCar;


    @Override
    protected int getContentId()
    {
        return R.layout.driver_account;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPrefUtil=SharedPrefUtil.getInstance();

        Log.e("onCreate:Image ",sharedPrefUtil.getString(SharedPrefUtil.IMAGE,""));

        setTab();
    }


    private void callVehicleDetails()
    {
        new RetrofitService(this, this,
                Constant.API_DRIVER_VEHICLE_DETAIL , 500, 1,"1").
                callService(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        callVehicleDetails();
        Log.e("onResume: ",sharedPrefUtil.getString(SharedPrefUtil.IMAGE,""));
     /*   if (!sharedPrefUtil.getString(SharedPrefUtil.IMAGE,"").equalsIgnoreCase("0"))
        {
            Picasso.with(this).load(sharedPrefUtil.getString(SharedPrefUtil.IMAGE,"")).
                    placeholder(getResources().getDrawable(R.drawable.pic_dummy_user)).into(ivUser);
        }*/
        tvName.setText(sharedPrefUtil.getString(SharedPrefUtil.FNAME,"")+" "+
                sharedPrefUtil.getString(SharedPrefUtil.LNAME,""));
    }

    @OnClick({R.id.linearWAY,R.id.linearDocuments,R.id.linearSettings,R.id.linaerABout,
            R.id.linearRatings,R.id.linearHelp,R.id.linearLogout,R.id.tvEditProfile,R.id.ivUser,
            R.id.tvEditCar,R.id.ivCar})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.linearWAY:

                Intent intent34=new Intent(DriverAccount.this, WayBillDriver.class);
                startActivity(intent34);

                break;

                case R.id.linearDocuments:

                    Intent intent22=new Intent(DriverAccount.this, DriverDocuments.class);
                    startActivity(intent22);

                break;


                case R.id.linearSettings:

                    Intent  intent=new Intent(DriverAccount.this, SettingsActivity.class);
                    startActivity(intent);

                break;


                case R.id.linearRatings:

                    Intent  intent1=new Intent(DriverAccount.this, DriverRating.class);
                    startActivity(intent1);

                break;

                case R.id.linaerABout:

                break;

                case R.id.linearHelp:

                break;

                case R.id.linearLogout:
                    callAlert();

                break;

                case R.id.tvEditProfile:
                    Log.e("EDITCLICK ","");
                    Intent  intent2=new Intent(this, EditProfile.class);
                    startActivity(intent2);
                break;
                 case R.id.ivUser:
                    Log.e("EDITCLICK ","");
                    Intent  intent3=new Intent(this, EditProfile.class);
                    startActivity(intent3);
                break;


                case R.id.tvEditCar:
                    Intent  intent4=new Intent(this, VehicleDetails.class);
                    startActivity(intent4);
                break;

                case R.id.ivCar:
                    Intent  intent5=new Intent(this, VehicleDetails.class);
                    startActivity(intent5);
                break;


        }
    }

    public void callAlert()
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

        tvMsg.setText("Are you sure");
        yes.setText("Logout");

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                callLogout();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void callLogout()
    {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("device_token", SharedPrefUtil.getInstance().getString(SharedPrefUtil.DEVICE_FCM_TOKEN,""));

            Log.e("LogoutParams",jsonObject.toString());
            new RetrofitService(this, this,
                    Constant.API_LOGOUT,jsonObject, 105, 2,"1").
                    callService(true);

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:
                Log.e( "onResponse: Logout " , response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        SharedPrefUtil.getInstance().onLogout();
                        Intent intent = new Intent(DriverAccount.this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case 500:
                Log.e("DriverVehicleResponse ",response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        JSONObject vehicle_detail=jsonObject.getJSONObject("vehicle_detail");


                        if (!vehicle_detail.getString("image").equalsIgnoreCase(""))
                        {
                            Picasso.with(this).load(vehicle_detail.getString("image")).
                                    placeholder(getResources().getDrawable(R.drawable.car_dummy)).into(ivCar);
                        }

                    }
                    else
                    {

                    }
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;

        }
    }


}
