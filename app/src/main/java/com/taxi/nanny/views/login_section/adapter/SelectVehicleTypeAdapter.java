package com.taxi.nanny.views.login_section.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.SelectVehicleTypeBeans;
import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SelectVehicleTypeAdapter extends RecyclerView.Adapter<SelectVehicleTypeAdapter.SelectVehicleTypeHolder>

    {
    Context context;
        int selectPos=-1;
    List<SelectVehicleTypeBeans> selectVehicleTypeBeansList;


        MyClickListener clickListener;

    public SelectVehicleTypeAdapter(List<SelectVehicleTypeBeans> selectVehicleTypeBeansList,Context context) {
        this.selectVehicleTypeBeansList=selectVehicleTypeBeansList;
        this.context=context;
    }

    @NonNull
    @Override
    public SelectVehicleTypeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view=LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.vehicle_type_view,viewGroup,false);
        return new SelectVehicleTypeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectVehicleTypeHolder selectVehicleTypeHolder, int i)
    {
//        if(selectPos==i)
        if(selectVehicleTypeBeansList.get(i).isFlag())
        {
            selectVehicleTypeHolder.rel_desc.setVisibility(View.VISIBLE);
            selectVehicleTypeHolder.img_tick.setVisibility(View.VISIBLE);
            selectVehicleTypeHolder.txtViewTitle.get(0).setTextColor(context.getResources().getColor(R.color.black));
            selectVehicleTypeHolder.imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.mini));
        }
        else {
            selectVehicleTypeHolder.rel_desc.setVisibility(View.GONE);
            selectVehicleTypeHolder.img_tick.setVisibility(View.INVISIBLE);
            selectVehicleTypeHolder.txtViewTitle.get(0).setTextColor(context.getResources().getColor(R.color.dark_gray));
            selectVehicleTypeHolder.imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_car));

            }
        selectVehicleTypeHolder.txtViewTitle.get(0).setText(selectVehicleTypeBeansList.get(i).getName());
        selectVehicleTypeHolder.txtViewTitle.get(1).setText(selectVehicleTypeBeansList.get(i).getDescription());
        selectVehicleTypeHolder.txtViewTitle.get(2).setText(selectVehicleTypeBeansList.get(i).getPassengerSeat()+" Max Person");

        selectVehicleTypeHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectPos=i;
                notifyDataSetChanged();
                clickListener.onItemClick(i,v);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return selectVehicleTypeBeansList.size();
    }

    class SelectVehicleTypeHolder extends RecyclerView.ViewHolder
    {
        @BindViews({R.id.txt_title,R.id.txt_desc,R.id.txt_person_count})
        List<TextView> txtViewTitle;
        @BindView(R.id.img_car_type)
        ImageView imgView;
        @BindView(R.id.img_tick)
        ImageView img_tick;
        @BindView(R.id.rel_desc)
        RelativeLayout rel_desc;

        public SelectVehicleTypeHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void onItemSelectedListener(MyClickListener  listener)
    {
        clickListener=listener;
    }

    public interface MyClickListener
    {
        public void onItemClick(int layoutPosition,View view);
    }
}
