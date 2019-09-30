package com.taxi.nanny.views.driver.waybill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RideHistoryModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

public class WayBillDriver extends BaseActivity implements RetrofitResponse
{
    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    ArrayList<RideHistoryModel> list=new ArrayList();


    @Override
    protected int getContentId()
    {
        return R.layout.way_bill;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Way Bill");
        callRideHistory();
    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        WayBillDriverAdapter wayBillDriverAdapter=new WayBillDriverAdapter(this,list);
        recycler.setAdapter(wayBillDriverAdapter);

        wayBillDriverAdapter.onItemSelectedListener(new WayBillDriverAdapter.MyClickListener()
        {
            @Override
            public void onViewAllClick(int layoutPosition, View view)
            {
                Intent intent=new Intent(WayBillDriver.this, WayBillDriverAllRiderDetails.class);
                intent.putExtra("rides_list",(Serializable) list.get(layoutPosition).getRiderList());
                startActivity(intent);
            }

        });
    }

    public void callRideHistory()
    {
        new RetrofitService(this, this, Constant.API_DRIVER_ALL_RIDES ,
                105, 1,"1").callService(true);
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

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:

                Log.e("ResponsePast ",response);

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        tvNoData.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);

                        list.clear();

                        JSONArray Array = jsonObject.getJSONArray("data");

                        if (Array.length() > 0)
                        {
                            for (int i = 0; i < Array.length(); i++)
                            {

                                JSONObject object = Array.getJSONObject(i);

                                RideHistoryModel rideHistoryModel = new RideHistoryModel();

                                rideHistoryModel.setBookingId(object.getString("booking_id"));
                                rideHistoryModel.setParent_name(object.getString("parent_name"));
                                if (object.getString("amount").equalsIgnoreCase("null"))
                                {
                                    rideHistoryModel.setAmount("NA");
                                }
                                else
                                {
                                    rideHistoryModel.setAmount(object.getString("amount")+" $");
                                }

                                rideHistoryModel.setBookingType(object.getString("booking_type"));
                                rideHistoryModel.setDate(object.getString("Created_at"));
                                rideHistoryModel.setBookingDays(object.getString("booking_days"));

                                JSONArray ride_history = object.getJSONArray("ride_history");

                                if (ride_history.length()>0)
                                {
                                    if (ride_history.length()==1)
                                    {
                                        rideHistoryModel.setRideArrayLength("0");//for single rider
                                    }
                                    else
                                    {
                                        rideHistoryModel.setRideArrayLength("1");//for multiple rider
                                    }

                                    ArrayList<RiderListModel> riderList=new ArrayList<>();
                                    for (int j = 0; j <ride_history.length() ; j++)
                                    {
                                        RiderListModel riderListModel=new RiderListModel();
                                        JSONObject jsonObject1=ride_history.getJSONObject(j);
                                        riderListModel.setId(jsonObject1.getString("child_id"));
                                        riderListModel.setDroplat(jsonObject1.getString("drop_latitude"));
                                        riderListModel.setDropup(jsonObject1.getString("dropup_location"));
                                        riderListModel.setDroplng(jsonObject1.getString("drop_longitude"));
                                        riderListModel.setPickLat(jsonObject1.getString("pickup_latitude"));
                                        riderListModel.setPicklng(jsonObject1.getString("pickup_longitude"));
                                        riderListModel.setPickup(jsonObject1.getString("pickup_location"));
                                        riderListModel.setPickPriority(jsonObject1.getInt("priority_pick"));
                                        riderListModel.setDropPriority(jsonObject1.getInt("priority_drop"));
                                        riderListModel.setFirst_name(jsonObject1.getString("Name"));
                                        riderListModel.setImage(jsonObject1.getString("image"));
                                        riderListModel.setRide_status(jsonObject1.getString("ride_status"));
                                        riderList.add(riderListModel);
                                    }
                                    rideHistoryModel.setRiderList(riderList);
                                }
                                else
                                {
                                    rideHistoryModel.setRideArrayLength("0");//for single rider
                                }

                                list.add(rideHistoryModel);
                                if (list.size()>0)
                                {
                                    setAdapter();
                                }
                            }
                        }
                        else
                        {
                            tvNoData.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        tvNoData.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                    }

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
        }

    }


}
