package com.taxi.nanny.views.booking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.req_model.EstimateFTModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.location.LocationDistanceDuration;
import com.taxi.nanny.utils.location.TrackGoogleLocation;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.adapter.ListofPickUpAdapter;
import com.taxi.nanny.views.home.ParentHome;
import com.taxi.nanny.views.login_section.register.parent.ListofChildren;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickDropConfirmation extends BaseActivity implements  Callback<ResponseBody>,
        LocationDistanceDuration
{
    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvContinue)
    TextView tvContinue;

   /* @BindView(R.id.ivCal)
    ImageView ivCal;*/

    @BindView(R.id.tvTime)
    TextView tvTime;

    String key="";
    String key_final="";
    int position;
    ArrayList<RiderListModel> riderList;
    public static String TAG=PickDropConfirmation.class.getSimpleName();

    String pickPriority="",destPriority="";

    String estDistance="",estDuration="";
    public static final int requestCode=40;

    @BindView(R.id.ivEnd)
    ImageView ivEnd;


    @BindView(R.id.cvDate)
    CardView cvDate;

    @BindView(R.id.cvTime)
    CardView cvTime;

    @BindView(R.id.tvDate)
    TextView tvDate;

    @Override
    protected int getContentId()
    {
        return R.layout.list_pick_up;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Pick up/Drop off Confirmation");
        ivEnd.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ivEnd.setImageDrawable(getDrawable(R.drawable.bin));
        }
        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");

        if (getIntent().hasExtra("key"))
        {
            key=getIntent().getExtras().getString("key");
            key_final=getIntent().getExtras().getString("key_final");
            position=getIntent().getExtras().getInt("position");

            if (key.equalsIgnoreCase("start_single"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e(TAG, "onCreate: RiderListSize "+riderList.size());
            }
            else if (key.equalsIgnoreCase("final_single"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e(TAG, "onCreate: RiderListSize "+riderList.size());
            }
            else if (key.equalsIgnoreCase("start_multiple"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e(TAG, "onCreate: RiderListSize "+riderList.size());
            }
            else if (key.equalsIgnoreCase("start_multiple_different"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e(TAG, "onCreate: RiderListSize "+riderList.size());
            }
        }

        setAdapter();
    }

    public void callCancelAlert()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvMsg.setText("Are you sure you would like to cancel this ride?");

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               Intent intent=new Intent(PickDropConfirmation.this, ParentHome.class);
               startActivity(intent);
               finish();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void callExitAlert()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvMsg.setText("Would you like to add/remove Rider's from this ride?");

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PickDropConfirmation.this, ListofChildren.class);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("keyData","key");
                startActivity(intent);
//                finish();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
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

    @OnClick({R.id.tvContinue,R.id.img_back_btn,R.id.ivEnd,R.id.cvDate,R.id.cvTime})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
              /*  if (riderList.size()>0)
                {
                    riderList.clear();
                }*/

                callExitAlert();

                break;

                case R.id.ivEnd:

                callCancelAlert();

                break;

             /*   case R.id.ivCal:

                callTimePick();

                break;*/


                case R.id.cvTime:

                callTimePick();

                break;

                case R.id.cvDate:

                setDatePicker();

                break;

            case R.id.tvContinue:

                if (checkValidations())
                {
                    callAlert();
                }


             /*   if (validate.equalsIgnoreCase("0"))
                {
                    for (int i = 0; i <riderList.size() ; i++)
                    {

                        if (riderList.get(i).getPickPriority()==0)
                        {
                            Toast.makeText(this, "Please choose pick priority ", Toast.LENGTH_SHORT).show();

                        }
                        else if (riderList.get(i).getDropPriority()==0)
                        {
                            Toast.makeText(this, "Please choose drop priority ", Toast.LENGTH_SHORT).show();

                        }
                    }
                    validate="1";
                }

                else
                {
                    callAlert();
                }
*/

                break;

        }
    }

    private int mYear, mMonth, mDay;

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
                tvDate.setText(year + "-" + (exactMonth) + "-" + day);
                Date=year+"-"+(exactMonth)+"-"+dayOfMonth;
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    Calendar mcurrentTime ;
    int hour ;
    int minute;
    TimePickerDialog mTimePicker;
    String time="";
    String Date="";

    public void callTimePick()
    {
        mcurrentTime = Calendar.getInstance();
        hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = mcurrentTime.get(Calendar.MINUTE);

        mTimePicker = new TimePickerDialog(PickDropConfirmation.this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                /*tvTime.setText(selectedHour+":"+selectedMinute);

                */

                time=selectedHour+":"+selectedMinute;
                Log.e("SelectedTime ",time);
                updateTime(selectedHour,selectedMinute);
            }
        }, hour, minute,false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void updateTime(int hours, int mins)
    {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        tvTime.setText(aTime);
    }

    public boolean checkValidations()
    {
        if (Date.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please choose date for booking", Toast.LENGTH_SHORT).show();
            return  false;
        }

        if (time.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please choose time for booking", Toast.LENGTH_SHORT).show();
            return  false;
        }
       else if (riderList.size()>0)
        {
            Log.e(TAG, "checkValidations: ListSize "+riderList.size());
            for (int i = 0; i <riderList.size() ; i++)
            {
                Log.e(TAG, "checkValidations: PickUp "+riderList.get(i).getPickup());
                Log.e(TAG, "checkValidations: DropUp "+riderList.get(i).getDropup());
                Log.e(TAG, "checkValidations: PikPriority "+riderList.get(i).getPickPriority());
                Log.e(TAG, "checkValidations: DropPrority "+riderList.get(i).getDropPriority());
                Log.e(TAG, "checkValidations: PickInstructions "+riderList.get(i).getPick_instructions());
                Log.e(TAG, "checkValidations: DropInstructions "+riderList.get(i).getDrop_instructions());
                if (riderList.get(i).getPickup().equalsIgnoreCase(""))
                {
                    Toast.makeText(this, "Please choose pick up location for "+riderList.get(i).getFirst_name(), Toast.LENGTH_SHORT).show();
                    return false;
                }
                else if (riderList.get(i).getDropup().equalsIgnoreCase(""))
                {
                    Toast.makeText(this, "Please choose drop up location for "+riderList.get(i).getFirst_name(), Toast.LENGTH_SHORT).show();
                    return false;
                }


            }
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        callExitAlert();
    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        ListofPickUpAdapter listofPickUpAdapter=new ListofPickUpAdapter(this,riderList);
        recycler.setAdapter(listofPickUpAdapter);

        listofPickUpAdapter.onItemSelectedListener(new ListofPickUpAdapter.myClickListener()
        {
            @Override
            public void onItemClick(int layoutPosition, View view, View selected, TextView tv)
            {

            }

            @Override
            public void onPickLocation(int layoutPosition, View view)
            {
                Intent intent=new Intent(PickDropConfirmation.this,EnterPickUp.class);
                if (key.equalsIgnoreCase("start_multiple"))
                {
                    intent.putExtra("key","start_multiple");
                    intent.putExtra("key_final","yes");
                }
                else if (key.equalsIgnoreCase("final_single"))
                {
                    intent.putExtra("key","final_single");
                    intent.putExtra("key_final",key_final);
                }
                else if (key.equalsIgnoreCase("start_single"))
                {
                    intent.putExtra("key","start_single");
                    intent.putExtra("key_final","yes");
                }
                else if (key.equalsIgnoreCase("start_multiple_different"))
                {
                    intent.putExtra("key","start_multiple_different");
                    intent.putExtra("key_final","yes");
                }
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("position",layoutPosition);
                startActivity(intent);
            }

            @Override
            public void onDRopLocation(int layoutPosition, View view)
            {
                Intent intent1=new Intent(PickDropConfirmation.this,EnterDropLocation.class);

                if (key.equalsIgnoreCase("start_single"))
                {
                    intent1.putExtra("key","start_single");
                    intent1.putExtra("key_final","yes");
                }

                else if (key.equalsIgnoreCase("start_multiple"))
                {
                    intent1.putExtra("key","start_multiple");
                    intent1.putExtra("key_final","yes");
                }

                else if (key.equalsIgnoreCase("start_multiple_different"))
                {
                    intent1.putExtra("key","start_multiple_different");
                    intent1.putExtra("key_final","yes");
                }

                intent1.putExtra("rider_list",(Serializable) riderList);
                intent1.putExtra("position",layoutPosition);
                startActivity(intent1);
            }

            @Override
            public void onPickItemSelected(int layoutPosition, View view, Spinner spPick, int SpinnerPosition)
            {
                Log.e("PickSelectedItem ",spPick.getSelectedItem().toString());
                Log.e("layoutPositionPick ",layoutPosition+"");
                Log.e("SpinnerPositionPick ",SpinnerPosition+"");

              /*  riderList.get(SpinnerPosition).setPickPriority(Integer.parseInt(spPick.getSelectedItem()+""));
                listofPickUpAdapter.notifyDataSetChanged();*/
            }

            @Override
            public void onDropItemSelected(int layoutPosition, View view, Spinner spDrop, int SpinnerPosition)
            {
                Log.e("DropSelectedItem ",spDrop.getSelectedItem().toString());
                Log.e("layoutPositionDrop ",layoutPosition+"");
                Log.e("SpinnerPositionDrop ",layoutPosition+"");
                Log.e("positionDrop ",position+"");

               /* riderList.get(layoutPosition).setDropPriority(Integer.parseInt(spDrop.getSelectedItem()+""));
                listofPickUpAdapter.notifyDataSetChanged();*/
            }

            @Override
            public void onSignInCheck(int layoutPosition, View view, CheckBox checkSignIn)
            {
                riderList.get(layoutPosition).setSignIn(riderList.get(layoutPosition).isSignIn()?false:true);
                Log.e(TAG, "onSignInCheck: "+riderList.get(layoutPosition).isSignIn()+"" );
                listofPickUpAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSignOutCheck(int layoutPosition, View view, CheckBox checkSignOut)
            {
                riderList.get(layoutPosition).setSignOut(riderList.get(layoutPosition).isSignOut()?false:true);
                Log.e(TAG, "onSignOutCheck: "+riderList.get(layoutPosition).isSignOut()+"" );
                listofPickUpAdapter.notifyDataSetChanged();
            }
        });

    }

    int positionList;

    public void callAlert()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

       int width = WindowManager.LayoutParams.WRAP_CONTENT;
       int height = WindowManager.LayoutParams.WRAP_CONTENT;

       tvMsg.setText("Is this a Recurring ride ?");

       disList.clear();
       timList.clear();

       LatLng latLng=null;
       LatLng latLng1=null;

       for (int i = 0; i <riderList.size() ; i++)
       {
        latLng=new LatLng(Double.parseDouble(riderList.get(i).getPickLat()),
                            Double.parseDouble(riderList.get(i).getPicklng()));
        latLng1=new LatLng(Double.parseDouble(riderList.get(i).getDroplat()),
                            Double.parseDouble(riderList.get(i).getDroplng()));

        Log.e(TAG, "callAlert:latLng "+latLng);
        Log.e(TAG, "callAlert:latLng1 "+latLng1);

        positionList=i;

        new TrackGoogleLocation(PickDropConfirmation.this,
                PickDropConfirmation.this).getEstimate(latLng, latLng1);
        }

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.e("DisListSize ",disList.size()+"");
                distanceList.clear();

                progress = new ProgressDialog(PickDropConfirmation.this);
                progress.setMessage("Processing...");
                progress.setCancelable(false);
                progress.show();

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < disList.size(); i++)
                        {
                            if (i%3==0)
                            {
                                Log.e(TAG, "onClick: Index If"+i );
                                distanceList.add(disList.get(i));
                                Log.e("DistanceListSizeInside ",distanceList.size()+"");
                            }
                            else
                            {
                                Log.e(TAG, "onClick: Index else"+i );
                            }
                        }
                        progress.cancel();

                        Intent intent=new Intent(PickDropConfirmation.this,ConfirmBooking.class);
                        intent.putExtra("rider_list",(Serializable) riderList);
                        intent.putExtra("dist_list", distanceList);
                        intent.putExtra("ride_type","2");
                        intent.putExtra("book_time",time);
                        intent.putExtra("book_date",Date);
                        startActivity(intent);
                    }
                },3000);


              alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                progress = new ProgressDialog(PickDropConfirmation.this);
                progress.setMessage("Processing...");
                progress.setCancelable(false);
                progress.show();

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < disList.size(); i++)
                        {
                            if (i%3==0)
                            {
                                Log.e(TAG, "onClick: Index If"+i );
                                distanceList.add(disList.get(i));
                                Log.e("DistanceListSizeInside ",distanceList.size()+"");
                            }
                            else
                            {
                                Log.e(TAG, "onClick: Index else"+i );
                            }
                        }
                        progress.cancel();

                        Intent intent=new Intent(PickDropConfirmation.this,ListofDays.class);
                        intent.putExtra("rider_list",(Serializable) riderList);
                        intent.putExtra("dist_list", distanceList);
                        intent.putExtra("book_time",time);
                        intent.putExtra("book_date",Date);
                        startActivity(intent);
                    }
                },3000);


              /*  Intent intent=new Intent(PickDropConfirmation.this,ListofDays.class);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("book_time",time);
                intent.putExtra("book_date",Date);
                startActivity(intent);*/
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    ProgressDialog progress;

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);
        Log.e(TAG, "onResponse: General<< "+generalResponse);
        try
        {
            JSONObject jsonObject=generalResponse.getResponse_object();
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                JSONArray data=jsonObject.getJSONArray("data");

                selectedList.clear();

                for (int i = 0; i < data.length(); i++)
                {
                    JSONObject jsonObject1=data.getJSONObject(i);

                    double time=Double.parseDouble(jsonObject1.getString("time"));
                    double price=Double.parseDouble(jsonObject1.getString("price"));
                    double distance=Double.parseDouble(jsonObject1.getString("distance"));

                   String amount= new DecimalFormat("##.##").format(price);
                   String timeee= new DecimalFormat("##.##").format(time);
                   String distanceeee= new DecimalFormat("##.##").format(distance);
                    double timeValue=Double.parseDouble(timeee)*60;


                    riderList.get(i).setEstPrice(String.valueOf(amount));

                }



                Intent intent=new Intent(PickDropConfirmation.this,ConfirmBooking.class);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("ride_type","2");
                intent.putExtra("book_time",time);
                intent.putExtra("book_date",Date);
                startActivity(intent);

            }
            else
            {
             Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
    //       replaceActivity();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    ArrayList<RiderListModel> selectedList=new ArrayList<>();
    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

    }

    public   String EstDist="";;

    public  String  getEstDistTime(LatLng pick,LatLng drop)
    {
        Log.e("getEstDistTime: ",pick+"");
        Log.e("getEstDistTime:DRop ",drop+"");

        GoogleDirection.withServerKey(Constant.PLACES_API)
                .from(pick)
                .to(drop)
                .alternativeRoute(true)
                .avoid("toll")
                .execute(new DirectionCallback()
                {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody)
                    {
                        if (direction.isOK())
                        {
                            Log.e( "onDirectionSuccess: " ,direction.getRouteList().toString());
                            getEstDistTime(direction.getRouteList());
                        }
                        else
                        {
                            Log.e("else ","else" );
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t)
                    {
                        Log.e("onDirectionFailure ",t.getMessage());
                    }
                });

        return EstDist;
    }

    String routeDistance="";
    String routeDuration="";

    private  String getEstDistTime(List<Route> routes)
    {
        Log.e("getEstDistTime ",routes.toString());
        String distIme="";
        String routeDistance="";
        String routeDuration="";

        for (Route route : routes)
        {
            List<Leg> legs = route.getLegList();
            for (Leg leg : legs)
            {
                routeDistance = leg.getDistance().getText();
                routeDuration = leg.getDuration().getText();
                for (int k = 0; k < riderList.size(); k++)
                {

                    riderList.get(k).setEstDistance(routeDistance);
                    riderList.get(k).setTime_eta(routeDuration);
                }
            }
        }

        distIme=routeDistance+"-"+routeDuration;
        Log.e("distIme ",distIme.toString());

        Intent intent=new Intent(PickDropConfirmation.this,ConfirmBooking.class);
        intent.putExtra("rider_list",(Serializable) riderList);
        intent.putExtra("ride_type","2");
        intent.putExtra("book_time",time);
        intent.putExtra("book_date",Date);
        startActivity(intent);

        return distIme;
    }

    @Override
    public void getResult(String distance, String duration)
    {
        Log.e("ResultConfirmDistance  ",distance);
        Log.e("ResultConfirmDuration  ",duration);

        disList.add(distance);
        timList.add(duration);

        actualDist=disList.get(0);
        actualTime=timList.get(0);
    }

    int mValue=0;

    ArrayList<String> distList=new ArrayList<>();
    ArrayList<String> timeList=new ArrayList<>();

    public   String actualDist="";
    public   String actualTime="";

    ArrayList<String> disList=new ArrayList<>();
    ArrayList<String> distanceList=new ArrayList<>();
    ArrayList<String> timList=new ArrayList<>();
}
