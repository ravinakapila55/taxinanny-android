package com.taxi.nanny.domain;

import com.taxi.nanny.model.DistanceModel;
import com.taxi.nanny.req_model.ConfirmBookingMain;
import com.taxi.nanny.req_model.ConfirmBookingModel;
import com.taxi.nanny.req_model.DeleteRiderListModel;
import com.taxi.nanny.req_model.EstimateFTModel;
import com.taxi.nanny.utils.SharedPrefUtil;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import static com.taxi.nanny.utils.Constant.*;

public interface ApiInterface
{

    SharedPrefUtil prefUtil=SharedPrefUtil.getInstance();
    String id=prefUtil.getString(SharedPrefUtil.USER_ID,"");

    @FormUrlEncoded
    @POST(API_REGISTER)
    Call<ResponseBody> callRegister(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_LOGIN)
    Call<ResponseBody> calllogin(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_ADD_RIDER)
    Call<ResponseBody> callAddRider(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_SAVE_RIDER_FAVOURITE_LOCATION)
    Call<ResponseBody> saveRiderLocation(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_UPDATE_DRIVER_LOCATION)
    Call<ResponseBody> saveDriverLocation(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_DRIVER_STATUS)
    Call<ResponseBody> saveDriverStatus(@FieldMap HashMap<String, String> param);


    @FormUrlEncoded
    @POST(API_ACCEPT_REQUEST)
    Call<ResponseBody> acceptRequestByDriver(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_REJECT_REQUEST)
    Call<ResponseBody> rejectRequestByDriver(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_ARRIVE_DRIVER)
    Call<ResponseBody> callArriveDriver(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_START_TRIP)
    Call<ResponseBody> callStartTrip(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_COMPLETE_TRIP)
    Call<ResponseBody> callCompleteTrip(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_EDIT_FAV_LOCATIONS)
    Call<ResponseBody> callEditFavouriteLocation(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_EDIT_PROFILE)
    Call<ResponseBody> callEditProfile(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_DRIVER_VEHICLE_EDIT_DETAIL)
    Call<ResponseBody> callEditVehicle(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_ADD_CARD)
    Call<ResponseBody> callAddCard(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_SUBMIT_RATING)
    Call<ResponseBody> callSubmitRating(@FieldMap HashMap<String, String> param);


    @FormUrlEncoded
    @POST(API_FARE_CHECK)
    Call<ResponseBody> callFareCheck(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_MAKE_PENDING_PAYMENT)
    Call<ResponseBody> callMakePayment(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_ADD_EMERGENCY)
    Call<ResponseBody> callAddEmergencyContacts(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_CHANGE_PASSWORD)
    Call<ResponseBody> callChangePassword(@FieldMap HashMap<String, String> param);


    @FormUrlEncoded
    @POST(API_CHANGE_NOTI)
    Call<ResponseBody> callChangeNoti(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_CHANGE_SMS)
    Call<ResponseBody> callChangeSMS(@FieldMap HashMap<String, String> param);


    @FormUrlEncoded
    @POST(API_SUPPORT_QUERY)
    Call<ResponseBody> callAddSupportQuery(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_FORGOT_PASSWORD)
    Call<ResponseBody> callForget(@FieldMap HashMap<String, String> param);


    @FormUrlEncoded
    @POST(API_CANCEL_RIDE)
    Call<ResponseBody> callCancelRideByDriver(@FieldMap HashMap<String, String> param);


    @FormUrlEncoded
    @POST(API_ACTUAL_DRIVER_LOCAION)
    Call<ResponseBody> callUpdateDriverActualLocation(@FieldMap HashMap<String, String> param);



    // @FormUrlEncoded
    @POST(API_GET_ESTIMATED_TIME_FARE)
    Call<ResponseBody> getEstimatedTimeFare(@Body EstimateFTModel req);

    // @FormUrlEncoded
    @POST(API_DELETE_RIDER)
    Call<ResponseBody> getDeleteRider(@Body DeleteRiderListModel req);


    // @FormUrlEncoded
    @POST(API_ESTIMATED_FARE)
    Call<ResponseBody> getFare(@Body DistanceModel req);


    // @FormUrlEncoded
    @POST(API_CONFIRM_BOOKING)
    Call<ResponseBody> callConfirmBooking(@Body ConfirmBookingModel req);


    // @FormUrlEncoded
    @POST(API_CONFIRM_BOOKING)
    Call<ResponseBody> callConfirmBookingMAin(@Body ConfirmBookingMain req);

    @FormUrlEncoded
    @POST(API_ADD_VEHICLE_DETAILS)
    Call<ResponseBody> callAddVehicle(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_ADD_DRIVING_LICENCE)
    Call<ResponseBody> callAddDrivingLicence(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST(API_ADD_DRIVING_INSURANCE)
    Call<ResponseBody> callAddVehicleInsurance(@FieldMap HashMap<String, String> param);

    @GET(API_VEHICLE_TYPE)
    Call<ResponseBody>getVehicleType();

    @GET(API_COUNTRY_LIST)
    Call<ResponseBody>getCountryList();

    @GET(API_LIST_OF_CHILDREN)
    Call<ResponseBody>getRiderList();

    @Multipart
    @POST(API_ADD_LICENCE)
    Call<ResponseBody> addVehicle(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);

    @Multipart
    @POST(API_ADD_RIDER)
    Call<ResponseBody> addRider(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);

    @Multipart
    @POST(API_ADD_VEHICLE_DETAILS)
    Call<ResponseBody> addVehicleDetails(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);

    @Multipart
    @POST(API_REGISTER)
    Call<ResponseBody> ParentRegister(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);

    @Multipart
    @POST(API_EDIT_PROFILE)
    Call<ResponseBody> editProfile(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);

    @Multipart
    @POST(API_DRIVER_VEHICLE_EDIT_DETAIL)
    Call<ResponseBody> editVehicle(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);

    @Multipart
    @POST(API_ADD_DRIVING_LICENCE)
    Call<ResponseBody> addDrivingLicence(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);

    @Multipart
    @POST(API_ADD_DRIVING_INSURANCE)
    Call<ResponseBody> addVehicleInsurance(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);


    @Multipart
    @POST(API_VEHICLE_REGISTRATION)
    Call<ResponseBody> addVehicleREgistration(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);


    @Multipart
    @POST(API_VEHICLE_PERMIT)
    Call<ResponseBody> addVehiclePermit(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);



    @Multipart
    @POST(API_ADD_LICENCE)
    Call<ResponseBody> addInsurance(@PartMap HashMap<String, RequestBody> param,@Part MultipartBody.Part part);

}
