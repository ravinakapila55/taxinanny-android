package com.taxi.nanny.views.driver.documents;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.DriverDocumentsModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.views.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class VehicleInsurance extends BaseActivity {


    DriverDocumentsModel driverDocumentsModel;

    @BindView(R.id.ivLicence)
    ImageView ivLicence;

    @BindView(R.id.tvCompany)
    TextView tvCompany;

    @BindView(R.id.tvpolicy)
    TextView tvpolicy;

    @BindView(R.id.tvIssued)
    TextView tvIssued;

    @BindView(R.id.tvExpiry)
    TextView tvExpiry;



    @Override
    protected int getContentId() {
        return R.layout.vehcile_inusrance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Vehicle Insurance");

        if (getIntent().hasExtra("data"))
        {
            driverDocumentsModel=(DriverDocumentsModel)getIntent().getSerializableExtra("data");

            saveData();
        }
    }

    private void saveData() {


        if (!driverDocumentsModel.getlImage().equalsIgnoreCase(""))
        {
            Picasso.with(this).load(Constant.BASE_DRIVER_DOC+driverDocumentsModel.getlImage()).
                    placeholder(getResources().getDrawable(R.drawable.licence)).into(ivLicence);
        }

        tvCompany.setText(driverDocumentsModel.getICompnayName());
        tvExpiry.setText(driverDocumentsModel.getInsuranceExpiryDate());
        tvIssued.setText(driverDocumentsModel.getInsuranceIssuedDate());
        tvpolicy.setText(driverDocumentsModel.getIPolicyNumber());

    }

    @OnClick({R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;



        }
    }
}
