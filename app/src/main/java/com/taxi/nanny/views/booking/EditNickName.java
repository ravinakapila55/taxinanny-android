package com.taxi.nanny.views.booking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.autoComplete.GooglePlacesAutocompleteAdapter;
import com.taxi.nanny.utils.autoComplete.PlacesAutoCompleteAdapter;
import com.taxi.nanny.views.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNickName extends BaseActivity implements Callback<ResponseBody> {


    @BindView(R.id.tvSave)
    TextView tvSave;

    @BindView(R.id.etLoc)
    AutoCompleteTextView etLoc;

    @BindView(R.id.etNickName)
    EditText etNickName;

    @BindView(R.id.etNote)
    EditText etNote;

    RiderListModel riderListModel;
    String key="";
    String key_final_pick="";
    int position;
    ArrayList<RiderListModel> riderList;
    String location="",lattitude,longitude="";

    public static String TAG=EditNickName.class.getSimpleName();

    @Override
    protected int getContentId()
    {
        return R.layout.edit_fav_loc;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Edit Favourite Location");

        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");

        if (getIntent().hasExtra("data"))
        {
            riderListModel=(RiderListModel)getIntent().getSerializableExtra("data");
            Log.e(TAG, "onCreate: RiderList "+riderListModel.getFav_loc());

            saveData();
        }

        PlacesAutoCompleteAdapter mAdapter = new PlacesAutoCompleteAdapter(this, R.layout.custom_spinner_new);
        etLoc.setAdapter(mAdapter);
        etLoc.setImeOptions(EditorInfo.IME_ACTION_DONE);
        setLoactionAdapter(etLoc);
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

            Log.e(TAG, "serviceArrive: Location "+loc);


            lattitude=loc.getString("lat");
            longitude=loc.getString("lng");

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

    public void saveData()
    {
        etNickName.setText(riderListModel.getFav_nick_name());
        etLoc.setText(riderListModel.getFav_loc());
        etNote.setText(riderListModel.getFav_description());
        lattitude=riderListModel.getFav_latt();
        longitude=riderListModel.getFav_lng();
    }

    @OnClick({R.id.tvSave,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvSave:

                Constant.hideKeyboard(EditNickName.this,view);

                if (checkValidation())
                {
                    callService();
                }


                break;

            case R.id.img_back_btn:
                finish();
                break;
        }
    }

    String token = "";
    SharedPrefUtil sharedPrefUtil;

    public void callService()
    {

        HashMap<String, String> param = new HashMap<>();
        param.put("rider_id",sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
        param.put("location_name",etLoc.getText().toString().trim());
        param.put("id",riderListModel.getFav_loc_id());
        param.put("nick_name",etNickName.getText().toString().trim());
        param.put("description",etNote.getText().toString().trim());
        param.put("lattitude",lattitude);
        param.put("longitude",longitude);
        Log.e("EditFavouriteLocation  ",param.toString());
        api(param,this,token,21);
    }


    public boolean checkValidation()
    {
        if (etNickName.getText().toString().trim().length() == 0)
        {
            etNickName.setError("Please enter Nick Name");
            etNickName.requestFocus();
            return false;
        }

        else if (etLoc.getText().toString().trim().equalsIgnoreCase(""))
        {
            etLoc.setError("Please choose favorite location ");
            etLoc.requestFocus();
            return false;
        }

        else if (lattitude .equalsIgnoreCase("") || longitude.equalsIgnoreCase(""))
        {
            etLoc.setError("Please choose favorite location ");
            etLoc.requestFocus();
            return false;
        }

        return true;
    }



    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        Log.e(TAG, "onResponse:AddRiderResponse "+response );
        progressDialog.dismiss();

        GeneralResponse generalResponse=new GeneralResponse(response);

        try {
            JSONObject jsonObject=generalResponse.getResponse_object();

            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                finish();
            }
            else
                {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}

