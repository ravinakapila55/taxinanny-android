package com.taxi.nanny.views.booking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.driver.DriverHome;
import java.text.DecimalFormat;
import butterknife.BindView;
import butterknife.OnClick;

public class CollectCash extends BaseActivity {

    @BindView(R.id.tvCollect)
    TextView tvCollect;

    String distance="";
    String fare="";

    String amount="";
    String miles="";

    @BindView(R.id.tvPrice)
    TextView tvPrice;

    @BindView(R.id.tvDistance)
    TextView tvDistance;

    int tab;

    @BindView(R.id.tvPick)
    TextView tvPick;

    @BindView(R.id.tvDest)
    TextView tvDest;

    String pick="";
    String drop="";


    @Override
    protected int getContentId() {
        return R.layout.collect_cash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Collect Cash");

        if(getIntent().hasExtra("distance"))
        {
            distance=getIntent().getExtras().getString("distance");
            fare=getIntent().getExtras().getString("fare");

            double pp=Double.parseDouble(fare);
            double dd=Double.parseDouble(distance);

             amount= new DecimalFormat("##.##").format(pp);
             miles= new DecimalFormat("##.##").format(dd);

            tvPrice.setText("$ "+amount);
            tvDistance.setText(miles+" MI");

            pick=getIntent().getExtras().getString("pick");
            drop=getIntent().getExtras().getString("drop");

            tvPick.setText(pick);
            tvDest.setText(drop);
        }


    }


/*    public void setPagerAdapter()
    {
        ViewPagerLocations pagerAdapter=new ViewPagerLocations(LiveTrackBooking.this,riderList);
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
                Log.e("onPageSelected: PageSelected "+i);
                Log.e("ListSize: PageSelected "+riderList.size());

                if (riderList.size()>0)
                {
                    if (i==0)
                    {
                        Log.e(TAG, "onPageSelected: Inside0 "+i);
                        ivLeft.setVisibility(View.INVISIBLE);
                        ivRight.setVisibility(View.VISIBLE);
                    }

                    else if (i>0)
                    {
                        if (i==riderList.size()-1)
                        {
                            Log.e(TAG, "onPageSelected: last "+i);
                            ivLeft.setVisibility(View.VISIBLE);
                            ivRight.setVisibility(View.INVISIBLE);
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

                LatLng latLng=new LatLng(Double.parseDouble(riderList.get(i).getPickLat()),
                        Double.parseDouble(riderList.get(i).getPicklng()));
                LatLng latLng1=new LatLng(Double.parseDouble(riderList.get(i).getDroplat()),
                        Double.parseDouble(riderList.get(i).getDroplng()));

                String setTime=riderList.get(i).getTime_eta();

             *//*   if (Integer.parseInt(time1)==60)
                {
                    setTime="1 hr";
                }
                else if (Integer.parseInt(time1)<60)
                {

                    setTime=time+" min";
                }
                else if (Integer.parseInt(time1)>60)
                {
                    int hours=Integer.parseInt(time1)/60;
                    int minutes=Integer.parseInt(time1)%60;

                    if (minutes==0)
                    {

                        setTime=hours+" hrs ";
                    }
                    else
                    {
                        setTime=hours+" hrs "+minutes+" min";
                    }
                }
*//*
                setMultipleMarkers(latLng,latLng1,riderList.get(i).getPickup(),riderList.get(i).getDropup(),setTime);
            }

            @Override
            public void onPageScrollStateChanged(int i)
            {

            }
        });
    }*/


    @OnClick({R.id.img_back_btn,R.id.tvCollect})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                 finish();
                 break;

            case R.id.tvCollect:
               /*  Intent intent=new Intent(CollectCash.this,ReviewTrip.class);
                intent.putExtra("distance",miles);
                intent.putExtra("fare",amount);
                 startActivity(intent); */
                 Intent intent=new Intent(CollectCash.this, DriverHome.class);
                 startActivity(intent);
                 break;



        }
    }

}
