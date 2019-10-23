package com.taxi.nanny.views.login_section.register;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.taxi.nanny.R;
import com.taxi.nanny.custom.camera.Camera;
import com.taxi.nanny.domain.APIRequest;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.AppUtils;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.login_section.dialog.InternetErrorDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class UploadVehicleInsuranceActivity extends BaseActivity
        implements  Callback<ResponseBody>
{

    String imageString="";
    String issueDate="";
    String expiryDate="";
    String vehcileId="";
    SharedPrefUtil sharedPrefUtil;
    public static File file;
    int TYPE_SELELCT;
    private static String TAG=UploadVehicleInsuranceActivity.class.getSimpleName();
    private String vehicleId;

    @Override
    protected int getContentId()
    {
        return R.layout.upload_vehicle_insurance;
    }

    @BindViews({R.id.edt_company_name,R.id.edt_policyno,R.id.btn_issued,R.id.btn_expiryDate})
    List<TextView> txtViewList;

    @BindView(R.id.img_licence)
    ImageView img_licence;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText(getString(R.string.upload_vehicle_insurance));
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        sharedPrefUtil=SharedPrefUtil.getInstance();

        if (getIntent().hasExtra("vehicleId"))
        {
            vehicleId=getIntent().getExtras().getString("vehicleId");
            Log.e(TAG, "onCreate: VehicleId "+vehicleId );
        }
    /*    if (txtViewList.get(1).hasFocus())
        {
            txtViewList.get(0).setError(null);
        }
        else if (txtViewList.get(0).hasFocus())
        {
            txtViewList.get(1).setError(null);
        }*/
    }

    public void callDatePickerDialog(String type)
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth)
                    {
                        Log.e( "onDateSet:Year-- ",year+"" );
                        Log.e( "onDateSet:Month-- ",monthOfYear+"" );
                        Log.e( "onDateSet:day-- ",dayOfMonth+"" );

                        if (type.equalsIgnoreCase("i"))
                        {
                            txtViewList.get(2).setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            issueDate=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                        }
                        else {

                            String ddd="";
                            ddd=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;

                            String compareDates=Constant.campareLicencseDatesValidate(ddd,issueDate);

                            if (compareDates.equalsIgnoreCase("equal"))
                            {
                                Toast.makeText(UploadVehicleInsuranceActivity.this,
                                        "Expiry Date must be after issue date",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            else {
                                txtViewList.get(3).setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                expiryDate=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                            }
                        }

                    }
                }, mYear, mMonth, mDay);

        if (type .equalsIgnoreCase("i"))
        {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        else {

        /*    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            Date date=null;
            try {
                date=simpleDateFormat.parse(issueDate);
                datePickerDialog.getDatePicker().setMinDate(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }
        datePickerDialog.show();
    }


    private boolean checkValidations()
    {
       /* if (user_pic_path0.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please upload image", Toast.LENGTH_SHORT).show();
            return false;
        }*/ if (file==null)
        {
            Toast.makeText(this, "Please upload vehicle insurance image", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtViewList.get(0).getText().toString().trim().length() == 0)
        {
            txtViewList.get(0).setError(getResources().getString(R.string.val_commpany_name));
            txtViewList.get(0).requestFocus();
            return false;
        }

        if (txtViewList.get(1).getText().toString().trim().length() == 0)
        {
            txtViewList.get(1).setError(getResources().getString(R.string.val_policy_number));
            txtViewList.get(1).requestFocus();
            return false;
        } if (txtViewList.get(1).getText().toString().trim().equalsIgnoreCase("0"))
        {
            txtViewList.get(1).setError("Please enter correct policy number");
            txtViewList.get(1).requestFocus();
            return false;
        }
        if (issueDate.equalsIgnoreCase(""))
        {
            Toast.makeText(this, getString(R.string.issue_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (expiryDate.equalsIgnoreCase(""))
        {
            Toast.makeText(this, getString(R.string.expiry_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @OnClick({R.id.btn_continue,R.id.img_back_btn,R.id.btn_issued,R.id.btn_expiryDate,R.id.img_licence})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_continue:


                if (checkValidations())
                {
                    if (user_pic_path0.equalsIgnoreCase(""))
                    {
                        imageString="0";
                    }
                    else {
                        imageString="1";
                    }

                    callService();
                }


                break;

            case R.id.img_back_btn:{
                finish();
                break;
            }
            case R.id.btn_issued:{
                callDatePickerDialog("i");
                break;
            }
            case R.id.btn_expiryDate:{

                if (issueDate.equalsIgnoreCase("") || issueDate.isEmpty())
                {
                    Toast.makeText(this, "Please choose issue date firstly", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    callDatePickerDialog("e");
                }
//                callDatePickerDialog("e");

                break;
            }
            case R.id.img_licence:{
                cameraPermission();
                break;
            }
        }
    }

    public void callService()
    {
        String token=sharedPrefUtil.getString(Constant.TOKEN,"");

        /*MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(),
                RequestBody.create(MediaType.parse("image/jpeg"),file));

        HashMap<String, RequestBody> param1=new HashMap<>();

        param1.put("driver_id",RequestBody.create(MediaType.parse("text/plain"), sharedPrefUtil.getString(SharedPrefUtil.USER_ID)));
        param1.put("vehicle_id",RequestBody.create(MediaType.parse("text/plain"),"26"));
        param1.put("company_name",RequestBody.create(MediaType.parse("text/plain"),txtViewList.get(0).getText().toString().trim()));
        param1.put("policy_no",RequestBody.create(MediaType.parse("text/plain"),txtViewList.get(1).getText().toString().trim()));
        param1.put("issued_on",RequestBody.create(MediaType.parse("text/plain"),issueDate));
        param1.put("expiry_date",RequestBody.create(MediaType.parse("text/plain"),expiryDate));

        Log.e("AddRiderParams1 ",param1.toString());

        apiRequest(param1,filePart,this,token,1);


        String key1="insurance";
        sharedPrefUtil.saveString(SharedPrefUtil.INSURANCE,key1);

        Intent intent = new Intent(this,UploadDocumentActivity.class);
        startActivity(intent);
*/
        if (imageString.equalsIgnoreCase("1"))
        {
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(),
                    RequestBody.create(MediaType.parse("image/jpeg"), file));

            HashMap<String, RequestBody> param1=new HashMap<>();

            param1.put("driver_id",RequestBody.create(MediaType.parse("text/plain"), sharedPrefUtil.getString(SharedPrefUtil.USER_ID)));
            param1.put("vehicle_id",RequestBody.create(MediaType.parse("text/plain"),vehicleId));
            param1.put("company_name",RequestBody.create(MediaType.parse("text/plain"),txtViewList.get(0).getText().toString().trim()));
            param1.put("policy_no",RequestBody.create(MediaType.parse("text/plain"),txtViewList.get(1).getText().toString().trim()));
            param1.put("issued_on",RequestBody.create(MediaType.parse("text/plain"),issueDate));
            param1.put("expiry_date",RequestBody.create(MediaType.parse("text/plain"),expiryDate));

            Log.e("AddInsuranceParams ",param1.toString());
            Log.e("id ",sharedPrefUtil.getString(SharedPrefUtil.USER_ID));
            Log.e("vID ",vehicleId);
            Log.e("cname ",sharedPrefUtil.getString(SharedPrefUtil.USER_ID));
            Log.e("pNo ",sharedPrefUtil.getString(SharedPrefUtil.USER_ID));
            Log.e("IssuedOn ",sharedPrefUtil.getString(SharedPrefUtil.USER_ID));
            Log.e("ExpDate ",sharedPrefUtil.getString(SharedPrefUtil.USER_ID));

            apiRequest(param1,filePart,this,token,1);
        }
        else
        {
            HashMap<String, String> param = new HashMap<>();
            param.put("driver_id", sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
            param.put("vehicle_id", vehicleId);
            param.put("issued_on", issueDate);
            param.put("expiry_date", expiryDate);
            param.put("driver_id",sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
            param.put("company_name",txtViewList.get(0).getText().toString().trim());
            param.put("policy_no",txtViewList.get(1).getText().toString().trim());
            Log.e("AddInsuranceParams ",param.toString());
            api(param,this,token,8);
        }
    }

    Camera camera;
    private void takePicture()
    {
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(4)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(UploadVehicleInsuranceActivity.this);
        try
        {
            camera.takePicture();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    String user_pic_path0="";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {

        if (data!=null)
        {
            switch (requestCode)
            {
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();

                        String path=resultUri.getPath();
                        user_pic_path0=path;
                        file=new File(path);

                        Log.e("Paaaath","Paaaath"+path);
                        Log.e("Fileeee","Fileeee"+file);
                        Bitmap bitmap = null;

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        img_licence.setImageBitmap(bitmap);

                    }
                    else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                    }
                    break;

                case 4:
                    if(camera!=null)
                    {
                        user_pic_path0 = camera.getCameraBitmapPath();
                        Log.e( "onActivityResult: ImagePath",user_pic_path0);
                        img_licence.setImageBitmap(camera.getCameraBitmap());
                        file=new File(user_pic_path0);
                        Log.e("ISFile",file.isFile()+"");
                        Log.e("directory ",file.isDirectory()+"");
                        Log.e("NAme",file.getName()+"");
                    }

                    break;

                case 2:
                    Log.e("2  ","2");
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    img_licence.setImageBitmap(bitmap);
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Anty";

                    file = new File(path);

                    if (!file.exists()) {
                        try {
                            file.mkdirs();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    file = new File(file, System.currentTimeMillis() + ".png");

                    if (file.exists()) {
                        file.delete();
                    }
                    FileOutputStream fileOutputStream=null;
                    try {
                        fileOutputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fileOutputStream);
                        try {
                            fileOutputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }




       /* if(requestCode ==4 &&resultCode==RESULT_OK)
        {
            if(camera!=null)
            {
                user_pic_path0 = camera.getCameraBitmapPath();
                Log.e( "onActivityResult: ImagePath",user_pic_path0);
                ivRider.setImageBitmap(camera.getCameraBitmap());
                file=new File(user_pic_path0);
                Log.e("ISFile",file.isFile()+"");
                Log.e("directory ",file.isDirectory()+"");
                Log.e("NAme",file.getName()+"");
            }
        }*/
    }


    String[] notYetpermitted;

    private void cameraPermission()
    {
        ArrayList<String> permissionNeeded = AppUtils.permissionNeeded(AppUtils.getCameraPermission(getApplicationContext()), getApplicationContext());
        if (permissionNeeded.size() > 0) {
            notYetpermitted = AppUtils.getStringArray(permissionNeeded);
            verifyPermissions(notYetpermitted);
        }else{

            ImageSelection();
//            takePicture();
        }
    }

    private void verifyPermissions(String[] permissions)
    {
        for (int i = 0; i < permissions.length; i++)
        {
            if (ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]))
                {
                    // normally it gets called if user checked "Never ask again"
                    ActivityCompat.requestPermissions(this, permissions, i);
                }
                else {
                    ActivityCompat.requestPermissions(this, permissions, i);
                }
                break;
            }
        }
    }

    public void ImageSelection() {

        CardView cvTakePic, cvGallery, cvCancel;
        TextView takePicture,gallery;

        final Dialog alert = new Dialog(this);
        alert.setContentView(R.layout.camera_options);

        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.MATCH_PARENT;

        alert.getWindow().setLayout(width, height);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;

        alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

     /*   cvTakePic = (CardView) alert.findViewById(R.id.cvTakePic);
        cvGallery = (CardView) alert.findViewById(R.id.cvGallery);*/
        cvCancel = (CardView) alert.findViewById(R.id.cvCancel);

        takePicture = (TextView) alert.findViewById(R.id.takePicture);
        gallery = (TextView) alert.findViewById(R.id.gallery);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(UploadVehicleInsuranceActivity.this);

                }catch (Exception e){
                    e.printStackTrace();
                }

                alert.dismiss();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                ImageSelection();

//                takePicture();
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 2);
                }catch (Exception e){
                    e.printStackTrace();
                }

                alert.dismiss();
            }

        });

        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        alert.show();
    }




    public void onRequestPermissionsResult(int requestCode, String per[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 0:
                {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    ImageSelection();

                } else {
                    verifyPermissions(notYetpermitted);
                }
                return;
            }
        }
    }




    protected void apiRequest(HashMap<String, RequestBody> param, MultipartBody.Part image, Callback<ResponseBody> responseBodyCallback, String token , int TYPE_REQUEST) {
        if (isNetworkConnected())
        {
            progressDialog = APIRequest.getProgress(this);
            APIRequest.apiInterface(token).addVehicleInsurance(param,image).enqueue(responseBodyCallback);
        } else {
            new InternetErrorDialog(this)
            {
                @Override
                protected void dataOnClick()
                {
                    apiRequest(param,image,responseBodyCallback,token,TYPE_REQUEST);
                }
            }.show();
        }
    }


    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e(TAG, "onResponse: GeneralVehicleInsurance << "+generalResponse);

        try {
            JSONObject jsonObject=generalResponse.getResponse_object();
        /*    String key1="insurance";
            sharedPrefUtil.saveString(SharedPrefUtil.INSURANCE,key1);*/
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {


                String key1="insurance";
                sharedPrefUtil.saveString(SharedPrefUtil.INSURANCE,key1);

                Intent intent = new Intent(this,UploadDocumentActivity.class);
                startActivity(intent);
                finish();
            }
            else {
/*                Intent intent = new Intent(this,UploadDocumentActivity.class);
                startActivity(intent);
                finish();*/
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
        progressDialog.dismiss();
    }
}
