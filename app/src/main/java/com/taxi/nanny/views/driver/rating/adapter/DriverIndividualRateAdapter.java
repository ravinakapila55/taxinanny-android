package com.taxi.nanny.views.driver.rating.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.taxi.nanny.R;

public class DriverIndividualRateAdapter extends RecyclerView.Adapter<DriverIndividualRateAdapter.MyViewHolder> {


    Context context;

    public DriverIndividualRateAdapter(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(context).inflate(R.layout.custom_individual_rating,viewGroup,false);
        return new DriverIndividualRateAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        if (position==0)
        {
            myViewHolder.tvPerson.setText("841");
            myViewHolder.tvStar.setText("5");
            myViewHolder.progress.setProgress(5);
        } else if (position==1)
        {
            myViewHolder.tvPerson.setText("345");
            myViewHolder.tvStar.setText("4");
            myViewHolder.progress.setProgress(4);
        } else if (position==2)
        {
            myViewHolder.tvPerson.setText("234");
            myViewHolder.tvStar.setText("3");
            myViewHolder.progress.setProgress(3);
        } else if (position==3)
        {
            myViewHolder.tvPerson.setText("1200");
            myViewHolder.tvStar.setText("2");
            myViewHolder.progress.setProgress(2);
        } else if (position==4)
        {
            myViewHolder.tvPerson.setText("1002");
            myViewHolder.tvStar.setText("1");
            myViewHolder.progress.setProgress(1);
        }

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        ProgressBar progress;
        TextView tvPerson;
        TextView tvStar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            progress=(ProgressBar)itemView.findViewById(R.id.progress);
            tvPerson=(TextView) itemView.findViewById(R.id.tvPerson);
            tvStar=(TextView) itemView.findViewById(R.id.tvStar);
        }
    }
}
