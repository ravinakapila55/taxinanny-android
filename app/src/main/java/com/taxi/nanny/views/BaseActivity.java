package com.taxi.nanny.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.APIRequest;
import com.taxi.nanny.model.DistanceModel;
import com.taxi.nanny.req_model.ConfirmBookingMain;
import com.taxi.nanny.req_model.ConfirmBookingModel;
import com.taxi.nanny.req_model.DeleteRiderListModel;
import com.taxi.nanny.req_model.EstimateFTModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.driver.DriverAccount;
import com.taxi.nanny.views.driver.DriverHome;
import com.taxi.nanny.views.driver.earning.DriverEarning;
import com.taxi.nanny.views.driver.schedule.DriverSchudule;
import com.taxi.nanny.views.login_section.dialog.InternetErrorDialog;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Callback;

public abstract class BaseActivity extends AppCompatActivity implements RetrofitResponse
{
    protected Unbinder binder;
    protected ProgressDialog progressDialog;
    private static final String[] tabArray = {"HOME", "EARNINGS", "Rating","ACCOUNT","ONLINE"};//Tab title array
    private static final Integer[] tabIcons = {R.drawable.home_header,
            R.drawable.earning,
            R.drawable.home_header,R.drawable.account,R.drawable.user};//Tab icons array

    GsonBuilder gsonb;
    Gson gson;

    String token = "";
    SharedPrefUtil sharedPrefUtil;

