package com.taxi.nanny.views.booking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.autoComplete.GooglePlacesAutocompleteAdapter;
import com.taxi.nanny.utils.autoComplete.PlacesAutoCompleteAdapter;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.adapter.FavouriteLocationsAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

public class EnterPickUp extends BaseActivity implements RetrofitResponse
{
    @BindView(R.id.tvContinue)
    TextView tvContinue;

    @BindView(R.id.atPick)
  AutoCompleteTextView atPick;

    Constant constant;

     String location="",lattitude,longitude="";

    ArrayList<RiderListModel> list=new ArrayList<>();

    String key="";
    String key_final_pick="";
    int position;
    ArrayList<RiderListModel> riderList;
    public static String TAG=EnterPickUp.class.getSimpleName();
    SharedPrefUtil sharedPrefUtil;

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.tvNoData)
    TextView tvNoData;

     String type="";

      String id="";


    @Override
    protected int getContentId()
    {
        return R.layout.enter_pick_up;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Enter Pickup Location");

        constant=new Constant();
        sharedPrefUtil=SharedPrefUtil.getInstance();

        callFavouriteList();

        if (getIntent().hasExtra("key"))
        {
            key=getIntent().getExtras().getString("key");
            key_final_pick=getIntent().getExtras().getString("key_final");


            Log.e(TAG, "onCreate: key "+key);
            Log.e(TAG, "onCreate: key_final_pick "+key_final_pick);

            position=getIntent().getExtras().getInt("position");
            if (key.equalsIgnoreCase("start_single"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e( "RiderListSize ",riderList.size()+"");
            }
            else if (key.equalsIgnoreCase("final_single"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e( "RiderListSize ",riderList.size()+"");
            }
            else if (key.equalsIgnoreCase("start_multiple"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e( "RiderListSize ",riderList.size()+"");
            }
            else if (key.equalsIgnoreCase("start_multiple_different"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e( "RiderListSize ",riderList.size()+"");
            }
        }
        PlacesAutoCompleteAdapter mAdapter = new PlacesAutoCompleteAdapter
                (this, R.layout.custom_spinner_new);
        atPick.setAdapter(mAdapter);
        atPick.setImeOptions(EditorInfo.IME_ACTION_DONE);
        setLoactionAdapter(atPick);
    }

    public void callFavouriteList()
    {
        new RetrofitService(this, this,
                Constant.API_RIDER_FAVOURITE_LOCATION_LIST +"/"+ sharedPrefUtil.getString(SharedPrefUtil.USER_ID,
                        ""), 200, 1,"1").
                callService(true);
    }

    public void hideKeyboard(View view)
    {
        Log.e(TAG, "hideKeyboard: Inside "+"inside" );
        /*InputMethodManager inputManager = (InputMethodManager)this .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view!=null)
        {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }*/

        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
    //Find the currently focused view, so we can grab the correct window token from it.
     view = this.getCurrentFocus();
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = new View(this);
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        try {
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

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            Log.e("JSONObjectValue ",jsonObj+"");

            JSONObject res = jsonObj.getJSONObject("result");
            JSONObject geo = res.getJSONObject("geometry");
            JSONObject loc = geo.getJSONObject("location");
//6284344268
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

    @OnClick({R.id.tvContinue,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvContinue:

                Constant.hideKeyboard(this,view);
                if (checkValidations())
                {
                    replaceActivity(type);
                }
                break;

            case R.id.img_back_btn:
                finish();
                break;
        }
    }

    public void replaceActivity(String type)
    {
        if (type.equalsIgnoreCase("fav"))
        {
            replaceDirect();
        }
        else
        {
            replaceNick();
        }
    }

    public void replaceNick()
    {
        if (key.equalsIgnoreCase("start_single"))
        {
            riderList.get(position).setPickup(atPick.getText().toString().trim());
            riderList.get(position).setPickLat(lattitude);
            riderList.get(position).setPicklng(longitude);

            Intent intent=new Intent(EnterPickUp.this,AddNickName.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);
        }
        else if (key.equalsIgnoreCase("final_single"))
        {
            riderList.get(position).setPickup(atPick.getText().toString().trim());
            riderList.get(position).setPickLat(lattitude);
            riderList.get(position).setPicklng(longitude);

            Intent intent=new Intent(EnterPickUp.this,PickDropConfirmation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);
        }else if (key.equalsIgnoreCase("start_multiple_different"))
        {
            riderList.get(position).setPickup(atPick.getText().toString().trim());
            riderList.get(position).setPickLat(lattitude);
            riderList.get(position).setPicklng(longitude);

            Intent intent=new Intent(EnterPickUp.this,AddNickName.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);
        }
        //when first time coming from Lis of children in case of multiple riders by clicking yes option
        else if (key.equalsIgnoreCase("start_multiple"))
        {
            //when come from final listing screen just for edit pick location of particualr position
            if (key_final_pick.equalsIgnoreCase("yes"))
            {

                riderList.get(position).setPickup(atPick.getText().toString().trim());
                riderList.get(position).setPickLat(lattitude);
                riderList.get(position).setPicklng(longitude);

                Intent intent=new Intent(EnterPickUp.this,AddNickName.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("position",position);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
            }
            else if (key_final_pick.equalsIgnoreCase("no"))
            {
                for (int i = 0; i < riderList.size(); i++)
                {
                    riderList.get(i).setPickup(atPick.getText().toString().trim());
                    riderList.get(i).setPickLat(lattitude);
                    riderList.get(i).setPicklng(longitude);
                }
                Intent intent=new Intent(EnterPickUp.this,AddNickName.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("position",position);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
            }
        }
    }

    public void replaceDirect()
    {
        if (key.equalsIgnoreCase("start_single"))
        {
            if (key_final_pick.equalsIgnoreCase("yes"))
            {
                Intent intent=new Intent(EnterPickUp.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("position",position);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
            }
            else
            {
                Intent intent=new Intent(EnterPickUp.this,EnterDropLocation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        }
        else if (key.equalsIgnoreCase("start_multiple"))
        {
            if (key_final_pick.equalsIgnoreCase("yes"))
            {
                Intent intent=new Intent(EnterPickUp.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("position",position);
                startActivity(intent);
            }
            else
            {
                Log.e(TAG, "replaceActivity: start_multiple "+key);
                callAlert();
            }
        }
        else if (key.equalsIgnoreCase("start_multiple_different"))
        {
            //callAlert();


            Log.e(TAG, "replaceActivity: start_multiple_different "+key);

            for (int i = 0; i <riderList.size() ; i++) {

                if (riderList.get(i).getDropup().equalsIgnoreCase(""))
                {
                    Log.e(TAG, "replaceActivity: start_multiple_different "+"IF");
                    callAlert();
                }
                else {
                    Log.e(TAG, "replaceActivity: start_multiple_different "+"else");
                    Log.e(TAG, "location "+location);

                    riderList.get(position).setPickup(location);
                    riderList.get(position).setPickLat(lattitude);
                    riderList.get(position).setPicklng(longitude);
                    riderList.get(position).setPick_saved_id(id);

                    Intent intent=new Intent(EnterPickUp.this,PickDropConfirmation.class);
                    intent.putExtra("key",key);
                    intent.putExtra("key_final",key_final_pick);
                    intent.putExtra("rider_list",(Serializable) riderList);
                    intent.putExtra("position",position);
                    startActivity(intent);
                }
            }
        }
    }

    public void callAlert()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvMsg.setText("Will all selected Rider's be dropped-off at the same location");

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Constant.hideKeyboard(EnterPickUp.this,view);


                if (key_final_pick.equalsIgnoreCase("no"))
                {

                    for (int i = 0; i < riderList.size(); i++)
                    {
                        riderList.get(i).setPickup(atPick.getText().toString().trim());
                        riderList.get(i).setPickLat(lattitude);
                        riderList.get(i).setPicklng(longitude);
                        riderList.get(i).setPick_saved_id(id);
                    }
                }

                else
                {
                    riderList.get(position).setPickup(atPick.getText().toString().trim());
                    riderList.get(position).setPickLat(lattitude);
                    riderList.get(position).setPicklng(longitude);
                    riderList.get(position).setPick_saved_id(id);
                }

                Intent intent=new Intent(EnterPickUp.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("position",position);
                startActivity(intent);
                alertDialog.dismiss();



            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Constant.hideKeyboard(EnterPickUp.this,view);

                if (key_final_pick.equalsIgnoreCase("no"))
                {

                    for (int i = 0; i < riderList.size(); i++)
                    {
                        riderList.get(i).setPickup(atPick.getText().toString().trim());
                        riderList.get(i).setPickLat(lattitude);
                        riderList.get(i).setPicklng(longitude);
                        riderList.get(i).setPick_saved_id(id);
                    }

                }
                else
                    {
                    riderList.get(position).setPickup(atPick.getText().toString().trim());
                    riderList.get(position).setPickLat(lattitude);
                    riderList.get(position).setPicklng(longitude);
                    riderList.get(position).setPick_saved_id(id);
                }

                Intent intent=new Intent(EnterPickUp.this,EnterDropLocation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("rider_list",(Serializable) riderList);
                intent.putExtra("position",position);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public boolean checkValidations()
    {
        if (atPick.getText().toString().trim().equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please choose pickup location", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!riderList.get(position).getDropup().equalsIgnoreCase(""))
        {
            if (atPick.getText().toString().trim().equalsIgnoreCase(riderList.get(position).getDropup()))
            {
                Toast.makeText(EnterPickUp.this, "Pick up location cannot be the same as Drop-off location", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 200:
                Log.e(TAG, "onResponse: ListOfChildrenREsponse "+response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        recycler.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
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
                                riderListModel.setFlag(false);
                                list .add(riderListModel);
                            }

                            if (list.size()>0)
                            {
                                recycler.setVisibility(View.VISIBLE);
                                tvNoData.setVisibility(View.GONE);
                                Log.e(TAG, "onResponse: ListSize "+list.size());
                                setAdapter();
                            }
                        }
                        else {
                            recycler.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                        {
                            recycler.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                         }
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
        }
    }

    public void setAdapter()
    {
        FavouriteLocationsAdapter locationsAdapter=new FavouriteLocationsAdapter(this,list);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(locationsAdapter);

        locationsAdapter.onItemSelectedListener(new FavouriteLocationsAdapter.myClickListener()
        {
            @Override
            public void onLocationClick(int layoutPosition, View view)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    if (i == layoutPosition)
                    {
                        list.get(i).setFlag(true);
                        type="fav";
                        Log.e(TAG, "onLocationClick: Lattitude "+list.get(layoutPosition).getFav_latt());
                        id=list.get(layoutPosition).getFav_loc_id();
                        atPick.setText(list.get(layoutPosition).getFav_loc());

                        lattitude=list.get(layoutPosition).getFav_latt();
                        longitude=list.get(layoutPosition).getFav_lng();
                        riderList.get(position).setPick_saved_id(list.get(layoutPosition).getFav_loc_id());
                        riderList.get(position).setPickup(list.get(layoutPosition).getFav_loc());
                        riderList.get(position).setPickLat(list.get(layoutPosition).getFav_latt());
                        riderList.get(position).setPicklng(list.get(layoutPosition).getFav_lng());

                        location=riderList.get(position).getPickup();

                        Log.e(TAG, "onLocationClick: SavedValue "+riderList.get(position).getPickup());
                        Log.e(TAG, "onLocationClick: location "+location);
                        Log.e(TAG, "onLocationClick: SavedValueLattitude "+riderList.get(position).getPickLat());
                    }
                    else
                    {
                        list.get(i).setFlag(false);
                    }
                    locationsAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
