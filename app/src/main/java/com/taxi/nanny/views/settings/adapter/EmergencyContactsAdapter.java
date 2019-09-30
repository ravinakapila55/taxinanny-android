package com.taxi.nanny.views.settings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.model.EmergencyContactList;
import com.taxi.nanny.views.settings.EmergencyContacts;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmergencyContactsAdapter extends RecyclerView.Adapter<EmergencyContactsAdapter.MyViewHolder>
{

    Context context;
    ArrayList<EmergencyContactList> list;

    public EmergencyContactsAdapter(Context context, ArrayList<EmergencyContactList> list) {
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_emergency_contacts,viewGroup,false);
        return new EmergencyContactsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i)
    {
            myViewHolder.tvName.setText(list.get(i).getName());
            myViewHolder.tvNumber.setText(list.get(i).getNumber());
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvNumber)
        TextView tvNumber;


        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
