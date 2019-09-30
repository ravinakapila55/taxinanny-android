package com.taxi.nanny.views.booking.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.model.RiderListModel;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter
{
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<RiderListModel> list;
    TextView pickup;
    TextView dropUp;

    public ViewPagerAdapter(Context context,ArrayList<RiderListModel> list)
    {
        context = context;
        this.list=list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == ((ConstraintLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
//        View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);
        View itemView = layoutInflater.inflate(R.layout.custom_estimate_ride, container, false);

      /*  TextView tvTime=(TextView)itemView.findViewById(R.id.tvTime);
        TextView tvFare=(TextView)itemView.findViewById(R.id.tvFare);
        TextView tvDist=(TextView)itemView.findViewById(R.id.tvDist);*/






      /*  Log.e("TimeList ",list.get(position).getTime_eta());
        Log.e("FareList ",list.get(position).getEstPrice());
        Log.e("Distance  ", String.valueOf(Math.round(Double.parseDouble(list.get(position).getEstDistance()))));
*/
      /*  String time= String.valueOf(Math.round(Double.parseDouble(list.get(position).getTime_eta())));

        if (Integer.parseInt(time)==60)
        {
            tvTime.setText("1 hr");
        }
        else if (Integer.parseInt(time)<60)
        {
            tvTime.setText(time+" min");
        }
        else if (Integer.parseInt(time)>60)
        {
            int hours=Integer.parseInt(time)/60;
            int minutes=Integer.parseInt(time)%60;

            if (minutes==0)
            {
                tvTime.setText(hours+" hrs ");
            }
            else
            {
                tvTime.setText(hours+" hrs "+minutes+" min");
            }
        }*/
     /*   Log.e("Timeeeee ",list.get(position).getTime_eta());
        Log.e("Distanceee ",list.get(position).getEstDistance());*/

     /*   tvTime.setText(list.get(position).getTime_eta());

        tvFare.setText("$ "+list.get(position).getEstPrice());
        tvDist.setText((list.get(position).getEstDistance()));*/
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((ConstraintLayout) object);
    }
}
