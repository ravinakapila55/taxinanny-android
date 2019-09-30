package com.taxi.nanny.views.login_section.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

abstract public class InternetErrorDialog extends Dialog {

    @BindView(R.id.txt_msg)
    TextView txt_msg;
    protected Activity activity;
    public InternetErrorDialog(Activity activity) {
        super(activity,R.style.CustomDialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.activity=activity;
        setContentView(R.layout.internet_error);
        ButterKnife.bind(this);
        show();

    }


    protected abstract void dataOnClick();

    @OnClick({R.id.btn_close,R.id.btn_try_again})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_try_again:{
                if(activity instanceof BaseActivity){
                    dataOnClick();
                    dismiss();
                }
                break;
            }
            case R.id.btn_close:{
                dismiss();

                break;
            }
        }
    }
}
