package com.taxi.nanny.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.taxi.nanny.R;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.booking.SubmitReview;
import com.taxi.nanny.views.driver.DriverHome;
import com.taxi.nanny.views.home.ParentHome;
import com.taxi.nanny.views.login_section.WelcomeActivity;
import com.taxi.nanny.views.login_section.register.AddVehicleDetails;
import com.taxi.nanny.views.login_section.register.SelectVehicleTypeActivity;
import com.taxi.nanny.views.login_section.register.UploadDocumentActivity;
import com.taxi.nanny.views.login_section.register.parent.SkipAddRider;



public class SplashActivity extends BaseActivity
{
    String deviceToken;

    @Override
    protected int getContentId()
    {
        return R.layout.activity_splash;
//        return R.layout.gif;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);



        Handler handler=new Handler();
        Runnable runnable=new Runnable()
        {
            @Override
            public void run()
            {
                SharedPrefUtil sharedPrefUtil= SharedPrefUtil.getInstance();

                if (sharedPrefUtil.getBoolean(SharedPrefUtil.LOGIN))
                {
                    if (sharedPrefUtil.getString(SharedPrefUtil.USERTYPE).equalsIgnoreCase("parent"))
                    {
                        if (sharedPrefUtil.getString(SharedPrefUtil.CHILD_ADDED).equalsIgnoreCase("0"))
                        {
                            Intent intent=new Intent(SplashActivity.this, SkipAddRider.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else
                        {
//                            Intent intent=new Intent(SplashActivity.this, ParentHome.class);
                            Intent intent=new Intent(SplashActivity.this, ParentHome.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                    else
                    {
                            if (sharedPrefUtil.getString(SharedPrefUtil.VEHICLE_SELECTED).equalsIgnoreCase("1"))
                            {
                                if (sharedPrefUtil.getString(SharedPrefUtil.VEHICLE_SAVED).equalsIgnoreCase("1"))
                                {
                                    if (sharedPrefUtil.getString(SharedPrefUtil.DOCUMENTS_SAVED).equalsIgnoreCase("1"))
                                    {
                                        Intent intent=new Intent(SplashActivity.this, DriverHome.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    else
                                    {
                                        Intent intent=new Intent(SplashActivity.this, UploadDocumentActivity.class);
                                        intent.putExtra("vehicleId",SharedPrefUtil.getInstance().getString(SharedPrefUtil.VEHICLE_SAVED_ID,""));
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                                else
                                {

                                    Intent intent=new Intent(SplashActivity.this, AddVehicleDetails.class);
                                    intent.putExtra("vehicleId",SharedPrefUtil.getInstance().getString(SharedPrefUtil.SELECTED_VEHICLE_ID,""));
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            }
                            else
                            {
                                Intent intent=new Intent(SplashActivity.this, SelectVehicleTypeActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }



                    }
                }
                else
                {
                    Intent intent=new Intent(SplashActivity.this, WelcomeScreens.class);
                    startActivity(intent);
                    finish();
                 }
            }
        };
        handler.postDelayed(runnable,4000);

    }

}
