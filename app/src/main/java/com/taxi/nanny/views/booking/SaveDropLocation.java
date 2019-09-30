package com.taxi.nanny.views.booking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveDropLocation extends BaseActivity implements Callback<ResponseBody>
{

    @BindView(R.id.tvSave)
    TextView tvSave;

    @BindView(R.id.etLoc)
    EditText etLoc;

    @BindView(R.id.etNickName)
    EditText etNickName;

    @BindView(R.id.etNote)
    EditText etNote;


    String key="";
    String key_final_pick="";
    int position;
    String token = "";
    SharedPrefUtil sharedPrefUtil;
    ArrayList<RiderListModel> riderList;
    public static String TAG=SaveDropLocation.class.getSimpleName();


    @Override
    protected int getContentId()
    {
        return R.layout.add_nick_name;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Enter Nick Name/Note");
        sharedPrefUtil = SharedPrefUtil.getInstance();
        token = sharedPrefUtil.getString(Constant.TOKEN, "");

        if (getIntent().hasExtra("key"))
        {
            key=getIntent().getExtras().getString("key");
            key_final_pick=getIntent().getExtras().getString("key_final");
            position=getIntent().getExtras().getInt("position");

            if (key.equalsIgnoreCase("start_single"))
            {
                riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
                Log.e(TAG, "onCreate: RiderListSize "+riderList.size());

                etLoc.setText(riderList.get(position).getDropup());
            }
            if (key.equalsIgnoreCase("start_multiple"))
           {

            riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
            Log.e(TAG, "onCreate: RiderListSize "+riderList.size());

            etLoc.setText(riderList.get(position).getDropup());
           }
            if (key.equalsIgnoreCase("start_multiple_different"))
            {
            riderList=(ArrayList<RiderListModel>) getIntent().getSerializableExtra("rider_list");
            Log.e(TAG, "onCreate: RiderListSize "+riderList.size());

            etLoc.setText(riderList.get(position).getDropup());
            }
        }
    }

    public void callService()
    {

        HashMap<String, String> param = new HashMap<>();

        param.put("rider_id",sharedPrefUtil.getString(SharedPrefUtil.USER_ID,""));
        param.put("location_name",etLoc.getText().toString().trim());
        param.put("nick_name",etNickName.getText().toString().trim());
        param.put("description",etNote.getText().toString().trim());
        param.put("lattitude",riderList.get(position).getDroplat());
        param.put("longitude",riderList.get(position).getDroplng());

        Log.e("AddPickLocationParama ",param.toString());
        api(param,this,token,10);
    }


    public boolean checkValidation()
    {
        if (etNickName.getText().toString().trim().length() == 0)
        {
            etNickName.setError("Please enter nick name");
            etNickName.requestFocus();
            return false;
        }

        return true;
    }

    @OnClick({R.id.tvSave,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvSave:

                Constant.hideKeyboard(this,view);

                if (checkValidation())
                {
                    callService();
                }

        /*        if (key.equalsIgnoreCase("start_single"))
                {
                    if (key_final_pick.equalsIgnoreCase("yes"))
                    {
                        Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
                        intent.putExtra("key",key);
                        intent.putExtra("key_final",key_final_pick);
                        intent.putExtra("position",position);
                        intent.putExtra("rider_list",(Serializable) riderList);
                        startActivity(intent);
                    }

                    else {
                        Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
                        intent.putExtra("key",key);
                        intent.putExtra("key_final",key_final_pick);
                        intent.putExtra("position",position);
                        intent.putExtra("rider_list",(Serializable) riderList);
                        startActivity(intent);
                    }

                }
                else if (key.equalsIgnoreCase("start_multiple"))
                {

                    Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
                    intent.putExtra("key",key);
                    intent.putExtra("key_final",key_final_pick);
                    intent.putExtra("position",position);
                    intent.putExtra("rider_list",(Serializable) riderList);
                    startActivity(intent);




                } else if (key.equalsIgnoreCase("start_multiple_different"))
                {

                    Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
                    intent.putExtra("key",key);
                    intent.putExtra("key_final",key_final_pick);
                    intent.putExtra("position",position);
                    intent.putExtra("rider_list",(Serializable) riderList);
                    startActivity(intent);
                }
*/


                break;

            case R.id.img_back_btn:
                finish();
                break;
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

        tvMsg.setText("Will all selected Rider's be dropped-off at the same location?");

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
              /*  Intent intent=new Intent(AddNickName.this,PickDropConfirmation.class);
                startActivity(intent);
                alertDialog.dismiss();*/

                Log.e("KeyNoCLick ",key);
                Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.e("KeyYesClick ",key);
                Intent intent=new Intent(SaveDropLocation.this,EnterDropLocation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
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
//                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                JSONObject data=jsonObject.getJSONObject("data");
               /* riderList.get(position).setDrop_saved_id(data.getString("id"));
                riderList.get(position).setDropup(data.getString("location_name"));
                riderList.get(position).setDroplat(data.getString("lattitude"));
                riderList.get(position).setDroplng(data.getString("longitude"));*/
               replaceActivity(data.getString("location_name"),data.getString("lattitude"),data.getString("longitude"),
                       data.getString("id") );
            }
            else

                {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                replaceActivity();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void replaceActivity(String location,String lattitude,String longitude,String id)
    {
        if (key.equalsIgnoreCase("start_single"))
        {
            riderList.get(position).setDropup(location);
            riderList.get(position).setDroplat(lattitude);
            riderList.get(position).setDroplng(longitude);
            riderList.get(position).setDrop_saved_id(id);

            //key_final_pick="no";
//                    Intent intent=new Intent(EnterDropLocation.this,PickDropConfirmation.class);
            Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);

        } else if (key.equalsIgnoreCase("final_single"))
        {
            riderList.get(position).setDropup(location);
            riderList.get(position).setDroplat(lattitude);
            riderList.get(position).setDroplng(longitude);
            riderList.get(position).setDrop_saved_id(id);

            Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
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

                riderList.get(position).setDropup(location);
                riderList.get(position).setDroplat(lattitude);
                riderList.get(position).setDroplng(longitude);
                riderList.get(position).setDrop_saved_id(id);
            }

            else if (key_final_pick.equalsIgnoreCase("no"))
            {
                for (int i = 0; i < riderList.size(); i++)
                {
                    riderList.get(i).setDropup(location);
                    riderList.get(i).setDroplat(lattitude);
                    riderList.get(i).setDroplng(longitude);
                    riderList.get(i).setDrop_saved_id(id);
                }
            }

            Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
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

                riderList.get(position).setDropup(location);
                riderList.get(position).setDroplat(lattitude);
                riderList.get(position).setDroplng(longitude);
                riderList.get(position).setDrop_saved_id(id);
            }

            else if (key_final_pick.equalsIgnoreCase("no"))
            {
                for (int i = 0; i < riderList.size(); i++)
                {
                    riderList.get(i).setDropup(location);
                    riderList.get(i).setDroplat(lattitude);
                    riderList.get(i).setDroplng(longitude);
                    riderList.get(i).setDrop_saved_id(id);
                }
            }

            Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);
        }




        /*
        if (key.equalsIgnoreCase("start_single"))
        {
            if (key_final_pick.equalsIgnoreCase("yes"))
            {
                Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("position",position);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
            }

            else
            {
                Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
                intent.putExtra("key",key);
                intent.putExtra("key_final",key_final_pick);
                intent.putExtra("position",position);
                intent.putExtra("rider_list",(Serializable) riderList);
                startActivity(intent);
            }

        }
        else if (key.equalsIgnoreCase("start_multiple"))
        {
            Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);

        } else if (key.equalsIgnoreCase("start_multiple_different"))
        {
            Intent intent=new Intent(SaveDropLocation.this,PickDropConfirmation.class);
            intent.putExtra("key",key);
            intent.putExtra("key_final",key_final_pick);
            intent.putExtra("position",position);
            intent.putExtra("rider_list",(Serializable) riderList);
            startActivity(intent);

        }*/
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {

    }
}
