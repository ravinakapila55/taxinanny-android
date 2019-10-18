package com.taxi.nanny.views.booking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.CardList;
import com.taxi.nanny.model.DriverBookingModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.payment.StripePayment;
import com.taxi.nanny.req_model.ConfirmBookingMain;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.location.GPSTracker;
import com.taxi.nanny.utils.realTimeTrack.GetCurrentLocation;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.home.ParentHome;
import com.taxi.nanny.views.payment.AddCard;
import com.taxi.nanny.views.payment.adapter.PaymentListAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseCard extends BaseActivity implements RetrofitResponse,
        GetCurrentLocation.LatLngResponse, Callback<ResponseBody>
{

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @BindView(R.id.tvContinue)
    TextView tvContinue;

    SharedPrefUtil sharedPrefUtil;

    ArrayList<CardList> list=new ArrayList<>();
    String token;

    ArrayList<RiderListModel> riderList;
    String token1;
    String bookingType="";
    String end_date="";
    double amount=0;

    GPSTracker gpsTracker1;

    @BindView(R.id.cbSignin)
    CheckBox cbSignin;

    @BindView(R.id.cbSignOut)
    CheckBox cbSignOut;

    public static final String TAG= StripePayment.class.getSimpleName();
    //    private static final String PUBLISHABLE_KEY = "pk_test_NE6jrlmS0s6m39LMpFepdb3R00L2m7KnBh";
//    private static final String PUBLISHABLE_KEY = "pk_test_NE6jrlmS0s6m39LMpFepdb3R00L2m7KnBh";
    private static final String PUBLISHABLE_KEY = "pk_test_MaQhP2b8XqG9R7PTL6VUmipt";
    private static final String SECRET_KEY = "sk_test_lC0U9YV8dratMHpIrJ8ObVrK00QUgucAmv";

    ArrayList<String> days_list;

   public static  String generatedToken="";
   public static String cardId="";

    String signIn="0",signOut="0";

    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    String book_time="";
    String book_date="";

    @Override
    protected int getContentId()
    {
        return R.layout.choose_card;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Choose Payment");

        tvContinue.setVisibility(View.VISIBLE);

        ivEnd.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ivEnd.setImageDrawable(getDrawable(R.drawable.add_white));
        }

        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");

        riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
        days_list=(ArrayList<String>) getIntent().getSerializableExtra("days_list");
        bookingType=getIntent().getExtras().getString("ride_type");
        book_time=getIntent().getExtras().getString("book_time");
        book_date=getIntent().getExtras().getString("book_date");
        Log.e(TAG, "onCreate: BookingTYpe "+bookingType);
        token1=sharedPrefUtil.getString(Constant.TOKEN,"");

        gpsTracker1=new GPSTracker(this);

        if (gpsTracker1.canGetLocation())
        {
            Log.e(TAG, "onCreate: GPSLattitude "+gpsTracker1.getLatitude());
            Log.e(TAG, "onCreate: GPSLongitude "+gpsTracker1.getLongitude());
        }
        else
        {
            gpsTracker1.showSettingsAlert();
        }

        if (getIntent().hasExtra("end_date"))
        {
            end_date=getIntent().getExtras().getString("end_date");
            Log.e(TAG, "onCreate: Days_list "+days_list.size());
            Log.e(TAG, "onCreate: end_date "+end_date);

            for (int i = 0; i < days_list.size(); i++)
            {
                Log.e(TAG, "onCreate: DaysSelected "+days_list.get(i));
            }
        }

        cbSignin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    signIn="1";
                }
                else
                {
                    signIn="0";
                }
                 SellectedDaysSize=0;
                 value=0;
                 total=0;
                 newAmount=0;

                calculateFare();
            }
        });

        cbSignOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    signOut="1";
                }
                else {
                    signOut="0";
                }

                SellectedDaysSize=0;
                value=0;
                total=0;
                newAmount=0;
                calculateFare();
            }
        });

        calculateFare();
    }

    private void checkFare()
    {
        ApiCall="2";
        HashMap<String, String> param = new HashMap<>();
        param.put("booking_amount", String.valueOf(newAmount));
        param.put("booking_type", bookingType);
        Log.e(TAG, "checkFare:Params "+param.toString() );
        api(param,this,token,29);
    }

    ProgressDialog progress;

    public boolean checkValdations()
    {
        if (generatedToken.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Choose card for payment", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick({R.id.tvContinue,R.id.img_back_btn,R.id.ivEnd})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvContinue:

                if (checkValdations())
                {
                    //for recurring
                    if (bookingType.equalsIgnoreCase("1"))
                    {
                        checkFare();
                    }
                    else if (bookingType.equalsIgnoreCase("2"))
                    {
                        callConfirmBooking(String.valueOf(newAmount));
                    }
                }
                break;

            case R.id.img_back_btn:
                Intent we=new Intent(ChooseCard.this, ParentHome.class);
                startActivity(we);
                break;

            case R.id.ivEnd:
             /*   Intent intent=new Intent(this, AddCard.class);
                intent.putExtra("key","book");
                startActivity(intent);*/

                Intent intent=new Intent(this, AddCard.class);
                intent.putExtra("key","book");
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("ride_type",bookingType);
                if (bookingType.equalsIgnoreCase("1"))
                {
                    Log.e(TAG, "onClick: days_list "+days_list.size());
                    Log.e(TAG, "onClick: end_date "+end_date);
                    intent.putExtra("end_date",end_date);
                    intent.putExtra("days_list",(Serializable) days_list);
                }
                startActivityForResult(intent,50);

                break;
        }
    }

    int SellectedDaysSize=0;
    double value=0;
    int total=0;
    double newAmount=0;

    public void calculateFare()
    {
        for (int i = 0; i < riderList.size(); i++)
        {
            Log.e(TAG, "calculateFare:Est "+riderList.get(i).getEstPrice());
            value=value+Double.parseDouble(riderList.get(i).getEstPrice());
        }

        if (bookingType.equalsIgnoreCase("1"))//for recurring
        {

            SellectedDaysSize=days_list.size();
            Log.e(TAG, "calculateFare: SellectedDaysSize "+SellectedDaysSize);
            double size=Double.parseDouble(String.valueOf(SellectedDaysSize));
            Log.e(TAG, "calculateFare: SizeDays "+size);
            amount=size*value;
            Log.e(TAG, "calculateFare: amount "+amount);
        }

        else
        {
            amount=value;
        }

        Log.e(TAG, "calculatedFare: "+value);

        if (signIn.equalsIgnoreCase("1") && signOut.equalsIgnoreCase("1"))
        {
            total=10;
        }
        else if (signIn.equalsIgnoreCase("0") && signOut.equalsIgnoreCase("1"))
        {
            total=5;
        }
        else if (signOut.equalsIgnoreCase("0") && signIn.equalsIgnoreCase("1"))
        {
            total=5;
        }
        else if (signOut.equalsIgnoreCase("0") && signIn.equalsIgnoreCase("0"))
        {
            total=0;
        }

        double totalPrice=Double.parseDouble(String.valueOf(total));

        newAmount=totalPrice+amount;

        String valAmount="";
        valAmount= new DecimalFormat("##.##").format(newAmount);

//        tvContinue.setText("Pay Amount : $"+String.valueOf(newAmount));
        tvContinue.setText("Pay Amount : $"+valAmount);
    }

    public void callConfirmBooking(String newBookAmount)
    {
        ApiCall="1";
        HashMap<String,String> param=new HashMap<>();

        GsonBuilder gsonb11 = new GsonBuilder();
        Gson gson11 = gsonb11.create();

        List<ConfirmBookingMain.RideDetailBean> rideDetailBeanList = new ArrayList<>();
        for (int i = 0; i < riderList.size(); i++)
        {
            ConfirmBookingMain.RideDetailBean rideDetailBean = new ConfirmBookingMain.RideDetailBean();

            rideDetailBean.setRider_id(riderList.get(i).getId());
            rideDetailBean.setPriority_pick(String.valueOf(riderList.get(i).getPickPriority()));
            rideDetailBean.setPriority_drop(String.valueOf(riderList.get(i).getDropPriority()));
            rideDetailBean.setRider_drop_location_id(riderList.get(i).getDrop_saved_id());
            rideDetailBean.setRider_pick_location_id(riderList.get(i).getPick_saved_id());
            rideDetailBean.setPick_up_instruction(riderList.get(i).getPick_instructions());
            rideDetailBean.setDrop_off_instruction(riderList.get(i).getDrop_instructions());

            if (riderList.get(i).isSignIn())
            {
                rideDetailBean.setKids_sign_in("1");
            }
            else
            {
                rideDetailBean.setKids_sign_in("0");
            }

            if (riderList.get(i).isSignOut())
            {
                rideDetailBean.setKids_sign_up("1");
            }
            else
            {
                  rideDetailBean.setKids_sign_up("0");
            }

            rideDetailBeanList.add(rideDetailBean);
        }

        ConfirmBookingMain confirmBookingModel = new ConfirmBookingMain();
        confirmBookingModel.setRide_detail(rideDetailBeanList);
        confirmBookingModel.setBooking_type(bookingType);
        confirmBookingModel.setToken(generatedToken);
        confirmBookingModel.setCard_id(cardId);
     /*   confirmBookingModel.setSign_in(signIn);
        confirmBookingModel.setSign_up(signOut);*/
        confirmBookingModel.setTime_of_ride(book_time);
        confirmBookingModel.setBooking_date(book_date);
        confirmBookingModel.setCurrency("USD");
//        confirmBookingModel.setAmount(String.valueOf(newAmount));
        confirmBookingModel.setAmount(String.valueOf(newBookAmount));
      /*  confirmBookingModel.setLattitude("30.7046");
        confirmBookingModel.setLongitude("76.7179"); */
        confirmBookingModel.setLattitude(String.valueOf(lattitude));
        confirmBookingModel.setLongitude(String.valueOf(longitude));
        confirmBookingModel.setParent_id(sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));

        //for recurring rides
        if (bookingType.equalsIgnoreCase("1"))
        {
//            confirmBookingModel.setBooking_end_date("2019-07-24");
            confirmBookingModel.setBooking_end_date(end_date);

            List<ConfirmBookingMain.RideRecurringDaysListBean> recurringDaysListBeans=new ArrayList<>();
            for (int h = 0; h <days_list.size() ; h++)
            {
                ConfirmBookingMain.RideRecurringDaysListBean rideRecurringDaysListBean=new
                        ConfirmBookingMain.RideRecurringDaysListBean();
                rideRecurringDaysListBean.setDay(days_list.get(h));
                recurringDaysListBeans.add(rideRecurringDaysListBean);
            }
            confirmBookingModel.setRideRecurringDaysList(recurringDaysListBeans);
        }

        param.put("abc", gson11.toJson(confirmBookingModel));
        Log.e(TAG, "callConfirmBooking: Params "+param.toString());

        api(param,this,token1,23);

