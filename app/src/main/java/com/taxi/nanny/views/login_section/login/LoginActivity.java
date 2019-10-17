package com.taxi.nanny.views.login_section.login;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.location.permission.Permission;
import com.taxi.nanny.utils.location.permission.PermissionGranted;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.SplashActivity;
import com.taxi.nanny.views.booking.EnterPickUp;
import com.taxi.nanny.views.booking.PickDropConfirmation;
import com.taxi.nanny.views.driver.DriverHome;
import com.taxi.nanny.views.home.ParentHome;
import com.taxi.nanny.views.home.adapter.ListofChildrenAdapter;
import com.taxi.nanny.views.login_section.register.AddVehicleDetails;
import com.taxi.nanny.views.login_section.register.RegisterUserTypeActivity;
import com.taxi.nanny.views.login_section.register.SelectVehicleTypeActivity;
import com.taxi.nanny.views.login_section.register.UploadDocumentActivity;
import com.taxi.nanny.views.login_section.register.parent.ListofChildren;
import com.taxi.nanny.views.login_section.register.parent.SkipAddRider;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements Callback<ResponseBody>, PermissionGranted
{
    @BindViews({R.id.edt_user_id,R.id.edtPassword})
    List<TextView>txtEdtValues;

    @BindView(R.id.btn_sign_up)
    TextView btn_sign_up;

    SharedPrefUtil helper;

    @BindView(R.id.rgType)
    RadioGroup rgType;

    @BindView(R.id.rbParent)
    RadioButton rbParent;

    @BindView(R.id.rbDriver)
    RadioButton rbDriver;

    String userType="";

    @Override
    protected int getContentId()
    {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        helper = SharedPrefUtil.getInstance();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            Log.e( "onCreate: Grant ","Fine_not" );
        }
        else
        {
            Log.e( "onCreate: Grant ","Fine_yes" );
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            Log.e( "onCreate: Grant ","Corase_not" );
        }
        else
        {
            Log.e( "onCreate: Grant ","Coarse_yes" );
        }

        Permission.checkPermissionLocation(this, this);

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rbParent:

                        userType="parent";

                        break;

                    case R.id.rbDriver:

                        userType="driver";
                        break;
                }
            }
        });
    }

    @OnClick({R.id.img_back_btn,R.id.txt_forgot_pass,R.id.btn_login,R.id.lin_REG,R.id.btn_sign_up})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                {  // Back  BTN
                finish();
                break;
            }
            case R.id.txt_forgot_pass:
            {
                Intent it = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(it);
                break;
            }
            case R.id.btn_login:

               /* Intent intent=new Intent(this, SkipAddRider.class);
                startActivity(intent);
                finishAffinity();*/
               Constant.hideKeyboard(this,view);
                {
                if (txtEdtValues.get(0).getText().toString().trim().length() == 0) {
                    txtEdtValues.get(0).setError(getResources().getString(R.string.val_valid_email_));
                    txtEdtValues.get(0).requestFocus();
                }
               else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEdtValues.get(0).getText().toString().trim()).matches())
               {
                    txtEdtValues.get(0).setError(getResources().getString(R.string.val_valid_email_));
                    txtEdtValues.get(0).requestFocus();
                }
               else if (txtEdtValues.get(1).getText().toString().trim().length() == 0)
                {
                    Toast.makeText(this, getResources().getString(R.string.val_pwd), Toast.LENGTH_SHORT).show();
//                    txtEdtValues.get(1).setError(getResources().getString(R.string.val_pwd));
//                    txtEdtValues.get(1).requestFocus();
                }

               else if (userType.equalsIgnoreCase(""))
                {
                    Toast.makeText(this, "Please choose user type", Toast.LENGTH_SHORT).show();
                }
               else {
                    String device_unique_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    Log.e( "DeviceUniqueId ",device_unique_id);
                    String deviceToken="";
                    deviceToken= FirebaseInstanceId.getInstance().getToken();
                  //  Log.e( "deviceToken ",deviceToken);
                    HashMap<String, String> param = new HashMap<>();
                    param.put("email", txtEdtValues.get(0).getText().toString());
                    param.put("password", txtEdtValues.get(1).getText().toString());
                    param.put("user_type", userType);
                  //  param.put("device_token", "eDigi-_FG0A:APA91bFVMWJF75brRhQF0rwr_a6niyE757tJOYkb2w_6TtKjTFmfMvXFbvMxDnEM0GzMSeV-9otMJOi2ejincQ0sRit8wrq1VURtEAzBdV9niWmB_NXV_2ROiuOwPGdBqwgD6rUcF_Un");

                    if (!deviceToken.equalsIgnoreCase(""))
                    {
                        param.put("device_token", deviceToken);
                        helper.saveString(SharedPrefUtil.DEVICE_FCM_TOKEN,deviceToken);
                    }
                    param.put("device_id", device_unique_id);
                    Log.e("LoginParams",param.toString());
                    api(param,this,"",2);
                }
                break;
            }
            case R.id.lin_REG:
            {
                Intent intent1=new Intent(LoginActivity.this,RegisterUserTypeActivity.class);
                startActivity(intent1);
                break;
            }
            case R.id.btn_sign_up:
            {
                Intent intent1=new Intent(LoginActivity.this,RegisterUserTypeActivity.class);
                startActivity(intent1);
                break;
            }
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        Log.e("LoginResponse ",response+"");
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        try
        {
            JSONObject jsonObject=generalResponse.getResponse_object();
            Log.e("LoginjsonObject ",jsonObject+"");
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                JSONObject data=jsonObject.getJSONObject("data");

                JSONObject user_details=data.getJSONObject("user_details");

            /*    Object user_details_parent=user_details.get("user_details_parent");
                Object user_details_driver=user_details.get("user_details_driver");*/

            if (userType.equalsIgnoreCase("parent"))
            {
                JSONObject user_details_parentObj=user_details.getJSONObject("user_details_parent");
                {
                    helper.saveString(Constant.TOKEN,data.getString("token"));
                    helper.saveBoolean(SharedPrefUtil.LOGIN, true);
                    helper.saveString(SharedPrefUtil.USERTYPE,user_details_parentObj.getString("user_type"));
                    helper.saveString(SharedPrefUtil.USER_ID,user_details_parentObj.getString("id"));
                    helper.saveString(SharedPrefUtil.FNAME,user_details_parentObj.getString("first_name"));
                    helper.saveString(SharedPrefUtil.LNAME,user_details_parentObj.getString("last_name"));
                    helper.saveString(SharedPrefUtil.PHONE_NUMBER,user_details_parentObj.getString("phone_no"));
                    helper.saveString(SharedPrefUtil.EMAIL,user_details_parentObj.getString("email"));
                    Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();

                    if (!user_details_parentObj.getString("image").equalsIgnoreCase("null") ||
                            !user_details_parentObj.getString("image").equalsIgnoreCase("") )
                    {
                        helper.saveString(SharedPrefUtil.IMAGE,user_details_parentObj.getString("image"));
                    }
                    else
                    {
                        helper.saveString(SharedPrefUtil.IMAGE,"");
                    }

                    helper.saveString(SharedPrefUtil.HOME_ADDRESS,user_details_parentObj.getString("address"));
                    helper.saveString(SharedPrefUtil.NOTIFICATION_SETTING,user_details_parentObj.getString("is_enable_push_notifications"));
                    helper.saveString(SharedPrefUtil.SMS_SETTINGS,user_details_parentObj.getString("is_enable_text_message_alert"));
                    helper.saveString(SharedPrefUtil.CHILD_ADDED,user_details_parentObj.getString("child_added"));

                    if (user_details_parentObj.getString("child_added").equalsIgnoreCase("0"))
                    {
                        Intent intent=new Intent(this, SkipAddRider.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else
                    {
                        Intent intent=new Intent(this, ParentHome.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                }
            }
            else if (userType.equalsIgnoreCase("driver"))
            {
                JSONObject user_details_driverObj=user_details.getJSONObject("user_details_driver");
                helper.saveString(Constant.TOKEN,data.getString("token"));
                helper.saveBoolean(SharedPrefUtil.LOGIN, true);
                helper.saveString(SharedPrefUtil.USERTYPE,user_details_driverObj.getString("user_type"));
                helper.saveString(SharedPrefUtil.DRIVER_STATUS,"0");
                helper.saveString(SharedPrefUtil.USER_ID,user_details_driverObj.getString("id"));
                helper.saveString(SharedPrefUtil.FNAME,user_details_driverObj.getString("first_name"));
                helper.saveString(SharedPrefUtil.LNAME,user_details_driverObj.getString("last_name"));
                helper.saveString(SharedPrefUtil.PHONE_NUMBER,user_details_driverObj.getString("phone_no"));

                helper.saveString(SharedPrefUtil.EMAIL,user_details_driverObj.getString("email"));

                helper.saveString(SharedPrefUtil.NOTIFICATION_SETTING,user_details_driverObj.getString("is_enable_push_notifications"));
                helper.saveString(SharedPrefUtil.SMS_SETTINGS,user_details_driverObj.getString("is_enable_text_message_alert"));

         /*       if (!user_details_driverObj.getString("image").equalsIgnoreCase("null"))
                {
                    helper.saveString(SharedPrefUtil.IMAGE,user_details_driverObj.getString("image"));
                }
                else
                {
                    helper.saveString(SharedPrefUtil.IMAGE,"0");
                }
*/


              /*  if (!user_details_driverObj.getString("image").equalsIgnoreCase("null") ||
                        !user_details_driverObj.getString("image").equalsIgnoreCase("") )*/
                if (user_details_driverObj.getString("image").equalsIgnoreCase("") )
                {
                    helper.saveString(SharedPrefUtil.IMAGE,"0");
                }
                else
                {
                    helper.saveString(SharedPrefUtil.IMAGE,user_details_driverObj.getString("image"));
                }
                helper.saveString(SharedPrefUtil.VEHICLE_SELECTED,"1");
                helper.saveString(SharedPrefUtil.VEHICLE_SAVED,"1");
                helper.saveString(SharedPrefUtil.DOCUMENTS_SAVED,"1");

                Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(LoginActivity.this, DriverHome.class);
                startActivity(intent);
                finish();

              /*      if (helper.getString(SharedPrefUtil.VEHICLE_SELECTED).equalsIgnoreCase("1"))
                    {
                        if (helper.getString(SharedPrefUtil.VEHICLE_SAVED).equalsIgnoreCase("1"))
                        {

                            if (helper.getString(SharedPrefUtil.DOCUMENTS_SAVED).equalsIgnoreCase("1"))
                            {
                                Intent intent=new Intent(LoginActivity.this, DriverHome.class);
                                startActivity(intent);
                                finish();
                            }

                            else
                            {
                                Intent intent=new Intent(LoginActivity.this, UploadDocumentActivity.class);
                                intent.putExtra("vehicleId",SharedPrefUtil.getInstance().getString(SharedPrefUtil.VEHICLE_SAVED_ID,""));
                                startActivity(intent);
                                finish();
                            }
                        }
                        else
                        {

                            Intent intent=new Intent(LoginActivity.this, AddVehicleDetails.class);
                            intent.putExtra("vehicleId",SharedPrefUtil.getInstance().getString(SharedPrefUtil.SELECTED_VEHICLE_ID,""));
                            startActivity(intent);
                            finishAffinity();
                        }
                    }

                    else
                    {
                        Intent intent=new Intent(LoginActivity.this, SelectVehicleTypeActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }*/
            }


              /*  //for both
                if(user_details.has("user_details_parent") &&  user_details.has("user_details_driver"))
                {
                    JSONObject user_details_driverObj=user_details.getJSONObject("user_details_driver");
                    JSONObject user_details_parentObj=user_details.getJSONObject("user_details_parent");
                    callAlert(user_details_parentObj,user_details_driverObj, data.getString("token"));
                }

                //only For Driver
                if(!user_details.has("user_details_parent") &&  user_details.has("user_details_driver"))
                {

                }

                //only For Parent
                if(user_details.has("user_details_parent") &&  !user_details.has("user_details_driver"))
                {

                }
*/
            }
            else
            {
                Toast.makeText(this, generalResponse.getResponse().getString("message"),
                        Toast.LENGTH_SHORT).show();
            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void callAlert(JSONObject user_details_parentObj, JSONObject user_details_driverObj, String token)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvMsg.setText("You would to like to login as:");
        no.setText("Parent");
        yes.setText("Driver");


        Log.e("listSize ", ListofChildrenAdapter.listIds.size()+"");

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                try{
                    helper.saveString(Constant.TOKEN,token);
                    helper.saveBoolean(SharedPrefUtil.LOGIN, true);
                    helper.saveString(SharedPrefUtil.USERTYPE,user_details_parentObj.getString("user_type"));
                    helper.saveString(SharedPrefUtil.USER_ID,user_details_parentObj.getString("id"));
                    helper.saveString(SharedPrefUtil.FNAME,user_details_parentObj.getString("first_name"));
                    helper.saveString(SharedPrefUtil.LNAME,user_details_parentObj.getString("last_name"));
                    helper.saveString(SharedPrefUtil.PHONE_NUMBER,user_details_parentObj.getString("phone_no"));
                    helper.saveString(SharedPrefUtil.HOME_ADDRESS,user_details_parentObj.getString("address"));
                    helper.saveString(SharedPrefUtil.NOTIFICATION_SETTING,user_details_parentObj.getString("is_enable_push_notifications"));
                    helper.saveString(SharedPrefUtil.SMS_SETTINGS,user_details_parentObj.getString("is_enable_text_message_alert"));
                    helper.saveString(SharedPrefUtil.EMAIL,user_details_parentObj.getString("email"));

                    if (!user_details_parentObj.getString("image").equalsIgnoreCase("null"))
                    {
                        helper.saveString(SharedPrefUtil.IMAGE,user_details_parentObj.getString("image"));
                    }
                    else {
                        helper.saveString(SharedPrefUtil.IMAGE,"0");
                    }
                    helper.saveString(SharedPrefUtil.CHILD_ADDED,user_details_parentObj.getString("child_added"));
                    Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();


                    if (user_details_parentObj.getString("child_added").equalsIgnoreCase("0"))
                    {
                        Intent intent=new Intent(LoginActivity.this, SkipAddRider.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else
                    {
                        Intent intent=new Intent(LoginActivity.this, ParentHome.class);
                        startActivity(intent);
                        finishAffinity();
                    }


                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }




                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                try{
                    helper.saveString(Constant.TOKEN,token);
                    helper.saveBoolean(SharedPrefUtil.LOGIN, true);
                    helper.saveString(SharedPrefUtil.USERTYPE,user_details_driverObj.getString("user_type"));
                    helper.saveString(SharedPrefUtil.DRIVER_STATUS,"0");
                    helper.saveString(SharedPrefUtil.USER_ID,user_details_driverObj.getString("id"));
                    helper.saveString(SharedPrefUtil.FNAME,user_details_driverObj.getString("first_name"));
                    helper.saveString(SharedPrefUtil.LNAME,user_details_driverObj.getString("last_name"));
                    helper.saveString(SharedPrefUtil.PHONE_NUMBER,user_details_driverObj.getString("phone_no"));

                Log.e("Image  ",user_details_driverObj.getString("image"));

                    if (!user_details_driverObj.getString("image").equalsIgnoreCase("null"))
                    {
                        helper.saveString(SharedPrefUtil.IMAGE,user_details_driverObj.getString("image"));
                    }
                    else {
                        helper.saveString(SharedPrefUtil.IMAGE,"0");
                    }
                    helper.saveString(SharedPrefUtil.EMAIL,user_details_driverObj.getString("email"));

                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this, DriverHome.class);
                startActivity(intent);
                finishAffinity();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 2:

                for (int i = 0; i < permissions.length; i++)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(this, "Permitions Allow", Toast.LENGTH_SHORT).show();
                        //  getLocation();
                    }
                    else if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        // checkPermissionLocation(context);
                        Toast.makeText(this, "Permitions Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                default:
                {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }



/*    public void callAlert(String child_added)
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

        tvMsg.setText("You would to like to login as:");
        no.setText("Parent");
        yes.setText("Driver");


        Log.e("listSize ", ListofChildrenAdapter.listIds.size()+"");

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                helper.saveString(SharedPrefUtil.USERTYPE,"parent");

                    helper.saveString(SharedPrefUtil.CHILD_ADDED,child_added);

                    if (child_added.equalsIgnoreCase("0"))
                    {
                        Intent intent=new Intent(LoginActivity.this, SkipAddRider.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else {
                        Intent intent=new Intent(LoginActivity.this, ParentHome.class);
                        startActivity(intent);
                        finishAffinity();
                    }

                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                helper.saveString(SharedPrefUtil.USERTYPE,"driver");
                helper.saveString(SharedPrefUtil.DRIVER_STATUS,"0");
                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this, DriverHome.class);
                startActivity(intent);
                finishAffinity();

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }*/

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
        progressDialog.dismiss();
    }

    @Override
    public void showPermissionAlert(ArrayList<String> permissionList, int code)
    {
        Log.e( "showPermissionAlert: ","Popup" );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Log.e( "showPermissionAlert: ","Inside" );
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), code);

        }
    }
}
