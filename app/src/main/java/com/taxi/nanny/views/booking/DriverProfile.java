package com.taxi.nanny.views.booking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.DriverBookingModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.views.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class DriverProfile extends BaseActivity
{
    public static String TAG=DriverProfile.class.getSimpleName();

    @BindView(R.id.tvCall)
    TextView tvCall;

    @BindView(R.id.ivDriver)
    ImageView ivDriver;

    @BindView(R.id.tvFname)
    TextView tvFname;

    @BindView(R.id.ivVehicle)
    ImageView ivVehicle;

    @BindView(R.id.tvModelName)
    TextView tvModelName;

    @BindView(R.id.tvMake)
    TextView tvMake;

    @BindView(R.id.tvLicence)
    TextView tvLicence;

    @BindView(R.id.tvState)
    TextView tvState;

    @BindView(R.id.tvRiderName)
    TextView tvRiderName;

    ArrayList<RiderListModel> riderList;
    ArrayList<DriverBookingModel> driverList;


    @Override
    protected int getContentId() {
        return R.layout.driver_profile;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Driver Profile");

        riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
        driverList=(ArrayList<DriverBookingModel>) getIntent().getSerializableExtra("driver_list");

        setData();
    }


    @OnClick({R.id.tvCall,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {

            case R.id.tvCall:

                Log.e(TAG, "onClick: PhoneNumber "+driverList.get(0).getPhoneNumber());
                String phone = driverList.get(0).getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", phone, null));
                startActivity(intent);
                break;

            case R.id.img_back_btn:
               onBackPressed();
                break;

        }
    }

    public void setData()
    {
      /*  if (!driverList.get(0).getImage().equalsIgnoreCase(""))
        {
            Picasso.with(this).load(driverList.get(0).getImage()).placeholder(getResources().getDrawable(R.drawable.userr)).into(ivDriver);
        }

        if (!driverList.get(0).getVehicle_image().equalsIgnoreCase(""))
        {
            Picasso.with(this).load(driverList.get(0).getVehicle_image()).placeholder
                    (getResources().getDrawable(R.drawable.sedan)).into(ivVehicle);
        }*/

        tvFname.setText(driverList.get(0).getName());
        tvRiderName.setText(riderList.get(0).getFirst_name()+" "+riderList.get(0).getLast_name());


        if (driverList.get(0).getVehicle_name().equalsIgnoreCase("null"))
        {
            tvModelName.setText("NA");
        }
        else {
            tvModelName.setText(driverList.get(0).getVehicle_name());
        }

        if (driverList.get(0).getVehicle_make().equalsIgnoreCase("null"))
        {
            tvMake.setText("NA");
        }
        else {
            tvMake.setText(driverList.get(0).getVehicle_make());
        }

        if (driverList.get(0).getLicence_number().equalsIgnoreCase("null"))
        {
            tvLicence.setText("NA");
        }
        else
        {
            tvLicence.setText(driverList.get(0).getLicence_number());
        }

        if (driverList.get(0).getIssue_state().equalsIgnoreCase("null"))
        {
            tvState.setText("NA");
        }
        else
        {
            tvState.setText(driverList.get(0).getIssue_state());
        }
    }
}
