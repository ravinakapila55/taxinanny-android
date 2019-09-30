package com.taxi.nanny.views.login_section.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.SelectVehicleTypeBeans;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.login_section.adapter.SelectVehicleTypeAdapter;
import org.json.JSONException;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.taxi.nanny.utils.Constant.TOKEN;


public class SelectVehicleTypeActivity extends BaseActivity implements Callback<ResponseBody>
{
    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    String selectedId="";

    public static String TAG=SelectVehicleTypeActivity.class.getSimpleName();

    @Override
    protected int getContentId()
    {
        return R.layout.select_vehicle_type_act;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Select vehicle type");
        recycler_view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false));
        SharedPrefUtil prefUtil=SharedPrefUtil.getInstance();
        String token=prefUtil.getString(TOKEN,"");
        SharedPrefUtil.getInstance().saveString(SharedPrefUtil.SELECTED_VEHICLE_ID,"Abc");
        api(new HashMap<>(),this,token,1);
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);
        try {
            if(generalResponse.getResponseObject(this))
            {
             List<SelectVehicleTypeBeans> selectVehicleTypeBeansList= generalResponse.getDataAsList("data",SelectVehicleTypeBeans.class);
             SelectVehicleTypeAdapter adapter=new SelectVehicleTypeAdapter(selectVehicleTypeBeansList,this);
             recycler_view.setAdapter(adapter);

             adapter.onItemSelectedListener(new SelectVehicleTypeAdapter.MyClickListener()
             {
                 @Override
                 public void onItemClick(int layoutPosition, View view)
                 {
                     for (int i = 0; i < selectVehicleTypeBeansList.size(); i++)
                     {
                         if (layoutPosition==i)
                         {
                             selectVehicleTypeBeansList.get(i).setFlag(true);
                             selectedId=String.valueOf(selectVehicleTypeBeansList.get(i).getId());
                             Log.e(TAG, "onItemClick: Id "+selectedId);
                         }
                         else
                             {
                             selectVehicleTypeBeansList.get(i).setFlag(false);
                         }
                         adapter.notifyDataSetChanged();
                     }
                 }
             });
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
        progressDialog.dismiss();
    }

    @OnClick({R.id.btn_continue,R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_continue:
                {
                    if (selectedId.equalsIgnoreCase(""))
                    {
                        Toast.makeText(this, "Please choose vehicle", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        Log.e("SelectedIdf ",selectedId);
                        SharedPrefUtil.getInstance().saveString(SharedPrefUtil.VEHICLE_SELECTED,"1");
                        SharedPrefUtil.getInstance().saveString(SharedPrefUtil.SELECTED_VEHICLE_ID,selectedId);
                        Intent intent = new Intent(this, AddVehicleDetails.class);
                        intent.putExtra("vehicleId",selectedId);
                        startActivity(intent);
                    }
                    break;
               }

               case R.id.img_back_btn:
                {
                onBackPressed();
                break;
                }
        }
    }

}
