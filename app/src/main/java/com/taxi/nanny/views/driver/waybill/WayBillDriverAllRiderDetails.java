package com.taxi.nanny.views.driver.waybill;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.views.BaseActivity;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

public class WayBillDriverAllRiderDetails extends BaseActivity
{
    @BindView(R.id.recycler)
    RecyclerView recycler;

    ArrayList<RiderListModel> rides_list;

    @Override
    protected int getContentId()
    {
        return R.layout.ride_history;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("All Rider Detail");
        rides_list=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rides_list");
        setAdapter();
    }

    public void setAdapter()
    {
        WayBillDetailsAdapter historyAdapter=new WayBillDetailsAdapter(this,rides_list);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(historyAdapter);
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
