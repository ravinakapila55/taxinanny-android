package com.taxi.nanny.vandhan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class HaatsDEtail extends BaseActivity {

    @BindView(R.id.tvEnd)
    TextView tvEnd;

    @BindView(R.id.ivEnd)
    ImageView ivEnd;
    @Override
    protected int getContentId() {
        return R.layout.haats_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvEnd.setVisibility(View.GONE);
        ivEnd.setVisibility(View.VISIBLE);
        ivEnd.setImageDrawable(getResources().getDrawable(R.drawable.edit));
        setHeaderText("Haats Detail");
    }

    @OnClick({R.id.ivEnd})
    public void onclick(View view)
    {
        switch (view.getId())
        {
            case R.id.ivEnd:

                Intent intent=new Intent(this,EditHatsDEtail.class);
                startActivity(intent);

                break;
        }
    }
}
