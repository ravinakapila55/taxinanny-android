package com.taxi.nanny.views.booking.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.views.booking.ChooseCard;
import com.taxi.nanny.views.booking.EnterPickUp;
import com.taxi.nanny.views.booking.MakePayment;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteLocationsAdapter extends RecyclerView.Adapter<FavouriteLocationsAdapter.MyViewHolder>
{
    Context context;
    ArrayList<RiderListModel> list;
    myClickListener listener;

    public FavouriteLocationsAdapter(Context context, ArrayList<RiderListModel> list)
    {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_favourite_location,viewGroup,false);
        return new FavouriteLocationsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewholder, int position)
    {
        viewholder.tvNick.setText(list.get(position).getFav_nick_name());
        viewholder.tvLocation.setText(list.get(position).getFav_loc());

        if (list.get(position).isFlag())
        {
//            viewholder.viewDisableLayout.setVisibility(View.VISIBLE);
            viewholder.constraint.setBackgroundColor(context.getResources().getColor(R.color.stroke_light));
        }
        else
        {
//            viewholder.viewDisableLayout.setVisibility(View.GONE);
            viewholder.constraint.setBackgroundColor(context.getResources().getColor(R.color.white));

        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    private void selectCurrItem(int position)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            if (i == position){
                list.get(i).setFlag(true);
            }
            else
            {
                list.get(i).setFlag(false);
            }


            notifyDataSetChanged();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvNick)
        TextView tvNick;

        @BindView(R.id.tvLocation)
        TextView tvLocation;

        @BindView(R.id.viewDisableLayout)
        View viewDisableLayout;

        @BindView(R.id.constraint)
        ConstraintLayout constraint;

        @BindView(R.id.ivEdit)
        ImageView ivEdit;



        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    listener.onLocationClick(getAdapterPosition(),v);
//                    selectCurrItem(getAdapterPosition());
                }
            });

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEditClick(getAdapterPosition(),v);
                }
            });
        }
    }

    public void onItemSelectedListener(myClickListener clickListener)
    {
        listener=clickListener;
    }

    public interface myClickListener
    {
        public void onLocationClick(int layoutPosition,View view);
        public void onEditClick(int layoutPosition,View view);
    }




}
