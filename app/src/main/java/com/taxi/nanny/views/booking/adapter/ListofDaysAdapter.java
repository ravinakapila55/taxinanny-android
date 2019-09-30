package com.taxi.nanny.views.booking.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.ListofDaysBean;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListofDaysAdapter extends RecyclerView.Adapter<ListofDaysAdapter.MyViewHolder> {

    Context context;
    ArrayList<ListofDaysBean> list;
    myClickListener clickListener;
    public boolean isClickable = false;


    public ListofDaysAdapter(Context context, ArrayList<ListofDaysBean> list)
    {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_days,viewGroup,false);
        return new ListofDaysAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position)
    {
        if (list.get(position).isFlag())
        {
            myViewHolder.img_tick.setVisibility(View.VISIBLE);
        }
        else {
            myViewHolder.img_tick.setVisibility(View.INVISIBLE);
        }
        myViewHolder.tvDay.setText(list.get(position).getDay());

        if (list.get(position).isEnable())
        {
            myViewHolder.viewDisableLayout.setVisibility(View.GONE);
        }
        else
        {
            myViewHolder.viewDisableLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.img_tick)
        ImageView img_tick;

        @BindView(R.id.tvDay)
        TextView tvDay;

        @BindView(R.id.viewDisableLayout)
        View viewDisableLayout;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (list.get(getAdapterPosition()).isEnable())
                    {
                        clickListener.onItemClick(getAdapterPosition(),v);
                    }
                }
            });
        }
    }

    public void onItemClickListener(myClickListener listener)
    {
        clickListener=listener;
    }

    public interface myClickListener
    {
        void onItemClick(int layoutPosition,View view);
    }
}
