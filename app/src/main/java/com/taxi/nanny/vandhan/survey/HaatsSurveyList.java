package com.taxi.nanny.vandhan.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.vandhan.AddHatsDEtail;
import com.taxi.nanny.views.BaseActivity;
import butterknife.BindView;
import butterknife.OnClick;

public class HaatsSurveyList extends BaseActivity {

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    @BindView(R.id.tvEnd)
    TextView tvEnd;


    @Override
    protected int getContentId() {
        return R.layout.haats_survey_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Haat Survey");
        tvEnd.setVisibility(View.VISIBLE);
        ivEnd.setVisibility(View.GONE);
        tvEnd.setText("Add");
        setAdapter();
    }

    public void setAdapter()
    {
        HaatsSurveyAdapter adapter=new HaatsSurveyAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }


    @OnClick({R.id.tvEnd})
    public void onclick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvEnd:
                Intent intent=new Intent(this, AddHaatsSurveyInitial.class);
                startActivity(intent);
                break;
        }
    }
}
