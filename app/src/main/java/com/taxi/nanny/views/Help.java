package com.taxi.nanny.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.taxi.nanny.R;

import butterknife.OnClick;

public class Help extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.help;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Help");
    }

    @OnClick({R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;
        }
    }
}
