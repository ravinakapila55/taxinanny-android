package com.taxi.nanny.views.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.AddTip;
import com.taxi.nanny.views.booking.ChooseCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCard extends BaseActivity implements Callback<ResponseBody>
{
    @BindView(R.id.card_name)
    CardView card_name;

    @BindView(R.id.card_input_widget)
    CardMultilineWidget card_input_widget;

    @BindView(R.id.tvSave)
    TextView tvSave;

    Card card;
    String token;
    String key;



    @BindView(R.id.et_card_name)
    EditText et_card_name;

    SharedPrefUtil sharedPrefUtil;

    @Override
    protected int getContentId()
    {
        return R.layout.stripe;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Add Card Details");

        card_name.setVisibility(View.VISIBLE);

        sharedPrefUtil=SharedPrefUtil.getInstance();

        if (getIntent().hasExtra("key"))
        {
            key=getIntent().getExtras().getString("key");
        }
    }


    @OnClick({R.id.img_back_btn,R.id.tvSave})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
            {
                finish();
            }
            break;

            case R.id.tvSave:
                card = card_input_widget.getCard();
                 token=sharedPrefUtil.getString(Constant.TOKEN,"");

                if (checkValidations())
                {
                    progress = new ProgressDialog(this);
                    progress.setMessage("Processing...");
                    progress.setCancelable(false);
                    progress.show();
                    createToken();
                }
                break;
        }
    }

    public boolean checkValidations()
    {
        if (et_card_name.getText().toString().trim().isEmpty())
        {
            et_card_name.setError("Please enter card holder name");
            et_card_name.requestFocus();
           // Toast.makeText(this, "Please enter card holder name ", Toast.LENGTH_SHORT).show();
            return false;
        }

       /* else if(!card_input_widget.validateAllFields())
        {
            Toast.makeText(this, "Please enter card details", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        return true;
    }

    public void callAddCard()
    {
        Log.e("CardExpiry ",card.getExpYear()+"");
        Log.e("CardExpiryMonth ",card.getExpMonth()+"");

        String month= String.valueOf(card.getExpMonth());

        HashMap<String,String> mapMonth=new HashMap<>();
        mapMonth.put("1","01");
        mapMonth.put("2","02");
        mapMonth.put("3","03");
        mapMonth.put("4","04");
        mapMonth.put("5","05");
        mapMonth.put("6","06");
        mapMonth.put("7","07");
        mapMonth.put("8","08");
        mapMonth.put("9","09");
        mapMonth.put("10","10");
        mapMonth.put("11","11");
        mapMonth.put("12","12");

        String exactMOnth=String.valueOf(mapMonth.get(month));

        String year=new String(String.valueOf(card.getExpYear()));
        Log.e("year ",year.substring(2,year.length()));

        String expiry=String.valueOf(exactMOnth+"/"+year.substring(2,year.length()));

        HashMap<String, String> param = new HashMap<>();

        param.put("card_holder_name",et_card_name.getText().toString().trim());
        param.put("card_number",card.getNumber());
        param.put("expiry_date",expiry.trim());
        param.put("cvv",card.getCVC());
        param.put("stripe_token",generatedToken);

        Log.e("AddCard ",param.toString());
       api(param,this,token,27);
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e("GeneralAddCard<< ",generalResponse+"");

        try {
            JSONObject jsonObject=generalResponse.getResponse_object();
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {

                if (key.equalsIgnoreCase("book"))
                {
                    Intent intent = new Intent(this, ChooseCard.class);
                    setResult(50,intent);
                    finish();
                } else if (key.equalsIgnoreCase("tip"))
                {
                    Intent intent = new Intent(this, AddTip.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(this, PaymentList.class);
                    startActivity(intent);
                    finish();
                }


            }
            else {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    ProgressDialog progress;
    String generatedToken="";

    private static final String PUBLISHABLE_KEY = "pk_test_MaQhP2b8XqG9R7PTL6VUmipt";
    private static final String SECRET_KEY = "sk_test_lC0U9YV8dratMHpIrJ8ObVrK00QUgucAmv";
    public void createToken()
    {
        Log.e( "onClick: insideCreateToken ","inside" );

        final Stripe stripe = new Stripe(this, PUBLISHABLE_KEY);
//        final Card card = Card.create("4242424242424242", 12, 2020, "123");
        stripe.createToken(card,new TokenCallback()
        {
            public void onSuccess(@NonNull Token token)
            {
                // Send token to your server
                Log.e("onSuccess:TokenType ",token.getType());
                Log.e("onSuccess:TokenID ",token.getId());//generated token here
                generatedToken= String.valueOf(token.getId());
                Log.e( "onSuccess:generatedToken ",generatedToken);
                progress.dismiss();
                callAddCard();
            }

            public void onError(@NonNull Exception error)
            {
                // Show localized error message
                Log.e( "Onerror ",error+"");
                Toast.makeText(AddCard.this,"Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
