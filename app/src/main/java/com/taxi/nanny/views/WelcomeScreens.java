package com.taxi.nanny.views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.taxi.nanny.R;
import com.taxi.nanny.model.SliderModel;
import com.taxi.nanny.views.login_section.WelcomeActivity;
import com.viewpagerindicator.CirclePageIndicator;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

public class WelcomeScreens extends BaseActivity
{
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.indicator)
    CirclePageIndicator indicator;

    WelcomeScreenAdapter welcomeScreenAdapter;

    ArrayList<SliderModel> list = new ArrayList<>();

    @BindView(R.id.btn_next)
    TextView btn_next;
    int tab;
    int count = 0;

    @Override
    protected int getContentId()
    {
        return R.layout.welcome_screens;
    }

    public void setData()
    {
        list.clear();

        SliderModel sliderModel = new SliderModel("0", "Welcome",
                getResources().getString(R.string.welcome_text));
        list.add(sliderModel);

        sliderModel = new SliderModel("1", "Customizable Rides",
                getResources().getString(R.string.your_ride));
        list.add(sliderModel);

        sliderModel = new SliderModel("2", "Certified TaxiNannies",
                getResources().getString(R.string.trusted_drivers));
        list.add(sliderModel);

        sliderModel = new SliderModel("3", "Get Real-Time Updates",
                getResources().getString(R.string.track_your_ride));
        list.add(sliderModel);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setBackgroundDrawableResource(R.drawable.white_background);
        }

        tab = viewpager.getCurrentItem();
        setadapter();
    }

    private void setadapter()
    {
        setData();

        welcomeScreenAdapter = new WelcomeScreenAdapter(this, list);
        viewpager.setAdapter(welcomeScreenAdapter);
        indicator.setViewPager(viewpager);
        viewpager.setPageTransformer(false, new DefaultTransformer());

        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
    }

    @OnClick({R.id.btn_next})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_next:
//                Log.e("count ",count+"");
                Intent it = new Intent(WelcomeScreens.this, WelcomeActivity.class);
                startActivity(it);
                finish();

               /* if (count==list.size()-1)
                {
                Intent it = new Intent(WelcomeScreens.this, WelcomeActivity.class);
                startActivity(it);
                finish();
                }
                else
                {
                    viewpager.arrowScroll(View.FOCUS_RIGHT);
                    count=count+1;
                }*/
                break;
        }
    }
}