/*        if (bookingType.equalsIgnoreCase("2"))
        {
            param.put("booking_type",bookingType);//1-recurring,2-simple
            param.put("token",generatedToken);
            param.put("currency","USD");
            param.put("amount",String.valueOf(amount));
            param.put("lattitude","30.7046");
            param.put("longitude","76.7179");

            param.put("parent_id",sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));

            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();

            List<ConfirmBookingModel.RideDetailBean> rideDetailBeanList = new ArrayList<>();
            for (int i = 0; i < riderList.size(); i++)
            {
                ConfirmBookingModel.RideDetailBean rideDetailBean = new ConfirmBookingModel.RideDetailBean();

                rideDetailBean.setRider_id(riderList.get(i).getId());
                rideDetailBean.setPriority_pick(String.valueOf(riderList.get(i).getPickPriority()));
                rideDetailBean.setPriority_drop(String.valueOf(riderList.get(i).getDropPriority()));
                rideDetailBean.setRider_drop_location_id(riderList.get(i).getDrop_saved_id());
                rideDetailBean.setRider_pick_location_id(riderList.get(i).getPick_saved_id());
                rideDetailBeanList.add(rideDetailBean);
            }

            ConfirmBookingModel confirmBookingModel = new ConfirmBookingModel();
            confirmBookingModel.setRide_detail(rideDetailBeanList);

            param.put("ride_detail", gson.toJson(confirmBookingModel));
            Log.e(TAG, "callConfirmBooking: Params "+param.toString());

            api(param,this,token1,15);
        }

      else  if (bookingType.equalsIgnoreCase("1"))
        {
            GsonBuilder gsonb11 = new GsonBuilder();
            Gson gson11 = gsonb11.create();

            List<ConfirmBookingMain.RideDetailBean> rideDetailBeanList = new ArrayList<>();
            for (int i = 0; i < riderList.size(); i++)
            {
                ConfirmBookingMain.RideDetailBean rideDetailBean = new ConfirmBookingMain.RideDetailBean();

                rideDetailBean.setRider_id(riderList.get(i).getId());
                rideDetailBean.setPriority_pick(String.valueOf(riderList.get(i).getPickPriority()));
                rideDetailBean.setPriority_drop(String.valueOf(riderList.get(i).getDropPriority()));
                rideDetailBean.setRider_drop_location_id(riderList.get(i).getDrop_saved_id());
                rideDetailBean.setRider_pick_location_id(riderList.get(i).getPick_saved_id());
                rideDetailBeanList.add(rideDetailBean);
            }


            ConfirmBookingMain confirmBookingModel = new ConfirmBookingMain();
            confirmBookingModel.setRide_detail(rideDetailBeanList);

            confirmBookingModel.setBooking_type(bookingType);
            confirmBookingModel.setToken(generatedToken);
            confirmBookingModel.setCurrency("USD");
            confirmBookingModel.setAmount(String.valueOf(amount));
            confirmBookingModel.setLattitude("30.7046");
            confirmBookingModel.setLongitude("76.7179");

            confirmBookingModel.setParent_id(sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));

            confirmBookingModel.setBooking_end_date("2019-07-31");

            List<ConfirmBookingMain.RideRecurringDaysListBean> recurringDaysListBeans=new ArrayList<>();
            for (int h = 0; h <days_list.size() ; h++) {
                ConfirmBookingMain.RideRecurringDaysListBean rideRecurringDaysListBean=new ConfirmBookingMain.RideRecurringDaysListBean();

                rideRecurringDaysListBean.setDay(days_list.get(h));
                recurringDaysListBeans.add(rideRecurringDaysListBean);
            }

            confirmBookingModel.setRideRecurringDaysList(recurringDaysListBeans);

            param.put("abc", gson11.toJson(confirmBookingModel));
            Log.e(TAG, "callConfirmBooking: Params "+param.toString());

            api(param,this,token1,23);
        }*/
    }
    public void callCardList()
    {
        new RetrofitService(this, this, Constant.API_PARENT_CARD_LIST ,
                105, 1,"1").callService(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        callCardList();
    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        PaymentListAdapter paymentListAdapter=new PaymentListAdapter(this,list,"c");
        recycler.setAdapter(paymentListAdapter);

        paymentListAdapter.onItemSelectedListener(new PaymentListAdapter.myclickListener()
        {
            @Override
            public void onDeleteClick(int layoutPosition, View view)
            {

            }

            @Override
            public void onTickClick(int layoutPosition, View view)
            {
              /*  for (int i = 0; i <list.size() ; i++) {

                    if (i==layoutPosition)
                    {
                        list.get(layoutPosition).setFlag(true);
                        generatedToken=list.get(layoutPosition).getStripe_token();
                        Log.e(TAG, "onTickClick: "+generatedToken );
                        cardId=list.get(layoutPosition).getCard_id();
                        Log.e(TAG, "onTickClick:CardId "+cardId );
                    }
                    else {
                        list.get(layoutPosition).setFlag(false);
                    }
                    paymentListAdapter.notifyDataSetChanged();
                }
*/
               /* if (list.get(layoutPosition).isFlag())
                {
                    list.get(layoutPosition).setFlag(false);
                }
                else
                {
                    list.get(layoutPosition).setFlag(true);
                    generatedToken=list.get(layoutPosition).getStripe_token();
                    Log.e(TAG, "onTickClick: "+generatedToken );
                    cardId=list.get(layoutPosition).getCard_id();
                    Log.e(TAG, "onTickClick:CardId "+cardId );
                }
                paymentListAdapter.notifyDataSetChanged();*/


               /* list.get(layoutPosition).setFlag(list.get(layoutPosition).isFlag()?false:true);
                paymentListAdapter.notifyDataSetChanged();*/
            }
        });
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:

                Log.e("ResponseCardList ",response);

                try
                {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        tvNoData.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);

                        list.clear();

                        JSONArray Array = jsonObject.getJSONArray("cards");

                        if (Array.length() > 0)
                        {
                            for (int i = 0; i < Array.length(); i++)
                            {

                                JSONObject object = Array.getJSONObject(i);

                                CardList cardList=new CardList();

                                cardList.setCard_id(object.getString("id"));
                                cardList.setNumber(object.getString("card_number"));
                                cardList.setExpiry(object.getString("expiry_date"));
                                cardList.setCvv(object.getString("cvv"));
                                cardList.setStripe_token(object.getString("stripe_token"));
                                cardList.setName(object.getString("card_holder_name"));
                                cardList.setFlag(false);

                                list.add(cardList);

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
                catch
                (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;
        }

    }

    double lattitude, longitude;

    private void getLatLong()
    {
        Log.e("Login", "getLatLong: " + "GetLocation");
        new GetCurrentLocation(this,this);
    }

    @Override
    public void currentLatLng(double lat, double lng)
    {
        if (lat == 0.0)
        {
            getLatLong();
        }
        else
        {
            this.lattitude = lat;
            this.longitude = lng;
        }
        Log.e("currentLatLngLattttt ",lattitude+"");
        Log.e( "currentLatLngLongitude ",longitude+"");
    }


    /*Todo booking response
    * when there is any pending amount
    * {"status":false,"message":"Sorry, You have to clear your pending payment First.",
    * "data":[{"id":810,"parent_id":73,"driver_id":30,"latitude":"0.0","longitude":"0.0","booking_type":2,
    * "booking_days":"","booking_end_date":null,
    * "booking_status":4,"datetime":null,
    * "created_at":"2019-08-bb 05:14:19","updated_at":"2019-08-bb 05:15:aa",
    * "time_of_ride":"aa:23:45","sign_in":0,"sign_up":1,
    * "booking_amount":127.21,"card_id":28,"pending_amount":1234,"amount_paid":0}]}
    */

    String ApiCall="2";
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e(TAG, "onResponse: GeneralBooking<< "+generalResponse);

        try {
            if (ApiCall.equalsIgnoreCase("1"))
            {
                JSONObject jsonObject = generalResponse.getResponse_object();
                Log.e("confirmBookingJSON ", jsonObject + "");
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {
                    JSONObject data=jsonObject.getJSONObject("data");

                    ArrayList<DriverBookingModel> driverList=new ArrayList<>();
                    driverList.clear();
                    DriverBookingModel bookingModel=new DriverBookingModel();

                    bookingModel.setDriverId(data.getString("id"));
                    bookingModel.setName(data.getString("name"));
                    bookingModel.setEmail(data.getString("email"));
                    bookingModel.setImage(data.getString("image"));
                    bookingModel.setVehicle_name(data.getString("model"));
                    bookingModel.setPhoneNumber(data.getString("phone_no"));
                    bookingModel.setRide_id(jsonObject.getString("booking_id"));
                    bookingModel.setVehicle_make(data.getString("make"));
                    bookingModel.setVehicle_image(data.getString("vehicle_image"));
                    bookingModel.setLicence_number(data.getString("licence_number"));
                    bookingModel.setIssue_state(data.getString("state_id"));

                  /*  bookingModel.setKids_in(jsonObject.getString("sign_in"));
                    bookingModel.setKids_out(jsonObject.getString("sign_up"));*/

                    bookingModel.setBooking_time(jsonObject.getString("time_of_ride"));

                    driverList.add(bookingModel);

                    Intent intent=new Intent(ChooseCard.this, LiveTrackBooking.class);
                    intent.putExtra("driver_list",(Serializable) driverList);
                    intent.putExtra("rider_list",(Serializable) riderList);
                    startActivity(intent);


//                bookingModel.setDriverId(data.getString("id"));
                }

                else if (jsonObject.getString("status").equalsIgnoreCase("pending_payment"))
                {
                    JSONArray data=jsonObject.getJSONArray("data");
                    String message="";

                    for (int i = 0; i <data.length(); i++)
                    {
                       JSONObject object=data.getJSONObject(i);

                       message="Sorry you have to clear your pending payment amount first (i.e $ "+
                               object.getString("pending_amount")+") to proceed further";

                       callPaymentAlert(message, object.getString("id"),
                               object.getString("pending_amount"));
                    }
                }
                else
                {
                    callMessageAlert(jsonObject.getString("message"));
//                    Toast.makeText(this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                }
            }

            else if (ApiCall.equalsIgnoreCase("2"))
            {
                JSONObject jsonObject = generalResponse.getResponse_object();
                Log.e("FareCheckREsponse ", jsonObject + "");
                String newBookAmount="";
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {
                    String amount=jsonObject.getString("discounted_amount");
                    Double price=Double.parseDouble(amount);

                    newBookAmount= new DecimalFormat("##.##").format(price);

                    //when some discount is applied
                    callConfirmBooking(newBookAmount);
                }
                else
                 {//if no discount is applied
                    callConfirmBooking(String.valueOf(newAmount));
                }
            }


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void callMessageAlert(String message)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        final TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);
        final RelativeLayout rl = (RelativeLayout) dialogView.findViewById(R.id.rl);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        rl.setVisibility(View.GONE);
        tvOk.setVisibility(View.VISIBLE);

        tvMsg.setText(message);
        tvOk.setText("OK");

        tvOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
                Intent intent=new Intent(ChooseCard.this,ParentHome.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.show();
    }


    public void callPaymentAlert(String message,String bookingId,String amount)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        final TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);
        final RelativeLayout rl = (RelativeLayout) dialogView.findViewById(R.id.rl);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        rl.setVisibility(View.GONE);
        tvOk.setVisibility(View.VISIBLE);

        tvMsg.setText(message);
        tvOk.setText("Make Payment");

        tvOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(ChooseCard.this, MakePayment.class);
                intent.putExtra("key","book");
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("ride_type",bookingType);
                intent.putExtra("booking_id",bookingId);
                intent.putExtra("booking_amount",amount);
                if (bookingType.equalsIgnoreCase("1"))
                {
                    Log.e(TAG, "onClick: days_list "+days_list.size());
                    Log.e(TAG, "onClick: end_date "+end_date);
                    intent.putExtra("end_date",end_date);
                    intent.putExtra("days_list",(Serializable) days_list);
                }
                startActivityForResult(intent,300);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
        Log.e(TAG, "onFailure: "+t.getMessage());
    }

}