    protected abstract int getContentId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getContentId() != 0)
        {
            setContentView(getContentId());
            binder = ButterKnife.bind(this);
        }
        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");
        gsonb  = new GsonBuilder();
        gson = gsonb.create();
    }

    protected void setHeaderText(String headerText)
    {
        TextView txt_title=findViewById(R.id.txt_title);
        txt_title.setText(headerText);
    }

    TabLayout tabLayout;
    TabLayout.Tab tab1;
    String tabValue="";

    protected void setTab()
    {
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            tabLayout.addTab(createTab("Home",getDrawable(R.drawable.home_header),tabLayout));
            tabLayout.addTab(createTab("Earning",getDrawable(R.drawable.earning),tabLayout));
            tabLayout.addTab(createTab("Schedule",getDrawable(R.drawable.schedule),tabLayout));
            tabLayout.addTab(createTab("Account",getDrawable(R.drawable.account),tabLayout));
//            tabLayout.addTab(createTab("Offline",getDrawable(R.drawable.btn),tabLayout));

            if (sharedPrefUtil.getString(SharedPrefUtil.DRIVER_STATUS).equalsIgnoreCase("0"))
            {
                tabLayout.addTab(createTab("Offline",getDrawable(R.drawable.btn),tabLayout));
            }
            else if (sharedPrefUtil.getString(SharedPrefUtil.DRIVER_STATUS).equalsIgnoreCase("1"))
            {
                tabLayout.addTab(createTab("Online",getDrawable(R.drawable.btn_active),tabLayout));
            }

            Log.e("TabCount ",tabLayout.getTabCount()+"");
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                tab1=tab;

                Log.e("TabAction ","TabSelected "+tab.getText()+"");

                if (tab.getPosition()==0)
                {
                    Intent intent=new Intent(BaseActivity.this, DriverHome.class);
                    startActivity(intent);
                }

                else  if (tab.getPosition()==1)
                {
                    Intent intent=new Intent(BaseActivity.this, DriverEarning.class);
                    startActivity(intent);
                }
                else if (tab.getPosition()==2)
                {
                    Intent intent=new Intent(BaseActivity.this, DriverSchudule.class);
//                    Intent intent=new Intent(BaseActivity.this, DriverRating.class);
                    startActivity(intent);
                }
                else if (tab.getPosition()==3)
                {
//                    Intent intent=new Intent(BaseActivity.this, DriverSchudule.class);
                    Intent intent=new Intent(BaseActivity.this, DriverAccount.class);
                    startActivity(intent);
                }

                else if (tab.getPosition()==4)
                {
                   // callStatusApi("1");
                    Log.e("TabSelectedText ",tab.getText().toString().trim());
                    if (tab.getText().toString().trim().equalsIgnoreCase("Online"))
                    {
                        tabValue="1";
                        callStatusApi("0");
                    }
                    else if (tab.getText().toString().trim().equalsIgnoreCase("Offline"))
                    {
                        tabValue="0";
                        callStatusApi("1");
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
                Log.e("TabAction ","onTabUnselected "+tab.getText()+"");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
                Log.e("TabAction ","onTabReselected "+tab.getText()+"");

                if (tab.getPosition()==0)
                {
                    Intent intent=new Intent(BaseActivity.this, DriverHome.class);
                    startActivity(intent);
                }

                else  if (tab.getPosition()==1)
                {
                    Intent intent=new Intent(BaseActivity.this, DriverEarning.class);
                    startActivity(intent);
                }

                else  if (tab.getPosition()==2)
                {
                    Intent intent=new Intent(BaseActivity.this, DriverSchudule.class);
//                    Intent intent=new Intent(BaseActivity.this, DriverRating.class);
                    startActivity(intent);
                }

                else if (tab.getPosition()==3)
                {
//                    Intent intent=new Intent(BaseActivity.this, DriverSchudule.class);
                    Intent intent=new Intent(BaseActivity.this, DriverAccount.class);
                    startActivity(intent);
                }

               else if (tab.getPosition()==4)
                {
                    // callStatusApi("1");
                    Log.e("TabSelectedText ",tab.getText().toString().trim());
                    if (tab.getText().toString().trim().equalsIgnoreCase("Online"))
                    {
                        tabValue="1";
                        callStatusApi("0");
                    }

                    else if (tab.getText().toString().trim().equalsIgnoreCase("Offline"))
                    {
                        tabValue="0";
                        callStatusApi("1");
                    }
                }
            }
        });
    }

    public void callStatusApi(String status)
    {
        try
        {
            JSONObject jsonObject=new JSONObject();

            jsonObject.put("driver_id",sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
            jsonObject.put("status",status);

            new RetrofitService(this, this, Constant.API_DRIVER_STATUS,jsonObject,
                    500, 2,"1").
                    callService(true);

            Log.e("ChangeStatus ",jsonObject.toString());
/*
            if (tabValue.equalsIgnoreCase("1"))
            {
                tab1.setText("Offline");
                tab1.setIcon(getResources().getDrawable(R.drawable.btn));
                sharedPrefUtil.saveString(SharedPrefUtil.DRIVER_STATUS,"0");
            }
            else
            {
                tab1.setText("Online");
               tab1.setIcon(getResources().getDrawable(R.drawable.btn_active));
                sharedPrefUtil.saveString(SharedPrefUtil.DRIVER_STATUS,"1");
            }*/
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private TabLayout.Tab createTab(String text, Drawable icon,TabLayout tabLayout)
    {
        TabLayout.Tab tab = tabLayout.newTab().setText(text).setIcon(icon).setCustomView(R.layout.custom_tab);
        // remove imageView bottom margin
        if (tab.getCustomView() != null)
        {
            ImageView imageView = (ImageView) tab.getCustomView().findViewById(android.R.id.icon);
            ViewGroup.MarginLayoutParams lp = ((ViewGroup.MarginLayoutParams) imageView.getLayoutParams());
            lp.bottomMargin = 0;
            imageView.requestLayout();
        }
        return tab;
    }

    // show keyboard
    protected void showKeyBoard(View view)
    {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    // hide keyboard
    protected void hideKeyboard(View view)
    {
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getCurrentDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    protected void showMessage(String message, View coordinatorLayout)
    {
        SpannableStringBuilder ssb = new SpannableStringBuilder()
                .append(message);

        ssb.setSpan(new ForegroundColorSpan(Color.WHITE),
                0,
                message.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        Snackbar.make(
                coordinatorLayout,
                ssb,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    protected boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    protected void api(HashMap<String, String> param,
                       Callback<ResponseBody> responseBodyCallback,
                       String token ,int TYPE_REQUEST)
    {
        if (isNetworkConnected())
        {
            progressDialog = APIRequest.getProgress(this);

            switch (TYPE_REQUEST)
            {
                case 0:
                    {
                    APIRequest.apiInterface(token).callRegister(param).enqueue(responseBodyCallback);
                    break;
                    }
                case 1:
                 {
                    APIRequest.apiInterface(token).getVehicleType().enqueue(responseBodyCallback);
                    break;
                 }
                case 2:
                 {
                    APIRequest.apiInterface(token).calllogin(param).enqueue(responseBodyCallback);
                    break;
                 }
                case 3:
                 {
                    APIRequest.apiInterface(token).calllogin(param).enqueue(responseBodyCallback);
                    break;
                 }
                 case 4:
                     {
                    APIRequest.apiInterface(token).getRiderList().enqueue(responseBodyCallback);
                    break;
                }case 5:{
                    APIRequest.apiInterface(token).callAddRider(param).enqueue(responseBodyCallback);
                    break;
                }case 6:{
                    APIRequest.apiInterface(token).callAddVehicle(param).enqueue(responseBodyCallback);
                    break;
                }case 7:{
                    APIRequest.apiInterface(token).callAddDrivingLicence(param).enqueue(responseBodyCallback);
                    break;
                }case 8:{
                    APIRequest.apiInterface(token).callAddVehicleInsurance(param).enqueue(responseBodyCallback);
                    break;
                }case 9:{
                    APIRequest.apiInterface(token).getCountryList().enqueue(responseBodyCallback);
                    break;
                }case 10:{
                APIRequest.apiInterface(token).saveRiderLocation(param).enqueue(responseBodyCallback);
                    break;
                }case 11:{
                EstimateFTModel estimateFTModel = gson.fromJson(param.get("ride_detail"),EstimateFTModel.class);
                APIRequest.apiInterface(token).getEstimatedTimeFare(estimateFTModel).enqueue(responseBodyCallback);
                    break;
                }
                case 12:
                {
                    APIRequest.apiInterface(token).saveDriverLocation(param).enqueue(responseBodyCallback);
                    break;
                }
                case 13:
                {
                    APIRequest.apiInterface(token).saveDriverStatus(param).enqueue(responseBodyCallback);
                    break;
                }
                case 14:
                {
                    DeleteRiderListModel deleteRiderListModel = gson.fromJson(param.get("ride_detail"),DeleteRiderListModel.class);
                    APIRequest.apiInterface(token).getDeleteRider(deleteRiderListModel).enqueue(responseBodyCallback);
                    break;
                }
                case 15:
                {
                    ConfirmBookingModel confirmBookingModel = gson.fromJson(param.get("ride_detail"), ConfirmBookingModel.class);
                    APIRequest.apiInterface(token).callConfirmBooking(confirmBookingModel).enqueue(responseBodyCallback);
                    break;
                }
                case 23:
                {
                    ConfirmBookingMain confirmBookingModel = gson.fromJson(param.get("abc"), ConfirmBookingMain.class);
                    APIRequest.apiInterface(token).callConfirmBookingMAin(confirmBookingModel).enqueue(responseBodyCallback);
                    break;
                }

                case 16:
                    {
                    APIRequest.apiInterface(token).acceptRequestByDriver(param).enqueue(responseBodyCallback);
                    break;
                }case 17:
                    {
                    APIRequest.apiInterface(token).rejectRequestByDriver(param).enqueue(responseBodyCallback);
                    break;
                }case 18:
                    {
                    APIRequest.apiInterface(token).callArriveDriver(param).enqueue(responseBodyCallback);
                    break;
                }case 19:
                    {
                    APIRequest.apiInterface(token).callStartTrip(param).enqueue(responseBodyCallback);
                    break;
                }case 20:
                {
                    APIRequest.apiInterface(token).callCompleteTrip(param).enqueue(responseBodyCallback);
                    break;
                }case 21:
                 {
                    APIRequest.apiInterface(token).callEditFavouriteLocation(param).enqueue(responseBodyCallback);
                    break;
                }case 22:
                {
                    APIRequest.apiInterface(token).callEditProfile(param).enqueue(responseBodyCallback);
                    break;
                }case 26:
                {
                    APIRequest.apiInterface(token).callEditVehicle(param).enqueue(responseBodyCallback);
                    break;
                }case 27:
                {
                    APIRequest.apiInterface(token).callAddCard(param).enqueue(responseBodyCallback);
                    break;
                }case 28:
                {
                    APIRequest.apiInterface(token).callSubmitRating(param).enqueue(responseBodyCallback);
                    break;
                }case 29:
                {
                    APIRequest.apiInterface(token).callFareCheck(param).enqueue(responseBodyCallback);
                    break;
                }case 30:
                {
                    APIRequest.apiInterface(token).callMakePayment(param).enqueue(responseBodyCallback);
                    break;
                }case 31:
                {
                    APIRequest.apiInterface(token).callAddEmergencyContacts(param).enqueue(responseBodyCallback);
                    break;
                }case 32:
                {
                    APIRequest.apiInterface(token).callChangePassword(param).enqueue(responseBodyCallback);
                    break;
                }case 33:
                {
                    APIRequest.apiInterface(token).callChangeNoti(param).enqueue(responseBodyCallback);
                    break;
                }case 34:
                {
                    APIRequest.apiInterface(token).callChangeSMS(param).enqueue(responseBodyCallback);
                    break;
                }case 35:
                {
                    APIRequest.apiInterface(token).callAddSupportQuery(param).enqueue(responseBodyCallback);
                    break;
                }case 36:
                {
                    APIRequest.apiInterface(token).callForget(param).enqueue(responseBodyCallback);
                    break;
                }case 37:
                {
                    APIRequest.apiInterface(token).callCancelRideByDriver(param).enqueue(responseBodyCallback);
                    break;
                }case 38:
                {
                    APIRequest.apiInterface(token).callUpdateDriverActualLocation(param).enqueue(responseBodyCallback);
                    break;
                } case 39:
                {
                    DistanceModel deleteRiderListModel = gson.fromJson(param.get("ride_detail"),DistanceModel.class);
                    APIRequest.apiInterface(token).getFare(deleteRiderListModel).enqueue(responseBodyCallback);
                    break;
                }
            }
        }
        else
        {
            new InternetErrorDialog(this)
            {
                @Override
                protected void dataOnClick()
                {
                    api(param,responseBodyCallback,token,TYPE_REQUEST);
                }
            }.show();
        }
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        Log.e("ResponseStatus ",response);
        switch (RequestCode)
        {
            case 500:

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        JSONObject data=jsonObject.getJSONObject("data");

                        Log.e("OnlienStatus ",data.getString("is_online"));

                       /* if (tabValue.equalsIgnoreCase("1"))
                        {
                            tab1.setText("Offline");
                            tab1.setIcon(getResources().getDrawable(R.drawable.btn));
                            sharedPrefUtil.saveString(SharedPrefUtil.DRIVER_STATUS,"0");
                        }
                        else
                        {
                            tab1.setText("Online");
                            tab1.setIcon(getResources().getDrawable(R.drawable.btn_active));
                            sharedPrefUtil.saveString(SharedPrefUtil.DRIVER_STATUS,"1");
                        }*/

                        if (data.getString("is_online").equalsIgnoreCase("1"))
                        {
                            tab1.setText("Online");
                            tab1.setText("Online");
                            tab1.setIcon(getResources().getDrawable(R.drawable.btn_active));
                            sharedPrefUtil.saveString(SharedPrefUtil.DRIVER_STATUS,"1");
                        }
                        else if (data.getString("is_online").equalsIgnoreCase("0"))
                        {
                            tab1.setText("Offline");
                            tab1.setText("Offline");
                            tab1.setIcon(getResources().getDrawable(R.drawable.btn));
                            sharedPrefUtil.saveString(SharedPrefUtil.DRIVER_STATUS,"0");
                        }
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
