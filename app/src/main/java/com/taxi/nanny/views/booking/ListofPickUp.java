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
import android.widget.ImageView;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;
import butterknife.BindView;
import butterknife.OnClick;

public class ListofPickUp extends BaseActivity
{
    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvContinue)
    TextView tvContinue;

    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    int size;

    @Override
    protected int getContentId()
    {
        return R.layout.list_pick_up;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("List Pick-Up Location");
        ivEnd.setVisibility(View.GONE);

        if (getIntent().hasExtra("size"))
        {
            size=getIntent().getExtras().getInt("size");
            Log.e("Size ",size+"");
        }
      //  setAdapter();
    }

    @OnClick({R.id.tvContinue,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;

                case R.id.tvContinue:
                    callAlert();
                break;


        }
    }


    /* @OnClick({R.id.tvContinue,R.id.img_back_btn,R.id.ivEnd})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;

                case R.id.tvContinue:
                    callAlert();
                break;

                case R.id.ivEnd:
                    Intent intent=new Intent(ListofPickUp.this, EnterPickUp.class);
                    intent.putExtra("key","multiple");
                    startActivity(intent);
                break;
        }
    }
*/
    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
       /* ListofPickUpAdapter listofPickUpAdapter=new ListofPickUpAdapter(this,size);
        recycler.setAdapter(listofPickUpAdapter);

        listofPickUpAdapter.onItemSelectedListener(new ListofPickUpAdapter.myClickListener() {
            @Override
            public void onItemClick(int layoutPosition, View view) {

            }

            @Override
            public void onPickLocation(int layoutPosition, View view) {
            Intent intent=new Intent(ListofPickUp.this,EnterPickUp.class);
            startActivity(intent);
            }

            @Override
            public void onDRopLocation(int layoutPosition, View view) {
                Intent intent1=new Intent(ListofPickUp.this,EnterDropLocation.class);
                startActivity(intent1);
            }
        });*/

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

        tvMsg.setText("Will all rider's drop up location will be same?");

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
                Intent intent=new Intent(ListofPickUp.this,ListofDropUp.class);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(ListofPickUp.this,EnterDropLocation.class);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
