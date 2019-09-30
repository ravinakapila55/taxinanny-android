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

public class EditHatsDEtail extends BaseActivity {

    @BindView(R.id.tvEnd)
    TextView tvEnd;

    @BindView(R.id.ivEnd)
    ImageView ivEnd;



    @Override
    protected int getContentId() {
        return R.layout.edit_hats;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvEnd.setVisibility(View.VISIBLE);
        tvEnd.setText("Save");
        ivEnd.setVisibility(View.GONE);
        setHeaderText("Edit Haats Detail");
    }

    @OnClick({R.id.tvEnd})
    public void onclick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvEnd:

                Intent intent=new Intent(this,HaatsDEtail.class);
                startActivity(intent);

                break;
        }
    }
}
