package com.taxi.nanny.views.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.driver.DriverHome;
import com.taxi.nanny.views.home.ParentHome;
import org.json.JSONObject;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends BaseActivity implements Callback<ResponseBody>
{
    @BindView(R.id.edtt_old_pswd)
    EditText edtt_old_pswd;

    @BindView(R.id.edt_new_pswd)
    EditText edt_new_pswd;

    @BindView(R.id.edt_confirm_pswd)
    EditText edt_confirm_pswd;

    @BindView(R.id.btn_submit)
    TextView btn_submit;

    SharedPrefUtil sharedPrefUtil;

    String token;

    @BindView(R.id.pswdValidationCard)
    CardView pswdValidationCard;

    @BindView(R.id.ivTickLimit)
    ImageView ivTickLimit;

    @BindView(R.id.tvLimit)
    TextView tvLimit;

    @BindView(R.id.ivTickCase)
    ImageView ivTickCase;

    @BindView(R.id.tvCase)
    TextView tvCase;

    @BindView(R.id.ivTickAtleast)
    ImageView ivTickAtleast;

    @BindView(R.id.tvAtleast)
    TextView tvAtleast;

    boolean capitalFlag = false;
    boolean lowerCaseFlag = false;
    boolean numberFlag = false;

    @Override
    protected int getContentId()
    {
        return R.layout.change_password;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Change Password");
        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");



        edt_new_pswd.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length()>0)
                {
                    pswdValidationCard.setVisibility(View.VISIBLE);
                    if (s.length()==8)
                    {
                        tvLimit.setTextColor(getResources().getColor(R.color.colorGreen));
                        ivTickLimit.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));
                    }

                    else if (s.length()<8)
                    {
                        tvLimit.setTextColor(getResources().getColor(R.color.black));
                        ivTickLimit.setImageDrawable(getResources().getDrawable(R.drawable.tick_black));
                    }

                    char ch;
                    for(int i=0;i < s.length();i++)
                    {
                        ch = s.charAt(i);
                        if( Character.isDigit(ch))
                        {
                            numberFlag = true;
                        }

                        if (Character.isUpperCase(ch))
                        {
                            capitalFlag = true;
                        }
                        if (Character.isLowerCase(ch))
                        {
                            lowerCaseFlag = true;
                        }
                    }

                    if (capitalFlag && lowerCaseFlag)
                    {
                        tvCase.setTextColor(getResources().getColor(R.color.colorGreen));
                        ivTickCase.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));
                    }
                    else if (!capitalFlag && !lowerCaseFlag)
                    {
                        tvCase.setTextColor(getResources().getColor(R.color.black));
                        ivTickCase.setImageDrawable(getResources().getDrawable(R.drawable.tick_black));
                    }
                    if (numberFlag)
                    {
                        tvAtleast.setTextColor(getResources().getColor(R.color.colorGreen));
                        ivTickAtleast.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));
                    }

                    else if (!numberFlag)
                    {
                        tvAtleast.setTextColor(getResources().getColor(R.color.black));
                        ivTickAtleast.setImageDrawable(getResources().getDrawable(R.drawable.tick_black));
                    }

                    if (numberFlag && capitalFlag && lowerCaseFlag &&( s.length()==8|| s.length()>8))
                    {
                        pswdValidationCard.setVisibility(View.GONE);
                    }
                }
                else
                {
                    pswdValidationCard.setVisibility(View.GONE);
                    numberFlag=false;
                    capitalFlag=false;
                    lowerCaseFlag=false;
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    @OnClick({R.id.img_back_btn,R.id.btn_submit})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;

                case R.id.btn_submit:
                if (checkValidations())
                {
                callChangePassword();
                }
                break;
        }
    }

    public boolean checkValidations()
    {
        if (edtt_old_pswd.getText().toString().trim().isEmpty())
        {
            /*edtt_old_pswd.setError("Enter old password");
            edtt_old_pswd.requestFocus();*/
            Toast.makeText(this, "Enter old password", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (edt_new_pswd.getText().toString().trim().isEmpty())
        {
            /*edt_new_pswd.setError("enter new password");
            edt_new_pswd.requestFocus();*/
            Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (!numberFlag || !capitalFlag || !lowerCaseFlag || edt_new_pswd.getText().toString().trim().length() < 8)
        {
            Toast.makeText(this, getResources().getString(R.string.validate_password), Toast.LENGTH_SHORT).show();
            return  false;
        }

        else if (edt_confirm_pswd.getText().toString().trim().isEmpty())
        {
            /*edt_confirm_pswd.setError("Confirm your new password");
            edt_confirm_pswd.requestFocus();*/
            Toast.makeText(this, "Confirm your password", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (!edt_confirm_pswd.getText().toString().trim().equalsIgnoreCase(edt_new_pswd.getText().toString().trim()))
        {
          /*  edt_confirm_pswd.setError("Password do not match");
            edt_confirm_pswd.requestFocus();*/
            Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void callChangePassword()
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("old_pass",edtt_old_pswd.getText().toString().trim());
        param.put("new_pass", edt_new_pswd.getText().toString().trim());
        param.put("user_type", sharedPrefUtil.getString(SharedPrefUtil.USERTYPE,""));
        Log.e("callMakePayment:Params ",param.toString() );
        api(param,this,token,32);
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e("GeneralBooking<< ",generalResponse+"");

        try
        {
            JSONObject jsonObject = generalResponse.getResponse_object();
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                if (sharedPrefUtil.getString(SharedPrefUtil.USERTYPE,"").equalsIgnoreCase("driver"))
                {
                    Intent intent=new Intent(ChangePassword.this, DriverHome.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(ChangePassword.this, ParentHome.class);
                    startActivity(intent);
                }
            }
            else
            {
                Toast.makeText(this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
