package com.taxi.nanny.views.driver.documents;

import android.content.Intent;
import android.nfc.TagLostException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.DriverDocumentsModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.views.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class DriverLicence extends BaseActivity
{

    DriverDocumentsModel driverDocumentsModel;

    @BindView(R.id.ivLicence)
    ImageView ivLicence;

    @BindView(R.id.tvFname)
    TextView tvFname;

    @BindView(R.id.tvCountry)
    TextView tvCountry;

    @BindView(R.id.tvIssuedOn)
    TextView tvIssuedOn;

    @BindView(R.id.tvLicence)
    TextView tvLicence;

    @BindView(R.id.tvBirthday)
    TextView tvBirthday;

    @BindView(R.id.tvExpiry)
    TextView tvExpiry;

    @BindView(R.id.tvAddress1)
    TextView tvAddress1;

    @BindView(R.id.tvAddress2)
    TextView tvAddress2;

    @BindView(R.id.tvCity)
    TextView tvCity;

    @BindView(R.id.tvState)
    TextView tvState;

    @BindView(R.id.tvZip)
    TextView tvZip;



    @Override
    protected int getContentId()
    {
        return R.layout.driver_licence;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Driver Licence");

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

        tvFname.setText(driverDocumentsModel.getLicence_full_name());
        tvCountry.setText("India");
        tvIssuedOn.setText(driverDocumentsModel.getlIssuedOn());
        tvLicence.setText(driverDocumentsModel.getLicence_no());
        tvBirthday.setText(driverDocumentsModel.getlBirthday());
        tvExpiry.setText(driverDocumentsModel.getlExpiry_date());
        tvAddress1.setText(driverDocumentsModel.getLaddress1());
        tvAddress2.setText("XYZ");
        tvCity.setText("Ludhiana");
        tvState.setText("Punjab");
        tvZip.setText(driverDocumentsModel.getLzipcode());
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
