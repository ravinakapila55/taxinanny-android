package com.taxi.nanny.views.booking.history;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.views.driver.earning.TodayEarningFragment;
import com.taxi.nanny.views.driver.earning.WeeklyEarningFragment;

public class HistoryPagerAdapter extends FragmentStatePagerAdapter implements RetrofitResponse
{

    int tabCount;
    String type;

    public HistoryPagerAdapter(FragmentManager fm, int tabCount,String type)
    {
        super(fm);
        this.tabCount= tabCount;
        this.type=type;
    }


    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                if (type.equalsIgnoreCase("b"))
                {
                    PastHistoryFragment historyFragment=new PastHistoryFragment();
                    return historyFragment;
                }
                else {
                    TodayEarningFragment earningFragment=new TodayEarningFragment();
                    return earningFragment;
                }


            case 1:

                if (type.equalsIgnoreCase("b"))
                {
                    UpcomingHistoryFragment upcomingHistoryFragment=new UpcomingHistoryFragment();
                    return upcomingHistoryFragment;
                }
                else {
                    WeeklyEarningFragment earningFragment=new WeeklyEarningFragment();
                    return earningFragment;
                }

        }
        return null;
    }

    @Override
    public int getCount()
    {
        return tabCount;
    }

    @Override
    public void onResponse(int RequestCode, String response) {

        switch (RequestCode)
        {

        }

    }
}
