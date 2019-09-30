package com.taxi.nanny.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


public class CustomButtonTextRegular extends Button {

    public CustomButtonTextRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomButtonTextRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButtonTextRegular(Context context) {
        super(context);
        init();
    }

    public void init() {
        if (!isInEditMode()){
       	Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        	setTypeface(normalTypeface);
        }
    }
}