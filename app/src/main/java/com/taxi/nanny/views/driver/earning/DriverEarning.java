package com.taxi.nanny.views.driver.earning;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.history.HistoryPagerAdapter;
import butterknife.BindView;

public class DriverEarning extends BaseActivity implements TabLayout.OnTabSelectedListener {


    TabLayout tabsss;

    @BindView(R.id.pager)
    ViewPager pager;

    @BindView(R.id.tvToday)
    TextView tvToday;

    @BindView(R.id.tvWeekly)
    TextView tvWeekly;


    @Override
    protected int getContentId()
    {
        return R.layout.driver_earning;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTab();
        setEarningTab();
    }

    public void setEarningTab()
    {
        tabsss=(TabLayout)findViewById(R.id.tabsss);
        tabsss.addTab(tabsss.newTab().setText("Today"));
        tabsss.addTab(tabsss.newTab().setText("Weekly"));

        //Creating our pager adapter
        HistoryPagerAdapter adapter = new HistoryPagerAdapter
                (getSupportFragmentManager(),tabsss.getTabCount(),"e");
        //Adding adapter to pager
        pager.setAdapter(adapter);
        tabsss.setupWithViewPager(pager);
        //Adding onTabSelectedListener to swipe views
        tabsss.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        if (tab.getPosition()==0)
        {
            tvToday.setTextColor(getResources().getColor(R.color.black));
            tvWeekly.setTextColor(getResources().getColor(R.color.light_gray));
        }
        else if (tab.getPosition()==1)
        {
            tvToday.setTextColor(getResources().getColor(R.color.light_gray));
            tvWeekly.setTextColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab)
    {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab)
    {

    }
}
