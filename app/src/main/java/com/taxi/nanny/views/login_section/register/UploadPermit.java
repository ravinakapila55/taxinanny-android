package com.taxi.nanny.views.login_section.register;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.APIRequest;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.FilePath;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.location.permission.Permission;
import com.taxi.nanny.utils.location.permission.PermissionGranted;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.login_section.dialog.InternetErrorDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;

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

public class UploadPermit extends BaseActivity implements Callback<ResponseBody>, PermissionGranted {

    SharedPrefUtil sharedPrefUtil;
    private String vehicleId;

    @BindView(R.id.tvContinue)
    TextView tvContinue;

    @BindView(R.id.tvUpload)
    TextView tvUpload;

    @BindView(R.id.tvNo)
    TextView tvNo;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Uri to store the image uri
    private Uri filePath;
    private String TAG=UploadPermit.class.getSimpleName();
    private String path="";

//cgghjj@mailinator.com
    @Override
    protected int getContentId()
    {
        return R.layout.upload_vehicle_permit;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Upload Permit");
        sharedPrefUtil= SharedPrefUtil.getInstance();
        final Calendar c = Calendar.getInstance();
        Permission.checkPermission(this, this);
        requestStoragePermission();

//        vehicleId="67";
        if (getIntent().hasExtra("vehicleId"))
        {
            vehicleId=getIntent().getExtras().getString("vehicleId");
            Log.e(TAG, "onCreate: VehicleId "+vehicleId );
        }
    }

    @OnClick({R.id.tvContinue,R.id.tvUpload,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvContinue:

                String token=sharedPrefUtil.getString(Constant.TOKEN,"");

                if (checkValidation())
                {
                   callService();
                }

                break;

            case R.id.img_back_btn:
            {
                finish();
                break;
            }

            case R.id.tvUpload:
            {
//                callPickPdf();
                callBrowseDocuments();
                break;
            }
        }
    }

    public  static File file;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

    /*    if (requestCode == 105 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filePath = data.getData();
//            path="abc";
            Log.e("FilePath ",filePath+"");

            path = FilePath.getPath(this, filePath);
            Log.e(TAG, "onActivityResult: Path"+path );
            file=new File(path);
            tvNo.setText(file.getName());
            Log.e(TAG, "onActivityResult: File"+file.getName() );
            Log.e(TAG, "onActivityResult: check"+file.isFile());
            Log.e(TAG, "onActivityResult: Drirectory"+file.isDirectory());
        }*/

                  if (data != null)
                    {
                        Uri uri = null;
                        if (data != null) // data can be null only for image capture
                            uri = data.getData();
                        String filePath = null;
                        try {
                            filePath = FilePath.getPath(this, uri);
                            Log.e("filepath", "file path is" + filePath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (filePath != null)
                        {
                            file = new File(filePath);//Get pdf file from storage
                            tvNo.setText(file.getName()+".pdf");
                            Log.e(TAG, "onActivityResult: File"+file.getName() );
                            Log.e(TAG, "onActivityResult: check"+file.isFile());
                            Log.e(TAG, "onActivityResult: Drirectory"+file.isDirectory());

                            if (file != null)
                            {
                                long docSize = file.length();
                                long mbSize = docSize / 1024;
                                Log.e("filename", "The file name:" + file.getName());

                                if ((file.getName()).contains(".pdf"))
                                {
                                   Log.e("InsideType ","PDF");
                                }
                            }
                        }
                    }
    }

    public void callService()
    {

        String token=sharedPrefUtil.getString(Constant.TOKEN,"");

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("vehicle_permit_document", file.getName(),
                RequestBody.create(MediaType.parse("text/plain"), file));

        HashMap<String, RequestBody> param1=new HashMap<>();

        param1.put("driver_id",RequestBody.create(MediaType.parse("text/plain"), sharedPrefUtil.getString(SharedPrefUtil.USER_ID)));
        param1.put("vehicle_id",RequestBody.create(MediaType.parse("text/plain"),vehicleId));
        Log.e("RequestParams ",param1.toString());
        Log.e("id ",sharedPrefUtil.getString(SharedPrefUtil.USER_ID));
        Log.e("VId ",vehicleId);
        Log.e("File ",file.getName());
        Log.e("token ",token);

        apiRequest(param1,filePart,this,token,1);

    }

    protected void apiRequest(HashMap<String, RequestBody> param, MultipartBody.Part image, Callback<ResponseBody> responseBodyCallback, String token , int TYPE_REQUEST) {
        if (isNetworkConnected())
        {
            progressDialog = APIRequest.getProgress(this);
            APIRequest.apiInterface(token).addVehiclePermit(param,image).enqueue(responseBodyCallback);
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

    public boolean checkValidation()
    {
        if (file==null)
        {
            Toast.makeText(this, "Please click on Choose file to upload vehicle permit pdf", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void callPickPdf()
    {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), 105);
        startActivityForResult(intent, 105);
    }

    public void callBrowseDocuments()
    {
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 105);
    }





    //Requesting permission
    private void requestStoragePermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE)
        {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            }
            else
            {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e(TAG, "onResponse: GeneralPermit<< "+generalResponse);

        try
        {
            JSONObject jsonObject=generalResponse.getResponse_object();
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                String key1="permit";
                sharedPrefUtil.saveString(SharedPrefUtil.PERMIT,key1);
                sharedPrefUtil.saveString(SharedPrefUtil.PERMIT_FILE,file.getName());
                Intent intent = new Intent(this,UploadDocumentActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

    }

    @Override
    public void showPermissionAlert(ArrayList<String> permissionList, int code)
    {
        Log.e( "showPermissionAlert: ","Popup" );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Log.e( "showPermissionAlert: ","Inside" );
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), code);
        }
    }




}
