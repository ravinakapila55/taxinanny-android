package com.taxi.nanny.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.DriverBookingModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.req_model.ConfirmBookingMain;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.location.GPSTracker;
import com.taxi.nanny.utils.realTimeTrack.GetCurrentLocation;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.LiveTrackBooking;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StripePayment extends BaseActivity implements Callback<ResponseBody>,
        GetCurrentLocation.LatLngResponse
{
    @BindView(R.id.card_input_widget)
    CardMultilineWidget card_input_widget;

    @BindView(R.id.tvSave)
    TextView tvSave;

    String generatedToken="";
    Card card;

    ArrayList<RiderListModel> riderList;
    SharedPrefUtil sharedPrefUtil;
    String token1;
    String bookingType="";
    String end_date="";
    double amount=0;

    GPSTracker gpsTracker1;

    public static final String TAG=StripePayment.class.getSimpleName();
//    private static final String PUBLISHABLE_KEY = "pk_test_NE6jrlmS0s6m39LMpFepdb3R00L2m7KnBh";
//    private static final String PUBLISHABLE_KEY = "pk_test_NE6jrlmS0s6m39LMpFepdb3R00L2m7KnBh";
    private static final String PUBLISHABLE_KEY = "pk_test_MaQhP2b8XqG9R7PTL6VUmipt";
    private static final String SECRET_KEY = "sk_test_lC0U9YV8dratMHpIrJ8ObVrK00QUgucAmv";

    ArrayList<String> days_list;

    @Override
    protected int getContentId()
    {
        return R.layout.stripe;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Make Payment");
        sharedPrefUtil= SharedPrefUtil.getInstance();
        riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
        days_list=(ArrayList<String>) getIntent().getSerializableExtra("days_list");
        bookingType=getIntent().getExtras().getString("ride_type");
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

            for (int i = 0; i < days_list.size(); i++)
            {
                Log.e(TAG, "onCreate: DaysSelected "+days_list.get(i));
            }
        }

        calculateFare();
    }

    private void getLatLong()
    {
        Log.e("Login", "getLatLong: " + "GetLocation");
        new GetCurrentLocation(this,this);
    }

    @Override
    public void currentLatLng(double lat, double lng) {
        if (lat == 0.0) {
            getLatLong();
        } else {
            this.lattitude = lat;
            this.longitude = lng;
        }
        Log.e(TAG, "currentLatLngLattttt "+lattitude );
        Log.e(TAG, "currentLatLngLongitude "+longitude );
    }


    ProgressDialog progress;

    @OnClick({R.id.tvSave,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvSave:

                card = card_input_widget.getCard();

              /*  Log.e(TAG, "onClick: CardStripeNumber "+card.getNumber());
                Log.e(TAG, "onClick: CardStripeExpiry "+card.getExpMonth()+"year "+card.getExpYear());
                Log.e(TAG, "onClick: CardStripeCvc "+card.getCVC());*/

                if (checkValidations())
                {
                    Log.e(TAG, "onClick: InsideCheckValidations "+"inside" );
                    progress = new ProgressDialog(this);
                    progress.setMessage("Processing...");
                    progress.setCancelable(false);
                    progress.show();
                    createToken();
//                    Toast.makeText(this, "Card Added Successfully", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

            case R.id.img_back_btn:
                finish();
                break;
        }
    }


    public void calculateFare()
    {
        double value=0;

        for (int i = 0; i < riderList.size(); i++)
        {
            Log.e(TAG, "calculateFare:Est "+riderList.get(i).getEstPrice());
            value=value+Double.parseDouble(riderList.get(i).getEstPrice());
        }

        if (bookingType.equalsIgnoreCase("1"))//for recurring
        {
            int SellectedDaysSize=0;
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
        tvSave.setText("Pay Amount : $"+String.valueOf(amount));

       // callConfirmBooking();
    }

    public void callConfirmBooking()
    {

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
            rideDetailBeanList.add(rideDetailBean);
        }

        ConfirmBookingMain confirmBookingModel = new ConfirmBookingMain();
        confirmBookingModel.setRide_detail(rideDetailBeanList);
        confirmBookingModel.setBooking_type(bookingType);
        confirmBookingModel.setToken(generatedToken);
        confirmBookingModel.setCurrency("USD");
        confirmBookingModel.setAmount(String.valueOf(amount));
      /*  confirmBookingModel.setLattitude("30.7046");
        confirmBookingModel.setLongitude("76.7179"); */

        confirmBookingModel.setLattitude(String.valueOf(lattitude));
        confirmBookingModel.setLongitude(String.valueOf(longitude));
        confirmBookingModel.setParent_id(sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));

        //for recurring rides
        if (bookingType.equalsIgnoreCase("1"))
        {
            confirmBookingModel.setBooking_end_date("2019-07-31");
            List<ConfirmBookingMain.RideRecurringDaysListBean> recurringDaysListBeans=new ArrayList<>();
            for (int h = 0; h <days_list.size() ; h++)
            {
                ConfirmBookingMain.RideRecurringDaysListBean rideRecurringDaysListBean=new ConfirmBookingMain.RideRecurringDaysListBean();
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

    public boolean checkValidations()
    {
        if(!card_input_widget.validateAllFields())
        {
            Toast.makeText(this, "Please enter card details", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }

    public void onAddCard(String cardNumber, Integer cardExpMonth,Integer cardExpYear, String cardCVC)
    {
        final Card card = Card.create(cardNumber, cardExpMonth, cardExpYear, cardCVC);
        card.validateNumber();
        card.validateCVC();
        card.validateExpiryDate();
    }

    public void callCharge()
    {
        final Map<String, Object> params = new HashMap<>();
        params.put("amount", 999);
        params.put("currency", "usd");
        params.put("description", "Example charge");
        params.put("source", generatedToken);
//        Charge charge = Charge.create(params);
    }

    public void createToken()
    {
        Log.e(TAG, "onClick: insideCreateToken "+"inside" );

        final Stripe stripe = new Stripe(this, PUBLISHABLE_KEY);
//        final Card card = Card.create("4242424242424242", 12, 2020, "123");
        stripe.createToken(card,new TokenCallback()
                {
                    public void onSuccess(@NonNull Token token)
                    {
                        // Send token to your server
                        Log.e(TAG, "onSuccess:TokenType "+token.getType());
                        Log.e(TAG, "onSuccess:TokenID "+token.getId());//generated token here
                        generatedToken= String.valueOf(token.getId());
                        Log.e(TAG, "onSuccess:generatedToken "+generatedToken);
                        progress.dismiss();
                        callConfirmBooking();
                    }

                    public void onError(@NonNull Exception error)
                    {
                        // Show localized error message
                        Log.e(TAG, "Onerror "+error);
                        Toast.makeText(StripePayment.this,"Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*{"status":true,"message":"Driver Notified","data":{"id":30,"name":"Dnndjd Dhhdhd","email":"pp@g.com","phone_no":"9595959595","device_token":"f3_PA2z-yak:APA91bGdr3nWzTB-YlZx8vykEohLy7pYv3AUXjhPja72DJMaOsp_rQkBhqgSRTYtdJN1arB6faWtU6xlyl7BB6aB0uMlN_lSDzPmEI-MRIXjNBlZzU77-PVxFx3gQmZyXCpeZSE-ac4M","device_id":"d3fc0417ca079363","distance":0.7574709542038278,"vehicle_name":null,"vehicle_number":null,"vehicle_color":null},"drop_loc":{"drop_location_name":"C-DAC Mohali, Phase 8, Industrial Area, Sector 73, Sahibzada Ajit Singh Nagar, Punjab, India","drop_location_lat":"30.7084673","drop_location_lng":"76.704362"},"booking_id":1,"ridestatus":1,"fcm":{"multicast_id":6575690490231879830,"success":1,"failure":0,"canonical_ids":0,"results":[{"message_id":"0:1563428493111989%633bc2e3f9fd7ecd"}]}}*/

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e(TAG, "onResponse: GeneralBooking<< "+generalResponse);

        try {
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
                bookingModel.setVehicle_name(data.getString("vehicle_name"));
                bookingModel.setPhoneNumber(data.getString("phone_no"));
                bookingModel.setRide_id(jsonObject.getString("booking_id"));

                driverList.add(bookingModel);

                Intent intent=new Intent(StripePayment.this, LiveTrackBooking.class);
                intent.putExtra("driver_list",(Serializable) driverList);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
//                bookingModel.setDriverId(data.getString("id"));
            }
            else
            {
                Toast.makeText(this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
        Log.e(TAG, "onResponse: onFailure<< "+t.toString());
    }

    double lattitude, longitude;

}
