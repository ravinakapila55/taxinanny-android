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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.taxi.nanny.R;
import com.taxi.nanny.custom.camera.Camera;
import com.taxi.nanny.domain.APIRequest;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.CountryMOdel;
import com.taxi.nanny.utils.AppUtils;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.login_section.dialog.InternetErrorDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONArray;
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
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.taxi.nanny.utils.Constant.TOKEN;

public class UploadDrivingActivity extends BaseActivity
        implements DatePickerDialog.OnDateSetListener, Callback<ResponseBody>
{
    int TYPE_SELELCT;
    @BindView(R.id.etIssue)
    TextView etIssue;

    @BindView(R.id.etExpiry)
    TextView etExpiry;

    @BindView(R.id.etDob)
    TextView etDob;

    String[] notYetpermitted;

    String user_pic_path0="";
    public static File file;

    SharedPrefUtil sharedPrefUtil;

    String imageString="";

    @BindView(R.id.edt_licence_number)
    EditText edt_licence_number;

    @BindView(R.id.etFullName)
    EditText etFullName;

    ArrayList<String> year=new ArrayList<>();
    String selectedYear="2003";
    @BindView(R.id.etAddress)
    EditText etAddress;

    @BindView(R.id.etAddress2)
    EditText etAddress2;

    @BindView(R.id.etZip)
    EditText etZip;

    @BindView(R.id.SpYear)
    Spinner SpYear;

    @BindView(R.id.SpCountry)
    Spinner SpCountry;

    @BindView(R.id.img_licence)
    ImageView img_licence;

    private int mYear, mMonth, mDay;

    String apiRequest="0";

    String issueDate="",dob="",expiryDate="",vehcileId;
    private static  String TAG=UploadDocumentActivity.class.getSimpleName();
    private String vehicleId;

    ArrayList<String> countryNameList=new ArrayList<>();
    ArrayList<CountryMOdel> countryList=new ArrayList<>();

    private String countryId="";

    @BindView(R.id.etCity)
    EditText etCity;

    @BindView(R.id.etState)
    EditText etState;

    @Override
    protected int getContentId()
    {
        return R.layout.upload_driving_licence_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText(getString(R.string.upload_driving_license));
        sharedPrefUtil=SharedPrefUtil.getInstance();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        setSpinner();

        SharedPrefUtil prefUtil=SharedPrefUtil.getInstance();
        String token=prefUtil.getString(TOKEN,"");
        apiRequest="0";
        api(new HashMap<>(),this,token,9);

        vehicleId="67";
/*
        if (getIntent().hasExtra("vehicleId"))
        {
            vehicleId=getIntent().getExtras().getString("vehicleId");
        }*/
    }

    @OnClick({R.id.btn_continue,R.id.img_back_btn,R.id.etIssue,R.id.etExpiry,R.id.img_licence,R.id.etDob})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_continue:
                if (checkValidations())
                {
                   /* if (file==null)
                    {
                        imageString="0";
                    }
                    else {
                        imageString="1";
                    }*/

                    callService();
                }
                break;

            case R.id.img_back_btn:
                {
                finish();
                break;
            }
            case R.id.etIssue:
                {
               /* TYPE_SELELCT=1;
                calendarDialog();*/

//                callDatePickerDialog("i");
                setDatePicker("i");
                break;
            }
            case R.id.etExpiry:
                {
              /*   TYPE_SELELCT=2;
                 calendarDialog();*/

              if (issueDate.equalsIgnoreCase("") || issueDate.isEmpty())
              {
                  Toast.makeText(this, "Please choose issue date firstly", Toast.LENGTH_SHORT).show();
                  return;
              }
              else {
//                  callDatePickerDialog("e");
                  setDatePicker("e");
              }

                break;
            }case R.id.etDob:
                {
              /*   TYPE_SELELCT=2;
                 calendarDialog();*/

//                    callDatePickerDialog("d");
                    setDatePicker("d");
                break;
            }
            case R.id.img_licence:
                {
                cameraPermission();
                break;
            }
        }
    }



    private void setDatePicker(String type)
    {
        final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        final String[] Days = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};

        String finalType = type;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                String mn = "";
                for (int i = 0; i < MONTHS.length; i++)
                {
                    if (i == monthOfYear)
                    {
                        mn = MONTHS[i];
                    }
                }
                int month = 0;
                month = monthOfYear + 1;

                HashMap<Integer, String> map = new HashMap<>();

                map.put(1, "01");
                map.put(2, "02");
                map.put(3, "03");
                map.put(4, "04");
                map.put(5, "05");
                map.put(6, "06");
                map.put(7, "07");
                map.put(8, "08");
                map.put(9, "09");
                map.put(10, "10");
                map.put(11, "11");
                map.put(12, "12");

                String exactMonth = String.valueOf(map.get(month));

                String day = "";
                if (dayOfMonth < Days.length)
                {
                    day = "0" + dayOfMonth;
                } else
                {
                    day = "" + dayOfMonth;
                }


                if (finalType.equalsIgnoreCase("i"))
                {
//                    etIssue.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    etIssue.setText(year + "-" + (exactMonth) + "-" + day);
                    issueDate=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                }
                else if (finalType.equalsIgnoreCase("d"))
                {
                    etDob.setText(year + "-" + (exactMonth) + "-" + day);
//                    etDob.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    dob=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                }
                else {

                    String ddd="";
                    ddd=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;

                    String compareDates=Constant.campareLicencseDatesValidate(ddd,issueDate);

                    if (compareDates.equalsIgnoreCase("equal"))
                    {
                        Toast.makeText(UploadDrivingActivity.this, "Expiry Date must be after issue date",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else
                    {
                        etExpiry.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        expiryDate=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                    }
                }

            }
        }, mYear, mMonth, mDay);
        if (type.equalsIgnoreCase("i"))
        {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        }else if (type.equalsIgnoreCase("d"))
        {
            int cYear = Calendar.getInstance().get(Calendar.YEAR);
            int cMonth = Calendar.getInstance().get(Calendar.MONTH);
            int cDay = Calendar.getInstance().get(Calendar.MONTH);

            int diff=cYear-currentYear;

            Log.e("DifferenecYear ",diff+"");


            Calendar calendar = Calendar.getInstance();
            calendar.set((cYear-18),cMonth,cDay);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

//            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        else {
           /* Log.e("InsideExpiry ","inside");
            String issueDate=etIssue.getText().toString().trim();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            Date date=null;
            try
            {
                date=simpleDateFormat.parse(issueDate);
                datePickerDialog.getDatePicker().setMinDate(date.getTime());
            } catch (ParseException e)
            {
                e.printStackTrace();
            }*/

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

            // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }

        datePickerDialog.show();
    }


    int month,currentYear,currentDay;
    public void callDatePickerDialog(String type)
    {
        String finalType = type;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth)
                    {
                        month=monthOfYear;
                        currentYear=year;
                        currentDay=dayOfMonth;

                        Log.e( "onDateSet:Year-- ",year+"" );
                        Log.e( "onDateSet:Month-- ",monthOfYear+"" );
                        Log.e( "onDateSet:day-- ",dayOfMonth+"" );


                        if (finalType.equalsIgnoreCase("i"))
                        {
                            etIssue.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            issueDate=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                        }
                        else if (finalType.equalsIgnoreCase("d"))
                        {
                            etDob.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            dob=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                        }
                        else {

                            String ddd="";
                            ddd=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;

                            String compareDates=Constant.campareLicencseDatesValidate(ddd,issueDate);

                                if (compareDates.equalsIgnoreCase("equal"))
                                {
                                    Toast.makeText(UploadDrivingActivity.this, "Expiry Date must be after issue date",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                else
                                {
                                    etExpiry.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                    expiryDate=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                                }
                        }
                    }
                }, mYear, mMonth, mDay);

        if (type.equalsIgnoreCase("i"))
        {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        }else if (type.equalsIgnoreCase("d"))
        {
            int cYear = Calendar.getInstance().get(Calendar.YEAR);
            int cMonth = Calendar.getInstance().get(Calendar.MONTH);
            int cDay = Calendar.getInstance().get(Calendar.MONTH);

            int diff=cYear-currentYear;

            Log.e("DifferenecYear ",diff+"");


            Calendar calendar = Calendar.getInstance();
            calendar.set((cYear-18),cMonth,cDay);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

//            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        else {
           /* Log.e("InsideExpiry ","inside");
            String issueDate=etIssue.getText().toString().trim();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            Date date=null;
            try
            {
                date=simpleDateFormat.parse(issueDate);
                datePickerDialog.getDatePicker().setMinDate(date.getTime());
            } catch (ParseException e)
            {
                e.printStackTrace();
            }*/

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

           // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }

        datePickerDialog.show();
    }

    public void setSpinner()
    {
        year.clear();
        year.add("Year");

        int mYear = Calendar.getInstance().get(Calendar.YEAR);

        Log.e(TAG, "setSpinner:Year "+mYear );

        for (int i = 2000; i <=mYear ; i++)
        {
            year.add(String.valueOf(i));
        }

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, year);

        SpYear.setAdapter(adapter);

        SpYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear=SpYear.getSelectedItem()+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    public void callService()
    {
        String token=sharedPrefUtil.getString(Constant.TOKEN,"");

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(),
                RequestBody.create(MediaType.parse("image/jpeg"), file));

        HashMap<String, RequestBody> param1=new HashMap<>();

        param1.put("driver_id",RequestBody.create(MediaType.parse("text/plain"),
                sharedPrefUtil.getString(SharedPrefUtil.USER_ID)));
        param1.put("vehicle_id",RequestBody.create(MediaType.parse("text/plain"),
                vehicleId));
        param1.put("fullname",RequestBody.create(MediaType.parse("text/plain"),
                etFullName.getText().toString().trim()));
        param1.put("address1",RequestBody.create(MediaType.parse("text/plain"),
                etAddress.getText().toString().trim()));
        if (etAddress2.getText().toString().trim().isEmpty())
        {
            param1.put("address2",RequestBody.create(MediaType.parse("text/plain"),
                   ""));
        }
        else {
            param1.put("address2",RequestBody.create(MediaType.parse("text/plain"),
                    etAddress2.getText().toString().trim()));
        }

        param1.put("licence_number",RequestBody.create(MediaType.parse("text/plain"),
                edt_licence_number.getText().toString().trim()));
        param1.put("issued_on",RequestBody.create(MediaType.parse("text/plain"),
                issueDate));
        param1.put("dob",RequestBody.create(MediaType.parse("text/plain"),
                dob));
        param1.put("expiry_date",RequestBody.create(MediaType.parse("text/plain"),
                expiryDate));
