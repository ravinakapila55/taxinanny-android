package com.taxi.nanny.views.booking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.DriverBookingModel;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.home.ParentHome;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitReview extends BaseActivity implements Callback<ResponseBody>
{
    String driver_name="";
    String ride_id="";
    String driver_id="";
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.rating)
    RatingBar rating;
    String token = "";
    SharedPrefUtil sharedPrefUtil;
    @BindView(R.id.etRating)
    EditText etRating;
    float ratingValue;
    @BindView(R.id.tvRate)
    TextView tvRate;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvHelp)
    TextView tvHelp;
    ArrayList<RiderListModel> riderList;
    ArrayList<DriverBookingModel> driverList;
    String distance="";
    String fare="";

    @BindView(R.id.tvEnd)
    TextView tvEnd;

    @BindView(R.id.tvTip)
    TextView tvTip;



    public static final String TAG=SubmitReview.class.getSimpleName();

    @Override
    protected int getContentId()
    {
        return R.layout.submit_review;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Submit Review");

        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");

        tvEnd.setVisibility(View.VISIBLE);
        tvEnd.setText("Skip");

      /*  if (getIntent().hasExtra("driver_name"))
        {
            riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
            driverList=(ArrayList<DriverBookingModel>) getIntent().getSerializableExtra("driver_list");

            driver_name=getIntent().getExtras().getString("driver_name");
            ride_id=riderList.get(0).getBooking_id();
            driver_id=driverList.get(0).getDriverId();

            distance=riderList.get(0).getEstDistance();
            fare=riderList.get(0).getEstPrice();

            double pp=Double.parseDouble(fare);
            double dd=Double.parseDouble(distance);

            String amount= new DecimalFormat("##.##").format(pp);
            String miles= new DecimalFormat("##.##").format(dd);

            tvPrice.setText("$ "+amount);
            tvDistance.setText(miles+" MI");

            tvName.setText(driver_name);
            Log.e(TAG, "onCreate: RideId"+ride_id );
            Log.e(TAG, "onCreate: driver_id"+driver_id );
        }

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                ratingValue=ratingBar.getRating();
                Log.e(TAG, "onRatingChanged: RatingValue "+ratingValue);
            }
        });*/
    }

    public void callHelpAlert()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.help_popup, null);

        final TextView tvSubmit = (TextView) dialogView.findViewById(R.id.tvSubmit);
        final TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        final EditText etQuery = (EditText) dialogView.findViewById(R.id.etQuery);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (etQuery.getText().toString().trim().equalsIgnoreCase(""))
                {
                    Toast.makeText(SubmitReview.this, "Please write your concern", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                 {
                    alertDialog.dismiss();
                    apiCall="2";
                    callNeedHelp(etQuery);
                }

            }
        });
        alertDialog.show();
    }

    public boolean checkValidations() {

       /* if (ratingValue == 0)
        {
            Toast.makeText(this, "Give Rating", Toast.LENGTH_SHORT).show();
            return false;
        }
        else*/
        if (etRating.getText().toString().trim().equalsIgnoreCase(""))
        {
            etRating.setError("Submit Your Reviews");
            etRating.requestFocus();
            return false;
        }

        return true;
    }


    @OnClick({R.id.img_back_btn,R.id.tvRate,R.id.tvHelp,R.id.tvEnd,R.id.tvTip})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
            {
                finish();
            }
            break;

            case R.id.tvEnd:
            {
                Intent intent=new Intent(SubmitReview.this,ParentHome.class);
                startActivity(intent);
            }
            break;

            case R.id.tvRate:
                if (checkValidations())
                {
                    apiCall="1";
                    callSubmit();
                }
            break;

            case R.id.tvHelp:
                callHelpAlert();
                break;


            case R.id.tvTip:

               Intent intent=new Intent(SubmitReview.this,AddTip.class);
               intent.putExtra("bookingId",ride_id);
               intent.putExtra("driver_id",driver_id);
               startActivity(intent);
                break;
        }
    }



    public void callNeedHelp(EditText et)
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("message", et.getText().toString().trim());

        Log.e("callAddQuery ",param.toString());
        api(param,this,token,35);
    }

    public void callSubmit()
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("booking_id", ride_id);
        param.put("driver_id", driver_id);
        param.put("rating", "3");
        param.put("review", etRating.getText().toString().trim());

        Log.e("callStartParams ",param.toString());
        api(param,this,token,28);
    }

    String apiCall="0";
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        Log.e("RatingResponse ",response+"");
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);
        Log.e(TAG, "generalResponse  "+generalResponse);

            try {

                if (apiCall.equalsIgnoreCase("1"))
                {
                    JSONObject jsonObject = generalResponse.getResponse_object();
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        Intent intent=new Intent(SubmitReview.this, ParentHome.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                    }
                }
                else if (apiCall.equalsIgnoreCase("2"))
                {
                    JSONObject jsonObject = generalResponse.getResponse_object();
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        Intent intent=new Intent(SubmitReview.this, ParentHome.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

    }
}
