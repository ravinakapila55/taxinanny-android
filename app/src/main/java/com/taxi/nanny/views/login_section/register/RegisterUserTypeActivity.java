package com.taxi.nanny.views.login_section.register;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import com.taxi.nanny.R;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.views.BaseActivity;
import java.util.List;
import butterknife.BindViews;
import butterknife.OnClick;

/*
*    type of
*
* */

public class RegisterUserTypeActivity extends BaseActivity
{
    @Override
    protected int getContentId()
    {
        return R.layout.register_register_usertype;
    }

    @BindViews({R.id.lin_btn_driver,R.id.lin_btn_parent})
    List<LinearLayout> linearLayoutList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Register");
    }

    boolean userType=true;;

    @OnClick({R.id.btn_next,R.id.img_back_btn,R.id.lin_btn_driver,R.id.lin_btn_parent})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_next:
             {
                Intent intent = new Intent(RegisterUserTypeActivity.this,
                        RegisterUserDetailsActivity.class);
                intent.putExtra(Constant.BUNDLE_USER_TYPE,userType?0:1);
                startActivity(intent);
                break;
             }

            case R.id.img_back_btn:
            {
                finish();
                break;
            }

            case R.id.lin_btn_driver:
            {
                userType=false;
                linearLayoutList.get(0).setBackgroundResource(R.drawable.button_green_border);
                linearLayoutList.get(1).setBackgroundColor(Color.WHITE);
                break;
            }

            case R.id.lin_btn_parent:
             {
                userType=true;
                linearLayoutList.get(1).setBackgroundResource(R.drawable.button_green_border);
                linearLayoutList.get(0).setBackgroundColor(Color.WHITE);
                break;
            }
        }
    }

}
