package com.taxi.nanny.views.booking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.views.BaseActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ParentProfile extends BaseActivity {


    @BindView(R.id.tvCall)
    TextView tvCall;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvRiderName)
    TextView tvRiderName;

    @BindView(R.id.tvPick)
    TextView tvPick;

    @BindView(R.id.tvDest)
    TextView tvDest;

    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.tvFare)
    TextView tvFare;

    @BindView(R.id.tvPreference)
    TextView tvPreference;

    @BindView(R.id.llInstructions)
    LinearLayout llInstructions;

    @BindView(R.id.tvpickInstructions)
    TextView tvpickInstructions;

    @BindView(R.id.tvDropInstructions)
    TextView tvDropInstructions;

    @BindView(R.id.llPreference)
    LinearLayout llPreference;

    ArrayList<RiderListModel> riderList;
    String time="";

    @BindView(R.id.ivRider)
    CircleImageView ivRider;

    @Override
    protected int getContentId()
    {
        return R.layout.parent_profile;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Ride Detail");
        riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
        time=getIntent().getExtras().getString("time");
        setData();
    }

    public void setData()
    {

        if (!riderList.get(0).getImage().equalsIgnoreCase(""))
        {
            Picasso.with(this).load(riderList.get(0).getImage()).placeholder
                    (getResources().getDrawable(R.drawable.pic_dummy_user)).into(ivRider);
        }


        tvTime.setText(time);
        tvPick.setText(riderList.get(0).getPickup());
        tvDest.setText(riderList.get(0).getDropup());


        double dd=Double.parseDouble(riderList.get(0).getEstPrice());

      String  amount= new DecimalFormat("##.##").format(dd);

        tvFare.setText("$ "+amount);

        String pickInst=riderList.get(0).getPick_instructions();
        String dropInst=riderList.get(0).getDrop_instructions();

        if (pickInst.equalsIgnoreCase("") && dropInst.equalsIgnoreCase(""))
        {
            llInstructions.setVisibility(View.GONE);
        }
        else {
            llInstructions.setVisibility(View.VISIBLE);

            if (!pickInst.equalsIgnoreCase("") && dropInst.equalsIgnoreCase(""))
            {
                tvpickInstructions.setVisibility(View.VISIBLE);
                tvDropInstructions.setVisibility(View.GONE);
                tvpickInstructions.setText("Pick:-"+pickInst);
            }
           else if (pickInst.equalsIgnoreCase("") && !dropInst.equalsIgnoreCase(""))
            {
                tvpickInstructions.setVisibility(View.GONE);
                tvDropInstructions.setVisibility(View.VISIBLE);
                tvDropInstructions.setText("Drop:-"+dropInst);
            } else if (!pickInst.equalsIgnoreCase("") && !dropInst.equalsIgnoreCase(""))
            {
                tvpickInstructions.setVisibility(View.VISIBLE);
                tvDropInstructions.setVisibility(View.VISIBLE);
                tvpickInstructions.setText("Pick:-"+pickInst);
                tvDropInstructions.setText("Drop:-"+dropInst);
            }
        }

        String kidsInPref=riderList.get(0).getKids_in();
        String kidsOutPref=riderList.get(0).getKids_out();





        if (kidsInPref.equalsIgnoreCase("0") && kidsOutPref.equalsIgnoreCase("0"))
        {
            llPreference.setVisibility(View.GONE);
        }
        else {
            llPreference.setVisibility(View.VISIBLE);

            if (kidsInPref.equalsIgnoreCase("1") && kidsOutPref.equalsIgnoreCase("0"))
            {
                tvPreference.setText("Kids-In");
            }else if (kidsInPref.equalsIgnoreCase("0") && kidsOutPref.equalsIgnoreCase("1"))
            {
                tvPreference.setText("KIds-Out");
            }if (kidsInPref.equalsIgnoreCase("1") && kidsOutPref.equalsIgnoreCase("1"))
            {
                tvPreference.setText("kids-In" + ","+"Kids-Out");
            }
        }


    }


    @OnClick({R.id.tvCall,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {

            case R.id.tvCall:

                Log.e("onClick: PhoneNumber ",riderList.get(0).getPhone_number());
                String phone = riderList.get(0).getPhone_number();
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", phone, null));
                startActivity(intent);
                break;

            case R.id.img_back_btn:
                onBackPressed();
                break;

        }
    }
}
