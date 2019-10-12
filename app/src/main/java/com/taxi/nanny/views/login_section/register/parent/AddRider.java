package com.taxi.nanny.views.login_section.register.parent;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.taxi.nanny.R;
import com.taxi.nanny.custom.camera.Camera;
import com.taxi.nanny.utils.AppUtils;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.views.BaseActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class AddRider extends BaseActivity
{
    @BindView(R.id.tvNext)
    TextView tvNext;

    @BindView(R.id.tvBirth)
    TextView tvBirth;

    @BindViews({R.id.etFirst,R.id.etLast})
    List<EditText> editList;

    String gender="";
    String imageString="";
    String birth="";

    @BindView(R.id.rg)
    RadioGroup rg;

    @BindView(R.id.rbMale)
    RadioButton rbMale;

    @BindView(R.id.rbFemale)
    RadioButton rbFemale;

    @BindView(R.id.ivRider)
    ImageView ivRider;

    @BindView(R.id.linearAdd)
    LinearLayout linearAdd;

    String[] notYetpermitted;

    String user_pic_path0="";
    public static File file;

    private int mYear, mMonth, mDay;

    @BindView(R.id.SpGender)
    Spinner SpGender;

    String[] Gender={"Male","Female","Do not prefer to answer"};

    @BindView(R.id.tvADd)
    TextView tvADd;


    @Override
    protected int getContentId()
    {
//        return R.layout.add_rider;
        return R.layout.rider_add;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText(getString(R.string.rider_add));
      //  setRadioGroup();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, Gender);

        SpGender.setAdapter(adapter);
        SpGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                gender=SpGender.getSelectedItem()+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // setSpinner();
    }

    public void callDatePickerDialog()
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
                        tvBirth.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        birth=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                    }
                }, mYear, mMonth, mDay);

        int cYear = Calendar.getInstance().get(Calendar.YEAR);
        int cMonth = Calendar.getInstance().get(Calendar.MONTH);
        int cDay = Calendar.getInstance().get(Calendar.MONTH);


        Calendar calendar = Calendar.getInstance();
        calendar.set((cYear-2),cMonth,cDay);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


        datePickerDialog.show();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent=new Intent(AddRider.this,ListofChildren.class);
        startActivity(intent);

    }

    private void setDatePicker()
    {
        final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        final String[] Days = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};

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

                tvBirth.setText(year + "-" + exactMonth + "-" + day);
                birth=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;

            }
        }, mYear, mMonth, mDay);
        try
        {
            int cYear = Calendar.getInstance().get(Calendar.YEAR);
            int cMonth = Calendar.getInstance().get(Calendar.MONTH);
            int cDay = Calendar.getInstance().get(Calendar.MONTH);


            Calendar calendar = Calendar.getInstance();
            calendar.set((cYear-2),cMonth,cDay);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

          //  datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        datePickerDialog.show();
    }


    /*private void setSpinner()
    {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, gender);

        spinnerList.get(0).setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, prefer);
        spinnerList.get(1).setAdapter(adapter1);
    }*/

    @OnClick({R.id.img_back_btn,R.id.tvNext,R.id.tvBirth,R.id.linearAdd})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;

           case R.id.tvNext:

               if (checkValidation())
               {
                   if (file==null)
                   {
                       imageString="0";
                   }
                   else {
                       imageString="1";
                   }
                   Intent intent=new Intent(this, AddRiderPreference.class);
                   intent.putExtra("fname",editList.get(0).getText().toString().trim());
                   intent.putExtra("lname",editList.get(1).getText().toString().trim());
//                   intent.putExtra("birthday",tvBirth.getText().toString().trim());
                   intent.putExtra("birthday","1998-5-20");
                   intent.putExtra("gender",gender);
                   intent.putExtra("image",imageString);
                   startActivity(intent);
               }
               break;

            case R.id.tvBirth:
//                callDatePickerDialog();

                Constant.hideKeyboard(this,view);
                setDatePicker();
                break;

                case R.id.linearAdd:
                    cameraPermission();
                break;
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
           // takePicture();
        }
    }

    public void setRadioGroup()
    {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rbMale:
                        gender="male";
                        break;

                    case R.id.rbFemale:
                        gender="female";
                        break;
                }
            }
        });
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.e("RequestPermission ","if");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    takePicture();
                    ImageSelection();

                } else
                {
                    Log.e("RequestPermission ","Else");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        Log.e("Dataaaaaa ",data+"");
        if (data != null)
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

                 /*   Log.e("Paaaath","Paaaath"+path);
                    Log.e("Fileeee","Fileeee"+file);*/
                        Bitmap bitmap = null;

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ivRider.setImageBitmap(bitmap);
                        tvADd.setText("Change photo");


                    }
                    else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    {
                    Exception error = result.getError();
                    }
                    break;

                case 4:
                    if(camera!=null)
                    {
                        user_pic_path0 = camera.getCameraBitmapPath();
//                       Log.e( "ImagePath ",user_pic_path0);
                        ivRider.setImageBitmap(camera.getCameraBitmap());
                        file=new File(user_pic_path0);
               /*        Log.e("ISFile",file.isFile()+"");
                       Log.e("directory ",file.isDirectory()+"");
                       Log.e("NAme",file.getName()+"");*/
                    }
                    break;

                case 2:
                    Log.e("2  ","2");
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ivRider.setImageBitmap(bitmap);
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

                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(AddRider.this);

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
    public boolean checkValidation()
    {
        if (editList.get(0).getText().toString().trim().length() == 0)
        {
            editList.get(0).setError(getResources().getString(R.string.val_first_name));
            editList.get(0).requestFocus();
            return false;
        }else if (editList.get(0).getText().toString().contains(" "))
        {
            editList.get(0).setError(getResources().getString(R.string.val_space));
            editList.get(0).requestFocus();
            return false;
        }
      else  if (editList.get(1).getText().toString().trim().length() == 0)
        {
            editList.get(1).setError(getResources().getString(R.string.val_last_name));
            editList.get(1).requestFocus();
        return false;
        }else if (editList.get(1).getText().toString().contains(" "))
        {
            editList.get(1).setError(getResources().getString(R.string.val_space));
            editList.get(1).requestFocus();
            return false;
        }
        if (birth.equalsIgnoreCase(""))
        {
            Toast.makeText(this, getString(R.string.val_birthday), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (gender.equalsIgnoreCase(""))
        {
            Toast.makeText(this, getString(R.string.val_gender), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
