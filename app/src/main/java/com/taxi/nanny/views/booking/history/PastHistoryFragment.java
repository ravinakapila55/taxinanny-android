package com.taxi.nanny.views.booking.history;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RideHistoryModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static com.taxi.nanny.utils.Constant.TOKEN;
import static com.taxi.nanny.utils.Constant.id;


public class PastHistoryFragment extends Fragment implements RetrofitResponse
{
    RecyclerView recycler;

    TextView tvNoData;

    HistoryAdapter historyAdapter;

    ArrayList<RideHistoryModel> list=new ArrayList();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.history,container,false);
        initValues(view);

        SharedPrefUtil prefUtil=SharedPrefUtil.getInstance();
        String token=prefUtil.getString(TOKEN,"");

        return view;
    }

    public void initValues(View view)
    {
        recycler=(RecyclerView)view.findViewById(R.id.recycler);
        tvNoData=(TextView) view.findViewById(R.id.tvNoData);
        callRideHistory();
    }

    public void callRideHistory()
    {
        new RetrofitService(getActivity(), this, Constant.API_RIDE_HISTORY ,
                105, 1,"1").callService(true);
    }

    public void setAdapter()
    {
        historyAdapter=new HistoryAdapter(getActivity(),list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(historyAdapter);

        historyAdapter.onItemSelectedListener(new HistoryAdapter.myClickListener()
        {
            @Override
            public void onItemClick(int layoutPosition, View view)
            {
                Intent intent=new Intent(getActivity(),AllRidersBookingHistory.class);
                intent.putExtra("rides_list",(Serializable) list.get(layoutPosition).getRiderList());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:

                Log.e("ResponsePast ",response);

                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        tvNoData.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);

                        list.clear();



                        if (jsonObject.has("data") )
                        {
                            Object dataObj=jsonObject.get("data");

                            if (dataObj instanceof  JSONObject){
                                JSONObject data=jsonObject.getJSONObject("data");

                                if (data.has("Previous"))
                                {
                                    JSONArray Array = data.getJSONArray("Previous");

                                    if (Array.length() > 0) {
                                        for (int i = 0; i < Array.length(); i++) {

                                            JSONObject object = Array.getJSONObject(i);

                                            RideHistoryModel rideHistoryModel = new RideHistoryModel();
                                            rideHistoryModel.setBookingId(object.getString("booking_id"));
                                            rideHistoryModel.setDriverName(object.getString("driver_name"));
                                            if (object.getString("amount").equalsIgnoreCase("null"))
                                            {
                                                rideHistoryModel.setAmount("NA");
                                            }
                                            else {
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

                                else {
                                    tvNoData.setVisibility(View.VISIBLE);
                                    recycler.setVisibility(View.GONE);
                                }


                            }

                            else if (dataObj instanceof JSONArray)
                            {
                                JSONArray dataArray = jsonObject.getJSONArray("data");

                                if (dataArray.length()==0)
                                {
                                    tvNoData.setVisibility(View.VISIBLE);
                                    recycler.setVisibility(View.GONE);
                                }
                                else {
                                    tvNoData.setVisibility(View.GONE);
                                    recycler.setVisibility(View.VISIBLE);
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
