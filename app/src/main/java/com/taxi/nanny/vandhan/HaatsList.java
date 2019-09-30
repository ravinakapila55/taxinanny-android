package com.taxi.nanny.vandhan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class HaatsList extends BaseActivity {

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    @BindView(R.id.tvEnd)
    TextView tvEnd;



    @Override
    protected int getContentId() {
        return R.layout.listing;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        tvEnd.setVisibility(View.GONE);
        ivEnd.setVisibility(View.VISIBLE);

        setHeaderText("Haats List");
        setAdapter();
    }

    public void setAdapter()
    {
        HaatsListAdapter adapter=new HaatsListAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        adapter.onItemSelectedListener(new HaatsListAdapter.MyClickListener() {
            @Override
            public void onitemClick(int layoutPosition, View view) {
                Intent intent=new Intent(HaatsList.this,HaatsDEtail.class);
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.ivEnd})
    public void onclick(View view)
    {
        switch (view.getId())
        {
            case R.id.ivEnd:
                Intent intent=new Intent(this,AddHatsDEtail.class);
                startActivity(intent);
                break;
        }
    }
}
