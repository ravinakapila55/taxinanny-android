package com.taxi.nanny.views.booking;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.DistanceModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.payment.StripePayment;
import com.taxi.nanny.req_model.EstimateFTModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.location.LocationDistanceDuration;
import com.taxi.nanny.utils.location.TrackGoogleLocation;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.adapter.ViewPagerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfirmBooking extends BaseActivity implements OnMapReadyCallback,
        Callback<ResponseBody>, LocationDistanceDuration
{
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.ivLeft)
    ImageView ivLeft;

    @BindView(R.id.ivRight)
    ImageView ivRight;

    @BindView(R.id.ivCash)
    ImageView ivCash;

    @BindView(R.id.tvContinue)
    TextView tvContinue;

    @BindView(R.id.tvPick)
    TextView tvPick;

    @BindView(R.id.tvDest)
    TextView tvDest;

    @BindView(R.id.tvRider)
    TextView tvRider;

    @BindView(R.id.ivRider)
    ImageView ivRider;

    GoogleMap map;
    Marker m1,m2;

    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.tvFare)
    TextView tvFare;

    @BindView(R.id.tvDist)
    TextView tvDist;

    String bookingType="";
    String book_time="";
    String book_date="";

    int tab;
    String confirm="0";
    ArrayList<RiderListModel> riderList;
    ArrayList<String> dist_list;
    ArrayList<String> days_list;
    SharedPrefUtil sharedPrefUtil;
    String token;
    public static final String TAG=ConfirmBooking.class.getSimpleName();

    @Override
    protected int getContentId()
    {
        return R.layout.confirm_booking;
    }

    public void setPagerAdapter()
    {
        ViewPagerAdapter  pagerAdapter=new ViewPagerAdapter(ConfirmBooking.this,riderList);
        viewpager.setAdapter(pagerAdapter);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int i, float v, int i1)
            {

            }

            @Override
            public void onPageSelected(int i)
            {
                Log.e(TAG, "onPageSelected: PageSelected "+i);
                Log.e(TAG, "ListSize: PageSelected "+riderList.size());

                if (riderList.size()>0)
                {
                    if (i==0)
                    {
                        Log.e(TAG, "onPageSelected: Inside0 "+i);
                        ivLeft.setVisibility(View.GONE);
                        ivRight.setVisibility(View.VISIBLE);
                    }

                    else if (i>0)
                    {
                        if (i==riderList.size()-1)
                        {
                            Log.e(TAG, "onPageSelected: last "+i);
                            ivLeft.setVisibility(View.VISIBLE);
                            ivRight.setVisibility(View.GONE);
                        }
                        else
                        {
                            Log.e(TAG, "onPageSelected: gretaerthan0 " + i);
                            ivLeft.setVisibility(View.VISIBLE);
                            ivRight.setVisibility(View.VISIBLE);
                        }
                    }
                }

            tvPick.setText(riderList.get(i).getPickup());
            tvDest.setText(riderList.get(i).getDropup());
            tvRider.setText(riderList.get(i).getFirst_name()+" "+riderList.get(i).getLast_name());
            Picasso.with(ConfirmBooking.this).load(riderList.get(i).getImage()).
                        placeholder(getResources().getDrawable(R.drawable.skip_rider))
                        .into(ivRider);
            LatLng latLng=new LatLng(Double.parseDouble(riderList.get(i).getPickLat()),
                    Double.parseDouble(riderList.get(i).getPicklng()));
            LatLng latLng1=new LatLng(Double.parseDouble(riderList.get(i).getDroplat()),
                    Double.parseDouble(riderList.get(i).getDroplng()));

            positionList=i;

            setMultipleMarkers(latLng,latLng1,riderList.get(i).getPickup(),riderList.get(i).getDropup());

            /*    m1 = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                m2 = map.addMarker(new MarkerOptions().position(latLng1).draggable(true));

                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(aa));*/

            }

            @Override
            public void onPageScrollStateChanged(int i)
            {
                Log.e(TAG, "onPageScrollStateChanged: "+" Inside" );
            }

        });
    }

    //TrackGoogleLocation
    ArrayList<LatLng> MarkerPoints;
    public void setMultipleMarkers(LatLng pick,LatLng drop,String pickLoc,String dropLoc)
    {
        map.clear();

     /*   map.addMarker(new MarkerOptions().position(pick).draggable(true));
        map.addMarker(new MarkerOptions().position(drop).draggable(true));
*/
        map.moveCamera(CameraUpdateFactory.newLatLng(pick));
        map.animateCamera(CameraUpdateFactory.zoomTo(1));

        disList.clear();
        timList.clear();

        //to draw poly line
        new TrackGoogleLocation(this, map, this).setMarkerLocateBooking(pick, drop, 10,
                pickLoc, dropLoc);
    }

    ArrayList<String> distList=new ArrayList<>();
    ArrayList<String> timeList=new ArrayList<>();

    public   String actualDist="";
    public   String actualTime="";

    ArrayList<String> disList=new ArrayList<>();
    ArrayList<String> timList=new ArrayList<>();

    @Override
    public void getResult(String distance, String duration)
    {
        Log.e("ResultConfirmDistance  ",distance);
        Log.e("ResultConfirmDuration  ",duration);

        disList.add(distance);
        timList.add(duration);

        distList.clear();
        timeList.clear();

        distList.add(distance);
        timeList.add(duration);

        actualDist=distList.get(0);
        actualTime=timeList.get(0);

        riderList.get(viewpager.getCurrentItem()).setEstDistance(disList.get(0));
        riderList.get(viewpager.getCurrentItem()).setTime_eta(timList.get(0));

        String getDistance=riderList.get(viewpager.getCurrentItem()).getEstDistance();

        //todo for usa
        if (getDistance.contains("mi"))
        {
            Log.e("Inside ","if");
            tvDist.setText(getDistance);
        }

        //todo for india
        else
        {
            Log.e("Inside ","Else");
            Number valuee = null;
            try {
                 valuee= NumberFormat.getNumberInstance(java.util.Locale.US).parse(getDistance);
                 //todo to remove comma inbetween the string like 1,234
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

        /*    String[] splited = getDistance.split("\\s+");


            Log.e("Splited 0",splited[0]);
            accDistance=Double.parseDouble(splited[0]);*/
            accDistance=Double.parseDouble(String.valueOf(valuee));

            Log.e("accDistance",accDistance+"");

            accDistMiles = 0.621 * accDistance;
            Log.e("accDistMiles",accDistMiles+"");

            DistancePppp= new DecimalFormat("##.##").format(accDistMiles);

            Log.e("PriceRideList ",riderList.get(viewpager.getCurrentItem()).getEstPrice());
            tvDist.setText(String.valueOf(DistancePppp) +" mi");
        }

        tvTime.setText(riderList.get(viewpager.getCurrentItem()).getTime_eta());
        tvFare.setText("$ "+riderList.get(viewpager.getCurrentItem()).getEstPrice());
    }

    String DistancePppp="";

    double accDistance,accDistMiles;
    int acccccDist;

    public void initializeMap()
    {
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);
    }

    int positionList;
    String end_date="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Pick-up And Drop-off");
        sharedPrefUtil=SharedPrefUtil.getInstance();
        riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
        days_list=(ArrayList<String>) getIntent().getSerializableExtra("days_list");
        dist_list=(ArrayList<String>) getIntent().getSerializableExtra("dist_list");
        token=sharedPrefUtil.getString(Constant.TOKEN,"");
        bookingType=getIntent().getExtras().getString("ride_type");
        book_time=getIntent().getExtras().getString("book_time");
        book_date=getIntent().getExtras().getString("book_date");

        Log.e("RiderListSize ",riderList.size()+"");
        Log.e("distListSize ",dist_list.size()+"");

        for (int zz = 0; zz < dist_list.size(); zz++)
        {
            Log.e("DistanceeeConfirm ",dist_list.get(zz));
        }

        for (int i = 0; i < riderList.size(); i++)
        {
            Log.e("RideListPrice ",riderList.get(i).getEstPrice());
            Log.e("pickInstructions ",riderList.get(i).getPick_instructions());
            Log.e("dropInstructions ",riderList.get(i).getDrop_instructions());
            Log.e("signIn ",riderList.get(i).isSignIn()+"");
            Log.e("signOut ",riderList.get(i).isSignOut()+"");
        }

        if (getIntent().hasExtra("end_date"))
        {
            end_date=getIntent().getExtras().getString("end_date");
            Log.e(TAG, "onCreate: End_date " +end_date );
        }

