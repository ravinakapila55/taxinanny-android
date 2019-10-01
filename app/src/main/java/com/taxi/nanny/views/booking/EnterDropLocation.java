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
import com.taxi.nanny.views.profile.EditProfile;

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

public class EnterDropLocation extends BaseActivity implements RetrofitResponse
{
    @BindView(R.id.tvContinue)
    TextView tvContinue;
    @BindView(R.id.atPick)
     AutoCompleteTextView atPick;
    Constant constant;
      String location="",lattitude,longitude="";
    String key="";
    String key_final_pick="";
    int position;
    ArrayList<RiderListModel> riderList;
    public static String TAG=EnterDropLocation.class.getSimpleName();
    ArrayList<RiderListModel> list=new ArrayList<>();
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
        setHeaderText("Enter Drop off Location");

        constant=new Constant();
        sharedPrefUtil=SharedPrefUtil.getInstance();



        if (getIntent().hasExtra("key"))
        {
            key=getIntent().getExtras().getString("key");
            position=getIntent().getExtras().getInt("position");
            key_final_pick=getIntent().getExtras().getString("key_final");


            Log.e(TAG, "onCreate: Drop Key "+key);
            Log.e(TAG, "onCreate: Drop KeyFinalPick "+key_final_pick);


            if (key.equalsIgnoreCase("start_single"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e( "RiderListSize ",riderList.size()+"");

            }
            if (key.equalsIgnoreCase("final_single"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e( "RiderListSize ",riderList.size()+"");

            }
            if (key.equalsIgnoreCase("start_multiple"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e( "RiderListSize ",riderList.size()+"");
            }
            if (key.equalsIgnoreCase("start_multiple_different"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e( "RiderListSize ",riderList.size()+"");
            }
        }

        PlacesAutoCompleteAdapter mAdapter = new PlacesAutoCompleteAdapter
                (this, R.layout.custom_spinner_new);
        atPick.setAdapter(mAdapter);
//        atPick.setSingleLine();
        atPick.setImeOptions(EditorInfo.IME_ACTION_DONE);
        setLoactionAdapter(atPick);
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

    public void callFavouriteList()
    {
        new RetrofitService(this, this,
                Constant.API_RIDER_FAVOURITE_LOCATION_LIST +"/"+ sharedPrefUtil.getString(SharedPrefUtil.USER_ID,
                        ""), 200, 1,"1").
                callService(true);
    }

    public boolean checkValidations()
    {
        if (atPick.getText().toString().trim().equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Please choose drop-off location", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (atPick.getText().toString().trim().equalsIgnoreCase(riderList.get(position).getPickup()))
        {
            Toast.makeText(EnterDropLocation.this, "Drop-Off location cannot be the same as Pick-Up location", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    private void setLoactionAdapter(AutoCompleteTextView autoCompleteTextView)
    {
        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(getApplicationContext(),
                R.layout.custom_spinner));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
//                constant.hideKeyboard(EnterDropLocation.this,view);

                hideKeyboard(view);
                String placeId = GooglePlacesAutocompleteAdapter.resultListId.get(position).toString();



              /*  final String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" +
                        placeId + "&key=AIzaSyCxj7Z3cWeV8phaVuua1cSQ88bWT_ls5u0";*/

                final String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" +
                        placeId + "&key=AIzaSyAK5It4p1CiJ2gFzWRbfs24Cibo2QTcPRU";

                type="loc";

                new AsyncTask<Void, Void, String>()
                {
                    @Override
                    protected void onPreExecute()
                    {
                        super.onPreExecute();
//                        DialogClass.showDialog(Listing.this);
                    }
                    @Override
                    protected String doInBackground(final Void... params)
                    {
                        try
                        {
                            serviceArrive(url);
                        } catch (Exception e)
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
        } catch (MalformedURLException e)
        {
            Log.e("EXC", "Error processing Places API URL", e);
        } catch (IOException e)
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

            Log.e(TAG, "serviceArrive: Location "+loc);


            lattitude=loc.getString("lat");
            longitude=loc.getString("lng");


            //2019-06-06 14:23:30.329 25992-26450/com.taxi.nanny E/EnterDropLocation: serviceArrive: Location {"lat":31.1048145,"lng":77.17340329999999}

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

        tvMsg.setText("Is this a Recurring ride ?");


        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                Intent intent=new Intent(EnterDropLocation.this,ConfirmBooking.class);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(EnterDropLocation.this,ListofDays.class);
                startActivity(intent);
                alertDialog.dismiss();

           /*     Intent intent=new Intent(ListofChildren.this,AddRider.class);
                startActivity(intent);
                alertDialog.dismiss();*/
            }
        });
        alertDialog.show();
    }

    public void replaceActivity(String type)
    {
        if (type.equalsIgnoreCase("fav"))
        {
            replaceDirect();
        }
        else {
            replaceNick();
        }

    }

    public void replaceNick()
    {
        if (key.equalsIgnoreCase("start_single"))
        {
            riderList.get(position).setDropup(atPick.getText().toString().trim());
            riderList.get(position).setDroplat(lattitude);
            riderList.get(position).setDroplng(longitude);

            //key_final_pick="no";
//                    Intent intent=new Intent(EnterDropLocation.this,PickDropConfirmation.class);
            Intent intent=new Intent(EnterDropLocation.this,SaveDropLocation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);

        } else if (key.equalsIgnoreCase("final_single"))
        {
            riderList.get(position).setDropup(atPick.getText().toString().trim());
            riderList.get(position).setDroplat(lattitude);
            riderList.get(position).setDroplng(longitude);

            Intent intent=new Intent(EnterDropLocation.this,PickDropConfirmation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);
        }

        else if (key.equalsIgnoreCase("start_multiple_different"))
        {

            if (key_final_pick.equalsIgnoreCase("yes"))
            {

                riderList.get(position).setDropup(atPick.getText().toString().trim());
                riderList.get(position).setDroplat(lattitude);
                riderList.get(position).setDroplng(longitude);
            }

            else if (key_final_pick.equalsIgnoreCase("no"))
            {
                for (int i = 0; i < riderList.size(); i++)
                {
                    riderList.get(i).setDropup(atPick.getText().toString().trim());
                    riderList.get(i).setDroplat(lattitude);
                    riderList.get(i).setDroplng(longitude);
                }
            }

            Intent intent=new Intent(EnterDropLocation.this,SaveDropLocation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);
        }

        else if (key.equalsIgnoreCase("start_multiple"))
        {

            if (key_final_pick.equalsIgnoreCase("yes"))
            {

                riderList.get(position).setDropup(atPick.getText().toString().trim());
                riderList.get(position).setDroplat(lattitude);
                riderList.get(position).setDroplng(longitude);
            }

            else if (key_final_pick.equalsIgnoreCase("no"))
            {
                for (int i = 0; i < riderList.size(); i++)
                {
                    riderList.get(i).setDropup(atPick.getText().toString().trim());
                    riderList.get(i).setDroplat(lattitude);
                    riderList.get(i).setDroplng(longitude);
                }
            }

            Intent intent=new Intent(EnterDropLocation.this,SaveDropLocation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);
        }
    }

    public void replaceDirect()
    {
        if (key.equalsIgnoreCase("start_single"))
        {
            if (key_final_pick.equalsIgnoreCase("yes"))
            {
                Intent intent=new Intent(EnterDropLocation.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("position",position);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
            }

            else
            {
                Intent intent=new Intent(EnterDropLocation.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("position",position);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
            }

        }
        else if (key.equalsIgnoreCase("start_multiple"))
        {

            if (key_final_pick.equalsIgnoreCase("yes"))
            {

                riderList.get(position).setDropup(atPick.getText().toString().trim());
                riderList.get(position).setDroplat(lattitude);
                riderList.get(position).setDroplng(longitude);
                riderList.get(position).setDrop_saved_id(id);
            }

            else if (key_final_pick.equalsIgnoreCase("no"))
            {
                for (int i = 0; i < riderList.size(); i++)
                {
                    riderList.get(i).setDropup(atPick.getText().toString().trim());
                    riderList.get(i).setDroplat(lattitude);
                    riderList.get(i).setDroplng(longitude);
                    riderList.get(i).setDrop_saved_id(id);
                }
            }


            Intent intent=new Intent(EnterDropLocation.this,PickDropConfirmation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);

        } else if (key.equalsIgnoreCase("start_multiple_different"))
        {
            if (key_final_pick.equalsIgnoreCase("yes"))
            {
              /*  riderList.get(position).setDropup(atPick.getText().toString().trim());
                riderList.get(position).setDroplat(lattitude);
                riderList.get(position).setDroplng(longitude);
                riderList.get(position).setDrop_saved_id(id);*/

                for (int i = 0; i < riderList.size(); i++)
                {
                    riderList.get(i).setDropup(atPick.getText().toString().trim());
                    riderList.get(i).setDroplat(lattitude);
                    riderList.get(i).setDroplng(longitude);
                    riderList.get(i).setDrop_saved_id(id);
                }
            }

            else if (key_final_pick.equalsIgnoreCase("no"))
            {
               /* for (int i = 0; i < riderList.size(); i++)
                {
                    riderList.get(i).setDropup(atPick.getText().toString().trim());
                    riderList.get(i).setDroplat(lattitude);
                    riderList.get(i).setDroplng(longitude);
                    riderList.get(i).setDrop_saved_id(id);

                }*/

                riderList.get(position).setDropup(atPick.getText().toString().trim());
                riderList.get(position).setDroplat(lattitude);
                riderList.get(position).setDroplng(longitude);
                riderList.get(position).setDrop_saved_id(id);
            }


            Intent intent=new Intent(EnterDropLocation.this,PickDropConfirmation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);
        }
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
                        else
                       {
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
                    if (i == layoutPosition){


                        type="fav";
                        Log.e(TAG, "onLocationClick: Lattitude1 "+list.get(layoutPosition).getFav_latt());
                        Log.e(TAG, "onLocationClick: Lattitude2 "+list.get(layoutPosition).getFav_lng());

                        id=list.get(layoutPosition).getFav_loc_id();

                        if (id.equalsIgnoreCase(riderList.get(position).getPick_saved_id()))
                        {
                            Toast.makeText(EnterDropLocation.this, "Drop-Off location cannot be the same as Pick-Up location", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                            {
                            list.get(i).setFlag(true);
                            atPick.setText(list.get(layoutPosition).getFav_loc());
                            lattitude=list.get(layoutPosition).getFav_latt();
                            longitude=list.get(layoutPosition).getFav_lng();
                            riderList.get(position).setDrop_saved_id(list.get(layoutPosition).getFav_loc_id());
                            riderList.get(position).setDropup(list.get(layoutPosition).getFav_loc());
                            riderList.get(position).setDroplat(list.get(layoutPosition).getFav_latt());
                            riderList.get(position).setDroplng(list.get(layoutPosition).getFav_lng());

                            Log.e(TAG, "onLocationClick: DRop<<< "+riderList.get(position).getDroplat());
                            Log.e(TAG, "onLocationClick: DRopLang<<< "+riderList.get(position).getDroplng());
                        }
                    }
                    else
                    {
                        list.get(i).setFlag(false);
                    }
                    locationsAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onEditClick(int layoutPosition, View view) {
                Intent intent=new Intent(EnterDropLocation.this, EditNickName.class);
                intent.putExtra("data",(Serializable)list.get(layoutPosition));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        callFavouriteList();
    }
}
