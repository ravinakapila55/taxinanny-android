package com.taxi.nanny.views.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.CabsBeans;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class HomeCabsAdapter extends RecyclerView.Adapter<HomeCabsAdapter.MyViewHolder>
{
    MyClickListener myClickListener;
    ArrayList<CabsBeans> list;
    Context context;

    public HomeCabsAdapter(ArrayList<CabsBeans> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.custom_cab,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position)
    {
        myViewHolder.tvList.get(0).setText(list.get(position).getCabName());
        myViewHolder.tvList.get(1).setText(list.get(position).getStatus());

        if (list.get(position).isAvailable())
        {
            myViewHolder.ivList.get(0).setVisibility(View.GONE);
            myViewHolder.ivList.get(1).setVisibility(View.VISIBLE);
            myViewHolder.tvList.get(0).setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
        else {
            myViewHolder.ivList.get(0).setVisibility(View.VISIBLE);
            myViewHolder.ivList.get(1).setVisibility(View.GONE);
            myViewHolder.tvList.get(0).setTextColor(context.getResources().getColor(R.color.grey));
        }

        myViewHolder.ll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myClickListener.onItemClick(position,v);
            }
        });

        if (position==list.size()-1)
        {
            myViewHolder.viewConnect.setVisibility(View.GONE);
        }
        else {
            myViewHolder.viewConnect.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.ll)
        LinearLayout ll;

        @BindViews({R.id.ivCar,R.id.ivSelected})
        List<ImageView> ivList;

        @BindViews({R.id.tvCarName,R.id.tvCarStatus})
        List<TextView> tvList;

        @BindView(R.id.viewConnect)
        View viewConnect;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void onItemSelectedListener(MyClickListener clickListener)
    {
        myClickListener=clickListener;
    }

    public interface MyClickListener
    {
         void onItemClick(int layoutPosition, View view);
    }
}