//        callEstimatedEta();


        Log.e("TabValue ",tab+"");

        initializeMap();

        setPagerAdapter();
        tab = viewpager.getCurrentItem();

        if (riderList.size()==1)
        {
            ivLeft.setVisibility(View.INVISIBLE);
            ivRight.setVisibility(View.INVISIBLE);

            Log.e(TAG, "onCreate:PickLat "+riderList.get(0).getPickLat());
            Log.e(TAG, "onCreate:DropLat "+riderList.get(0).getDroplat());
            Log.e(TAG, "onCreate:PickLng "+riderList.get(0).getPicklng());
            Log.e(TAG, "onCreate:DropLng "+riderList.get(0).getDroplng());

            tvPick.setText(riderList.get(0).getPickup());
            tvDest.setText(riderList.get(0).getDropup());
            tvRider.setText(riderList.get(0).getFirst_name()+" "+riderList.get(0).getLast_name());

            if (!riderList.get(0).getImage().equalsIgnoreCase(""))
            {
                Picasso.with(ConfirmBooking.this).load(riderList.get(0).getImage()).
                        placeholder(getResources().getDrawable(R.drawable.skip_rider))
                        .into(ivRider);
            }
        }
        else if (riderList.size()>1)
        {
            ivLeft.setVisibility(View.GONE);
            ivRight.setVisibility(View.VISIBLE);

            for (int i = 0; i <riderList.size() ; i++)
            {
                Log.e(TAG, "Position "+i);
                Log.e(TAG, "onCreate:Pick "+riderList.get(i).getPickup());
                Log.e(TAG, "onCreate:Drop "+riderList.get(i).getDropup() );

                Log.e(TAG, "onCreate:PickLat "+riderList.get(i).getPickLat());
                Log.e(TAG, "onCreate:DropLat "+riderList.get(i).getDroplat());
                Log.e(TAG, "onCreate:PickLng "+riderList.get(i).getPicklng());
                Log.e(TAG, "onCreate:DropLng "+riderList.get(i).getDroplng());

                Log.e(TAG, "-------------------------- "+"----------------------");
                tvPick.setText(riderList.get(0).getPickup());
                tvDest.setText(riderList.get(0).getDropup());
                tvRider.setText(riderList.get(0).getFirst_name()+" "+riderList.get(0).getLast_name());
                Picasso.with(ConfirmBooking.this).load(riderList.get(i).getImage()).
                        placeholder(getResources().getDrawable(R.drawable.skip_rider))
                        .into(ivRider);
            }
        }
        callEstimatedFare();
    }

    public void callEstimatedEta()
    {
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

    public void callEstimatedFare()
    {
        HashMap<String,String> param=new HashMap<>();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        List<DistanceModel.RideDistanceBean> distanceBeanList = new ArrayList<>();
        JsonArray ride_detail=new JsonArray();

        String dist[];

        for (int i = 0; i < dist_list.size(); i++)
        {
            DistanceModel.RideDistanceBean distanceBean=new DistanceModel.RideDistanceBean();
//            dist=riderList.get(i).getEstDistance().split(" ");
            dist=dist_list.get(i).split(" ");
//          distanceBean.setDistance(riderList.get(i).getEstDistance());
            distanceBean.setRider_distance(dist[0]);
            distanceBeanList.add(distanceBean);
        }

        DistanceModel distanceModel=new DistanceModel();
        distanceModel.setRide_detail(distanceBeanList);

        param.put("ride_detail", gson.toJson(distanceModel));
        Log.e(TAG, "callDistance:Params: "+param);
        api(param,this,token,39);
    }

    @OnClick({R.id.ivLeft,R.id.ivRight,R.id.ivCash,R.id.img_back_btn,R.id.tvContinue})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ivLeft:
               /* if (tab > 0)
                {
                    tab--;
                    viewpager.setCurrentItem(tab);
                } else if (tab == 0)
                {
                    viewpager.setCurrentItem(tab);
                }
                */
                viewpager.arrowScroll(View.FOCUS_LEFT);
                break;

            case R.id.ivRight:
                viewpager.arrowScroll(View.FOCUS_RIGHT);
                break;

            case R.id.ivCash:
                callAlert();
                break;

            case R.id.img_back_btn:
                onBackPressed();
                break;

            case R.id.tvContinue:
            Log.e(TAG, "onClick: RiderList "+riderList.size());
            Log.e(TAG, "onClick: bookingType "+bookingType);
//          Intent intent=new Intent(ConfirmBooking.this,StripePayment.class);
            Intent intent=new Intent(ConfirmBooking.this,ChooseCard.class);
            intent.putExtra("rider_list",(Serializable) riderList);
            intent.putExtra("ride_type",bookingType);
            intent.putExtra("book_time",book_time);
            intent.putExtra("book_date",book_date);

            if (bookingType.equalsIgnoreCase("1"))
            {
                        Log.e(TAG, "onClick: days_list "+days_list.size());
                        Log.e(TAG, "onClick: end_date "+end_date);
                        intent.putExtra("end_date",end_date);
                        intent.putExtra("days_list",(Serializable) days_list);
            }
            startActivity(intent);
            break;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
      /*  Intent intent=new Intent(this,PickDropConfirmation.class);
        startActivity(intent);*/
       // finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        switch (requestCode)
        {

            case 2:

                for (int i = 0; i < permissions.length; i++)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(this, "Permitions Allow", Toast.LENGTH_SHORT).show();
                        //  getLocation();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        // checkPermissionLocation(context);
                        Toast.makeText(this, "Permitions Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
            {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


    public void setDelay()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                tvContinue.setVisibility(View.GONE);
            }
        },8000);
    }

    public void callAlert()
    {
        final Dialog alert = new Dialog(this);
        alert.setContentView(R.layout.select_cash_option);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        alert.getWindow().setLayout(width, height);
//        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final ConstraintLayout main = (ConstraintLayout) alert.findViewById(R.id.main);
        final TextView tvContinue = (TextView) alert.findViewById(R.id.tvContinue);


        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;

        alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        main.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alert.dismiss();
            }
        });

        tvContinue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(ConfirmBooking.this,StripePayment.class);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("ride_type",bookingType);
                startActivity(intent);
                alert.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map=googleMap;

      /*  LatLng latLng=new LatLng(30.6496,76.7567);
        map.addMarker(new MarkerOptions().position(latLng).title("Marker in Mohali"));
        Log.e(TAG, "onMapReady: "+googleMap.getCameraPosition());
//        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
                .zoom(13).build()));*/

        LatLng latLng=new LatLng(Double.parseDouble(riderList.get(0).getPickLat()),
                Double.parseDouble(riderList.get(0).getPicklng()));
        LatLng latLng1=new LatLng(Double.parseDouble(riderList.get(0).getDroplat()),
                Double.parseDouble(riderList.get(0).getDroplng()));

        positionList=0;

     /*   positionList=0;
        new TrackGoogleLocation(this,this).getEstimate(latLng,latLng1);*/

        setMultipleMarkers(latLng,latLng1,riderList.get(0).getPickup(),riderList.get(0).getDropup());
