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
import com.taxi.nanny.model.ScheduleBookingRiderDetailsModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BookingDetailsAdapter extends RecyclerView.Adapter<BookingDetailsAdapter.MyViewHolder> {

    Context context;
    ArrayList<ScheduleBookingRiderDetailsModel> list;

    public BookingDetailsAdapter(Context context, ArrayList<ScheduleBookingRiderDetailsModel> list) {
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_way_bill,viewGroup,false);
        return new BookingDetailsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position)
    {

        myViewHolder.tvName.setText(list.get(position).getName());
        myViewHolder.tvPick.setText(list.get(position).getPick_loc());
        myViewHolder.tvDest.setText(list.get(position).getDest_loc());

        if (!list.get(position).getImage().equalsIgnoreCase(""))
        {
            Picasso.with(context).load(list.get(position).getImage()).placeholder(context.getResources().getDrawable(R.drawable.pic_dummy_user))
                    .into(myViewHolder.ivUser);
        }

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvAll)
        TextView tvAll;

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvTime)
        TextView tvTime;

        @BindView(R.id.tvCash)
        TextView tvCash;

        @BindView(R.id.ivUser)
        CircleImageView ivUser;

        @BindView(R.id.tvPick)
        TextView tvPick;

        @BindView(R.id.tvDest)
        TextView tvDest;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tvAll.setVisibility(View.GONE);
            tvCash.setVisibility(View.INVISIBLE);
        }
    }
}
