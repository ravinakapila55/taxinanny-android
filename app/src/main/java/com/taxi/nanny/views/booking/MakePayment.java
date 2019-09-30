package com.taxi.nanny.views.booking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.CardList;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.payment.AddCard;
import com.taxi.nanny.views.payment.adapter.PaymentListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakePayment extends BaseActivity implements RetrofitResponse, Callback<ResponseBody> {


    @BindView(R.id.ll)
    LinearLayout ll;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @BindView(R.id.tvContinue)
    TextView tvContinue;

    SharedPrefUtil sharedPrefUtil;

    ArrayList<CardList> list=new ArrayList<>();
    String token;

  public static   String generatedToken="";
  public static   String cardId="";

    String booking_id="",booking_amount="";

    @Override
    protected int getContentId() {
        return R.layout.choose_card;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Make Payment");
        ll.setVisibility(View.GONE);
        tvContinue.setVisibility(View.VISIBLE);


        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");

        if (getIntent().hasExtra("booking_id"))
        {
            booking_id=getIntent().getExtras().getString("booking_id");
            booking_amount=getIntent().getExtras().getString("booking_amount");
            tvContinue.setText("Make Payment:-$ "+booking_amount);
        }

    }

    private void callMakePayment()
    {
        HashMap<String, String> param = new HashMap<>();
        param.put("amount",booking_amount);
        param.put("booking_id", booking_id);
        param.put("cardid", cardId);
        Log.e("callMakePayment:Params ",param.toString() );
        api(param,this,token,30);
    }


    public boolean checkValdations()
    {
        if (generatedToken.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Choose card for payment", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick({R.id.tvContinue,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvContinue:

                if (checkValdations())
                {

                    callMakePayment();

                }
                break;

            case R.id.img_back_btn:
                finish();
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
        PaymentListAdapter paymentListAdapter=new PaymentListAdapter(this,list,"Pay");
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

                Log.e("ResponseCard ",response);

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

        Log.e("GeneralBooking<< ",generalResponse+"");

        try{
            JSONObject jsonObject = generalResponse.getResponse_object();

            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                callProceedBooking();
            }
            else {
                Toast.makeText(this,"Payment Not Successful ,Please try again" , Toast.LENGTH_SHORT).show();

            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }

    public void callProceedBooking()
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

        tvMsg.setText("Payment Successfully Done !! You can now proceed with your new  booking");


        tvOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MakePayment.this, ChooseCard.class);
                setResult(300,intent);
                finish();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