//        setMarkers();
    }

    public void setMarkers()
    {
        MarkerOptions markerOptions=new MarkerOptions();

        map.clear();
        LatLng latLng=new LatLng(Double.parseDouble(riderList.get(0).getPickLat()),
                Double.parseDouble(riderList.get(0).getPicklng()));
        LatLng latLng1=new LatLng(Double.parseDouble(riderList.get(0).getDroplat()),
                Double.parseDouble(riderList.get(0).getDroplng()));

        m1 = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
        m2 = map.addMarker(new MarkerOptions().position(latLng1).draggable(true));

       /* map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng)
                .zoom(7).build()));*/
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(1));
    }

    int total=0;
    double newAmount;
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
//                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                JSONArray data=jsonObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++)
                {
                    JSONObject jsonObject1=data.getJSONObject(i);
                    if (riderList.get(i).isSignIn() && riderList.get(i).isSignOut())
                    {
                        total=10;
                    }
                    else if (!riderList.get(i).isSignIn() && riderList.get(i).isSignOut())
                    {
                        total=5;
                    }
                    else if (riderList.get(i).isSignIn() && !riderList.get(i).isSignOut())
                    {
                        total=5;
                    }
                    else if (!riderList.get(i).isSignIn() && !riderList.get(i).isSignOut())
                    {
                        total=0;
                    }

                    Log.e("total ",total+"");
                    Log.e("Priceeee ",jsonObject1.getString("price")+"");

                    double totalD;

                    totalD =Double.parseDouble(String.valueOf(total));

                    newAmount=totalD+Double.parseDouble(jsonObject1.getString("price"));
                    Log.e("NewAmount ",newAmount+"");

                    String val=String.valueOf(newAmount);

                    double price=Double.parseDouble(val);

                    Log.e(TAG, "onResponse: PriceAmount "+price);

                    String amount= new DecimalFormat("##.##").format(price);

                    Log.e("EstimatedPrice ",amount+"");

                    riderList.get(i).setEstPrice(String.valueOf(amount));

                    tvFare.setText("$ "+riderList.get(i).getEstPrice());
                }

            }
            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                replaceActivity();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
    progressDialog.dismiss();
    }

}
