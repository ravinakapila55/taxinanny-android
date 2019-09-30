package com.taxi.nanny.views.booking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.driver.DriverHome;

import butterknife.BindView;
import butterknife.OnClick;

public class ReviewTrip extends BaseActivity {

    @BindView(R.id.tvHome)
    TextView tvHome;



    String distance="";
    String fare="";

    @BindView(R.id.tvPrice)
    TextView tvPrice;

    @BindView(R.id.tvDistance)
    TextView tvDistance;


    @Override
    protected int getContentId()
    {
        return R.layout.review_trip;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Review your trip");

        if(getIntent().hasExtra("distance"))
        {
            distance=getIntent().getExtras().getString("distance");
            fare=getIntent().getExtras().getString("fare");

            tvPrice.setText("$ "+fare);
            tvDistance.setText(distance+" MI");


        }
    }

    @OnClick({R.id.img_back_btn,R.id.tvHome})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;

            case R.id.tvHome:
                Intent intent=new Intent(ReviewTrip.this, DriverHome.class);
                startActivity(intent);
                break;
        }
    }
}
