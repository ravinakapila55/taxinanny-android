package com.taxi.nanny.vandhan.survey;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.vandhan.HaatsListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HaatsSurveyAdapter extends RecyclerView.Adapter<HaatsSurveyAdapter.MYViewHolder> {

    Context context;

    public HaatsSurveyAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MYViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_haats_survey,viewGroup,false);
        return new HaatsSurveyAdapter.MYViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MYViewHolder myViewHolder, int i) {

        myViewHolder.tvId.setText(String.valueOf(i+1));

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MYViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvId)
        TextView tvId;
        public MYViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