//        param1.put("year",RequestBody.create(MediaType.parse("text/plain"),selectedYear));
        param1.put("year",RequestBody.create(MediaType.parse("text/plain"),selectedYear));
        param1.put("country_id",RequestBody.create(MediaType.parse("text/plain"),countryId));
        param1.put("state_id",RequestBody.create(MediaType.parse("text/plain"),"2"));
        param1.put("city_id",RequestBody.create(MediaType.parse("text/plain"),"1"));
        param1.put("zip_code",RequestBody.create(MediaType.parse("text/plain"),
                etZip.getText().toString().trim()));

        Log.e("AddLicenceParams ",param1.toString());

        apiRequest="1";
        apiRequest(param1,filePart,this,token,1);

 /*       if (imageString.equalsIgnoreCase("1"))
        {
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", AddRider.file.getName(),
                    RequestBody.create(MediaType.parse("image/jpeg"), AddRider.file));

            HashMap<String, RequestBody> param1=new HashMap<>();

            param1.put("driver_id",RequestBody.create(MediaType.parse("text/plain"), sharedPrefUtil.getString(SharedPrefUtil.USER_ID)));
            param1.put("vehicle_id",RequestBody.create(MediaType.parse("text/plain"),vehicleId));
            param1.put("valid_vehicle_type",RequestBody.create(MediaType.parse("text/plain"),etFullName.getText().toString().trim()));
//            param1.put("licence_number",RequestBody.create(MediaType.parse("text/plain"),edt_licence_number.getText().toString().trim()));
            param1.put("issued_on",RequestBody.create(MediaType.parse("text/plain"),issueDate));
            param1.put("expiry_date",RequestBody.create(MediaType.parse("text/plain"),expiryDate));

            Log.e("AddRiderParams1 ",param1.toString());

            apiRequest(param1,filePart,this,token,1);
        }
        else {
            HashMap<String, String> param = new HashMap<>();
            param.put("driver_id", sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
            param.put("vehicle_id", vehicleId);
            param.put("issued_on", issueDate);
            param.put("expiry_date", expiryDate);
            param.put("driver_id",sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
            param.put("valid_vehicle_type",etFullName.getText().toString().trim());
//            param.put("licence_number",edt_licence_number.getText().toString().trim());
            Log.e("AddRiderParams ",param.toString());
            api(param,this,token,7);
        }*/
    }

    private boolean checkValidations()
    {
       /* if (user_pic_path0.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please upload image", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        if (file==null)
        {
            Toast.makeText(this, "Please upload image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etFullName.getText().toString().trim().length() == 0)
        {
            etFullName.setError("Full Name Should Not be empty");
            etFullName.requestFocus();
            return false;
        }
        else if (countryId.equalsIgnoreCase("Select Country") || countryId.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please choose country", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if (issueDate.equalsIgnoreCase(""))
        {
            Toast.makeText(this, getString(R.string.issue_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edt_licence_number.getText().toString().trim().length() == 0)
        {
            edt_licence_number.setError(getResources().getString(R.string.val_licence_number));
            edt_licence_number.requestFocus();
            return false;
        }
        if (dob.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Choose date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (expiryDate.equalsIgnoreCase(""))
        {
            Toast.makeText(this, getString(R.string.expiry_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etAddress.getText().toString().trim().length() == 0)
        {
            etAddress.setError("Address Should Not be empty");
            etAddress.requestFocus();
            return false;
        }
     /*   if (etAddress2.getText().toString().trim().length() == 0)
        {
            etAddress2.setError("Address Should Not be empty");
            etAddress2.requestFocus();
            return false;
        }*/

        if (etCity.getText().toString().trim().length() == 0)
        {
            etCity.setError("City Should Not be empty");
            etCity.requestFocus();
            return false;
        }
        if (etState.getText().toString().trim().length() == 0)
        {
            etState.setError("State Should Not be empty");
            etState.requestFocus();
            return false;
        }
        if (etZip.getText().toString().trim().length() == 0)
        {
            etZip.setError("Zip Should Not be empty");
            etZip.requestFocus();
            return false;
        }

       /* if (selectedYear.equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Choose year", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        return true;
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
                .build(UploadDrivingActivity.this);
        try {
            camera.takePicture();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (data!=null)
        {
            switch (requestCode)
            {
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK)
                    {
                        Uri resultUri = result.getUri();

                        String path=resultUri.getPath();
                        file=new File(path);

                        Log.e("Paaaath","Paaaath"+path);
                        Log.e("Fileeee","Fileeee"+file);
                        Bitmap bitmap = null;

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        img_licence.setImageBitmap(bitmap);

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    {
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

                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(UploadDrivingActivity.this);

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

    Date selectDateIssuedON;

 /*   private void calendarDialog()
    {
            if(TYPE_SELELCT==1)
            {
                Date currentDate = new Date();
                Date minYear = new Date();
                minYear.setYear(currentDate.getYear() - 102);
                Date maxYear = new Date();
                DatePickerDialog dialog;
                if (selectDateIssuedON == null) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(currentDate);
                    dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                } else {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(selectDateIssuedON);
                    dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }
                dialog.getDatePicker().setMinDate(minYear.getTime());
                dialog.getDatePicker().setMaxDate(maxYear.getTime());
                dialog.show();
            }else {
                Date currentDate = new Date();
                Date minYear = new Date();
                minYear.setYear(currentDate.getYear() - 102);
                Date maxYear = new Date();
                DatePickerDialog dialog;

                if (expiryDate == null)
                {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(currentDate);
                    dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                }
                else {
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
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        if(TYPE_SELELCT==1)
        {
            selectDateIssuedON = new Date();
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DATE, dayOfMonth);
            selectDateIssuedON.setTime(c.getTimeInMillis());
            etIssue.setText(year + "-" + month + "-" + dayOfMonth);
        } else
            {
            expiryDate = new Date();
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DATE, dayOfMonth);
            expiryDate.setTime(c.getTimeInMillis());
            etExpiry.setText(year + "-" + month + "-" + dayOfMonth);
        }
    }*/


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
//            takePicture();
        }
    }

    private void verifyPermissions(String[] permissions)
    {
        for (int i = 0; i < permissions.length; i++)
        {
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


    public void onRequestPermissionsResult(int requestCode, String per[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 0:
                {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    takePicture();
                    ImageSelection();
                }
                else {
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
            APIRequest.apiInterface(token).addDrivingLicence(param,image).enqueue(responseBodyCallback);
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
        if (apiRequest.equalsIgnoreCase("0"))
        {
            GeneralResponse generalResponse=new GeneralResponse(response);

            Log.e(TAG, "onResponse: General<< "+generalResponse);

            try {
                JSONObject jsonObject=generalResponse.getResponse_object();
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {

                    countryList.clear();
                    countryNameList.clear();


                    countryNameList.add("Select Country");

                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject obj=jsonArray.getJSONObject(i);

                        CountryMOdel countryMOdel=new CountryMOdel();

                        countryMOdel.setCid(obj.getString("id"));
                        countryMOdel.setCname(obj.getString("name"));
                        countryMOdel.setIso3(obj.getString("iso3"));
                        countryMOdel.setNumcode(obj.getString("numcode"));
                        countryMOdel.setPhncode(obj.getString("phonecode"));

                        countryList.add(countryMOdel);
                        countryNameList.add(obj.getString("name"));

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            this, android.R.layout.simple_spinner_item, countryNameList);

/*
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner,countryNameList)
                    {

                        public View getView(int position, View convertView, ViewGroup parent) {

                            View v = super.getView(position, convertView, parent);

                            ((TextView) v).setTextSize(16);

                            return v;

                        }

                        public View getDropDownView(int position, View convertView,ViewGroup parent)
                        {

                            View v = super.getDropDownView(position, convertView,parent);

                            ((TextView) v).setGravity(Gravity.CENTER);

                            return v;

                        }

                    };*/

                    SpCountry.setAdapter(adapter);

                    SpCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            countryId=countryList.get(position).getCid()+"";
                            Log.e(TAG, "onItemSelected: SelectCountry"+countryId );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else {
                    Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (apiRequest.equalsIgnoreCase("1"))
        {
            GeneralResponse generalResponse=new GeneralResponse(response);

            Log.e(TAG, "onResponse: General<< "+generalResponse);

            try {
                JSONObject jsonObject=generalResponse.getResponse_object();

                Log.e("JsonObjectResponse..",jsonObject+"");
                String key1="licence";
                sharedPrefUtil.saveString(SharedPrefUtil.LICENCE,key1);
                if (jsonObject.getString("status").equalsIgnoreCase("true"))
                {

                    Intent intent = new Intent(this,UploadDocumentActivity.class);
                    startActivity(intent);
//                    setResult(Constant.ACT_RESULT_DRIVING_LICENCE,intent);
//                    startActivity(intent);
                }
                else
                    {
                    Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
        progressDialog.dismiss();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {

    }
}
