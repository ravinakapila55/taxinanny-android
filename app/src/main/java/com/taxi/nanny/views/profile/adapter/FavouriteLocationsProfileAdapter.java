package com.taxi.nanny.views.profile.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RiderListModel;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteLocationsProfileAdapter extends RecyclerView.Adapter<FavouriteLocationsProfileAdapter.MyViewHolder> {
     Context context;
    ArrayList<RiderListModel> list;
    onClickListener onClickListener;

    public FavouriteLocationsProfileAdapter(Context context, ArrayList<RiderListModel> list) {
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_profile_fav,viewGroup,false);
        return new FavouriteLocationsProfileAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.tvLocation.setText(list.get(position).getFav_loc());
        holder.tvNick.setText(list.get(position).getFav_nick_name());

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvLocation)
        TextView tvLocation;

        @BindView(R.id.tvNick)
        TextView tvNick;

        @BindView(R.id.ivEdit)
        ImageView ivEdit;


        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);

            ivEdit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    onClickListener.onEditClick(getAdapterPosition(),v);
                }
            });
        }
    }

    public void onItemSelectedLister(onClickListener clickListener )
    {
        onClickListener=clickListener;
    }

    public interface  onClickListener
    {
        public void onEditClick(int layoutPosition,View view);
    }
}
