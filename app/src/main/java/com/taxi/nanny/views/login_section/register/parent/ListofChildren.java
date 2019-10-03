package com.taxi.nanny.views.login_section.register.parent;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.taxi.nanny.R;
import com.taxi.nanny.domain.GeneralResponse;
import com.taxi.nanny.model.RiderListModel;
import com.taxi.nanny.req_model.DeleteRiderListModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.booking.EnterPickUp;
import com.taxi.nanny.views.booking.PickDropConfirmation;
import com.taxi.nanny.views.home.adapter.ListofChildrenAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListofChildren extends BaseActivity implements RetrofitResponse, Callback<ResponseBody>
{
    @BindView(R.id.tvSave)
    TextView tvSave;

    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.ivAdd)
    ImageView ivAdd;

    @BindView(R.id.cbAll)
    CheckBox cbAll;

    @BindView(R.id.tvALl)
    TextView tvALl;
    String click="0";

    @BindView(R.id.ivEnd)
    ImageView ivEnd;

    boolean clickFlag=false;

    ListofChildrenAdapter childrenAdapter;
    private String TAG="ListofChildren";
//    List<RiderListModel>  listModels=new ArrayList<>();
    ArrayList<RiderListModel>  listModels=new ArrayList<>();
    ArrayList<RiderListModel>  listModelsSelected=new ArrayList<>();
    SharedPrefUtil sharedPrefUtil;
    public  static    boolean flag;
    String token;

    @Override
    protected int getContentId()
    {
        return R.layout.list_of_children;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHeaderText("List All Children");
        SharedPrefUtil prefUtil=SharedPrefUtil.getInstance();
        Log.e(TAG, "onCreate: "+"list of children");
        sharedPrefUtil=SharedPrefUtil.getInstance();
        token=sharedPrefUtil.getString(Constant.TOKEN,"");

    }


    public void callListofChildren()
    {
        new RetrofitService(this, this,
                Constant.API_LIST_OF_CHILDREN +"/"+ sharedPrefUtil.getString(SharedPrefUtil.USER_ID,
                        ""), 105, 1,"1").
                callService(true);
    }

    ArrayList<RiderListModel> riderList;

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e(TAG, "onResume: "+"list of children");
        callListofChildren();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.e(TAG, "onStart: "+"list of children");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: "+"list of children" );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onPause: "+"list of children" );

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: "+"list of children" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: "+"list of children" );
    }

    @OnClick({R.id.tvSave,R.id.img_back_btn,R.id.ivAdd,R.id.tvAdd,R.id.tvALl,R.id.ivEnd})
    public void onclick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvSave:

                if (listModels.size()>0)
                {
                    listModelsSelected.clear();
                    for (int i = 0; i <listModels.size() ; i++)
                    {
                        RiderListModel riderListModel=new RiderListModel();
                        if (listModels.get(i).isFlag())
                        {
                            riderListModel.setId(listModels.get(i).getId());
                            riderListModel.setImage(listModels.get(i).getImage());
                            riderListModel.setFirst_name(listModels.get(i).getFirst_name());
                            riderListModel.setLast_name(listModels.get(i).getLast_name());
                            listModelsSelected.add(riderListModel);
                        }
                    }

                    Log.e(TAG, "onCheckClick: SelectedListSize "+listModelsSelected.size());
                    if (listModelsSelected.size()>1)
                    {

                        callAlert();
                    }
                    else if (listModelsSelected.size()==0)
                    {
                        Toast.makeText(this, "Please choose atleast one rider for booking", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (listModelsSelected.size()==1)
                    {
                        Intent intent=new Intent(ListofChildren.this, EnterPickUp.class);
                        intent.putExtra("key","start_single");
                        intent.putExtra("key_final","no");
                        intent.putExtra("position","0");
                        intent.putExtra("rider_list",(Serializable) listModelsSelected);
                        startActivity(intent);
                    }
                }

                break;

            case R.id.img_back_btn:
                finish();
                break;

                case R.id.ivAdd:
                    Intent intent1=new Intent(this, AddRider.class);
                    startActivity(intent1);
                break;

                case R.id.tvAdd:
                    Intent intent2=new Intent(this, AddRider.class);
                    startActivity(intent2);
                break;

                case R.id.tvALl:
                break;

                case R.id.ivEnd:

                 callConfirmDelete();
                break;
        }
    }

    public void callDeleteService()
    {
        HashMap<String,String> param=new HashMap<>();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        List<DeleteRiderListModel.RideDetailBean> deleteBeanList = new ArrayList<>();
        JsonArray ride_detail=new JsonArray();
        for (int i = 0; i < listModelsSelected.size(); i++)
        {
            DeleteRiderListModel.RideDetailBean deleteModel = new DeleteRiderListModel.RideDetailBean();
            deleteModel.setRider_id(listModelsSelected.get(i).getId());
            deleteBeanList.add(deleteModel);
        }

        DeleteRiderListModel deleteListModel = new DeleteRiderListModel();
        deleteListModel.setRide_detail(deleteBeanList);

        param.put("ride_detail", gson.toJson(deleteListModel));
        Log.e(TAG, "callDeleteRider: Params "+param );
        api(param,this,token,14);
    }

    public void setAdapter()
    {
        childrenAdapter=new ListofChildrenAdapter(this,listModels,"register");


        recycler.setLayoutManager(new LinearLayoutManager(this));

        recycler.setAdapter(childrenAdapter);

//        childrenAdapter.setHasStableIds(true);

        childrenAdapter.onItemSelectedListener(new ListofChildrenAdapter.MyClickListener()
        {
            @Override
            public void onCheckClick(int layoutPosition, View view, CheckBox checkBox)
            {
                click="1";

                listModels.get(layoutPosition).setFlag(listModels.get(layoutPosition).isFlag()?false:true);
                childrenAdapter.notifyDataSetChanged();

                listModelsSelected.clear();
                for (int i = 0; i <listModels.size() ; i++)
                {
                    RiderListModel riderListModel=new RiderListModel();
                    if (listModels.get(i).isFlag())
                    {
                        riderListModel.setId(listModels.get(i).getId());
                        riderListModel.setImage(listModels.get(i).getImage());
                        riderListModel.setFirst_name(listModels.get(i).getFirst_name());
                        riderListModel.setLast_name(listModels.get(i).getLast_name());
                        listModelsSelected.add(riderListModel);
                    }
                }

                //to show delete icon at header
                if (listModelsSelected.size()>0)
                {
                    ivEnd.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        ivEnd.setImageDrawable(getDrawable(R.drawable.bin));
                    }
                }
                else
                {
                    ivEnd.setVisibility(View.GONE);
                }

                //when checkbox is disabled  after selecting all option
           /*     if (listModelsSelected.size()==listModels.size())
                {
                    if (listModels.get(layoutPosition).isFlag())
                    {
                        clickFlag=true;
                    }
                }
*/
                   if (listModelsSelected.size()==listModels.size())
                    {
                        cbAll.setChecked(true);

                    }
                    else
                    {
                        clickFlag=true;
                        selectedPositon=layoutPosition;
                        cbAll.setChecked(false);
                    }
            }
        });
    }

    int selectedPositon;


    public void callConfirmDelete()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_pop_up, null);
        final TextView no = (TextView) dialogView.findViewById(R.id.tvNo);
        final TextView yes = (TextView) dialogView.findViewById(R.id.tvYes);
        final TextView tvMsg = (TextView) dialogView.findViewById(R.id.tvMsg);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvMsg.setText("Are you sure? You want to Delete");



        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                listModelsSelected.clear();
                for (int i = 0; i <listModels.size() ; i++)
                {
                    RiderListModel riderListModel=new RiderListModel();
                    if (listModels.get(i).isFlag())
                    {
                        riderListModel.setId(listModels.get(i).getId());
                        riderListModel.setImage(listModels.get(i).getImage());
                        riderListModel.setFirst_name(listModels.get(i).getFirst_name());
                        riderListModel.setLast_name(listModels.get(i).getLast_name());
                        listModelsSelected.add(riderListModel);
                    }
                }
                callDeleteService();

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
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
        AlertDialog alertDialog = dialogBuilder.create();
        // alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = WindowManager.LayoutParams.WRAP_CONTENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvMsg.setText("Will all selected Rider's be picked-up from the same location?");

        Log.e("listSize ",ListofChildrenAdapter.listIds.size()+"");

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
                Intent intent=new Intent(ListofChildren.this, PickDropConfirmation.class);
                intent.putExtra("key","start_multiple_different");
                intent.putExtra("key_final","no");
                intent.putExtra("position","0");
                intent.putExtra("rider_list",(Serializable) listModelsSelected);
                startActivityForResult(intent,PickDropConfirmation.requestCode);
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(ListofChildren.this, EnterPickUp.class);
                intent.putExtra("key","start_multiple");
                intent.putExtra("key_final","no");
                intent.putExtra("position","0");
                intent.putExtra("rider_list",(Serializable) listModelsSelected);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 105:
                Log.e(TAG, "onResponse: ListOfChildrenREsponse "+response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        recycler.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        cbAll.setVisibility(View.VISIBLE);
                        tvALl.setVisibility(View.VISIBLE);

                        listModels.clear();
                        listModelsSelected.clear();
                        JSONArray data=jsonObject.getJSONArray("data");

                        if (data.length()>0)
                        {
                            for (int i = 0; i <data.length() ; i++)
                            {
                                RiderListModel riderListModel=new RiderListModel();

                                JSONObject object=data.getJSONObject(i);
                                riderListModel.setId(object.getString("id"));
                                riderListModel.setFirst_name(object.getString("first_name"));
                                riderListModel.setLast_name(object.getString("last_name"));
                                riderListModel.setImage(object.getString("image"));
                                riderListModel.setFlag(false);

                                listModels.add(riderListModel);
                            }

                            // listModels=generalResponse.getDataAsList("data",RiderListModel.class);

                            if (listModels.size()>0)
                            {
                                setAdapter();
                                selectAll();
                            }
                        }
                        else {
                            tvNoData.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                            cbAll.setVisibility(View.GONE);
                            tvALl.setVisibility(View.GONE);
                        }
                    }
                    else {
                        tvNoData.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                        cbAll.setVisibility(View.GONE);
                        tvALl.setVisibility(View.GONE);
                    }
                }catch (Exception ex)
                {
                        ex.printStackTrace();
                }
                break;
        }

    }

    public void selectAll()
    {
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (listModels.size()>0)
                {
                    for (int i = 0; i <listModels.size() ; i++)
                    {
                        if (isChecked)
                        {
                            clickFlag=false;
                            ivEnd.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            {
                                ivEnd.setImageDrawable(getDrawable(R.drawable.bin));
                            }
                            listModels.get(i).setFlag(true);
                        }
                        else
                         {
                             if (clickFlag)
                             {
                                 ivEnd.setVisibility(View.GONE);
                                 listModels.get(selectedPositon).setFlag(false);
                             }
                             else
                             {
                                 ivEnd.setVisibility(View.GONE);
                                 listModels.get(i).setFlag(false);
                             }
                         }
                    }
                    childrenAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
    {
        progressDialog.dismiss();
        GeneralResponse generalResponse=new GeneralResponse(response);

        Log.e(TAG, "onResponse: GeneralDelete<< "+generalResponse);

        try
        {
        JSONObject jsonObject=generalResponse.getResponse_object();

        if (jsonObject.getString("status").equalsIgnoreCase("true"))
        {
         ivEnd.setVisibility(View.GONE);
         callListofChildren();
        }
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t)
    {
        Log.e("OnFailure ","ResponseBody");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 40)
        {
            Log.e("OnActivityResult ", data + "");

           /* if (!data.equals(null))
            {

            }*/

        }


    }
}
