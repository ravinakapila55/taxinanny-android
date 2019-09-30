package com.taxi.nanny.views.driver.earning.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.EarningModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder>
{

    Context context;
    ArrayList<EarningModel> list;

    public TripAdapter(Context context,ArrayList<EarningModel> list)
    {
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_trip,viewGroup,false);
        return new TripAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvName.setText(list.get(position).getParent_name());
        holder.tvAmountEarned.setText(list.get(position).getAmount());

        Picasso.with(context).load(list.get(position).getParent_image()).placeholder(context.getResources().getDrawable(R.drawable.pic_dummy_user))
                .into(holder.ivUser);

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("HH:mm");

        Date date = null, date1 = null;
        String validTime = "";

        try {
            date = input.parse(list.get(position).getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        validTime = output.format(date);

        holder.tvTimeOnline.setText(validTime);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivUser;
        TextView tvName;
        TextView tvTimeOnline;
        TextView tvAmountEarned;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUser=(CircleImageView)itemView.findViewById(R.id.ivUser);
            tvName=(TextView) itemView.findViewById(R.id.tvName);
            tvTimeOnline=(TextView) itemView.findViewById(R.id.tvTimeOnline);
            tvAmountEarned=(TextView) itemView.findViewById(R.id.tvAmountEarned);
        }
    }
}
