package com.taxi.nanny.views.login_section.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.driver.DriverHome;
import com.taxi.nanny.views.home.ParentHome;
import com.taxi.nanny.views.login_section.register.parent.SkipAddRider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity implements Callback<ResponseBody> {
    
    @BindView(R.id.edt_user_id)
    EditText edt_user_id;
    
    @BindView(R.id.btn_submit)
    TextView btn_submit;

    @BindView(R.id.rgType)
    RadioGroup rgType;

    @BindView(R.id.rbParent)
    RadioButton rbParent;

    @BindView(R.id.rbDriver)
    RadioButton rbDriver;

    String userType="";

    @Override
    protected int getContentId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Forgot Password");

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rbParent:

                        userType="parent";

                        break;

                    case R.id.rbDriver:

                        userType="driver";
                        break;
                }
            }
        });
    }

    @OnClick({R.id.img_back_btn,R.id.btn_submit})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn: {  // Back  BTN
                finish();
                break;
            } 
                case R.id.btn_submit:
                {
                    if (edt_user_id.getText().toString().trim().length() == 0) {
                        edt_user_id.setError(getResources().getString(R.string.val_valid_email_));
                        edt_user_id.requestFocus();
                    }
                    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edt_user_id.getText().toString().trim()).matches())
                    {
                        edt_user_id.setError(getResources().getString(R.string.val_valid_email_));
                        edt_user_id.requestFocus();
                    }
                    else if (userType.equalsIgnoreCase(""))
                    {
                        Toast.makeText(this, "Please choose user type", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        callForget();
                    }
                break;
            }
        }
    }

    private void callForget()
    {
        HashMap<String,String> params=new HashMap<>();
        params.put("email", edt_user_id.getText().toString());
        params.put("user_type", userType);
        api(params,this,"",36);
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();

        GeneralResponse generalResponse=new GeneralResponse(response);
        try {
            JSONObject jsonObject=generalResponse.getResponse_object();
            Log.e("ForgetResponse  ",jsonObject+"");
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                Toast.makeText(this, generalResponse.getResponse().getString("message"),
                        Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, generalResponse.getResponse().getString("message"),
                        Toast.LENGTH_SHORT).show();
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
        progressDialog.dismiss();
    }
}
