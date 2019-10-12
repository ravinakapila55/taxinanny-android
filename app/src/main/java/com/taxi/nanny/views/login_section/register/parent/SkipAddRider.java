package com.taxi.nanny.views.login_section.register.parent;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.Drawer;
import com.taxi.nanny.views.home.ParentHome;
import com.taxi.nanny.views.login_section.WelcomeActivity;
import com.taxi.nanny.views.login_section.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class SkipAddRider extends BaseActivity
{
    protected int userType;

  /*  @BindView(R.id.tvSkip)
    TextView tvSkip;  */

    @BindView(R.id.tvEnd)
    TextView tvEnd;

    @BindView(R.id.bt_add)
    Button bt_add;
    SharedPrefUtil helper;

    @Override
    protected int getContentId()
    {
//        return R.layout.skip_add_rider;
        return R.layout.skip_riders;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        tvEnd.setVisibility(View.VISIBLE);
        setHeaderText(getResources().getString(R.string.rider_add));
        helper=SharedPrefUtil.getInstance();
        helper.saveString(SharedPrefUtil.CHILD_ADDED,"0");
        // userType= getIntent().getIntExtra(Constant.BUNDLE_USER_TYPE,0);
    }

    @OnClick({R.id.tvEnd,R.id.bt_add,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvEnd:
                helper.saveString(SharedPrefUtil.CHILD_ADDED,"2");
                Intent intent=new Intent(SkipAddRider.this, ParentHome.class);
                startActivity(intent);
                finishAffinity();
                break;

            case R.id.bt_add:
                Intent intent1=new Intent(SkipAddRider.this, AddRider.class);
                startActivity(intent1);
                break;

            case R.id.img_back_btn:
                SharedPrefUtil sharedPrefUtil=SharedPrefUtil.getInstance();
                callAlert();
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        callAlert();
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

        tvMsg.setText("Do you want to log out?");

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
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
                    Constant.API_LOGOUT,jsonObject, 700, 2,"1").
                    callService(true);
            SharedPrefUtil.getInstance().onLogout();
            Intent intent = new Intent(SkipAddRider.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(int RequestCode, String response) {
        switch (RequestCode) {
            case 700:
                Log.e( "onResponse: Logout " , response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        SharedPrefUtil.getInstance().onLogout();
                        Intent intent = new Intent(SkipAddRider.this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else
                    {

                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
        }
    }


}
