package com.taxi.nanny.views.booking.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RideHistoryModel;
import com.taxi.nanny.model.RiderListModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.MyViewHolder>
{
    HistoryAdapter parentAdapter;

    Context context;
    ArrayList<RiderListModel> list;

    public BookingHistoryAdapter(Context context, ArrayList<RiderListModel> rides_list)
    {
        this.context = context;
        this.list=rides_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_rider_detail,viewGroup,false);
        return new BookingHistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i)
    {
        if (list.get(i).getFirst_name().equalsIgnoreCase(""))
        {
            myViewHolder.tvName.setText("NA");
        }
         else
         {
            myViewHolder.tvName.setText(list.get(i).getFirst_name());
         }

        Picasso.with(context).load(list.get(i).getImage()).
                placeholder(context.getResources().getDrawable(R.drawable.placeholder)).into(myViewHolder.ivUser);

        if (list.get(i).getRide_status().equalsIgnoreCase("0"))
        {
            myViewHolder.tvStatus.setText("Pending");
        }else if (list.get(i).getRide_status().equalsIgnoreCase("1"))
        {
            myViewHolder.tvStatus.setText("Accepted");
        }else if (list.get(i).getRide_status().equalsIgnoreCase("2"))
        {
            myViewHolder.tvStatus.setText("Rejected");
        }else if (list.get(i).getRide_status().equalsIgnoreCase("3"))
        {
            myViewHolder.tvStatus.setText("On the Way");
        }else if (list.get(i).getRide_status().equalsIgnoreCase("4"))
        {
            myViewHolder.tvStatus.setText("Completed");
        }else if (list.get(i).getRide_status().equalsIgnoreCase("5"))
        {
            myViewHolder.tvStatus.setText("Arrive");
        }


    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView ivUser;
        TextView tvName;
        TextView tvStatus;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ivUser=(CircleImageView)itemView.findViewById(R.id.ivUser);
            tvName=(TextView)itemView.findViewById(R.id.tvName);
            tvStatus=(TextView)itemView.findViewById(R.id.tvStatus);
        }
    }
}
