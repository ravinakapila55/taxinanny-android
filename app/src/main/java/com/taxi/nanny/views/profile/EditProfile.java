package com.taxi.nanny.views.profile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.custom.camera.Camera;
import com.taxi.nanny.domain.APIRequest;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.AppUtils;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.autoComplete.GooglePlacesAutocompleteAdapter;
import com.taxi.nanny.utils.autoComplete.PlacesAutoCompleteAdapter;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.EditNickName;
import com.taxi.nanny.views.booking.PickDropConfirmation;
import com.taxi.nanny.views.driver.DriverAccount;
import com.taxi.nanny.views.login_section.dialog.InternetErrorDialog;
import com.taxi.nanny.views.profile.adapter.FavouriteLocationsProfileAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends BaseActivity implements RetrofitResponse, Callback<ResponseBody>
{
    @BindView(R.id.edt_first_name)
    EditText edt_first_name;

    @BindView(R.id.edt_user_last_name)
    EditText edt_user_last_name;

    @BindView(R.id.edt_user_email_id)
    EditText edt_user_email_id;

    @BindView(R.id.edt_phone)
    EditText edt_phone;

    @BindView(R.id.et_address)
    AutoCompleteTextView et_address;

    @BindView(R.id.tvEnd)
    TextView tvEnd;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.ivUser)
    CircleImageView ivUser;

    @BindView(R.id.ivEdit)
    ImageView ivEdit;

    SharedPrefUtil  sharedPrefUtil;

    ArrayList<RiderListModel> list=new ArrayList<>();

    String[] notYetpermitted;
    String user_pic_path0="";
    public static File file;

    @BindView(R.id.tvLabel)
    TextView tvLabel;

    @BindView(R.id.viewwww)
    View viewwww;

    @BindView(R.id.cvDistance)
    CardView cvDistance;

    @BindView(R.id.edt_distance)
    EditText edt_distance;

    @BindView(R.id.progress_edit)
    ProgressBar progress_edit;


    @Override
    protected int getContentId()
    {
        return R.layout.edit_profile;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Edit Profile");
        tvEnd.setVisibility(View.VISIBLE);
        tvEnd.setText("Save");
        sharedPrefUtil=SharedPrefUtil.getInstance();

        if (sharedPrefUtil.getString(SharedPrefUtil.USERTYPE,"").equalsIgnoreCase("driver"))
        {
            tvLabel.setVisibility(View.GONE);
            viewwww.setVisibility(View.GONE);
            recycler.setVisibility(View.GONE);
            cvDistance.setVisibility(View.VISIBLE);
        }
        else
        {
            tvLabel.setVisibility(View.VISIBLE);
            viewwww.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.VISIBLE);
            cvDistance.setVisibility(View.GONE);
        }
//        setData();

        PlacesAutoCompleteAdapter mAdapter = new PlacesAutoCompleteAdapter
                (this, R.layout.custom_spinner_new);
        et_address.setAdapter(mAdapter);
        et_address.setImeOptions(EditorInfo.IME_ACTION_DONE);
        setLoactionAdapter(et_address);

        callUserData();
    }

    private void setLoactionAdapter(AutoCompleteTextView autoCompleteTextView)
    {
        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(getApplicationContext(),
                R.layout.custom_spinner_new));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                String placeId = GooglePlacesAutocompleteAdapter.resultListId.get(position).toString();

  /*              final String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" +
                        placeId + "&key=AIzaSyCxj7Z3cWeV8phaVuua1cSQ88bWT_ls5u0";*/

                final String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" +
                        placeId + "&key=AIzaSyAK5It4p1CiJ2gFzWRbfs24Cibo2QTcPRU";


                hideKeyboard(view);

