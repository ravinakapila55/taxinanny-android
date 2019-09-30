package com.taxi.nanny.vandhan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taxi.nanny.R;
import com.taxi.nanny.views.home.adapter.HomeCabsAdapter;

public class HaatsListAdapter extends RecyclerView.Adapter<HaatsListAdapter.MyViewHolder> {

    Context context;
    MyClickListener clickListener;

    public HaatsListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.custom_haats,viewGroup,false);
        return new HaatsListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onitemClick(getAdapterPosition(),v);
                }
            });
        }
    }

    public void onItemSelectedListener(MyClickListener myClickListener)
    {
        clickListener=myClickListener;
    }

    public interface MyClickListener{
        public void onitemClick(int layoutPosition,View view);
    }
}
