package com.taxi.nanny.views.login_section.register;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.model.UploadDocumentBeans;
import com.taxi.nanny.model.UploadDocumentType;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.driver.DriverHome;
import com.taxi.nanny.views.login_section.adapter.UploadDocumentAdapter;
import com.taxi.nanny.views.login_section.login.LoginActivity;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

public class SavedDriverDocuments extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    ArrayList<UploadDocumentBeans> uploadDocumentBeansArrayList;

    @BindView(R.id.btn_continue)
    Button btn_continue;

    @Override
    protected int getContentId()
    {
        return R.layout.upload_document;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        uploadDocumentBeansArrayList=new ArrayList<>();
        setHeaderText("Saved Documents");
        btn_continue.setText(getResources().getString(R.string.getting_started));
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
                uploadDocumentType.setTitle("Driving licence");
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
                uploadDocumentType.setTitle("Vehicle insurance.");
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
                uploadDocumentType.setTitle("Vehicle Permit");
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
                uploadDocumentType.setTitle("Vehicle Registration");
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
        recycler_view.setAdapter(new UploadDocumentAdapter(uploadDocumentBeansArrayList,this,"2",this));
    }

    @OnClick({R.id.btn_continue,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_continue:
                callMessageAlert();

                break;

            case R.id.img_back_btn:
                finish();
                break;
        }
    }

    public void callMessageAlert()
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

        tvMsg.setText("Thank you for your submission. We are reviewing your information and will reach out to you shortly!");
        tvOk.setText("OK");

        tvOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(SavedDriverDocuments.this, LoginActivity.class);
                startActivity(intent);
                finish();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
