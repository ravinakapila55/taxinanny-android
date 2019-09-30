package com.taxi.nanny.views.booking.history;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;
import butterknife.BindView;
import butterknife.OnClick;

public class BookingHistory extends BaseActivity implements  TabLayout.OnTabSelectedListener
//public class BookingHistory extends BaseActivity
{
    TabLayout tabsss;

    @BindView(R.id.pager)
    ViewPager pager;


    @Override
    protected int getContentId()
    {
        return R.layout.booking_history;
//        return R.layout.ride_history;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("History");
//        setAdapter(recyclerPast);
        setTab();
    }

    public void setTab()
    {
        tabsss=(TabLayout)findViewById(R.id.tabsss);
        tabsss.addTab(tabsss.newTab().setText("Past"));
        tabsss.addTab(tabsss.newTab().setText("Upcoming"));

        //Creating our pager adapter
        HistoryPagerAdapter adapter = new HistoryPagerAdapter(getSupportFragmentManager(),tabsss.getTabCount(),"b");
        //Adding adapter to pager
        pager.setAdapter(adapter);
        tabsss.setupWithViewPager(pager);
        //Adding onTabSelectedListener to swipe views
        tabsss.setOnTabSelectedListener(this);
    }



    private TabLayout.Tab createTab(String text, Drawable icon, TabLayout tabsss)
    {
        TabLayout.Tab tab = tabsss.newTab().setText(text).setIcon(icon).setCustomView(R.layout.custom_tab);
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


    @OnClick({R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;

        }
    }


    
    
    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        pager.setCurrentItem(tab.getPosition());
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
