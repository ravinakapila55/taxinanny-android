package com.taxi.nanny.views.driver.waybill;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RiderListModel;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class WayBillDetailsAdapter extends RecyclerView.Adapter<WayBillDetailsAdapter.MyViewHolder>
{

    Context context;
    ArrayList<RiderListModel> rides_list;

    public WayBillDetailsAdapter(Context context, ArrayList<RiderListModel> rides_list)
    {
        this.context = context;
        this.rides_list = rides_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_way_bill,viewGroup,false);
        return new WayBillDetailsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.tvName.setText(rides_list.get(position).getFirst_name()+" "+rides_list.get(position).getLast_name());
        holder.tvPick.setText(rides_list.get(position).getPickup());
        holder.tvDest.setText(rides_list.get(position).getDropup());

        if (rides_list.get(position).getImage()!=null)
        {
            Picasso.with(context).load(rides_list.get(position).getImage())
            .placeholder(context.getResources().getDrawable(R.drawable.pic_dummy_user)).into(holder.ivUser);
        }

    }

    @Override
    public int getItemCount()
    {
        return rides_list.size();
    }

    public class  MyViewHolder extends RecyclerView.ViewHolder
    {

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


        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);

            tvAll.setVisibility(View.GONE);
            tvCash.setVisibility(View.INVISIBLE);
            tvTime.setVisibility(View.GONE);
        }
    }
}
