package com.taxi.nanny.views.booking.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.taxi.nanny.R;


public class BookingHistoryViewPager extends PagerAdapter
{
    Context context;
    LayoutInflater layoutInflater;


    public BookingHistoryViewPager(Context context)
    {
        this.context=context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == ((ConstraintLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        View itemView = layoutInflater.inflate(R.layout.custom_history, container, false);

      /*  TextView tvPick=(TextView)itemView.findViewById(R.id.tvPick);
        TextView tvDest=(TextView)itemView.findViewById(R.id.tvDest);

        tvPick.setText(list.get(position).getPickup());
        tvDest.setText(list.get(position).getDropup());
*/
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((ConstraintLayout) object);
    }
}


