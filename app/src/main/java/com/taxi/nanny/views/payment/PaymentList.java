package com.taxi.nanny.views.payment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.CardList;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.PickDropConfirmation;
import com.taxi.nanny.views.home.ParentHome;
import com.taxi.nanny.views.payment.adapter.PaymentListAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

public class PaymentList extends BaseActivity implements RetrofitResponse
{

    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    SharedPrefUtil sharedPrefUtil;

    ArrayList<CardList> list=new ArrayList<>();
    String token;

    @Override
    protected int getContentId()
    {
        return R.layout.my_cards;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("My Cards");
        ivEnd.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ivEnd.setImageDrawable(getDrawable(R.drawable.add_white));
        }
        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");


    }
    PaymentListAdapter paymentListAdapter;
    @Override
    protected void onResume() {
        super.onResume();
        callCardList();
    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));

         paymentListAdapter=new PaymentListAdapter(this,list,"p");
        recycler.setAdapter(paymentListAdapter);

        paymentListAdapter.onItemSelectedListener(new PaymentListAdapter.myclickListener() {
            @Override
            public void onDeleteClick(int layoutPosition, View view) {
               callAlert(layoutPosition);
            }

            @Override
            public void onTickClick(int layoutPosition, View view) {

            }
        });

    }

    public void callAlert(int layoutPosition)
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

        tvMsg.setText("Are you sure you want to delete this card?");

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                callDeleteService(list.get(layoutPosition).getCard_id());
                list.remove(layoutPosition);
                paymentListAdapter.notifyItemRemoved(layoutPosition);
                paymentListAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    public void callDeleteService(String id)
    {
        new RetrofitService(this, this,
                Constant.API_DELETE_PAYMENT_CARD +"/"+ id, 200, 1,"1").
                callService(true);
    }

    public void callCardList()
    {
        new RetrofitService(this, this, Constant.API_PARENT_CARD_LIST ,
                105, 1,"1").callService(true);
    }


    @OnClick({R.id.img_back_btn,R.id.ivEnd})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;

            case R.id.ivEnd:
                Intent intent=new Intent(this,AddCard.class);
                intent.putExtra("key","card");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(PaymentList.this, ParentHome.class);
        startActivity(intent);
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:

                Log.e("ResponseCard ",response);

                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        tvNoData.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);

                        list.clear();

                        JSONArray Array = jsonObject.getJSONArray("cards");

                        if (Array.length() > 0) {
                            for (int i = 0; i < Array.length(); i++) {

                                JSONObject object = Array.getJSONObject(i);

                                CardList cardList=new CardList();

                                cardList.setCard_id(object.getString("id"));
                                cardList.setNumber(object.getString("card_number"));
                                cardList.setExpiry(object.getString("expiry_date"));
                                cardList.setCvv(object.getString("cvv"));
                                cardList.setName(object.getString("card_holder_name"));
                                cardList.setStripe_token(object.getString("stripe_token"));


                                list.add(cardList);

                                if (list.size()>0)
                                {
                                    setAdapter();
                                }
                            }
                        }
                        else {
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


            case 200:

                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                    callCardList();
                    }
                    else {

                    }

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;
        }

    }

}
