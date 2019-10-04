package com.taxi.nanny.views.login_section.register;

import android.app.AlertDialog;
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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.taxi.nanny.R;
import com.taxi.nanny.custom.camera.Camera;
import com.taxi.nanny.domain.APIRequest;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.utils.AppUtils;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.autoComplete.GooglePlacesAutocompleteAdapter;
import com.taxi.nanny.utils.autoComplete.PlacesAutoCompleteAdapter;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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


public class RegisterUserDetailsActivity extends BaseActivity implements
        Callback<ResponseBody>, View.OnFocusChangeListener
   {

    @Override
    protected int getContentId()
    {
        return R.layout.activity_register_userdetails;
    }

    int userType;

    @BindView(R.id.btn_submit)
    TextView btn_submit;

    @BindViews({R.id.edt_first_name,R.id.edt_user_last_name,R.id.edt_phone,R.id.edt_user_email_id,
            R.id.edtPassword,R.id.edtCPassword})
    List<TextView>edtTextViewList;

    @BindView(R.id.pswdValidationCard)
    CardView pswdValidationCard;

    @BindView(R.id.ivTickLimit)
    ImageView  ivTickLimit;

    @BindView(R.id.tvLimit)
    TextView tvLimit;

    @BindView(R.id.ivTickCase)
    ImageView ivTickCase;

    @BindView(R.id.tvCase)
    TextView tvCase;

    @BindView(R.id.ivTickAtleast)
    ImageView ivTickAtleast;

    @BindView(R.id.tvAtleast)
    TextView tvAtleast;

    @BindView(R.id.cbTerms)
    CheckBox cbTerms;

    @BindView(R.id.rlImage)
    RelativeLayout rlImage;

    @BindView(R.id.llPayment)
    LinearLayout llPayment;

    @BindView(R.id.tvAccount)
    TextView tvAccount;

    @BindView(R.id.cvAddress)
    CardView cvAddress;

    @BindView(R.id.llNotifications)
    LinearLayout llNotifications;

    @BindView(R.id.et_card_name)
    EditText et_card_name;

    @BindView(R.id.card_name)
    CardView card_name;

    boolean capitalFlag = false;
    boolean lowerCaseFlag = false;
    boolean numberFlag = false;
    SharedPrefUtil helper;

    @BindView(R.id.ivUser)
    de.hdodenhof.circleimageview.CircleImageView ivUser;

    @BindView(R.id.ivEdit)
    ImageView ivEdit;

    @BindView(R.id.et_address)
    AutoCompleteTextView et_address;

    @BindView(R.id.swNoti)
    Switch swNoti;

    @BindView(R.id.swSms)
    Switch swSms;

    @BindView(R.id.card_input_widget)
    CardMultilineWidget card_input_widget;

    Card card;
    String terms="0";

    String notiSettings="0";
    String smsSettings="0";

    @BindView(R.id.cvProxy)
    CardView cvProxy;

    @BindView(R.id.edtProxy)
    EditText edtProxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText(getResources().getString(R.string.header_register));
        helper = SharedPrefUtil.getInstance();
        userType = getIntent().getIntExtra(Constant.BUNDLE_USER_TYPE, 0);

        if (userType==0)
        {
            rlImage.setVisibility(View.VISIBLE);
            llPayment.setVisibility(View.VISIBLE);
            cvAddress.setVisibility(View.VISIBLE);
            llNotifications.setVisibility(View.VISIBLE);
            card_name.setVisibility(View.VISIBLE);
            card_input_widget.setVisibility(View.VISIBLE);
            cvProxy.setVisibility(View.GONE);
            tvAccount.setVisibility(View.GONE);
        }

        //driver
        else
        {
            rlImage.setVisibility(View.VISIBLE);
            llPayment.setVisibility(View.GONE);
            cvAddress.setVisibility(View.VISIBLE);
            cvProxy.setVisibility(View.VISIBLE);
            llNotifications.setVisibility(View.VISIBLE);
            tvAccount.setVisibility(View.GONE);
            card_name.setVisibility(View.GONE);
            card_input_widget.setVisibility(View.GONE);
        }

        PlacesAutoCompleteAdapter mAdapter = new PlacesAutoCompleteAdapter(this, R.layout.custom_spinner_new);
        et_address.setAdapter(mAdapter);
        et_address.setImeOptions(EditorInfo.IME_ACTION_DONE);
        setLoactionAdapter(et_address);

        cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                if (isChecked)
                {
                    terms="1";
                }
                else {
                    terms="0";
                }
            }
        });

        swNoti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    notiSettings="1";
                }
                else
                {
                    notiSettings="0";
                }
            }
        });

        swSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    smsSettings="1";
                }
                else {
                    smsSettings="0";
                }
            }
        });

        edtTextViewList.get(4).addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length()>0)
                {
                    pswdValidationCard.setVisibility(View.VISIBLE);
                    if (s.length()==8)
                    {
                        tvLimit.setTextColor(getResources().getColor(R.color.colorGreen));
                        ivTickLimit.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));
                    }

                    else if (s.length()<8)
                    {
                        tvLimit.setTextColor(getResources().getColor(R.color.black));
                        ivTickLimit.setImageDrawable(getResources().getDrawable(R.drawable.tick_black));
                    }

                    char ch;
                    for(int i=0;i < s.length();i++)
                    {
                        ch = s.charAt(i);
                        if( Character.isDigit(ch))
                        {
                            numberFlag = true;
                        }

                         if (Character.isUpperCase(ch))
                         {
                            capitalFlag = true;
                         }
                         if (Character.isLowerCase(ch))
                         {
                            lowerCaseFlag = true;
                         }
                    }

                    if (capitalFlag && lowerCaseFlag)
                    {
                        tvCase.setTextColor(getResources().getColor(R.color.colorGreen));
                        ivTickCase.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));
                    }
                    else if (!capitalFlag && !lowerCaseFlag)
                    {
                        tvCase.setTextColor(getResources().getColor(R.color.black));
                        ivTickCase.setImageDrawable(getResources().getDrawable(R.drawable.tick_black));
                    }
                    if (numberFlag)
                    {
                        tvAtleast.setTextColor(getResources().getColor(R.color.colorGreen));
                        ivTickAtleast.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));
                    }

                   else if (!numberFlag)
                    {
                        tvAtleast.setTextColor(getResources().getColor(R.color.black));
                        ivTickAtleast.setImageDrawable(getResources().getDrawable(R.drawable.tick_black));
                    }

                   if (numberFlag && capitalFlag && lowerCaseFlag &&( s.length()==8|| s.length()>8))
                   {
                       pswdValidationCard.setVisibility(View.GONE);
                   }
                }
                else
                {
                    pswdValidationCard.setVisibility(View.GONE);
                    numberFlag=false;
                    capitalFlag=false;
                    lowerCaseFlag=false;
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
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

                type="loc";

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

            Log.e("serviceArrive: ",loc+"");

            lattitude=loc.getString("lat");
            longitude=loc.getString("lng");

         /*   lattitude=loc.getString("lat");
            longitude=loc.getString("lng");*/

        /*    if (type.equalsIgnoreCase("p"))
            {
                lattitude = Double.parseDouble(loc.getString("lat"));
                longitude = Double.parseDouble(loc.getString("lng"));
                Log.e("LocationPick ", atPick.getText().toString().trim());
                Log.e("LatitudePick ", lattitude + " longitudePick " + longitude);
                place1=new MarkerOptions().position(new LatLng(lattitude,longitude)).title(atPick.getText().toString().trim());
                showMarker(lattitude,longitude);
            }
            else
                {
                lattituded = Double.parseDouble(loc.getString("lat"));
                longituded = Double.parseDouble(loc.getString("lng"));
                Log.e("LocationDrop ", atDrop.getText().toString().trim());
                Log.e("LatitudeDrop ", lattituded + " longitudeDrop " + longituded);
                LatLng point = new LatLng(lattituded, longituded);
                place2=new MarkerOptions().position(point).title(atDrop.getText().toString().trim());
                Log.e(TAG, "serviceArrive: Place2"+place2);
                showMarker(lattituded,longituded);
            }*/
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    String lattitude="",longitude="";
    String imageString="";
    String type;

    @OnClick({R.id.img_back_btn,R.id.btn_submit,R.id.ivUser,R.id.ivEdit})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
            {
                finish();
                break;
            }

            case R.id.ivUser:
            {
                cameraPermission();
                break;
            }

            case R.id.ivEdit:
            {
                cameraPermission();
                break;
            }

           /* case R.id.tvAccount:
            {
                Intent intent=new Intent(RegisterUserDetailsActivity.this, DriverStripeAccountSetup.class);
                startActivityForResult(intent,56);
                break;
            }*/

            case R.id.btn_submit:
            {
                if (checkValidations())
                {
                    if (user_pic_path0.equalsIgnoreCase(""))
                    {
                        imageString="0";
                    }
                    else
                    {
                        imageString="1";
                    }
                    if (userType == 0)
                    {
                        type = "parent";
                        card = card_input_widget.getCard();
                        progress = new ProgressDialog(this);
                        progress.setMessage("Processing...");
                        progress.setCancelable(false);
                        progress.show();

                    } else {
                        type = "driver";
                    }
                    device_unique_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    Log.e("DeviceUniqueId ", device_unique_id);

                    deviceToken = FirebaseInstanceId.getInstance().getToken();

                    Log.e("deviceToken ", deviceToken);

                    helper.saveString(SharedPrefUtil.DEVICE_FCM_TOKEN, deviceToken);

                    if (userType==0)
                    {
                        createToken();
                    }

                    else
                        {
                            if (imageString.equalsIgnoreCase("1"))
                            {
                                Log.e( "onClick: ",file.isFile()+"" );
                                Log.e( "onClick:Name ",file.getName() );

                                MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(),
                                        RequestBody.create(MediaType.parse("image/jpeg"), file));
                                HashMap<String, RequestBody> param1=new HashMap<>();
                                if (!deviceToken.equalsIgnoreCase(""))
                                {
                                    param1.put("device_token",RequestBody.create(MediaType.parse("text/plain"), deviceToken));
                                }

                                param1.put("address_latitude",RequestBody.create(MediaType.parse("text/plain"), lattitude));
                                param1.put("address_longitude", RequestBody.create(MediaType.parse("text/plain"),longitude));

                                param1.put("device_id",RequestBody.create(MediaType.parse("text/plain"), device_unique_id));
                                param1.put("first_name",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(0).getText().toString()));
                                param1.put("last_name",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(1).getText().toString()));
                                param1.put("phone_no",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(2).getText().toString()));
                                param1.put("email",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(3).getText().toString()));
                                param1.put("user_type",RequestBody.create(MediaType.parse("text/plain"), type));
                                param1.put("password",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(4).getText().toString()));
                                param1.put("address",RequestBody.create(MediaType.parse("text/plain"), et_address.getText().toString().trim()));
                                param1.put("proximity",RequestBody.create(MediaType.parse("text/plain"), edtProxy.getText().toString().trim()));
                                param1.put("card_holder_name",RequestBody.create(MediaType.parse("text/plain"), et_card_name.getText().toString().trim()));
                                param1.put("is_enable_push_notifications",RequestBody.create(MediaType.parse("text/plain"), notiSettings));
                                param1.put("is_enable_text_message_alert",RequestBody.create(MediaType.parse("text/plain"), smsSettings));
                                Log.e("SignupParamars ",param1.toString());
                                apiRequest(param1,filePart,this,"",1);
                            }

                            else
                             {
                                HashMap<String, String> param = new HashMap<>();
                                param.put("first_name", edtTextViewList.get(0).getText().toString());
                                param.put("last_name", edtTextViewList.get(1).getText().toString());
                                param.put("phone_no", edtTextViewList.get(2).getText().toString());
                                param.put("email", edtTextViewList.get(3).getText().toString());
                                param.put("proximity",edtProxy.getText().toString().trim());
                                param.put("user_type", type);

                                param.put("address_latitude", lattitude);
                                param.put("address_longitude", longitude);

                                param.put("address",  et_address.getText().toString().trim());
                                param.put("password", edtTextViewList.get(4).getText().toString());

                                param.put("is_enable_push_notifications",notiSettings);
                                param.put("is_enable_text_message_alert",smsSettings);


                                if (!deviceToken.equalsIgnoreCase(""))
                                {
                                    param.put("device_token", deviceToken);
                                    helper.saveString(SharedPrefUtil.DEVICE_FCM_TOKEN, deviceToken);
                                }

                                param.put("device_id", device_unique_id);
                                Log.e("RegisterParams ", param.toString());
                                api(param, this, "", 0);
                            }
                    }
                }
            }
        }
    }

    String deviceToken = "";
    String device_unique_id="";
    protected void apiRequest(HashMap<String, RequestBody> param, MultipartBody.Part image,
                              Callback<ResponseBody> responseBodyCallback, String token , int TYPE_REQUEST)
    {
        if (isNetworkConnected())
        {
            progressDialog = APIRequest.getProgress(this);
            APIRequest.apiInterface(token).ParentRegister(param,image).enqueue(responseBodyCallback);
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


    public boolean checkValidations()
    {
        if (userType==0)
        {
            if (edtTextViewList.get(0).getText().toString().length() == 0)
            {
                edtTextViewList.get(0).setError(getResources().getString(R.string.val_first_name));
                edtTextViewList.get(0).requestFocus();
                return  false;
            } else if (edtTextViewList.get(0).getText().toString().contains(" "))
            {
                edtTextViewList.get(0).setError(getResources().getString(R.string.val_space));
                edtTextViewList.get(0).requestFocus();
                return  false;
            }
            else if (edtTextViewList.get(1).getText().toString().trim().length() == 0)
            {
                edtTextViewList.get(1).setError(getResources().getString(R.string.val_last_name));
                edtTextViewList.get(1).requestFocus();
                return  false;
            }
            else if (edtTextViewList.get(1).getText().toString().contains(" "))
            {
                edtTextViewList.get(1).setError(getResources().getString(R.string.val_space));
                edtTextViewList.get(1).requestFocus();
                return  false;
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.
                    matcher(edtTextViewList.get(3).getText().toString().trim()).matches())
            {
                edtTextViewList.get(3).setError(getResources().getString(R.string.val_valid_email_));
                edtTextViewList.get(3).requestFocus();
                return  false;
            }
            else if ((edtTextViewList.get(3).getText().toString().trim()).startsWith("."))
            {
                edtTextViewList.get(3).setError(getResources().getString(R.string.val_valid_email_));
                edtTextViewList.get(3).requestFocus();
                return  false;
            }
            else if (edtTextViewList.get(2).getText().toString().trim().length() == 0)
            {
                edtTextViewList.get(2).setError(getResources().getString(R.string.val_phone));
                edtTextViewList.get(2).requestFocus();
                return  false;
            }
            else if (edtTextViewList.get(2).getText().toString().trim().length() < 10 || edtTextViewList.get(2).getText().toString().trim().length() > 10 )
            {
                edtTextViewList.get(2).setError("Please enter a valid Phone No of 10 digits.");
                edtTextViewList.get(2).requestFocus();
                return  false;
            }


         else  if(lattitude.equalsIgnoreCase("") && longitude.equalsIgnoreCase(""))
            {
                et_address.setError("Please enter home address");
                et_address.requestFocus();
                return  false;
            }

            else if (edtTextViewList.get(4).getText().toString().trim().length() == 0)
            {
                Toast.makeText(this, getResources().getString(R.string.val_pwd), Toast.LENGTH_SHORT).show();
                return  false;
            }

            else if (!numberFlag || !capitalFlag || !lowerCaseFlag || edtTextViewList.get(4).getText().toString().trim().length() < 8)
            {
                Toast.makeText(this, getResources().getString(R.string.validate_password), Toast.LENGTH_SHORT).show();
                return  false;
            }

            else if (edtTextViewList.get(4).getText().toString().contains(" "))
            {
                Toast.makeText(this, getResources().getString(R.string.val_space), Toast.LENGTH_SHORT).show();
                return  false;
            }
            else if (edtTextViewList.get(5).getText().toString().trim().length() == 0)
            {
                Toast.makeText(this, getResources().getString(R.string.val_cpwd), Toast.LENGTH_SHORT).show();
                return  false;
            }
            else if (!edtTextViewList.get(5).getText().toString().equalsIgnoreCase(edtTextViewList.get(4).getText().toString().trim()))
            {
                Toast.makeText(this, getResources().getString(R.string.pswd_do_not_match), Toast.LENGTH_SHORT).show();
                return  false;
            }

            if (et_card_name.getText().toString().trim().isEmpty())
            {
                Toast.makeText(this, "Please enter card holder name ", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(!card_input_widget.validateAllFields())
            {
                Toast.makeText(this, "Please enter card details", Toast.LENGTH_SHORT).show();
                return  false;
            }

            else if (terms.equalsIgnoreCase("0"))
            {
                Toast.makeText(this, "Please Accept Terms Conditions & Privacy Policy", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            if (edtTextViewList.get(0).getText().toString().length() == 0)
            {
                edtTextViewList.get(0).setError(getResources().getString(R.string.val_first_name));
                edtTextViewList.get(0).requestFocus();
                return  false;
            } else if (edtTextViewList.get(0).getText().toString().contains(" "))
            {
                edtTextViewList.get(0).setError(getResources().getString(R.string.val_space));
                edtTextViewList.get(0).requestFocus();
                return  false;
            }
            else if (edtTextViewList.get(1).getText().toString().trim().length() == 0)
            {
                edtTextViewList.get(1).setError(getResources().getString(R.string.val_last_name));
                edtTextViewList.get(1).requestFocus();
                return  false;
            }
            else if (edtTextViewList.get(1).getText().toString().contains(" "))
            {
                edtTextViewList.get(1).setError(getResources().getString(R.string.val_space));
                edtTextViewList.get(1).requestFocus();
                return  false;
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.
                    matcher(edtTextViewList.get(3).getText().toString().trim()).matches())
            {
                edtTextViewList.get(3).setError(getResources().getString(R.string.val_valid_email_));
                edtTextViewList.get(3).requestFocus();
                return  false;
            }
            else if ((edtTextViewList.get(3).getText().toString().trim()).startsWith("."))
            {
                edtTextViewList.get(3).setError(getResources().getString(R.string.val_valid_email_));
                edtTextViewList.get(3).requestFocus();
                return  false;
            }
            else if (edtTextViewList.get(2).getText().toString().trim().length() == 0)
            {
                edtTextViewList.get(2).setError(getResources().getString(R.string.val_phone));
                edtTextViewList.get(2).requestFocus();
                return  false;
            }
            else if (edtTextViewList.get(2).getText().toString().trim().length() < 10 || edtTextViewList.get(2).getText().toString().trim().length() > 10 )
            {
                edtTextViewList.get(2).setError("Please enter a valid Phone No of 10 digits.");
                edtTextViewList.get(2).requestFocus();
                return  false;
            }

            else  if(lattitude.equalsIgnoreCase("") && longitude.equalsIgnoreCase(""))
            {
                et_address.setError("Please enter home address");
                et_address.requestFocus();
                return  false;
            }

            else if (edtTextViewList.get(4).getText().toString().trim().length() == 0)
            {
                Toast.makeText(this, getResources().getString(R.string.val_pwd), Toast.LENGTH_SHORT).show();
                return  false;
            }

            else if (!numberFlag || !capitalFlag || !lowerCaseFlag || edtTextViewList.get(4).getText().toString().trim().length() < 8)
            {
                Toast.makeText(this, getResources().getString(R.string.validate_password), Toast.LENGTH_SHORT).show();
                return  false;
            }

            else if (edtTextViewList.get(4).getText().toString().contains(" "))
            {
                Toast.makeText(this, getResources().getString(R.string.val_space), Toast.LENGTH_SHORT).show();
                return  false;
            }
            else if (edtTextViewList.get(5).getText().toString().trim().length() == 0)
            {
                Toast.makeText(this, getResources().getString(R.string.val_cpwd), Toast.LENGTH_SHORT).show();
                return  false;
            }
            else if (!edtTextViewList.get(5).getText().toString().equalsIgnoreCase(edtTextViewList.get(4).getText().toString().trim()))
            {
                Toast.makeText(this, getResources().getString(R.string.pswd_do_not_match), Toast.LENGTH_SHORT).show();
                return  false;
            }
            else if (edtProxy.getText().toString().isEmpty())
            {
                Toast.makeText(this,getString(R.string.dist_val), Toast.LENGTH_SHORT).show();
                return  false;
            }

            else if (Integer.parseInt(edtProxy.getText().toString().trim())==0 || Integer.parseInt(edtProxy.getText().toString().trim())>1000)
            {
                Toast.makeText(this,"Proximity must be greater than 0 or less than 1000", Toast.LENGTH_SHORT).show();
                return  false;
            }

            else if (terms.equalsIgnoreCase("0"))
            {
                Toast.makeText(this, "Please Accept Terms Conditions & Privacy Policy", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    ProgressDialog progress;
    String generatedToken="";

    private static final String PUBLISHABLE_KEY = "pk_test_MaQhP2b8XqG9R7PTL6VUmipt";
    private static final String SECRET_KEY = "sk_test_lC0U9YV8dratMHpIrJ8ObVrK00QUgucAmv";

    public void createToken()
    {
        Log.e( "insideCreateToken ","inside" );

        final Stripe stripe = new Stripe(this, PUBLISHABLE_KEY);
//        final Card card = Card.create("4242424242424242", 12, 2020, "123");
        stripe.createToken(card,new TokenCallback()
        {
            public void onSuccess(@NonNull Token token)
            {
                // Send token to your server
                Log.e("onSuccess:TokenType ",token.getType());
                Log.e("onSuccess:TokenID ",token.getId());//generated token here
                generatedToken= String.valueOf(token.getId());
                Log.e( "generatedToken ",generatedToken);
                progress.dismiss();
                callSignUpParent();
            }

            public void onError(@NonNull Exception error)
            {
                // Show localized error message
                Log.e( "Onerror ",error+"");
                Toast.makeText(RegisterUserDetailsActivity.this,"Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callSignUpParent()
    {
        if (userType==0)
        {

            String month= String.valueOf(card.getExpMonth());

            HashMap<String,String> mapMonth=new HashMap<>();
            mapMonth.put("1","01");
            mapMonth.put("2","02");
            mapMonth.put("3","03");
            mapMonth.put("4","04");
            mapMonth.put("5","05");
            mapMonth.put("6","06");
            mapMonth.put("7","07");
            mapMonth.put("8","08");
            mapMonth.put("9","09");
            mapMonth.put("10","10");
            mapMonth.put("aa","aa");
            mapMonth.put("12","12");

            String exactMOnth=String.valueOf(mapMonth.get(month));

            String year=new String(String.valueOf(card.getExpYear()));
            Log.e("year ",year.substring(2,year.length()));

            String expiry=String.valueOf(exactMOnth+"/"+year.substring(2,year.length()));


            Log.e("onClick:Month ", card.getExpMonth() + "");
            Log.e("onClick:year ", card.getExpYear() + "");
            if (imageString.equalsIgnoreCase("1"))
            {
                Log.e( "onClick: ",file.isFile()+"" );
                Log.e( "onClick:Name ",file.getName() );
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), file));

                HashMap<String, RequestBody> param1=new HashMap<>();

                if (!deviceToken.equalsIgnoreCase(""))
                {
                    param1.put("device_token",RequestBody.create(MediaType.parse("text/plain"), deviceToken));
                }

                param1.put("device_id",RequestBody.create(MediaType.parse("text/plain"), device_unique_id));


                param1.put("first_name",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(0).getText().toString()));
                param1.put("last_name",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(1).getText().toString()));
                param1.put("phone_no",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(2).getText().toString()));
                param1.put("email",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(3).getText().toString()));
                param1.put("user_type",RequestBody.create(MediaType.parse("text/plain"), type));
                param1.put("password",RequestBody.create(MediaType.parse("text/plain"), edtTextViewList.get(4).getText().toString()));
                param1.put("address",RequestBody.create(MediaType.parse("text/plain"), et_address.getText().toString().trim()));
                param1.put("card_holder_name",RequestBody.create(MediaType.parse("text/plain"), et_card_name.getText().toString().trim()));
                param1.put("is_enable_push_notifications",RequestBody.create(MediaType.parse("text/plain"), notiSettings));
                param1.put("is_enable_text_message_alert",RequestBody.create(MediaType.parse("text/plain"), smsSettings));
                param1.put("card_number",RequestBody.create(MediaType.parse("text/plain"), card.getNumber()));
                param1.put("cvv",RequestBody.create(MediaType.parse("text/plain"),card.getCVC()));
//                param1.put("expiry_date",RequestBody.create(MediaType.parse("text/plain"),"10/23".trim()));
                param1.put("expiry_date",RequestBody.create(MediaType.parse("text/plain"),expiry.trim()));
                param1.put("stripe_token",RequestBody.create(MediaType.parse("text/plain"),generatedToken));

                param1.put("address_latitude",RequestBody.create(MediaType.parse("text/plain"), lattitude));
                param1.put("address_longitude", RequestBody.create(MediaType.parse("text/plain"),longitude));
                Log.e("SignupParamars ",param1.toString());

                apiRequest(param1,filePart,this,"",1);
            }

            else {
                HashMap<String, String> param = new HashMap<>();
                param.put("first_name", edtTextViewList.get(0).getText().toString());
                param.put("last_name", edtTextViewList.get(1).getText().toString());
                param.put("phone_no", edtTextViewList.get(2).getText().toString());
                param.put("email", edtTextViewList.get(3).getText().toString());
//                        param.put("user_type", "Parent");
                param.put("user_type", type);
                param.put("password", edtTextViewList.get(4).getText().toString());

                param.put("stripe_token",generatedToken);
                param.put("address_latitude",lattitude);
                param.put("address_longitude",longitude);


                param.put("address", et_address.getText().toString().trim());
                param.put("card_holder_name", et_card_name.getText().toString().trim());
                param.put("is_enable_push_notifications", notiSettings);
                param.put("is_enable_text_message_alert", smsSettings);
                param.put("card_number", card.getNumber());
                param.put("cvv", card.getCVC());
//                param.put("expiry_date", "10/bb".trim());
                param.put("expiry_date", expiry.trim());


                if (!deviceToken.equalsIgnoreCase(""))
                {
                    param.put("device_token", deviceToken);
                    helper.saveString(SharedPrefUtil.DEVICE_FCM_TOKEN, deviceToken);
                }

                param.put("device_id", device_unique_id);
                Log.e("RegisterParams ", param.toString());
                api(param, this, "", 0);
            }
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        if (progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        Log.e( "onResponse: Response<<<",response.toString());
        GeneralResponse generalResponse=new GeneralResponse(response);
        Log.e( "onResponse:Responsee ",generalResponse.getResponse()+"");

        try
        {
            JSONObject jsonObject=generalResponse.getResponse_object();
            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                JSONObject token=jsonObject.getJSONObject("token");

                SharedPrefUtil helper = SharedPrefUtil.getInstance();
                helper.saveString(Constant.TOKEN,token.getString("token"));

                JSONObject user_details=jsonObject.getJSONObject("data");

                helper.saveString(SharedPrefUtil.USER_ID,user_details.getString("id"));
                helper.saveString(SharedPrefUtil.FNAME,user_details.getString("first_name"));
                helper.saveString(SharedPrefUtil.LNAME,user_details.getString("last_name"));
                helper.saveString(SharedPrefUtil.PHONE_NUMBER,user_details.getString("phone_no"));
                helper.saveString(SharedPrefUtil.USERTYPE,user_details.getString("user_type"));
                helper.saveString(SharedPrefUtil.EMAIL,user_details.getString("email"));
                helper.saveString(SharedPrefUtil.DRIVER_STATUS,"0");

                if (user_details.has("image"))
                {
                    if (!user_details.getString("image").equalsIgnoreCase("null"))
                    {
                        helper.saveString(SharedPrefUtil.IMAGE,user_details.getString("image"));
                    }
                    else
                    {
                        helper.saveString(SharedPrefUtil.IMAGE,"0");
                    }
                }

                else
                {
                    helper.saveString(SharedPrefUtil.IMAGE,"0");
                }

                if (user_details.getString("user_type").equalsIgnoreCase("driver"))
                {
                    helper.saveString(SharedPrefUtil.VEHICLE_SELECTED,"0");
                    helper.saveString(SharedPrefUtil.VEHICLE_SAVED,"0");
                    helper.saveString(SharedPrefUtil.DOCUMENTS_SAVED,"0");
                    helper.saveString(SharedPrefUtil.DRIVER_PROXIMITY,user_details.getString("proximity"));
                }
                //email=bsbsbs@g.com
                else
                {
                    helper.saveBoolean(SharedPrefUtil.LOGIN, true);
                    helper.saveString(SharedPrefUtil.HOME_ADDRESS,user_details.getString("address"));
                    helper.saveString(SharedPrefUtil.ADDRESS_LATITUDE,user_details.getString("address_latitude"));
                    helper.saveString(SharedPrefUtil.ADDRESS_LONGITUDE,user_details.getString("address_longitude"));
                    helper.saveString(SharedPrefUtil.NOTIFICATION_SETTING,user_details.getString("is_enable_push_notifications"));
                    helper.saveString(SharedPrefUtil.SMS_SETTINGS,user_details.getString("is_enable_text_message_alert"));
                }

                callSuccessPopup();

               /* new RegisterSuccessAlert(RegisterUserDetailsActivity.this,"s")
                {
                    @Override
                    protected void dataOnClick()
                    {
                        if (userType==0)
                        {
                            Intent intent=new Intent(RegisterUserDetailsActivity.this,
                                    OTPConfirmationActivity.class);
                            intent.putExtra(Constant.BUNDLE_USER_TYPE,userType);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else
                        {
                            Intent intent=new Intent(RegisterUserDetailsActivity.this,
                                    OTPConfirmationActivity.class);
                            intent.putExtra(Constant.BUNDLE_USER_TYPE,userType);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                };*/
            }
            else
            {
                Toast.makeText(RegisterUserDetailsActivity.this,
                        generalResponse.getResponse().getString("message"),
                        Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

       public void callSuccessPopup()
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

           tvMsg.setText("User Successfully Registered");


           tvOk.setOnClickListener(new View.OnClickListener()
           {
               @Override
               public void onClick(View view)
               {
                   if (userType==0)
                   {
                       Intent intent=new Intent(RegisterUserDetailsActivity.this,
                               OTPConfirmationActivity.class);
                       intent.putExtra(Constant.BUNDLE_USER_TYPE,userType);
                       startActivity(intent);
                       finishAffinity();
                   }
                   else
                   {
                       Intent intent=new Intent(RegisterUserDetailsActivity.this,
                               OTPConfirmationActivity.class);
                       intent.putExtra(Constant.BUNDLE_USER_TYPE,userType);
                       startActivity(intent);
                       finishAffinity();
                   }
                   alertDialog.dismiss();
               }
           });
           alertDialog.show();
       }

    String[] notYetpermitted;

    String user_pic_path0="";
    public static File file;

    private void cameraPermission()
    {
        ArrayList<String> permissionNeeded = AppUtils.permissionNeeded
                (AppUtils.getCameraPermission(getApplicationContext()), getApplicationContext());
        if (permissionNeeded.size() > 0)
        {
            notYetpermitted = AppUtils.getStringArray(permissionNeeded);
            verifyPermissions(notYetpermitted);
        }
        else
        {
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
                else
                {
                    ActivityCompat.requestPermissions(this, permissions, i);
                }
                break;
            }
        }
    }

    public void ImageSelection()
    {
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

        cvCancel = (CardView) alert.findViewById(R.id.cvCancel);

        takePicture = (TextView) alert.findViewById(R.id.takePicture);
        gallery = (TextView) alert.findViewById(R.id.gallery);

        gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).
                            start(RegisterUserDetailsActivity.this);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                alert.dismiss();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 2);
                }catch (Exception e){
                    e.printStackTrace();
                }

//                takePicture();
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
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    takePicture();
                    ImageSelection();

                } else
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
            Log.e("camera  ",camera+"");
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        Log.e("data  ",data+"");
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

                        Log.e("Paaaath","Paaaath"+path);
                        Log.e("Fileeee","Fileeee"+file);
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
                        Log.e( "onActivityResult: ImagePath",user_pic_path0);
                        ivUser.setImageBitmap(camera.getCameraBitmap());
                        file=new File(user_pic_path0);
                        Log.e("ISFile",file.isFile()+"");
                        Log.e("directory ",file.isDirectory()+"");
                        Log.e("NAme",file.getName()+"");
                    }
                    break;


                case 56:

                    Log.e("IntentData56 ",data.getData()+"");

                    break;


                case 2:
                    Log.e("2  ","2");
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ivUser.setImageBitmap(bitmap);
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

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
        Log.e("onFailure ","t");
        progressDialog.dismiss();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        switch (v.getId())
        {
            case R.id.edt_first_name:

                if (hasFocus)
                {
                    edtTextViewList.get(1).setError(null);
                    edtTextViewList.get(2).setError(null);
                    edtTextViewList.get(3).setError(null);
                    edtTextViewList.get(4).setError(null);
                    edtTextViewList.get(5).setError(null);
                }
                break;

                case R.id.edt_user_last_name:

                    edtTextViewList.get(0).setError(null);
                    edtTextViewList.get(2).setError(null);
                    edtTextViewList.get(3).setError(null);
                    edtTextViewList.get(4).setError(null);
                    edtTextViewList.get(5).setError(null);

                break;

                case R.id.edt_phone:

                    edtTextViewList.get(0).setError(null);
                    edtTextViewList.get(1).setError(null);
                    edtTextViewList.get(2).setError(null);
                    edtTextViewList.get(4).setError(null);
                    edtTextViewList.get(5).setError(null);

                break;

                case R.id.edt_user_email_id:

                    edtTextViewList.get(0).setError(null);
                    edtTextViewList.get(1).setError(null);
                    edtTextViewList.get(3).setError(null);
                    edtTextViewList.get(4).setError(null);
                    edtTextViewList.get(5).setError(null);
                break;

                case R.id.edtPassword:
                    edtTextViewList.get(0).setError(null);
                    edtTextViewList.get(1).setError(null);
                    edtTextViewList.get(2).setError(null);
                    edtTextViewList.get(3).setError(null);
                    edtTextViewList.get(5).setError(null);
                break;

                case R.id.edtCPassword:
                    edtTextViewList.get(0).setError(null);
                    edtTextViewList.get(1).setError(null);
                    edtTextViewList.get(2).setError(null);
                    edtTextViewList.get(3).setError(null);
                    edtTextViewList.get(4).setError(null);
                break;
        }

    }
}
