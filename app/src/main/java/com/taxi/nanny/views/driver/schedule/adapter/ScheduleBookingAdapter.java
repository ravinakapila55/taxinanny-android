package com.taxi.nanny.views.driver.schedule.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.ScheduleBookingsModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ScheduleBookingAdapter extends RecyclerView.Adapter<ScheduleBookingAdapter.MyViewHolder> {

    Context context;
    myClickListener clickListener;
    ArrayList<ScheduleBookingsModel> list;

    public ScheduleBookingAdapter(Context context, ArrayList<ScheduleBookingsModel> scheduleList) {
        this.context = context;
        this.list=scheduleList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_bookings,viewGroup,false);
        return new ScheduleBookingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (!list.get(i).getParent_image().equalsIgnoreCase("null"))
        {
            Picasso.with(context).load(list.get(i).getParent_image()).placeholder(context
            .getResources().getDrawable(R.drawable.pic_dummy_user)).into(myViewHolder.ivUser);
        }

        myViewHolder.tvName.setText(list.get(i).getParent_name());

        if (!list.get(i).getBooking_time().equalsIgnoreCase("null"))
        {
            myViewHolder.tvTime.setVisibility(View.VISIBLE);
            myViewHolder.tvTime.setText("Time :"+list.get(i).getBooking_time());
        }
        else {
           myViewHolder.tvTime.setVisibility(View.GONE);
        }


//        myViewHolder.tvType.setText("Booking Type:- Recurring");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivUser)
        CircleImageView ivUser;

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvType)
        TextView tvType;

        @BindView(R.id.tvBook)
        TextView tvBook;

        @BindView(R.id.tvTime)
        TextView tvTime;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);

            tvBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition(),v);
                }
            });
        }
    }

    public void onItemSelectedListener(myClickListener listener)
    {
        this.clickListener=listener;
    }

    public interface myClickListener{
        public void onItemClick(int layoutPosition,View view);


    }

}