//                constant.hideKeyboard(EnterPickUp.this,view);

                new AsyncTask<Void, Void, String>()
                {
                    @Override
                    protected void onPreExecute()
                    {
                        super.onPreExecute();
//                      DialogClass.showDialog(Listing.this);
                    }
                    @Override
                    protected String doInBackground(final Void... params)
                    {
                        try
                        {
                            serviceArrive(url);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(final String result)
                    {
//                        DialogClass.logout();
                    }

                }.execute();
            }
        });
    }

    private void serviceArrive(String url1)
    {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try
        {
            URL url = new URL(url1);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            Log.e("url ", "" + url);
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1)
            {
                jsonResults.append(buff, 0, read);
            }
        }

        catch (MalformedURLException e)
        {
            Log.e("EXC", "Error processing Places API URL", e);
        }

        catch (IOException e)
        {
            Log.e("EXC", "Error connecting to Places API", e);
        }

        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }

        try
        {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject res = jsonObj.getJSONObject("result");
            JSONObject geo = res.getJSONObject("geometry");
            JSONObject loc = geo.getJSONObject("location");

            lattitude=loc.getString("lat");
            longitude=loc.getString("lng");
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    String lattitude="",longitude="";

    public void setData()
    {
        edt_first_name.setText(sharedPrefUtil.getString(SharedPrefUtil.FNAME,""));
        edt_user_last_name.setText(sharedPrefUtil.getString(SharedPrefUtil.LNAME,""));
        edt_user_email_id.setText(sharedPrefUtil.getString(SharedPrefUtil.EMAIL,""));
        edt_phone.setText(sharedPrefUtil.getString(SharedPrefUtil.PHONE_NUMBER,""));

        if (sharedPrefUtil.getString(SharedPrefUtil.ADDRESS,"").equalsIgnoreCase("null"))
        {
            et_address.setText("");
        }
        else
        {
            et_address.setText(sharedPrefUtil.getString(SharedPrefUtil.ADDRESS,""));
        }

        if (sharedPrefUtil.getString(SharedPrefUtil.DRIVER_PROXIMITY,"").equalsIgnoreCase("null"))
        {
            edt_distance.setText("");
        }
        else
        {
            edt_distance.setText(sharedPrefUtil.getString(SharedPrefUtil.DRIVER_PROXIMITY,"") +" mi");
        }

        if (!sharedPrefUtil.getString(sharedPrefUtil.getString(SharedPrefUtil.ADDRESS_LATITUDE,"")).equalsIgnoreCase("null"))

        {
            lattitude=sharedPrefUtil.getString(SharedPrefUtil.ADDRESS_LATITUDE,"");
        }
        else
        {
            lattitude="";
        }

        if (!sharedPrefUtil.getString(sharedPrefUtil.getString(SharedPrefUtil.ADDRESS_LONGITUDE,"")).equalsIgnoreCase("null"))
        {
            longitude=sharedPrefUtil.getString(SharedPrefUtil.ADDRESS_LONGITUDE,"");
        }
        else
        {
            longitude="";
        }

        Log.e("Imageeeeeeeee ",sharedPrefUtil.getString(SharedPrefUtil.IMAGE,""));

      /* if (!sharedPrefUtil.getString(SharedPrefUtil.IMAGE,"").equalsIgnoreCase("null") ||
               !sharedPrefUtil.getString(SharedPrefUtil.IMAGE,"").equalsIgnoreCase(""))*/

       if (!sharedPrefUtil.getString(SharedPrefUtil.IMAGE,"").equalsIgnoreCase("0"))
        {

            Picasso.with(this)
                    .load(sharedPrefUtil.getString(SharedPrefUtil.IMAGE,""))
                    .into(ivUser, new com.squareup.picasso.Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            progress_edit.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError()
                        {

                        }
                    });

          /*  Picasso.with(this).load(sharedPrefUtil.getString(SharedPrefUtil.IMAGE,"")).
                    placeholder(getResources().getDrawable(R.drawable.pic_dummy_user))
                    .into(ivUser);*/
        }
    }
    ProgressDialog progress;

    public void callFavouriteList()
    {
        new RetrofitService(this, this,
                Constant.API_RIDER_FAVOURITE_LOCATION_LIST +"/"+ sharedPrefUtil.getString(SharedPrefUtil.USER_ID,
                        ""), 200, 1,"1").
                callService(true);
    }


    public void callUserData()
    {
        new RetrofitService(this, this,
                Constant.API_VIEW_PROFILE +"/"+ sharedPrefUtil.getString(SharedPrefUtil.USER_ID,
                        ""), 500, 1,"1").
                callService(true);
    }

    @OnClick({R.id.img_back_btn,R.id.ivEdit,R.id.tvEnd})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;

            case R.id.ivEdit:

                cameraPermission();

                break;


                case R.id.tvEnd:

                    if (et_address.getText().toString().trim().isEmpty())
                    {
                        lattitude="";
                        longitude="";
                    }

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

    String imageString="0";

    public void callService()
    {
        String token=sharedPrefUtil.getString(Constant.TOKEN,"");

        Log.e("ImageString ",imageString);

        if (imageString.equalsIgnoreCase("1"))
        {
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(),
                    RequestBody.create(MediaType.parse("image/jpeg"), file));

            HashMap<String, RequestBody> param1=new HashMap<>();

            param1.put("user_id",RequestBody.create(MediaType.parse("text/plain"),
                    sharedPrefUtil.getString(SharedPrefUtil.USER_ID)));
            param1.put("first_name",RequestBody.create(MediaType.parse("text/plain"),edt_first_name.getText().toString().trim()));
            param1.put("last_name",RequestBody.create(MediaType.parse("text/plain"),edt_user_last_name.getText().toString().trim()));
            param1.put("phone_no",RequestBody.create(MediaType.parse("text/plain"),edt_phone.getText().toString().trim()));
            param1.put("address_latitude",RequestBody.create(MediaType.parse("text/plain"),lattitude));
            param1.put("address_longitude",RequestBody.create(MediaType.parse("text/plain"),longitude));
            param1.put("address",RequestBody.create(MediaType.parse("text/plain"),et_address.getText().toString().trim()));


            if (sharedPrefUtil.getString(SharedPrefUtil.USERTYPE,"").equalsIgnoreCase("driver"))
            {
                param1.put("proximity",RequestBody.create(MediaType.parse("text/plain"),edt_distance.getText().toString().trim()));
            }


                Log.e("EditProfileParams ",param1.toString());
            apiRequest(param1,filePart,this,token,1);
        }
        else
        {
            HashMap<String, String> param = new HashMap<>();
            param.put("user_id", sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
            param.put("first_name",edt_first_name.getText().toString().trim());
            param.put("last_name",edt_user_last_name.getText().toString().trim());
            param.put("phone_no",edt_phone.getText().toString().trim());
            param.put("address_latitude",lattitude);
            param.put("address_longitude",longitude);
            param.put("address",et_address.getText().toString().trim());

             if (sharedPrefUtil.getString(SharedPrefUtil.USERTYPE,"").equalsIgnoreCase("driver"))
             {
                 param.put("proximity",edt_distance.getText().toString().trim());
             }

            Log.e("EditProfileParams ",param.toString());
            api(param,this,token,22);
        }
    }

    protected void apiRequest(HashMap<String, RequestBody> param, MultipartBody.Part image, Callback<ResponseBody> responseBodyCallback,
                              String token , int TYPE_REQUEST)
    {
        if (isNetworkConnected())
        {
            progressDialog = APIRequest.getProgress(this);
            APIRequest.apiInterface(token).editProfile(param,image).enqueue(responseBodyCallback);
        }
        else
        {
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
        String distValue="";



        if (edt_distance.getText().toString().trim().contains(" mi"))
        {
            distValue=edt_distance.getText().toString().trim().replace(" mi","");
        }
        else
        {
            distValue=edt_distance.getText().toString().trim();
        }
        if (edt_first_name.getText().toString().trim().length() == 0)
        {
            edt_first_name.setError("Please enter First Name");
            edt_first_name.requestFocus();
            return false;
        }
        else if (edt_first_name.getText().toString().contains(" "))
        {
            edt_first_name.setError(getResources().getString(R.string.val_space));
            edt_first_name.requestFocus();
            return  false;
        }
        else if (edt_user_last_name.getText().toString().trim().length() == 0)
        {
            edt_user_last_name.setError("Please enter Last Name");
            edt_user_last_name.requestFocus();
            return false;
        }

        else if (edt_user_last_name.getText().toString().contains(" "))
        {
            edt_user_last_name.setError(getResources().getString(R.string.val_space));
            edt_user_last_name.requestFocus();
            return  false;
        }

        else if (edt_phone.getText().toString().trim().equalsIgnoreCase(""))
        {
            edt_phone.setError("Please enter phone number");
            edt_phone.requestFocus();
            return false;
        }

        else if (edt_phone.getText().toString().trim().length() <10)
        {
            edt_phone.setError("Please enter Phone number of 10 digits");
            edt_phone.requestFocus();
            return false;
        }

        else if (lattitude.equalsIgnoreCase("") && longitude.equalsIgnoreCase(""))
        {
            et_address.setError("Please choose home address");
            et_address.requestFocus();
            return false;
        }

        if (sharedPrefUtil.getString(SharedPrefUtil.USERTYPE,"").equalsIgnoreCase("driver"))
        {
         if (edt_distance.getText().toString().trim().equalsIgnoreCase(""))
         {
                edt_distance.setError("Please enter distance");
                edt_distance.requestFocus();
                return false;
            }
//         else if (Integer.parseInt(edt_distance.getText().toString().trim())==0 || Integer.parseInt(edt_distance.getText().toString().trim())>1000)
         else if (Integer.parseInt(distValue)==0 || Integer.parseInt(distValue)>1000)
         {
             Toast.makeText(this,"Proximity must be greater than 0 or less than 1000",
                     Toast.LENGTH_SHORT).show();
             return  false;
         }
        }

        return true;
    }

    private void cameraPermission()
    {
        ArrayList<String> permissionNeeded = AppUtils.permissionNeeded(AppUtils.getCameraPermission(getApplicationContext()), getApplicationContext());
        if (permissionNeeded.size() > 0)
        {
            notYetpermitted = AppUtils.getStringArray(permissionNeeded);
            verifyPermissions(notYetpermitted);
        }
        else
        {
            ImageSelection();
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
                else
                {
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
                else
                {
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
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        Log.e("OnActivityResultData ",data+"");

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
                        user_pic_path0=resultUri.getPath();
                        file=new File(path);

                 /*   Log.e("Paaaath","Paaaath"+path);
                    Log.e("Fileeee","Fileeee"+file);*/
                        Bitmap bitmap = null;

                        try
                        {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        ivUser.setImageBitmap(bitmap);

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
                        ivUser.setImageBitmap(camera.getCameraBitmap());
                        file=new File(user_pic_path0);
               /*        Log.e("ISFile",file.isFile()+"");
                       Log.e("directory ",file.isDirectory()+"");
                       Log.e("NAme",file.getName()+"");*/
                    }

                    break;


                case 2:
                    Log.e("2  ","2");
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ivUser.setImageBitmap(bitmap);
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Anty";
                    user_pic_path0=path;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPrefUtil.getString(SharedPrefUtil.USERTYPE,"").equalsIgnoreCase("parent"))
        {
            callFavouriteList();
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

                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(EditProfile.this);

                }catch (Exception e){
                    e.printStackTrace();
                }

                alert.dismiss();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 2);
                }
                catch (Exception e)
                {
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


    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        FavouriteLocationsProfileAdapter favouriteLocationsProfileAdapter=new
                FavouriteLocationsProfileAdapter(EditProfile.this,list);
        recycler.setAdapter(favouriteLocationsProfileAdapter);

        favouriteLocationsProfileAdapter.onItemSelectedLister(new FavouriteLocationsProfileAdapter.onClickListener()
        {
            @Override
            public void onEditClick(int layoutPosition, View view)
            {
                Intent intent=new Intent(EditProfile.this, EditNickName.class);
                intent.putExtra("data",(Serializable)list.get(layoutPosition));
                startActivity(intent);
            }

        });

    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 500:

                Log.e("ViewProfile ",response);

                try
                {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        JSONObject data=jsonObject.getJSONObject("data");

                        sharedPrefUtil.saveString(SharedPrefUtil.FNAME,data.getString("first_name"));
                        sharedPrefUtil.saveString(SharedPrefUtil.LNAME,data.getString("last_name"));
                        sharedPrefUtil.saveString(SharedPrefUtil.PHONE_NUMBER,data.getString("phone_no"));
                        sharedPrefUtil.saveString(SharedPrefUtil.EMAIL,data.getString("email"));
                        sharedPrefUtil.saveString(SharedPrefUtil.ADDRESS,data.getString("address"));
                        sharedPrefUtil.saveString(SharedPrefUtil.ADDRESS_LATITUDE,data.getString("address_latitude"));
                        sharedPrefUtil.saveString(SharedPrefUtil.ADDRESS_LONGITUDE,data.getString("address_longitude"));
                        sharedPrefUtil.saveString(SharedPrefUtil.DRIVER_PROXIMITY,data.getString("proximity"));

                        if (data.getString("image")!=("null") || data.getString("image")!=("") )
                        {
                            sharedPrefUtil.saveString(SharedPrefUtil.IMAGE,data.getString("image"));
                        }
                        else {
                            sharedPrefUtil.saveString(SharedPrefUtil.IMAGE,"0");
                        }

                        setData();
                    }
                    else
                    {

                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;

                case 200:

                Log.e("FavouriteLocations ",response);

                try
                {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        recycler.setVisibility(View.VISIBLE);
                        list.clear();
                        JSONArray data=jsonObject.getJSONArray("data");

                        if (data.length()>0)
                        {
                            for (int i = 0; i <data.length() ; i++)
                            {
                                RiderListModel riderListModel=new RiderListModel();

                                JSONObject object=data.getJSONObject(i);
                                riderListModel.setFav_loc_id(object.getString("id"));
                                riderListModel.setFav_loc(object.getString("location_name"));
                                riderListModel.setFav_latt(object.getString("lattitude"));
                                riderListModel.setFav_lng(object.getString("longitude"));
                                riderListModel.setFav_nick_name(object.getString("nick_name"));
                                riderListModel.setFav_description(object.getString("description"));
                                list .add(riderListModel);
                            }

                            if (list.size()>0)
                            {
                                recycler.setVisibility(View.VISIBLE);
                                Log.e(" ListSize ",list.size()+"");
                                setAdapter();
                            }
                        }
                        else {
                            recycler.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        recycler.setVisibility(View.GONE);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e( "onResponse:EditProfile " ,generalResponse+"");

        try
        {
            JSONObject jsonObject=generalResponse.getResponse_object();
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                Toast.makeText(this, jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                JSONObject data=jsonObject.getJSONObject("data");

                sharedPrefUtil.saveString(SharedPrefUtil.FNAME,data.getString("first_name"));
                sharedPrefUtil.saveString(SharedPrefUtil.LNAME,data.getString("last_name"));
                sharedPrefUtil.saveString(SharedPrefUtil.PHONE_NUMBER,data.getString("phone_no"));
                sharedPrefUtil.saveString(SharedPrefUtil.EMAIL,data.getString("email"));
                sharedPrefUtil.saveString(SharedPrefUtil.ADDRESS,data.getString("address"));
                sharedPrefUtil.saveString(SharedPrefUtil.ADDRESS_LATITUDE,data.getString("address_latitude"));
                sharedPrefUtil.saveString(SharedPrefUtil.ADDRESS_LONGITUDE,data.getString("address_longitude"));

                if (jsonObject.getString("profile_pic")==("null") ||jsonObject.getString("profile_pic").equalsIgnoreCase("") )
                {
                    sharedPrefUtil.saveString(SharedPrefUtil.IMAGE,"0");
                }
                else {

                    sharedPrefUtil.saveString(SharedPrefUtil.IMAGE,jsonObject.getString("profile_pic"));
                }

                setData();
                callUserData();

                if (sharedPrefUtil.getString(SharedPrefUtil.USERTYPE,"").equalsIgnoreCase("driver"))
                {
                    Intent intent=new Intent(this, DriverAccount.class);
                    startActivity(intent);
                }
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
}
