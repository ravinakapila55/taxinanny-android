package com.taxi.nanny.views.login_section.register;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.login_section.dialog.RegisterSuccessAlert;
import com.taxi.nanny.views.login_section.register.parent.SkipAddRider;
import butterknife.BindView;
import butterknife.OnClick;

public class OTPConfirmationActivity extends BaseActivity
{
    protected int userType;

    @BindView(R.id.tvLabel)
    TextView tvLabel;

    @Override
    protected int getContentId()
    {
        return R.layout.activity_otp_confirmation;
    }

    @BindView(R.id.btn_submit)
    TextView btn_submit;

    @BindView(R.id.edt_otp)
    TextView edt_otp;

    String mobileNumber="";

    SharedPrefUtil sharedPrefUtil;

    @OnClick({R.id.img_back_btn,R.id.btn_submit})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                {
                finish();
                break;
            }
            case R.id.btn_submit:
                {
               /* Intent intent=new Intent(OTPConfirmationActivity.this, SkipAddRider.class);
                startActivity(intent);
                finishAffinity();*/
                if(edt_otp.getText().toString().trim().length()==0)
                {
                    edt_otp.setError("Please enter OTP");
                    edt_otp.requestFocus();
                }
                if(edt_otp.getText().toString().trim().length()<6)
                {
                    edt_otp.setError("Please enter valid OTP");
                    edt_otp.requestFocus();
                }else {
//                    sharedPrefUtil.saveBoolean(SharedPrefUtil.LOGIN, true);
                    if(userType==0)
                    {

                        callSuccessPopup();
                       /* new RegisterSuccessAlert(OTPConfirmationActivity.this,"s")
                        {
                            @Override
                            protected void dataOnClick()
                            {
                               // sharedPrefUtil.saveBoolean(SharedPrefUtil.LOGIN, true);
                                Intent intent=new Intent(OTPConfirmationActivity.this,
                                        SkipAddRider.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        };*/
                    }
                    else
                    {
                            sharedPrefUtil.saveString(SharedPrefUtil.OTP_SAVED,"1");
                       // Intent intent = new Intent(OTPConfirmationActivity.this, UploadDocumentActivity.class);
                        Intent intent = new Intent(OTPConfirmationActivity.this,
                                SelectVehicleTypeActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            }
        }
    }


    public void callSuccessPopup()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        final TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);
        final RelativeLayout rl = (RelativeLayout) dialogView.findViewById(R.id.rl);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        rl.setVisibility(View.GONE);
        tvOk.setVisibility(View.VISIBLE);

        tvMsg.setText("Successfully Verified");


        tvOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(OTPConfirmationActivity.this,
                        SkipAddRider.class);
                startActivity(intent);
                finishAffinity();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userType= getIntent().getIntExtra(Constant.BUNDLE_USER_TYPE,0);

        sharedPrefUtil=SharedPrefUtil.getInstance();
        mobileNumber=sharedPrefUtil.getString(SharedPrefUtil.PHONE_NUMBER,"");



       /* if(userType==1)
        {
            btn_submit.setText(getResources().getString(R.string.txt_next));
        } else
            {
            btn_submit.setText(getResources().getString(R.string.txt_submit));
        }*/
        setHeaderText("Verify Mobile");

        tvLabel.setText("We are unable to auto-verify your mobile number please enter the code sent to +"+mobileNumber);
    }
}
