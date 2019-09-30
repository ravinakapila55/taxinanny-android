package com.taxi.nanny.views.driver.earning;

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
import com.taxi.nanny.model.EarningModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.driver.earning.adapter.TripAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static com.taxi.nanny.utils.Constant.TOKEN;

public class TodayEarningFragment  extends Fragment implements RetrofitResponse
{

    RecyclerView recycler;
    TextView tvLabel;

    TextView tvNoData;
    TextView tvEarning;
    TextView tvTime;
    TextView tvTrip;

    ArrayList<EarningModel> list=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.today_earning,container,false);
        initValues(view);

        SharedPrefUtil prefUtil=SharedPrefUtil.getInstance();
        String token=prefUtil.getString(TOKEN,"");
        initValues(view);

        return view;
    }

    public void initValues(View view)
    {
        recycler=(RecyclerView)view.findViewById(R.id.recycler);
        tvLabel=(TextView) view.findViewById(R.id.tvLabel);
        tvEarning=(TextView) view.findViewById(R.id.tvEarning);
        tvTime=(TextView) view.findViewById(R.id.tvTime);
        tvNoData=(TextView) view.findViewById(R.id.tvNoData);
        tvTrip=(TextView) view.findViewById(R.id.tvTrip);
        tvLabel.setText("TODAY'S TRIP");
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        callTodayEarning();

    }

    private void callTodayEarning()
    {
        new RetrofitService(getActivity(), this, Constant.API_DRIVER_EARNING ,
                105, 1,"1").callService(true);
    }

    public void setAdapter()
    {
        TripAdapter tripAdapter=new TripAdapter(getActivity(),list);
        recycler.setAdapter(tripAdapter);
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

                        JSONObject data=jsonObject.getJSONObject("Trip_detail");
                        JSONArray Array = data.getJSONArray("Today");

                        tvEarning.setText(data.getString("amount_final_today")+" $");

                        tvTrip.setText(data.getString("No_of_trips_today"));

                        SimpleDateFormat input = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat output = new SimpleDateFormat("mm:ss");

                        Date date = null, date1 = null;
                        String validTime = "";

                        try {
                            date = input.parse(data.getString("time_today"));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        validTime = output.format(date);

                        tvTime.setText(validTime);

                        if (Array.length() > 0) {
                            for (int i = 0; i < Array.length(); i++) {

                                JSONObject object = Array.getJSONObject(i);

                                EarningModel earningModel=new EarningModel();

                                earningModel.setBooking_id(object.getString("booking_id"));
                                earningModel.setParent_name(object.getString("parent_name"));
                                earningModel.setParent_image(object.getString("image"));
                                earningModel.setTime(object.getString("Created_at"));

                                if (object.getString("amount").equalsIgnoreCase("null"))
                                {
                                    earningModel.setAmount("NA");
                                }
                                else {
                                    earningModel.setAmount(object.getString("amount") + " $");
                                }
                                list.add(earningModel);

                                if (list.size()>0)
                                {
                                    setAdapter();
                                }

                            }
                        }
                        else {
                            tvNoData.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        }


                    }
                    else {
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

