package com.taxi.nanny.vandhan.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class AddHaatsSurveySecond extends BaseActivity {

    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    @BindView(R.id.tvEnd)
    TextView tvEnd;

    String[] regulation={"Select"};
    String[] days={"Select day"};

    String[] predict={"Select"};
    @BindView(R.id.SpRegulation)
    Spinner SpRegulation;

    @BindView(R.id.SpPeriodicity)
    Spinner SpPeriodicity;

    @BindView(R.id.SpDays)
    Spinner SpDays;

    @Override
    protected int getContentId() {
        return R.layout.add_haats_survey_seconf;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Haat Survey");
        tvEnd.setVisibility(View.VISIBLE);
        ivEnd.setVisibility(View.GONE);
        tvEnd.setText("Next");
        setSpinner();
    }


    private void setSpinner()
    {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, regulation);

        SpRegulation.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, predict);
        SpPeriodicity.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, days);

        SpDays.setAdapter(adapter2);


    }

    @OnClick({R.id.tvEnd})
    public void onclick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvEnd:
               /* Intent intent=new Intent(this, AddHaatsSurveySecond.class);
                startActivity(intent);*/
                break;
        }
    }
}
