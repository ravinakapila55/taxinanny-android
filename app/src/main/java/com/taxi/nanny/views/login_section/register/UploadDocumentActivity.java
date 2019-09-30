package com.taxi.nanny.views.login_section.register;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.model.UploadDocumentBeans;
import com.taxi.nanny.model.UploadDocumentType;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.login_section.adapter.UploadDocumentAdapter;
import com.taxi.nanny.views.login_section.login.LoginActivity;
import com.taxi.nanny.views.login_section.register.parent.SkipAddRider;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

public class UploadDocumentActivity extends BaseActivity
{
    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    ArrayList<UploadDocumentBeans> uploadDocumentBeansArrayList;
    public static String vehicleId;
    String selectedtype="";
    public    String key1="";
    public    String key2="";
    public    String key3="";
  public   String key4="";
    SharedPrefUtil helper = SharedPrefUtil.getInstance();
    UploadDocumentAdapter uploadDocumentAdapter;

    @Override
    protected int getContentId()
    {
        return R.layout.upload_document;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        uploadDocumentBeansArrayList=new ArrayList<>();
        setHeaderText(getString(R.string.upload_document));

        if (getIntent().hasExtra("vehicleId"))
        {
            vehicleId=getIntent().getExtras().getString("vehicleId");
            Log.e("VehicleId ",vehicleId);
        }
        setValue();
        setAdapter();
    }

    private void setValue()
    {
        /*<-------------Document 1------------------>*/
        {
            UploadDocumentBeans uploadDocumentBeans = new UploadDocumentBeans();
            ArrayList<UploadDocumentType> uploadDocumentTypes=new ArrayList<>();
            uploadDocumentBeans.setTitle("Driver Licence");
            {
                UploadDocumentType uploadDocumentType = new UploadDocumentType();
                uploadDocumentType.setTitle("Please upload your Driving licence");
                uploadDocumentType.setStatus(false);
                uploadDocumentTypes.add(uploadDocumentType);
            }
            uploadDocumentBeans.setUploadDocumentType(uploadDocumentTypes);
            uploadDocumentBeansArrayList.add(uploadDocumentBeans);
        }
        /*<-----------------Document 2------------------>*/
        {
            UploadDocumentBeans uploadDocumentBeans = new UploadDocumentBeans();
            ArrayList<UploadDocumentType> uploadDocumentTypes=new ArrayList<>();
            uploadDocumentBeans.setTitle("Vehicle Insurance");
            {
                UploadDocumentType uploadDocumentType = new UploadDocumentType();
                uploadDocumentType.setTitle("Please upload your Vehicle insurance.");
                uploadDocumentType.setStatus(false);
                uploadDocumentTypes.add(uploadDocumentType);
            }
            uploadDocumentBeans.setUploadDocumentType(uploadDocumentTypes);
            uploadDocumentBeansArrayList.add(uploadDocumentBeans);
        }
        /*<----------------Document 3------------------>*/
        {
            UploadDocumentBeans uploadDocumentBeans = new UploadDocumentBeans();
            ArrayList<UploadDocumentType> uploadDocumentTypes=new ArrayList<>();
            uploadDocumentBeans.setTitle("Vehicle Permit");
            {
                UploadDocumentType uploadDocumentType = new UploadDocumentType();
                uploadDocumentType.setTitle("Please upload your Vehicle Permit");
                uploadDocumentType.setStatus(false);
                uploadDocumentTypes.add(uploadDocumentType);
            }
            uploadDocumentBeans.setUploadDocumentType(uploadDocumentTypes);
            uploadDocumentBeansArrayList.add(uploadDocumentBeans);
        }
        /*<-------------Document 4------------------>*/
        {
            UploadDocumentBeans uploadDocumentBeans = new UploadDocumentBeans();
            ArrayList<UploadDocumentType> uploadDocumentTypes=new ArrayList<>();
            uploadDocumentBeans.setTitle("Vehicle Registration");
            {
                UploadDocumentType uploadDocumentType = new UploadDocumentType();
                uploadDocumentType.setTitle("Please upload your Vehicle Registration");
                uploadDocumentType.setStatus(false);
                uploadDocumentTypes.add(uploadDocumentType);
            }
            uploadDocumentBeans.setUploadDocumentType(uploadDocumentTypes);
            uploadDocumentBeansArrayList.add(uploadDocumentBeans);
        }
    }

