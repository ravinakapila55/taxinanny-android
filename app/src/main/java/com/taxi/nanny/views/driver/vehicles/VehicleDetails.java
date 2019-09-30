package com.taxi.nanny.views.driver.vehicles;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.custom.camera.Camera;
import com.taxi.nanny.domain.APIRequest;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.DriverDocumentsModel;
import com.taxi.nanny.utils.AppUtils;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.FullImage;
import com.taxi.nanny.views.driver.DriverAccount;
import com.taxi.nanny.views.login_section.dialog.InternetErrorDialog;
import com.taxi.nanny.views.profile.EditProfile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleDetails extends BaseActivity implements RetrofitResponse, Callback<ResponseBody> {

    @BindView(R.id.ivVehicle)
    ImageView ivVehicle;

    @BindView(R.id.ivAdd)
    ImageView ivAdd;

    @BindView(R.id.etMake)
    EditText etMake;

    @BindView(R.id.etModel)
    EditText etModel;

    @BindView(R.id.SpYear)
    Spinner SpYear;

    @BindView(R.id.tvSave)
    TextView tvSave;

    String selectedYear="";


    String[] notYetpermitted;
    String user_pic_path0="";
    public static File file;




    @Override
    protected int getContentId()
    {
        return R.layout.vehicle_details;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Vehicle Details");

        sharedPrefUtil=SharedPrefUtil.getInstance();

        callVehicleDetails();

        year.clear();
        year.add("Year");

        int mYear = Calendar.getInstance().get(Calendar.YEAR);

        Log.e("setSpinner:Year ",mYear +"");

        for (int i = 2000; i <=mYear ; i++)
        {
            year.add(String.valueOf(i));
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, year);

        SpYear.setAdapter(adapter);

        SpYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear=SpYear.getSelectedItem()+"";
                Log.e("selectedYear ",selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


    }

    private void callVehicleDetails()
    {
        new RetrofitService(this, this,
                Constant.API_DRIVER_VEHICLE_DETAIL , 500, 1,"1").
                callService(true);
    }

    @OnClick({R.id.img_back_btn,R.id.ivAdd,R.id.tvSave})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;

            case R.id.ivAdd:

                cameraPermission();

                break;

                case R.id.ivVehicle:

                   Intent intent=new Intent(VehicleDetails.this, FullImage.class);
                   startActivity(intent);


                break;


            case R.id.tvSave:
                if (checkValidation())
                {
                    Log.e("user_pic_path0 ",user_pic_path0);
                    Log.e("user_pic_path0  ",user_pic_path0);
                    if (user_pic_path0.equalsIgnoreCase(""))
                    {
                        imageString="0";
                    }
                    else
                    {
                        imageString="1";
                    }

                    callService();
                }

                break;
        }
    }

    SharedPrefUtil sharedPrefUtil;

    public void callService()
    {
        String token=sharedPrefUtil.getString(Constant.TOKEN,"");

        Log.e("ImageString ",imageString);

        if (imageString.equalsIgnoreCase("1"))
        {
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(),
                    RequestBody.create(MediaType.parse("image/jpeg"), file));

            HashMap<String, RequestBody> param1=new HashMap<>();

            param1.put("make",RequestBody.create(MediaType.parse("text/plain"),etMake.getText().toString().trim()));
            param1.put("model",RequestBody.create(MediaType.parse("text/plain"),etModel.getText().toString().trim()));
            param1.put("year",RequestBody.create(MediaType.parse("text/plain"),selectedYear));

            Log.e("EditVehicleParams ",param1.toString());
            apiRequest(param1,filePart,this,token,1);
        }
        else
        {
            HashMap<String, String> param = new HashMap<>();

            param.put("make",etMake.getText().toString().trim());
            param.put("model",etModel.getText().toString().trim());
            param.put("year",selectedYear);

            Log.e("EditVehicleParams ",param.toString());
            api(param,this,token,26);

        }
    }


    protected void apiRequest(HashMap<String, RequestBody> param, MultipartBody.Part image, Callback<ResponseBody> responseBodyCallback,
                              String token , int TYPE_REQUEST) {
        if (isNetworkConnected())
        {
            progressDialog = APIRequest.getProgress(this);
            APIRequest.apiInterface(token).editVehicle(param,image).enqueue(responseBodyCallback);
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


    private void cameraPermission()
    {
        ArrayList<String> permissionNeeded = AppUtils.permissionNeeded(AppUtils.getCameraPermission(getApplicationContext()), getApplicationContext());
        if (permissionNeeded.size() > 0)
        {
            notYetpermitted = AppUtils.getStringArray(permissionNeeded);
            verifyPermissions(notYetpermitted);
        } else
        {
            ImageSelection();
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
//                    takePicture();
                    ImageSelection();

                } else {
                    verifyPermissions(notYetpermitted);
                }
                return;
            }
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
                .build(this);
        try
        {
            camera.takePicture();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
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

//        cvTakePic = (CardView) alert.findViewById(R.id.cvTakePic);
//        cvGallery = (CardView) alert.findViewById(R.id.cvGallery);
        cvCancel = (CardView) alert.findViewById(R.id.cvCancel);

        takePicture = (TextView) alert.findViewById(R.id.takePicture);
        gallery = (TextView) alert.findViewById(R.id.gallery);

        gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try {

                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(VehicleDetails.this);

                }catch (Exception e){
                    e.printStackTrace();
                }

                alert.dismiss();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        switch (requestCode)
        {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK)
                {
                    Uri resultUri = result.getUri();

                    String path=resultUri.getPath();
                    user_pic_path0=resultUri.getPath();
                    file=new File(path);

                 /*   Log.e("Paaaath","Paaaath"+path);
                    Log.e("Fileeee","Fileeee"+file);*/
                    Bitmap bitmap = null;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ivVehicle.setImageBitmap(bitmap);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
                break;

            case 4:
                if(camera!=null)
                {
                    user_pic_path0 = camera.getCameraBitmapPath();
//                       Log.e( "ImagePath ",user_pic_path0);
                    ivVehicle.setImageBitmap(camera.getCameraBitmap());
                    file=new File(user_pic_path0);
               /*        Log.e("ISFile",file.isFile()+"");
                       Log.e("directory ",file.isDirectory()+"");
                       Log.e("NAme",file.getName()+"");*/
                }

                break;


            case 2:
                Log.e("2  ","2");
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivVehicle.setImageBitmap(bitmap);
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

    String imageString="0";

    String yearSaved="";
    String vehicleId="";
    ArrayList<String> year=new ArrayList<>();


    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 500:
                Log.e("DriverVehicleResponse ",response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        JSONObject vehicle_detail=jsonObject.getJSONObject("vehicle_detail");

                        if (vehicle_detail.getString("model").equalsIgnoreCase("null"))
                        {
                            etModel.setText("NA");
                        }
                        else {
                            etModel.setText(vehicle_detail.getString("model"));
                        }

                        if (vehicle_detail.getString("make").equalsIgnoreCase("null"))
                        {
                            etMake.setText("NA");
                        }
                        else
                        {
                            etMake.setText(vehicle_detail.getString("make"));
                        }
                        if (!vehicle_detail.getString("image").equalsIgnoreCase(""))
                        {
                            Picasso.with(this).load(vehicle_detail.getString("image")).
                                    placeholder(getResources().getDrawable(R.drawable.car_dummy)).into(ivVehicle);
                        }
                        yearSaved=vehicle_detail.getString("year");
                        vehicleId=vehicle_detail.getString("id");
                        Log.e("SavedYear ",yearSaved);


                        for (int i = 0; i < year.size(); i++)
                        {
                            Log.e("Index ",i+"");
                            Log.e("IndexValue ",year.get(i));
                            Log.e("SavedYear ",yearSaved);
                            if (year.get(i).equalsIgnoreCase(yearSaved))
                            {
                                Log.e("Inside ",yearSaved);
                                SpYear.setSelection(i);
                            }
                        }
                    }
                    else
                    {

                    }
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;

        }
    }

    public boolean checkValidation()
    {
        if (etMake.getText().toString().trim().length() == 0)
        {
            etMake.setError("Please enter make of vehicle");
            etMake.requestFocus();
            return false;
        }
        else if (etModel.getText().toString().trim().length() == 0)
        {
            etModel.setError("Please enter model of vehicle");
            etModel.requestFocus();
            return false;
        }

        else if (selectedYear.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Choose year", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e( "onResponse: EditVehicle" ,generalResponse+"");

        try {
            JSONObject jsonObject=generalResponse.getResponse_object();
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {

                Toast.makeText(this, jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                JSONObject vehicle_detail=jsonObject.getJSONObject("data");

                if (vehicle_detail.getString("model").equalsIgnoreCase("null"))
                {
                    etModel.setText("NA");
                }
                else {
                    etModel.setText(vehicle_detail.getString("model"));
                }

                if (vehicle_detail.getString("make").equalsIgnoreCase("null"))
                {
                    etMake.setText("NA");
                }
                else
                {
                    etMake.setText(vehicle_detail.getString("make"));
                }
                if (!vehicle_detail.getString("image").equalsIgnoreCase(""))
                {
                    Picasso.with(this).load(vehicle_detail.getString("image")).
                            placeholder(getResources().getDrawable(R.drawable.car_dummy)).into(ivVehicle);
                }
                yearSaved=vehicle_detail.getString("year");
                vehicleId=vehicle_detail.getString("id");
                Log.e("SavedYearEdit ",yearSaved);


                for (int i = 0; i < year.size(); i++)
                {
                    Log.e("Index ",i+"");
                    Log.e("IndexValue ",year.get(i));
                    Log.e("SavedYear ",yearSaved);
                    if (year.get(i).equalsIgnoreCase(yearSaved))
                    {
                        Log.e("Inside ",yearSaved);
                        SpYear.setSelection(i);
                    }
                }


            }
            else {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
