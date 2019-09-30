package com.taxi.nanny.views.driver.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.model.ScheduleBookingRiderDetailsModel;
import com.taxi.nanny.model.ScheduleBookingsModel;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.driver.schedule.adapter.BookingDetailsAdapter;
import com.taxi.nanny.views.driver.schedule.adapter.ScheduleBookingAdapter;
import com.taxi.nanny.views.settings.ChangePassword;
import com.taxi.nanny.views.settings.EmergencyContacts;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class BookingDetails extends BaseActivity {


    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    ArrayList<ScheduleBookingRiderDetailsModel> list;


    @Override
    protected int getContentId() {
        return R.layout.emergency_contacts;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Booking Details");

        list=(ArrayList<ScheduleBookingRiderDetailsModel>) getIntent().getSerializableExtra("list");

        setAdapter();
    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        BookingDetailsAdapter bookingDetailsAdapter=new BookingDetailsAdapter(this,list);
        recycler.setAdapter(bookingDetailsAdapter);
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
