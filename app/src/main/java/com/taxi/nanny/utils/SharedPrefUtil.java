package com.taxi.nanny.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {

    public static final String NAME = "name";
    public static final String FNAME = "fname";
    public static final String LNAME = "lname";
    public static final String ENMAIL = "email";
    public static final String USERTYPE = "user_type";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String DEVICE_FCM_TOKEN = "device_fcm_token";
    public static final String CSI_ID = "csi_id" ;
    public static final String EMAIL = "email";
    public static final String LICENCE = "licence";
    public static final String INSURANCE = "insurance";
    public static final String PERMIT = "permit";
    public static final String PERMIT_FILE = "permit_file";
    public static final String REGISTRATION = "registration";
    public static final String REGISTRATION_File = "registration_file";
    public static final String CHILD_ADDED = "child_added";
    public static final String ADDRESS = "addresss";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String PAN = "pan";
    public static final String DESIGNATION = "Designation";
    public static final String AUTHORITY = "authority";
    public static final String IMAGE = "profileImage";
    public static final String USER_ID = "userId";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String AUTH_TOKEN = "authToken";
    public static final String LOGIN = "login";
    public static final String PROFILE = "profile";
    public static final String PASSWORD = "password";
    public static final String FACILITY_ID ="facility_id" ;
    public static final String PROFILE_PIC = "profile_pic";
    public static final String USER_LOGIN_TYPE = "user_login";
    public static final String ACCESS_CODE ="access_code" ;
    public static final String IPPB_CODE ="ippb_code" ;
    public static final String ROLE_ID ="role_id" ;
    public static final String DRIVER_STATUS ="driver_status" ;
    public static final String NOTIFY_KEY ="notify_key" ;
    public static final String VEHICLE_SAVED ="vehicle_saved" ;
    public static final String VEHICLE_SAVED_ID ="vehicle_saved_id" ;
    public static final String VEHICLE_SELECTED ="vehicle_selected" ;
    public static final String SELECTED_VEHICLE_ID ="selected_vehicle_id" ;
    public static final String DOCUMENTS_SAVED ="document_saved" ;
    public static final String HOME_ADDRESS ="address_home" ;
    public static final String SMS_SETTINGS ="sms_setting" ;
    public static final String NOTIFICATION_SETTING ="notification_setting" ;
    public static final String DRIVER_PROXIMITY ="driver_proximity" ;
    public static final String ADDRESS_LATITUDE ="address_latitude" ;
    public static final String ADDRESS_LONGITUDE ="address_longitude" ;
    public static final String LIVE_TRACKING_ONGOING ="live_tracking_ongoing" ;

    /**
     * Name of the preference file
     */
    private static final String APP_PREFS = "application_preferences";
    private Context mContext;
    private static SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static SharedPrefUtil instance;

    public SharedPrefUtil(Context mContext) {
        this.mContext = mContext;
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPrefUtil(context);
        }
    }

    public static SharedPrefUtil getInstance() {
        if (instance == null) {
            instance = new SharedPrefUtil(MyApplication.getInstance());
        }
        return instance;
    }


    public void saveString(String key, String value) {

        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }


    public void saveInt(String key, int value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.apply();
    }


    public void saveBoolean(String key, boolean value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }


    public String getString(String key) {
        //    mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(key, "");
    }


    public String getString(String key, String defaultValue) {
        //    mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(key, defaultValue);
    }


    public int getInt(String key) {
        // mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(key, 0);
    }


    public int getInt(String key, int defaultValue) {
        //     mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(key, defaultValue);
    }


    public boolean getBoolean(String key) {
        //     mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(key, false);
    }


    public boolean getBoolean(String key, boolean defaultValue) {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(key, defaultValue);
    }


    /**
     * save the device token.
     *
     * @param token Vslue retrieved from the firebase.
     */
    public void saveDeviceToken(String token) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(DEVICE_TOKEN, token);
        mEditor.apply();
    }


    /**
     * get the current device token
     *
     * @return Returns the last update device token got from firebase.
     */
    public String getDeviceToken() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(DEVICE_TOKEN, "1234567890");
    }
//

    /**
     * save the userId of current user using the application
     *
     * @param userId this is the userId of the user
     */
    public void saveUserId(String userId) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(USER_ID, userId);
        mEditor.apply();
    }


    public String getUserId() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(USER_ID, "");
    }


    public void saveAuthToken(String authToken) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(AUTH_TOKEN, authToken);
        mEditor.apply();
    }

    /**
     * get the authorization of the user
     */
    public String getAuthToken() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(AUTH_TOKEN, "");
    }


    /**
     * set login state oin the device is there any user current login device
     */
    public void setLogin(boolean value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(LOGIN, value);
        mEditor.apply();
    }

    /**
     * @return is anyuser current login into the device accordingly true/false.
     */
    public boolean isLogin() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(LOGIN, false);
    }


    /**
     * save name of the user
     */
    public void saveName(String name) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(NAME, name);
        mEditor.apply();
    }

    /**
     * get name of the user
     */
    public String getName() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(NAME, "");
    }


    /**
     * save email of the user
     */
    public void saveEmail(String email) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(EMAIL, email);
        mEditor.apply();
    }

    /**
     * get email of the user
     */
    public String getEmail() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(EMAIL, "");
    }

    /**
     * save phoneNumber of the user
     */
    public void savePhoneNumber(String phoneNumber) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(PHONE_NUMBER, phoneNumber);
        mEditor.apply();
    }

    /**
     * get phoneNumber of the user
     */
    public String getPhoneNumber() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(PHONE_NUMBER, "");
    }

    /**
     * save image of the user
     */
    public void saveImage(String image) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(IMAGE, image);
        mEditor.apply();
    }

    /**
     * get image of the user
     */
    public String getImage() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(IMAGE, "");
    }

    /**
     * Clears the shared preference file
     */
    public void clear() {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        mSharedPreferences.edit().clear().apply();
    }

    public void onLogout() {

        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        mSharedPreferences.edit().clear().apply();
//        mEditor = mSharedPreferences.edit();
//        mEditor.clear();
////        mEditor.remove(USER_ID);
////        setLogin(false);
//        mEditor.apply();
    }
}
