package com.taxi.nanny.views.driver.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.model.RecurringDaysModel;
import com.taxi.nanny.views.booking.adapter.ListofDaysAdapter;

import java.util.ArrayList;

public class RecurringDaysAdapter extends RecyclerView.Adapter<RecurringDaysAdapter.MyViewHolder> {
    Context context;

    ArrayList<RecurringDaysModel> list;

    public RecurringDaysAdapter(Context context, ArrayList<RecurringDaysModel> daysRecurringList) {
        this.context = context;
        this.list=daysRecurringList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_recurring_days,viewGroup,false);
        return new RecurringDaysAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.tvDay.setText(list.get(position).getDay());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvDay,tvDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate=(TextView)itemView.findViewById(R.id.tvDate);
            tvDay=(TextView)itemView.findViewById(R.id.tvDay);
        }
    }
}
