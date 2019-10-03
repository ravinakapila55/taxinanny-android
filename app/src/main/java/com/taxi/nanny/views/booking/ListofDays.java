package com.taxi.nanny.views.booking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.ListofDaysBean;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.req_model.EstimateFTModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.adapter.ListofDaysAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListofDays extends BaseActivity implements Callback<ResponseBody>
{
    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvDate)
    TextView tvDate;

    @BindView(R.id.ivDate)
    ImageView ivDate;

    ArrayList<String> selectedDaysFinal=new ArrayList<>();

    ArrayList<ListofDaysBean> list=new ArrayList<>();
    public static String TAG=ListofDays.class.getSimpleName();
    private String date="";
    private int mYear, mMonth, mDay;

    ArrayList<String> selectedDays=new ArrayList<>();
    ArrayList<RiderListModel> riderList;
    ArrayList<String> dist_list;
    ListofDaysAdapter listofDaysAdapter;
    String book_time="";
    String book_date="";

    @Override
    protected int getContentId()
    {
        return R.layout.list_of_days;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");
        riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
        dist_list=(ArrayList<String>) getIntent().getSerializableExtra("dist_list");
        book_time=getIntent().getExtras().getString("book_time");
        book_date=getIntent().getExtras().getString("book_date");
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        setHeaderText("List of Days");
        setData();
        setAdapter();
    }

    private void setData()
    {
        selectedDays.clear();
        ListofDaysBean listofDaysBean=new ListofDaysBean("1","Sunday",false,true);
        list.add(listofDaysBean);

        listofDaysBean=new ListofDaysBean("2","Monday",false,true);
        list.add(listofDaysBean);

        listofDaysBean=new ListofDaysBean("3","Tuesday",false,true);
        list.add(listofDaysBean);

        listofDaysBean=new ListofDaysBean("4","Wednesday",false,true);
        list.add(listofDaysBean);

        listofDaysBean=new ListofDaysBean("5","Thursday",false,true);
        list.add(listofDaysBean);

        listofDaysBean=new ListofDaysBean("6","Friday",false,true);
        list.add(listofDaysBean);

        listofDaysBean=new ListofDaysBean("7","Saturday",false,true);
        list.add(listofDaysBean);
    }

    @OnClick({R.id.tvContinue,R.id.img_back_btn,R.id.tvDate,R.id.ivDate})
    public void onClick(View view)
    {
        switch (view.getId())
        {
                case R.id.img_back_btn:
                finish();
                break;

                case R.id.tvDate:
                setDatePicker();
                break;

                case R.id.ivDate:
                setDatePicker();
                break;

                case R.id.tvContinue:
                 selectedDays.clear();
                for (int i = 0; i <list.size() ; i++)
                {
                if (list.get(i).isFlag())
                {
                selectedDays.add(list.get(i).getDay());
                }
                }
                Log.e("DaysListSize ",selectedDays.size()+"");

                if (checkValidations())
                {
                 callEstimatedEta();
                }
                break;
        }
    }

    public boolean checkValidations()
    {
        if (date.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please choose end date", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (selectedDays.size()==0)
        {
            Toast.makeText(this, "Select Recurring days", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    private void setDatePicker()
    {
        final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        final String[] Days = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                String mn = "";
                for (int i = 0; i < MONTHS.length; i++)
                {
                    if (i == monthOfYear)
                    {
                        mn = MONTHS[i];
                    }
                }
                int month = 0;
                month = monthOfYear + 1;

                HashMap<Integer, String> map = new HashMap<>();

                map.put(1, "01");
                map.put(2, "02");
                map.put(3, "03");
                map.put(4, "04");
                map.put(5, "05");
                map.put(6, "06");
                map.put(7, "07");
                map.put(8, "08");
                map.put(9, "09");
                map.put(10, "10");
                map.put(11, "11");
                map.put(12, "12");

                String exactMonth = String.valueOf(map.get(month));

                String day = "";
                if (dayOfMonth < Days.length)
                {
                    day = "0" + dayOfMonth;
                } else
                {
                    day = "" + dayOfMonth;
                }

                tvDate.setText(year + "/" + exactMonth + "/" + day);
                date=year+"-"+(exactMonth)+"-"+dayOfMonth;

                //to get current date
                String currentDate= Constant.getCurrentDate();
                Log.e(TAG, "onDateSet: Current "+currentDate);
                Log.e(TAG, "onDateSet: choose "+date);
                //to compare 2 dates
                String compareDates=Constant.compareDates(currentDate,date);
                Log.e(TAG, "onDateSet: compareDates "+compareDates);

                long difference=Constant.difference2Dates(date,currentDate);//bb
                Log.e(TAG, "onDateSet: Difference "+difference);

                daysValidate(difference);

            }
        }, mYear, mMonth, mDay);
        try
        {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        datePickerDialog.show();
    }

    public void daysValidate(long difference)
    {
        Log.e(TAG, "daysValidate: difference "+difference);
        int currentDay=Constant.getCurrentDay();
        Log.e(TAG, "onDateSet: currentDay "+currentDay);
        if (difference==0)
        {
            Log.e(TAG, "onDateSet: Value "+"0");
            for (int i = 0; i <list.size() ; i++)
            {
                if (currentDay==Integer.parseInt(list.get(i).getId()))
                {
                    list.get(i).setFlag(true);
                    list.get(i).setEnable(true);
                }
                else {
                    list.get(i).setFlag(false);
                    list.get(i).setEnable(false);
                }
                listofDaysAdapter.notifyDataSetChanged();
            }

        }
        else if (difference<7)
        {
            Log.e(TAG, "onDateSet: Value "+"less than 7");
            int count=currentDay+Integer.parseInt(String.valueOf(difference));
            Log.e("Count ",count+"");
            Log.e("listSize ",list.size()+"");

            ArrayList<Integer> selectedIds=new ArrayList<>();
            selectedIds.clear();
            int dayOfWeek = 0;
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date endDate=simpleDateFormat.parse(date);
                Calendar c = Calendar.getInstance();
                c.setTime(endDate); // yourdate is an object of type Date
                dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                Log.e(TAG, "daysValidate: DaysUntil "+dayOfWeek);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
            if (currentDay<dayOfWeek)
            {
                for (int i = currentDay; i <= dayOfWeek; i++) {
                    selectedIds.add(i);
                }
            }
            //4 1
            else if (currentDay>dayOfWeek)
            {
                for (int z = currentDay; z <=list.size(); z++)
                {
                    selectedIds.add(z);
                }
                for (int fg = 1; fg <=dayOfWeek ; fg++) {
                    selectedIds.add(fg);
                }
            }

            ArrayList<Integer> allIds=new ArrayList<>();
            allIds.clear();

            for (int q = 0; q <list.size() ; q++)
            {
                allIds.add(Integer.valueOf(list.get(q).getId()));
            }

            Collections.sort(allIds);
            Collections.sort(selectedIds);

            Log.e(TAG, "daysValidate: SelecedFilterDays "+selectedIds.toString());
            Log.e(TAG, "daysValidate: allIds "+allIds.toString());

            for(int i = 0; i <allIds.size(); i++){
                if(selectedIds.contains(allIds.get(i))){
                    Log.e(TAG, "daysValidate: yes "+i);
                    list.get(i).setEnable(true);
                    list.get(i).setFlag(false);
                }
                else{
                    Log.e(TAG, "daysValidate: no "+i);
                    list.get(i).setEnable(false);
                    list.get(i).setFlag(false);
                }
                listofDaysAdapter.notifyDataSetChanged();
            }
        }
        else if (difference>7 || difference==7)
        {
            Log.e(TAG, "onDateSet: Value "+"more than 7");

            if (difference==7)
            {
                Log.e("Equal 7 ","yes");
            }
            else if (difference>7)
            {
                long reminder;
                reminder=difference%7;
                Log.e("reminder ",reminder+"");
                if (difference%7==0)
                {
                    Log.e("divisible by 7 ","yes");
                }
                else
                {
                    Log.e("divisible by 7 ","No");
                }
            }
            for (int i = 0; i <list.size() ; i++)
            {
                list.get(i).setFlag(false);
                list.get(i).setEnable(true);
                listofDaysAdapter.notifyDataSetChanged();
            }
        }
    }

    SharedPrefUtil sharedPrefUtil;
    String token;

    public void callEstimatedEta()
    {
        /*for (int z = 0; z <riderList.size() ; z++)
        {
            riderList.get(z).setEstDistance("23");
            riderList.get(z).setEstPrice("56");
        }*/
        HashMap<String,String> param=new HashMap<>();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        List<EstimateFTModel.RideDetailBean> estimateFTModelList = new ArrayList<>();
        JsonArray ride_detail=new JsonArray();
        for (int i = 0; i < riderList.size(); i++)
        {
            EstimateFTModel.RideDetailBean estimateFTModel = new EstimateFTModel.RideDetailBean();
            estimateFTModel.setRider_drop_location_id(riderList.get(i).getDrop_saved_id());
            estimateFTModel.setRider_pick_location_id(riderList.get(i).getPick_saved_id());
            estimateFTModelList.add(estimateFTModel);
        }
        EstimateFTModel estimateFTModel = new EstimateFTModel();
        estimateFTModel.setRide_detail(estimateFTModelList);
        param.put("ride_detail", gson.toJson(estimateFTModel));
        Log.e(TAG, "callEstimatedEta: Params "+param );
        api(param,this,token,11);
    }

    public void setAdapter()
    {
         listofDaysAdapter=new ListofDaysAdapter(this,list);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(listofDaysAdapter);

        listofDaysAdapter.onItemClickListener(new ListofDaysAdapter.myClickListener()
        {
            @Override
            public void onItemClick(int layoutPosition, View view)
            {

                if (date.equalsIgnoreCase(""))
                {
                    Toast.makeText(ListofDays.this, "Please choose end date", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    list.get(layoutPosition).setFlag(list.get(layoutPosition).isFlag()?false:true);
                    listofDaysAdapter.notifyDataSetChanged();
                    Log.e(TAG, "onItemClick:Position "+layoutPosition);
                    listofDaysAdapter.isClickable = true;
                }
            }
        });
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e(TAG, "onResponse: General<< "+generalResponse);

        try {

            JSONObject jsonObject=generalResponse.getResponse_object();

            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
//                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                JSONArray data=jsonObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++)
                {
                    JSONObject jsonObject1=data.getJSONObject(i);
//                    RiderListModel riderListModel=new RiderListModel();

                    double time=Double.parseDouble(jsonObject1.getString("time"));
                    double price=Double.parseDouble(jsonObject1.getString("price"));
                    double distance=Double.parseDouble(jsonObject1.getString("distance"));

                    String amount= new DecimalFormat("##.##").format(price);
                    String timeee= new DecimalFormat("##.##").format(time);
                    String distanceeee= new DecimalFormat("##.##").format(distance);

                    double timeValue=Double.parseDouble(timeee)*60;
                    // long amount=Math.round(price);

                    Log.e("EstimatedTime ",timeValue+"");
                    Log.e("EstimatedPrice ",amount+"");
                    Log.e("EstimatedDistance ",distanceeee+"");

                    riderList.get(i).setTime_eta(String.valueOf(timeValue));
                    riderList.get(i).setEstPrice(String.valueOf(amount));
                    riderList.get(i).setEstDistance(String.valueOf(distanceeee));


                }

                Intent intent=new Intent(ListofDays.this,ConfirmBooking.class);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("days_list",(Serializable) selectedDays);
                intent.putExtra("dist_list", dist_list);
                intent.putExtra("end_date", date.trim());
                intent.putExtra("ride_type","1");
                intent.putExtra("book_time",book_time);
                intent.putExtra("book_date",book_date);

                startActivity(intent);
            }

            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                replaceActivity();
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

    }
}
