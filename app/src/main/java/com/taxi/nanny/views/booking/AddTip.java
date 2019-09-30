package com.taxi.nanny.views.booking;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.CardList;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.payment.AddCard;
import com.taxi.nanny.views.payment.adapter.PaymentListAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTip extends BaseActivity implements Callback<ResponseBody>
{
    @BindView(R.id.etTip)
    EditText etTip;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @BindView(R.id.tvContinue)
    TextView tvContinue;

    SharedPrefUtil sharedPrefUtil;

    ArrayList<CardList> list=new ArrayList<>();
    String token;

    String booking_id="",driver_id="";

    public static  String generatedToken="";
    public static  String cardId="";

    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    @Override
    protected int getContentId()
    {
        return R.layout.add_tip;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Add Tip");
        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");

        if (getIntent().hasExtra("booking_id"))
        {
            booking_id=getIntent().getExtras().getString("bookingId");
            driver_id=getIntent().getExtras().getString("driver_id");

            Log.e( "onCreate:BookingId ",booking_id );
            Log.e( "onCreate:DriverId ",driver_id );
        }

        ivEnd.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ivEnd.setImageDrawable(getDrawable(R.drawable.add_white));
        }
    }

    public void callSubmit()
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("booking_id", booking_id);
        param.put("driver_id", driver_id);
        param.put("amount", etTip.getText().toString().trim());
        param.put("card_id", cardId);

        Log.e("callStartParams ",param.toString());
      //  api(param,this,token,28);
    }

    public boolean checkValdations()
    {
        if (etTip.getText().toString().trim().isEmpty())
        {
            etTip.setError("Enter Tip Amount");
            etTip.requestFocus();
            return false;
        }
        else if (generatedToken.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Choose card for payment", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick({R.id.tvContinue,R.id.img_back_btn,R.id.ivEnd})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvContinue:

                if (checkValdations())
                {
                    callSubmit();
                }
                break;

            case R.id.img_back_btn:
                finish();
                break;

            case R.id.ivEnd:

                Intent intent=new Intent(this, AddCard.class);
                intent.putExtra("key","tip");
                startActivity(intent);

                break;

        }
    }

    public void callCardList()
    {
        new RetrofitService(this, this, Constant.API_PARENT_CARD_LIST ,
                105, 1,"1").callService(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        callCardList();
    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        PaymentListAdapter paymentListAdapter=new PaymentListAdapter(this,list,"tip");
        recycler.setAdapter(paymentListAdapter);

        paymentListAdapter.onItemSelectedListener(new PaymentListAdapter.myclickListener()
        {
            @Override
            public void onDeleteClick(int layoutPosition, View view)
            {

            }

            @Override
            public void onTickClick(int layoutPosition, View view)
            {
               /* if (list.get(layoutPosition).isFlag())
                {
                    list.get(layoutPosition).setFlag(false);
                }
                else {
                    list.get(layoutPosition).setFlag(true);
                    generatedToken=list.get(layoutPosition).getStripe_token();
                    Log.e( "onTickClick: ",generatedToken );
                    cardId=list.get(layoutPosition).getCard_id();
                    Log.e( "onTickClick:CardId ",cardId );
                }
                paymentListAdapter.notifyDataSetChanged();
*/
               /* list.get(layoutPosition).setFlag(list.get(layoutPosition).isFlag()?false:true);
                paymentListAdapter.notifyDataSetChanged();*/
            }
        });
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:
                Log.e("ResponseCardList ",response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        tvNoData.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);

                        list.clear();

                        JSONArray Array = jsonObject.getJSONArray("cards");

                        if (Array.length() > 0)
                        {
                            for (int i = 0; i < Array.length(); i++)
                            {

                                JSONObject object = Array.getJSONObject(i);

                                CardList cardList=new CardList();

                                cardList.setCard_id(object.getString("id"));
                                cardList.setNumber(object.getString("card_number"));
                                cardList.setExpiry(object.getString("expiry_date"));
                                cardList.setCvv(object.getString("cvv"));
                                cardList.setStripe_token(object.getString("stripe_token"));
                                cardList.setName(object.getString("card_holder_name"));
                                cardList.setFlag(false);

                                list.add(cardList);

                                if (list.size()>0)
                                {
                                    setAdapter();
                                }
                            }
                        }
                        else
                        {
                            tvNoData.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        }


                    }
                    else {
                        tvNoData.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                    }

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e("onResponse:AddTip<< ",generalResponse+"");

        try {

            JSONObject jsonObject = generalResponse.getResponse_object();
            Log.e("JsonAddTip ", jsonObject + "");
            String newBookAmount="";
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {

            }
            else
            {

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
