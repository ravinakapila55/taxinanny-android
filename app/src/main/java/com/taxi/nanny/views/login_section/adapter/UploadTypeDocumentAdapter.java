package com.taxi.nanny.views.login_section.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.UploadDocumentType;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.login_section.register.UploadDocumentActivity;
import com.taxi.nanny.views.login_section.register.UploadDrivingActivity;
import com.taxi.nanny.views.login_section.register.UploadPermit;
import com.taxi.nanny.views.login_section.register.UploadVehicleInsuranceActivity;
import com.taxi.nanny.views.login_section.register.UploadVehicleRegistration;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadTypeDocumentAdapter extends RecyclerView.Adapter<UploadTypeDocumentAdapter.UploadDocumentHolder>
{
    ArrayList<UploadDocumentType>  title ;
    int selectPos=-1;
    Context context;

    public static int selectedType=0;
    SharedPrefUtil sharedPrefUtil;
    public UploadTypeDocumentAdapter(ArrayList<UploadDocumentType> title,Context context)
    {
       this. title=title;
       this.context=context;
    }

    @NonNull
    @Override
    public UploadDocumentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.upload_document_typeview,viewGroup,false);
        return new UploadDocumentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadDocumentHolder uploadDocumentHolder, int i)
    {
        sharedPrefUtil=SharedPrefUtil.getInstance();

        uploadDocumentHolder.txt_title.setText((i+1)+") "+title.get(i).getTitle());

        if (title.get(i).isStatus())
        {
            uploadDocumentHolder.img_tick.setVisibility(View.VISIBLE);
            uploadDocumentHolder.rlll.setBackgroundColor(context.getResources().getColor(R.color.white));

            if (uploadDocumentHolder.txt_title.getText().toString().trim().equalsIgnoreCase("1) Please upload your Driving licence"))
            {
                uploadDocumentHolder.txt_title.setText("Driving Licence Uploaded");
            } else if (uploadDocumentHolder.txt_title.getText().toString().trim().equalsIgnoreCase("1) Please upload your Vehicle insurance."))
            {
                uploadDocumentHolder.txt_title.setText("Vehicle Insurance Uploaded");
            } else if (uploadDocumentHolder.txt_title.getText().toString().trim().equalsIgnoreCase("1) Please upload your Vehicle Permit"))

            {
                uploadDocumentHolder.txt_title.setText("Permit:- "+sharedPrefUtil.getString(SharedPrefUtil.PERMIT_FILE,""));
            } else if (uploadDocumentHolder.txt_title.getText().toString().trim().equalsIgnoreCase("1) Please upload your Vehicle Registration"))

            {
                uploadDocumentHolder.txt_title.setText("Vehicle Registration:- "+sharedPrefUtil.getString(SharedPrefUtil.REGISTRATION_File,""));
            }
            uploadDocumentHolder.txt_title.setEnabled(false);
        }
        else {
            uploadDocumentHolder.img_tick.setVisibility(View.INVISIBLE);
            uploadDocumentHolder.rlll.setBackgroundColor(context.getResources().getColor(R.color.lighter_gray));

            uploadDocumentHolder.txt_title.setText((i+1)+") "+title.get(i).getTitle());
            uploadDocumentHolder.txt_title.setEnabled(true);

            Log.e("Value  ",uploadDocumentHolder.txt_title.getText().toString().trim());
            uploadDocumentHolder.txt_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Log.e( "onClick:IDDD ",UploadDocumentActivity.vehicleId );
                    if (uploadDocumentHolder.txt_title.getText().toString().trim().equalsIgnoreCase("1) Please upload your Driving licence"))
                    {
                        //driving
                        Intent intent = new Intent(context, UploadDrivingActivity.class);
                        intent.putExtra("vehicleId",UploadDocumentActivity.vehicleId);
                        context.  startActivity(intent);
                        return;
                    }
                    else if (uploadDocumentHolder.txt_title.getText().toString().trim().equalsIgnoreCase("1) Please upload your Vehicle insurance."))
                    {
                        //insurance
                        Log.e( "onClick:IDDDINISDE ",UploadDocumentActivity.vehicleId );
                        Intent intent = new Intent(context, UploadVehicleInsuranceActivity.class);
                        intent.putExtra("vehicleId",UploadDocumentActivity.vehicleId);
                        context.  startActivity(intent);
                        return;
                    }
                    else if (uploadDocumentHolder.txt_title.getText().toString().trim().equalsIgnoreCase("1) Please upload your Vehicle Permit"))
                    {
                        //permit
                        Intent intent = new Intent(context, UploadPermit.class);
                        intent.putExtra("vehicleId",UploadDocumentActivity.vehicleId);
                        context. startActivity(intent);
                        return;
                    }
                    else if (uploadDocumentHolder.txt_title.getText().toString().trim().equalsIgnoreCase("1) Please upload your Vehicle Registration"))
                    {
                        //registration
                        Intent intent = new Intent(context, UploadVehicleRegistration.class);
                        intent.putExtra("vehicleId",UploadDocumentActivity.vehicleId);
                        context. startActivity(intent);
                        return;

                    }
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    class UploadDocumentHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.txt_title)
        TextView txt_title;

        @BindView(R.id.img_tick)
        ImageView img_tick;

        @BindView(R.id.rlll)
        RelativeLayout rlll;

        public UploadDocumentHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);



        }
    }
}
