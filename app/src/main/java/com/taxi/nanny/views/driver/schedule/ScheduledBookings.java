package com.taxi.nanny.views.driver.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.model.DriverScheduledBookingsModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.model.ScheduleBookingsModel;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.driver.schedule.adapter.ScheduleBookingAdapter;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class ScheduledBookings extends BaseActivity {


    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    ArrayList<ScheduleBookingsModel> scheduleList;



    @Override
    protected int getContentId() {
        return R.layout.emergency_contacts;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Scheduled Bookings");



        scheduleList=(ArrayList<ScheduleBookingsModel>) getIntent().getSerializableExtra("list");

        Log.e("scheduleListSize ",scheduleList.size()+"");

        setAdapter();

    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        ScheduleBookingAdapter scheduleBookingAdapter=new ScheduleBookingAdapter(this,scheduleList);
        recycler.setAdapter(scheduleBookingAdapter);

        scheduleBookingAdapter.onItemSelectedListener(new ScheduleBookingAdapter.myClickListener() {
            @Override
            public void onItemClick(int layoutPosition, View view) {
                Intent intent=new Intent(ScheduledBookings.this,BookingDetails.class);
                intent.putExtra("list",(Serializable)scheduleList.get(layoutPosition).getScheduleBookingRiderDetailsModellist());
                startActivity(intent);
            }
        });
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
