package com.taxi.nanny.views.driver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.model.RecurringDaysModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.adapter.ListofDaysAdapter;
import com.taxi.nanny.views.driver.adapter.RecurringDaysAdapter;

import java.util.ArrayList;

import butterknife.BindView;

public class RecurringDaysBooking extends BaseActivity
{
    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvDate)
    TextView tvDate;

    ArrayList<RecurringDaysModel> daysRecurringList;

    @Override
    protected int getContentId()
    {
        return R.layout.recurring_days_booking;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Recurring Days");
        tvDate.setText(getIntent().getExtras().getString("end_date"));
        daysRecurringList=(ArrayList<RecurringDaysModel>) getIntent().getSerializableExtra("daysRecurringList");
        setAdapter();
    }

    public void setAdapter()
    {
       RecurringDaysAdapter recurringDaysAdapter=new RecurringDaysAdapter(this,daysRecurringList);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(recurringDaysAdapter);
    }
}
