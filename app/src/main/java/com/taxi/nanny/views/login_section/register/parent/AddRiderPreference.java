package com.taxi.nanny.views.login_section.register.parent;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.domain.APIRequest;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.home.ParentHome;
import com.taxi.nanny.views.login_section.dialog.InternetErrorDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.SocketHandler;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.taxi.nanny.utils.Constant.TOKEN;

public class AddRiderPreference extends BaseActivity implements Callback<ResponseBody> {
    @BindView(R.id.tvSave)
    TextView tvSave;

    String fname, lname, gender, birthday, booster = "0", front = "0";

    @BindViews({R.id.swBooster, R.id.swFront})
    List<Switch> switchLsit;

    SharedPrefUtil sharedPrefUtil;
    String token = "";
    private String TAG=AddRiderPreference.this.getClass().getSimpleName();

    String imageString="";

    @Override
    protected int getContentId() {
        return R.layout.add_rider_select_booster;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText(getString(R.string.rider_add));

        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");

        Log.e(TAG, "onCreate: TokenValue"+token );

        if (getIntent().hasExtra("fname")) {
            fname = getIntent().getExtras().getString("fname");
            lname = getIntent().getExtras().getString("lname");
            birthday = getIntent().getExtras().getString("birthday");
            gender = getIntent().getExtras().getString("gender");
            imageString = getIntent().getExtras().getString("image");
        }

        switchFunction();
    }

    public void switchFunction() {
        switchLsit.get(0).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    booster = "1";
                } else {
                    booster = "0";
                }
            }
        });

        switchLsit.get(1).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    front = "1";
                } else {
                    front = "0";
                }

            }
        });
    }

    @OnClick({R.id.tvSave, R.id.img_back_btn})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.tvSave: {
                callService();
            }
            break;

            case R.id.img_back_btn:
                finish();
                break;
        }
    }

    public void callService()
    {

        if (imageString.equalsIgnoreCase("1"))
        {
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", AddRider.file.getName(),
                    RequestBody.create(MediaType.parse("image/jpeg"), AddRider.file));

            HashMap<String, RequestBody> param1=new HashMap<>();
            param1.put("first_name",RequestBody.create(MediaType.parse("text/plain"),fname));
            param1.put("last_name",RequestBody.create(MediaType.parse("text/plain"),lname));
            param1.put("parent_id",RequestBody.create(MediaType.parse("text/plain"),sharedPrefUtil.getString(SharedPrefUtil.USER_ID)));
            param1.put("birthday",RequestBody.create(MediaType.parse("text/plain"),birthday));
            param1.put("gender",RequestBody.create(MediaType.parse("text/plain"),gender));
            param1.put("need_booster",RequestBody.create(MediaType.parse("text/plain"),booster));
            param1.put("sit_front_seat",RequestBody.create(MediaType.parse("text/plain"),front));

            Log.e("AddRiderParams1 ",param1.toString());

            apiRequest(param1,filePart,this,token,1);
        }
        else {
            HashMap<String, String> param = new HashMap<>();
            param.put("first_name", fname);
            param.put("last_name", lname);
            param.put("parent_id",sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
            param.put("birthday",birthday);
            param.put("gender",gender);
            param.put("need_booster",booster);
            param.put("sit_front_seat",front);

            Log.e("AddRiderParams ",param.toString());
            api(param,this,token,5);
        }
    }

    protected void apiRequest(HashMap<String, RequestBody> param, MultipartBody.Part image, Callback<ResponseBody> responseBodyCallback, String token , int TYPE_REQUEST) {
        if (isNetworkConnected())
        {
            progressDialog = APIRequest.getProgress(this);
            APIRequest.apiInterface(token).addRider(param,image).enqueue(responseBodyCallback);
        } else {
            new InternetErrorDialog(this)
            {
                @Override
                protected void dataOnClick()
                {
                    apiRequest(param,image,responseBodyCallback,token,TYPE_REQUEST);
                }
            }.show();
        }
    }

    public void callAlert()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
       // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (sharedPrefUtil.getBoolean(SharedPrefUtil.LOGIN))
                {
                    Intent intent=new Intent(AddRiderPreference.this, ListofChildren.class);
                    startActivity(intent);
                    finish();
                    alertDialog.dismiss();
                }
                else {
//                    Intent intent=new Intent(AddRiderPreference.this, ParentHome.class);
                    Intent intent=new Intent(AddRiderPreference.this, ListofChildren.class);
                    startActivity(intent);
                    finish();
                    alertDialog.dismiss();
                }
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AddRiderPreference.this,AddRider.class);
                startActivity(intent);
                finish();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    SharedPrefUtil helper;

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        Log.e(TAG, "onResponse:AddRiderResponse "+response);
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);
        try {
            JSONObject jsonObject=generalResponse.getResponse_object();
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                helper= SharedPrefUtil.getInstance();
                helper.saveString(SharedPrefUtil.CHILD_ADDED,"1");
                callAlert();
            }
            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
