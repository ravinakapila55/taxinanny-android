package com.taxi.nanny.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.taxi.nanny.R;

import butterknife.BindView;

public class FullImage extends BaseActivity {


    @BindView(R.id.image)
    ImageView image;

    @Override
    protected int getContentId() {
        return R.layout.full_image;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Vehicle Image");
    }
}
