package com.taxi.nanny.views.booking.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RideHistoryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>  {

    Context context;
    int tab;

    myClickListener listener;

    ArrayList<RideHistoryModel> list;


    public HistoryAdapter(Context context,ArrayList<RideHistoryModel> list) {
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_history,viewGroup,false);
//        View view= LayoutInflater.from(context).inflate(R.layout.history_adapter,viewGroup,false);
        return new HistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position)
    {

        Log.e( "onBindViewHolder:RiderListLength ",list.get(position).getRiderList().size() +"");
        if (list.get(position).getRideArrayLength().equalsIgnoreCase("1"))
        {
            myViewHolder.tvAll.setVisibility(View.VISIBLE);
        }
        else
        {
            myViewHolder.tvAll.setVisibility(View.GONE);
        }

        myViewHolder.tvPrice.setText(list.get(position).getAmount());
        myViewHolder.tvCarName.setText(list.get(position).getDriverName());

        myViewHolder.tvDateTime.setText(list.get(position).getDate());




    }


    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }





    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvDateTime)
        TextView tvDateTime;

        @BindView(R.id.tvPrice)
        TextView tvPrice;

        @BindView(R.id.tvCarName)
        TextView tvCarName;

        @BindView(R.id.ivCar)
        ImageView ivCar;

        @BindView(R.id.rating)
        RatingBar rating;

        @BindView(R.id.tvAll)
        TextView tvAll;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);

            tvAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition(),v);
                }
            });
        }
    }

    public void onItemSelectedListener(myClickListener clickListener)
    {
        listener=clickListener;
    }


    public interface myClickListener{
        public void onItemClick(int layoutPosition,View view);
    }
}
