package com.taxi.nanny.views.driver.documents;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.taxi.nanny.R;
import com.taxi.nanny.model.DriverDocumentsModel;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.BaseActivity;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class DriverDocuments extends BaseActivity implements RetrofitResponse
{

    @BindView(R.id.cvLicence)
    CardView cvLicence;

    @BindView(R.id.cvInsurance)
    CardView cvInsurance;

    @BindView(R.id.cvPermit)
    CardView cvPermit;

    @BindView(R.id.cvRegistration)
    CardView cvRegistration;

    ArrayList<DriverDocumentsModel> list=new ArrayList<>();

    @Override
    protected int getContentId()
    {
        return R.layout.driver_documents;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHeaderText("Driver Documents");
        callAllDocuments();
    }

    private void callAllDocuments()
    {
        new RetrofitService(this, this,
                Constant.API_DRIVER_ALL_DOC , 500, 1,"1").
                callService(true);
    }

    @OnClick({R.id.img_back_btn,R.id.cvLicence,R.id.cvInsurance,R.id.cvPermit,R.id.cvRegistration})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;
                case R.id.cvLicence:

                    if (list.size()>0)
                    {
                        Intent DriverLicence=new Intent(DriverDocuments.this, DriverLicence.class);
                        DriverLicence.putExtra("data",(Serializable)list.get(0));
                        startActivity(DriverLicence);
                    }
                    else {
                        Toast.makeText(this, "No Documents Uploaded", Toast.LENGTH_SHORT).show();
                    }


                break;

                case R.id.cvInsurance:

                    if (list.size()>0)
                    {
                        Intent it=new Intent(DriverDocuments.this, VehicleInsurance.class);
                        it.putExtra("data",(Serializable)list.get(0));
                        startActivity(it);
                    }
                    else {
                        Toast.makeText(this, "No Documents Uploaded", Toast.LENGTH_SHORT).show();
                    }


                    break;
//http://178.128.116.149/taxinanny1/public/images/vehicle_document/1567595793.dummy(1).pdf
            //http://178.128.116.149/taxinanny1/public/1567595782.dummy.pdf
                case R.id.cvPermit:

                    if (list.size()>0)
                    {
                        if (!list.get(0).getPermit_doc().trim().equalsIgnoreCase(""))
                        {
//                            String permitUrl=Constant.BASE_DRIVER_DOC+list.get(0).getPermit_doc();
                            String permitUrl=list.get(0).getPermit_doc();
                            Log.e("PermitUrl ",permitUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(permitUrl), "application/pdf");
                            try{
                                view.getContext().startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                //user does not have a pdf viewer installed
                            }
                        }
                    }
                    else {
                        Toast.makeText(this, "No Documents Uploaded", Toast.LENGTH_SHORT).show();
                    }


                    break;

                case R.id.cvRegistration:

                    if (list.size()>0)
                    {
                        if (!list.get(0).getVehicleReg_file().trim().equalsIgnoreCase(""))
                        {
                            String regUrl=Constant.BASE_DRIVER_DOC+list.get(0).getVehicleReg_file().trim();
                            Log.e("RegURl ",regUrl);
                            Intent intent1 = new Intent(Intent.ACTION_VIEW);
                            intent1.setDataAndType(Uri.parse(regUrl), "application/pdf");
                            try
                            {
                                view.getContext().startActivity(intent1);
                            }
                            catch (ActivityNotFoundException e)
                            {
                                //user does not have a pdf viewer installed
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "No Documents Uploaded", Toast.LENGTH_SHORT).show();
                    }

                    break;
        }
    }


    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 500:
                Log.e("DriverAllDoc ",response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        list.clear();

                        DriverDocumentsModel driverDocumentsModel=new DriverDocumentsModel();

                        JSONObject vehicle_documents=jsonObject.getJSONObject("vehicle_documents");

                        driverDocumentsModel.setVehicleRegId(vehicle_documents.getString("id"));
                        driverDocumentsModel.setVehicleReg_file(vehicle_documents.getString("vehicle_registration_document"));

                        JSONObject licence_details=jsonObject.getJSONObject("licence_details");

                        driverDocumentsModel.setLicence_id(licence_details.getString("id"));
                        driverDocumentsModel.setLicence_full_name(licence_details.getString("fullname"));
                        driverDocumentsModel.setlBirthday(licence_details.getString("dob"));
                        driverDocumentsModel.setLaddress1(licence_details.getString("address1"));
                        driverDocumentsModel.setLaddress2(licence_details.getString("address2"));
                        driverDocumentsModel.setlCountry(licence_details.getString("country_id"));
                        driverDocumentsModel.setlIssuedOn(licence_details.getString("issue_date"));
                        driverDocumentsModel.setlExpiry_date(licence_details.getString("expiry_date"));
                        driverDocumentsModel.setlImage(licence_details.getString("image"));
                        driverDocumentsModel.setVehicleId(licence_details.getString("vehicle_id"));
                        driverDocumentsModel.setLicence_no(licence_details.getString("licence_number"));
                        driverDocumentsModel.setLstate(licence_details.getString("state_id"));
                        driverDocumentsModel.setLcity(licence_details.getString("city_id"));
                        driverDocumentsModel.setLzipcode(licence_details.getString("zip_code"));

                        JSONObject VehicleInsurance=jsonObject.getJSONObject("VehicleInsurance");

                        driverDocumentsModel.setInsuranceId(VehicleInsurance.getString("id"));
                        driverDocumentsModel.setICompnayName(VehicleInsurance.getString("company_name"));
                        driverDocumentsModel.setIPolicyNumber(VehicleInsurance.getString("policy_no"));
                        driverDocumentsModel.setInsuranceIssuedDate(VehicleInsurance.getString("issue_date"));
                        driverDocumentsModel.setInsuranceExpiryDate(VehicleInsurance.getString("expiry_date"));
                        driverDocumentsModel.setlImage(VehicleInsurance.getString("image"));

                        JSONObject VehiclePermit=jsonObject.getJSONObject("VehiclePermit");

                        driverDocumentsModel.setPermitID(VehiclePermit.getString("id"));
                        driverDocumentsModel.setPermit_doc(VehiclePermit.getString("vehicle_permit_document"));
                        list.add(driverDocumentsModel);
                        Log.e("ListDocumentsSize ",list.size()+"");
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

        }
    }
}
