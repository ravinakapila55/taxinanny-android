package com.taxi.nanny.views.driver.rating;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.driver.rating.adapter.DriverIndividualRateAdapter;
import com.taxi.nanny.views.driver.rating.adapter.DriverRatingAdapter;

import butterknife.BindView;
import butterknife.OnClick;

public class DriverRating extends BaseActivity
{

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.recyclerRating)
    RecyclerView recyclerRating;

    @Override
    protected int getContentId()
    {
        return R.layout.driver_rating;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setTab();
        setHeaderText("Ratings");
        setRatingAdapter();
        setAdapter();
    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        DriverRatingAdapter driverRatingAdapter=new DriverRatingAdapter(this);
        recycler.setAdapter(driverRatingAdapter);
    }

    public void setRatingAdapter()
    {
        recyclerRating.setLayoutManager(new LinearLayoutManager(this));
        DriverIndividualRateAdapter driverRatingAdapter=new DriverIndividualRateAdapter(this);
        recyclerRating.setAdapter(driverRatingAdapter);
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
