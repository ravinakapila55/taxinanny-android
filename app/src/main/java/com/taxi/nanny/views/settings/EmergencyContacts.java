package com.taxi.nanny.views.settings;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.EmergencyContactList;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.location.permission.Permission;
import com.taxi.nanny.utils.location.permission.PermissionGranted;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.settings.adapter.EmergencyContactsAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyContacts extends BaseActivity implements PermissionGranted,
        Callback<ResponseBody>, RetrofitResponse
{

    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    SharedPrefUtil sharedPrefUtil;

    ArrayList<EmergencyContactList> list=new ArrayList<>();
    String token;

    public  String apiHit="0";

    @Override
    protected int getContentId()
    {
        return R.layout.emergency_contacts;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Emergency Contacts");

        //todo get contact permissions runtime
        Permission.checkContactPermissions(this, this);
        ivEnd.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ivEnd.setImageDrawable(getDrawable(R.drawable.add_white));
        }

        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");

        callEmergencyList();

       // setAdapter();
    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        EmergencyContactsAdapter emergencyContactsAdapter=new EmergencyContactsAdapter(this,list);
        recycler.setAdapter(emergencyContactsAdapter);
    }

    @OnClick({R.id.img_back_btn,R.id.ivEnd})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:

                finish();
                break;

            case R.id.ivEnd:

                if (list.size()>0)
                {
                    if (list.size()>=5)
                    {
                        Toast.makeText(this, "Not allowed to add more contacts",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                     Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                     ContactsContract.Contacts.CONTENT_URI);
                     startActivityForResult(contactPickerIntent, 23);
                    }
                }
                else
                {
                    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(contactPickerIntent,23);
                }

              /*  Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, 23);*/
                break;

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case 23:
                    try
                    {
                        Uri result = data.getData();

                        String id = result.getLastPathSegment();

                        getContactName(id);

                       Log.e("NameGet ","Selected Name: \""
                                + mContactName );
                       Log.e("NumberGet ","Selected Number: \""
                                + mCOntactNUmber + "\"\n" + mAllCols);
                       Log.e("ALLCOLUMNS ","AllCOumns: \"" + mAllCols);

                       apiHit="1";

                       callAddContacts();
                    }
                    catch (Exception e)
                    {
                        Log.e("Error", "Error parsing contacts", e);
                    }
                    break;
            }
        }
        else
        {
            Log.e("Result Not Found", "Error reading contacts");
        }
    }

    public boolean checkValidations()
    {
        for (int i = 0; i <list.size() ; i++)
        {
            Log.e("", "checkValidations: name"+list.get(i).getName());
            Log.e("", "checkValidations: number"+list.get(i).getNumber());
            Log.e("", "checkValidations: mContactName"+mContactName);
            Log.e("", "checkValidations: mCOntactNUmber"+mCOntactNUmber);

            if (list.get(i).getName().equalsIgnoreCase(mContactName) ||
                    list.get(i).getNumber().equalsIgnoreCase(mCOntactNUmber))
            {
                Toast.makeText(this, "Number already exist", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void callAddContacts()
    {
        Log.e("NameGetInside ","Selected Name: \""
                + mContactName );

        Log.e("NumberGetInside ","Selected Number: \""
                + mCOntactNUmber + "\"\n" + mAllCols);

        if (checkValidations())
        {
            HashMap<String, String> param = new HashMap<>();
            param.put("related_name",mContactName.trim());
            param.put("number", mCOntactNUmber.trim());
            Log.e("callMakePayment:Params ",param.toString());
            api(param,this,token,31);
        }
    }

    private void getContactName(String id)
    {
        Cursor cursor = null;
        String result = "";
        String number="";
        String number1="";

        try
        {
            cursor = getContentResolver().query(ContactsContract
                            .Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=?",
                    new String[] { id },
                    null);

            if (cursor.moveToFirst())
            {
                result = cursor.getString(cursor.getColumnIndex
                        (ContactsContract.Data.DISPLAY_NAME));

                number1= cursor.getString(cursor.getColumnIndex(ContactsContract.Data.HAS_PHONE_NUMBER));

                Log.e("Number1GTETTE ",number1);

                if (number1.equalsIgnoreCase("1"))
                {
                    number=cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1)).trim();
                }
                else {
                    number="";
                }

                // Get all columns
                mAllCols = "";
                String columns[] = cursor.getColumnNames();
                for (String column : columns) {
                    int index = cursor.getColumnIndex(column);
                    mAllCols += ("(" + index + ") [" + column + "] = ["
                            + cursor.getString(index) + "]\n");
                }

                cursor.close();
            }
        }
        catch (Exception e)
        {
            Log.e("GetCOntentsError ","Error reading contacts", e);
        }

        mContactName = result;
        mCOntactNUmber = number;
    }

    private String mContactName="";
    private String mCOntactNUmber="";
    private String mAllCols="";

    @Override
    public void showPermissionAlert(ArrayList<String> permissionList, int code)
    {
        Log.e( "showPermissionAlert: ","Popup" );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Log.e( "showPermissionAlert: ","Inside" );
            requestPermissions(permissionList.toArray(new String[permissionList.size()]),code);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        apiHit="0";

    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e("GeneralBooking<< ",generalResponse+"");

        try {
            JSONObject jsonObject = generalResponse.getResponse_object();

            if (jsonObject.getString("status").equalsIgnoreCase("true"))
            {
                callEmergencyList();
            }
            else
            {
                Toast.makeText(this,"Not Successful ,Please try again" , Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:

                Log.e("ResponseEmergencyContacts ",response);

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        tvNoData.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);

                        list.clear();

                        JSONArray Array = jsonObject.getJSONArray("data");

                        if (Array.length() > 0)
                        {
                            for (int i = 0; i < Array.length(); i++)
                            {
                                JSONObject object = Array.getJSONObject(i);

                                EmergencyContactList emergencyContactList=new EmergencyContactList();

                                emergencyContactList.setId(object.getString("id"));
                                emergencyContactList.setNumber(object.getString("number"));
                                emergencyContactList.setName(object.getString("related_name"));

                                list.add(emergencyContactList);

                                if (list.size()>0)
                                {
                                    setAdapter();
                                }
                            }
                        }
                        else
                        {
                            tvNoData.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        tvNoData.setVisibility(View.VISIBLE);
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


    public void callEmergencyList()
    {
        new RetrofitService(this, this, Constant.API_EMERGENCY_CONTACT_LIST ,
                105, 1,"1").callService(true);
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
