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
import com.taxi.nanny.model.RideHistoryModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class WayBillDriverAdapter extends RecyclerView.Adapter<WayBillDriverAdapter.MyViewHolder> {

    Context context;
    MyClickListener clickListener;
    ArrayList<RideHistoryModel> list;

    public WayBillDriverAdapter(Context context, ArrayList<RideHistoryModel> list) {
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_way_bill,viewGroup,false);
        return new WayBillDriverAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position)
    {
        if (list.get(position).getRideArrayLength().equalsIgnoreCase("1"))
        {
            myViewHolder.tvAll.setVisibility(View.VISIBLE);
        }
        else
        {
            myViewHolder.tvAll.setVisibility(View.GONE);
        }

        myViewHolder.tvCash.setText(list.get(position).getAmount());
        myViewHolder.tvTime.setText(list.get(position).getDate());


        if (list.get(position).getRiderList().get(0).getFirst_name().equalsIgnoreCase("null"))
        {
            myViewHolder.tvName.setText("NA");
        }
        else {
            myViewHolder.tvName.setText(list.get(position).getRiderList().get(0).getFirst_name()+" "+list.get(position).getRiderList().get(0).getLast_name());
        }

        myViewHolder.tvPick.setText(list.get(position).getRiderList().get(0).getPickup());
        myViewHolder.tvDest.setText(list.get(position).getRiderList().get(0).getDropup());

        if (list.get(position).getRiderList().get(0).getImage()!=null)
        {
            Picasso.with(context).load(list.get(position).getRiderList().get(0).getImage()).
                    placeholder(context.getResources().
                    getDrawable(R.drawable.pic_dummy_user)).into(myViewHolder.ivUser);
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
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


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            tvAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onViewAllClick(getAdapterPosition(),v);
                }
            });
        }
    }

    public void onItemSelectedListener(MyClickListener clickListener)
    {
        this.clickListener=clickListener;
    }

    public interface MyClickListener
    {
        public void onViewAllClick(int layoutPosition,View view);
    }
}