    private void setAdapter()
    {
        recycler_view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false));

         uploadDocumentAdapter=new UploadDocumentAdapter(uploadDocumentBeansArrayList,this,"1",this);
        recycler_view.setAdapter(uploadDocumentAdapter);

        uploadDocumentAdapter.onItemSelectedListener(new UploadDocumentAdapter.myClickListener() {
            @Override
            public void onItemCLick(int layoutPosition, View view) {

                /*if (layoutPosition==0)
                {
                    //driving
                    Intent intent = new Intent(UploadDocumentActivity.this, UploadDrivingActivity.class);
                    intent.putExtra("vehicleId",vehicleId);
                    startActivity(intent);
                }
                else if (layoutPosition==1)
                {
                    //insurance
                    Intent intent = new Intent(UploadDocumentActivity.this, UploadVehicleInsuranceActivity.class);
                    intent.putExtra("vehicleId",vehicleId);
                    startActivity(intent);
                }
                else if (layoutPosition==2)
                {
                    //permit
                    Intent intent = new Intent(UploadDocumentActivity.this, UploadPermit.class);
                    intent.putExtra("vehicleId",vehicleId);
                    startActivity(intent);
                }
                else if (layoutPosition==3)
                {
                    //registration
                    Intent intent = new Intent(UploadDocumentActivity.this, UploadVehicleRegistration.class);
                    intent.putExtra("vehicleId",vehicleId);
                    startActivity(intent);
                }*/
            }
        });


    }

    @Override
    protected void onResume()
    {
        Log.e( "onResume: Inside ","onResume");
        super.onResume();

        if (helper.getString(SharedPrefUtil.LICENCE,"").equalsIgnoreCase("licence"))
        {
            UploadDocumentType uploadDocumentType = new UploadDocumentType();
            uploadDocumentType.setStatus(false);

            uploadDocumentBeansArrayList.get(0).getUploadDocumentTypes().get(0).setStatus(true);

        }

        if( helper.getString(SharedPrefUtil.INSURANCE,"").equalsIgnoreCase("insurance"))
        {
            Log.e( "onResume: Inside ","INSURANCE");
            UploadDocumentType uploadDocumentType = new UploadDocumentType();
            uploadDocumentType.setStatus(false);

            uploadDocumentBeansArrayList.get(1).getUploadDocumentTypes().get(0).setStatus(true);

        }
        if( helper.getString(SharedPrefUtil.PERMIT,"").equalsIgnoreCase("permit"))
        {
            Log.e( "onResume: Inside ","INSURANCE");
            UploadDocumentType uploadDocumentType = new UploadDocumentType();
            uploadDocumentType.setStatus(false);

            uploadDocumentBeansArrayList.get(2).getUploadDocumentTypes().get(0).setStatus(true);

        }
        if( helper.getString(SharedPrefUtil.REGISTRATION,"").equalsIgnoreCase("registration"))
        {
            Log.e( "onResume: Inside ","INSURANCE");
            UploadDocumentType uploadDocumentType = new UploadDocumentType();
            uploadDocumentType.setStatus(false);

            uploadDocumentBeansArrayList.get(3).getUploadDocumentTypes().get(0).setStatus(true);

        }
        uploadDocumentAdapter.notifyDataSetChanged();


    }

    @OnClick({R.id.btn_continue,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_continue:
                Log.e( "onClick:Valueeee ",UploadDocumentAdapter.selectedType+"");
                Log.e( "vehicleId ",vehicleId+"");

                if (helper.getString(SharedPrefUtil.LICENCE,"").equalsIgnoreCase("licence")&&
                        helper.getString(SharedPrefUtil.INSURANCE,"").equalsIgnoreCase("insurance")
                        && helper.getString(SharedPrefUtil.PERMIT,"").equalsIgnoreCase("permit") &&
                        helper.getString(SharedPrefUtil.REGISTRATION,"").equalsIgnoreCase("registration"))
                {

                    helper.saveString(SharedPrefUtil.DOCUMENTS_SAVED,"1");
                    Intent intent = new Intent(this, SavedDriverDocuments.class);
                    startActivity(intent);
                    finishAffinity();
                }
                else
                {
                    Toast.makeText(this, "Please firstly upload all documents to proceed further",
                            Toast.LENGTH_SHORT).show();
                }

/*
                if (UploadDocumentAdapter.selectedType==1)
                {

                    UploadDocumentAdapter.selectedType=0;
                    Intent intent = new Intent(this, UploadDrivingActivity.class);
                    intent.putExtra("vehicleId",vehicleId);
                    startActivity(intent);
                } else if (UploadDocumentAdapter.selectedType==2)
                {

                    UploadDocumentAdapter.selectedType=0;
                    Intent intent = new Intent(this, UploadVehicleInsuranceActivity.class);
                    intent.putExtra("vehicleId",vehicleId);
                    startActivity(intent);
                } else if (UploadDocumentAdapter.selectedType==3)
                {


                    UploadDocumentAdapter.selectedType=0;
                    Intent intent = new Intent(this, UploadPermit.class);
                    intent.putExtra("vehicleId",vehicleId);
                    startActivity(intent);
                } else if (UploadDocumentAdapter.selectedType==4)
                {

                    UploadDocumentAdapter.selectedType=0;
                    Intent intent = new Intent(this, UploadVehicleRegistration.class);
                    intent.putExtra("vehicleId",vehicleId);
                    startActivity(intent);
                }
                else {

                    Log.e( "onClick: ",helper.getString(SharedPrefUtil.LICENCE,"") );
                    Log.e( "onClick: ",helper.getString(SharedPrefUtil.INSURANCE,"") );
                    Log.e( "onClick: ",helper.getString(SharedPrefUtil.PERMIT,"") );
                    Log.e( "onClick: ",helper.getString(SharedPrefUtil.REGISTRATION,"") );
                    if (helper.getString(SharedPrefUtil.LICENCE,"").equalsIgnoreCase("licence")&& helper.getString(SharedPrefUtil.INSURANCE,"").equalsIgnoreCase("insurance")
                    && helper.getString(SharedPrefUtil.PERMIT,"").equalsIgnoreCase("permit") &&
                            helper.getString(SharedPrefUtil.REGISTRATION,"").equalsIgnoreCase("registration"))
                    {
                        Intent intent = new Intent(this, SavedDriverDocuments.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else {
                        Toast.makeText(this, "Please firstly upload all documents to proceed further",
                                Toast.LENGTH_SHORT).show();
                    }
                }*/
                break;

            case R.id.img_back_btn:
               callAlert();
                break;
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

        tvMsg.setText("Do you want to log out?");

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();


            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPrefUtil.getInstance().onLogout();
                Intent intent = new Intent(UploadDocumentActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        Log.e("ActivityResultData ",data.getData()+"");
        if(requestCode==Constant.ACT_RESULT_DRIVING_LICENCE &&resultCode==Activity.RESULT_OK)
        {
            key1=data.getStringExtra("licence");
        }
        else if(requestCode==Constant.ACT_RESULT_VEHICLE_INSURANCE&&resultCode==Activity.RESULT_OK)
        {
            key2=data.getStringExtra("insurance");
        }
        else if(requestCode==Constant.ACT_RESULT_VEHICLE_PERMIT&&resultCode==Activity.RESULT_OK)
        {
            key3=data.getStringExtra("permit");
        }
        else if(requestCode==Constant.ACT_RESULT_VEHICLE_REGISTRATION&&resultCode==Activity.RESULT_OK)
        {
            key4=data.getStringExtra("registration");
        }
    }
}
