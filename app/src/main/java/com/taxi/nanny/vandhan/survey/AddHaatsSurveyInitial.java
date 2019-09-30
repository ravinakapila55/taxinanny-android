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
import com.taxi.nanny.vandhan.AddHatsDEtail;
import com.taxi.nanny.views.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class AddHaatsSurveyInitial extends BaseActivity {


    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    @BindView(R.id.tvEnd)
    TextView tvEnd;

    @BindView(R.id.SpState)
    Spinner SpState;

    @BindView(R.id.SpDistrict)
    Spinner SpDistrict;

    @BindView(R.id.SpOwner)
    Spinner SpOwner;

    @BindView(R.id.SpPremises)
    Spinner SpPremises;


    String[] state={"Select State","Punjab","Himachal"};
    String[] district={"Select district"};

    String[] ownership={"Select Ownership of RPM"};
    String[] premises={"Select Own Premises/Leased"};


    @Override
    protected int getContentId() {
        return R.layout.add_haats_survey;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
                this, android.R.layout.simple_spinner_item, state);

        SpState.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, district);
        SpDistrict.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, ownership);

        SpOwner.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, premises);
        SpPremises.setAdapter(adapter3);
    }


    @OnClick({R.id.tvEnd})
    public void onclick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvEnd:
                Intent intent=new Intent(this, AddHaatsSurveySecond.class);
                startActivity(intent);
                break;
        }
    }

}
