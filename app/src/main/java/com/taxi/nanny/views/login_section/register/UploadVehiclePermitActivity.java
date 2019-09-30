package com.taxi.nanny.views.login_section.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.custom.camera.Camera;
import com.taxi.nanny.domain.APIRequest;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.AppUtils;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.login_section.dialog.InternetErrorDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.taxi.nanny.utils.Constant.TOKEN;

public class UploadVehiclePermitActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, Callback<ResponseBody> {
    int TYPE_SELELCT;

    @BindView(R.id.tvNo)
    TextView tvNo;

    @Override
    protected int getContentId() {
        return R.layout.upload_vehicle_insurance;
    }

    @BindViews({R.id.edt_company_name,R.id.edt_policyno,R.id.btn_issued,R.id.btn_expiryDate})
    List<TextView> txtViewList;
    @BindView(R.id.img_licence)
    ImageView img_licence;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Upload Vehicle Insurance");
    }



    @OnClick({R.id.btn_continue,R.id.img_back_btn,R.id.btn_issued,R.id.btn_expiryDate,R.id.img_licence})
    public void onClick(View view)
    {
        switch (view.getId()){
            case R.id.btn_continue:
                {
                if(user_pic_path0==null){
                    Toast.makeText(this, "Please select Image Upload", Toast.LENGTH_SHORT).show();

                }
                else if(txtViewList.get(0).getText().toString().trim().length()==0){
                    txtViewList.get(0).setText("Please enter Company Name");
                    txtViewList.get(0).requestFocus();
                } else if(txtViewList.get(1).getText().toString().trim().length()==0){
                    txtViewList.get(1).setText("Please enter Policy No");
                    txtViewList.get(1).requestFocus();
                }
                else if(txtViewList.get(2).getText().toString().length()==0){

                    Toast.makeText(this, "Please select Issued Date", Toast.LENGTH_SHORT).show();

                }else if(txtViewList.get(3).getText().toString().length()==0){
                    Toast.makeText(this, "Please select Expiry Date", Toast.LENGTH_SHORT).show();

                }else{

                    apiRequest();

                }

                break;
            }
            case R.id.img_back_btn:{
                finish();
                break;
            }
            case R.id.btn_issued:{
                TYPE_SELELCT=1;
                calendarDialog();
                break;
            }
            case R.id.btn_expiryDate:{
                 TYPE_SELELCT=2;
                 calendarDialog();
                break;
            }
            case R.id.img_licence:{
                cameraPermission();
                break;
            }
        }
    }



    Camera camera;
    private void takePicture(){
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(4)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(UploadVehiclePermitActivity.this);
        try {
            camera.takePicture();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    String user_pic_path0=null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode ==4 &&resultCode==RESULT_OK) {
            if(camera!=null) {
                user_pic_path0 = camera.getCameraBitmapPath();
                img_licence.setImageBitmap(camera.getCameraBitmap());
            }
        }
    }
    Date selectDateIssuedON;
    Date expiryDate;
        private void calendarDialog(){
            if(TYPE_SELELCT==1) {
                Date currentDate = new Date();
                Date minYear = new Date();
                minYear.setYear(currentDate.getYear() - 102);
                Date maxYear = new Date();
                DatePickerDialog dialog;
                if (selectDateIssuedON == null) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(currentDate);
                    dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                } else {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(selectDateIssuedON);
                    dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }
                dialog.getDatePicker().setMinDate(minYear.getTime());
                dialog.getDatePicker().setMaxDate(maxYear.getTime());
                dialog.show();
            }else{
                Date currentDate = new Date();
                Date minYear = new Date();
                minYear.setYear(currentDate.getYear() - 102);
                Date maxYear = new Date();
                DatePickerDialog dialog;
                if (expiryDate == null) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(currentDate);
                    dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                } else {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(expiryDate);
                    dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }
                dialog.getDatePicker().setMinDate(minYear.getTime());
                dialog.getDatePicker().setMaxDate(maxYear.getTime());
                dialog.show();

            }
        }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(TYPE_SELELCT==1) {
            selectDateIssuedON = new Date();
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DATE, dayOfMonth);
            selectDateIssuedON.setTime(c.getTimeInMillis());
            txtViewList.get(1).setText(year + "-" + month + "-" + dayOfMonth);
        }else{
            expiryDate = new Date();
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DATE, dayOfMonth);
            expiryDate.setTime(c.getTimeInMillis());
            txtViewList.get(2).setText(year + "-" + month + "-" + dayOfMonth);
        }
    }

    String[] notYetpermitted;


    private void cameraPermission(){
        ArrayList<String> permissionNeeded = AppUtils.permissionNeeded(AppUtils.getCameraPermission(getApplicationContext()), getApplicationContext());
        if (permissionNeeded.size() > 0) {
            notYetpermitted = AppUtils.getStringArray(permissionNeeded);
            verifyPermissions(notYetpermitted);
        }else{
            takePicture();
        }
    }

    private void verifyPermissions(String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    // normally it gets called if user checked "Never ask again"
                    ActivityCompat.requestPermissions(this, permissions, i);
                } else {
                    ActivityCompat.requestPermissions(this, permissions, i);
                }
                break;
            }
        }
    }



    public void onRequestPermissionsResult(int requestCode, String per[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    takePicture();

                } else {
                    verifyPermissions(notYetpermitted);
                }
                return;
            }
        }
    }

    private void apiRequest(){
        HashMap<String, RequestBody> param=new HashMap<>();
        param.put("company_name",RequestBody.create(MediaType.parse("text/plain"),txtViewList.get(0).getText().toString()));
        param.put("permit_type_id",RequestBody.create(MediaType.parse("text/plain"),txtViewList.get(1).getText().toString()));
        param.put("expiry_date",RequestBody.create(MediaType.parse("text/plain"),txtViewList.get(3).getText().toString()));
        File licenceImage = new File(user_pic_path0);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", licenceImage.getName(), RequestBody.create(MediaType.parse("image/jpeg"), licenceImage));
        apiRequest(param,filePart,this,TOKEN,1);
    }


    protected void apiRequest(HashMap<String, RequestBody> param, MultipartBody.Part image, Callback<ResponseBody> responseBodyCallback, String token , int TYPE_REQUEST) {
        if (isNetworkConnected()) {
            progressDialog = APIRequest.getProgress(this);
            APIRequest.apiInterface(token).addInsurance(param,image).enqueue(responseBodyCallback);

        } else {
            new InternetErrorDialog(this) {
                @Override
                protected void dataOnClick() {
                    apiRequest(param,image,responseBodyCallback,token,TYPE_REQUEST);
                }
            }.show();
        }
    }


    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        progressDialog.dismiss();

        GeneralResponse generalResponse = new GeneralResponse(response);

    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        progressDialog.dismiss();

    }
}
