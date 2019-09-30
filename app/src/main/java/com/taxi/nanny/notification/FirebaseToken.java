/*
package com.taxi.nanny.notification;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;


public class FirebaseToken extends FirebaseInstanceId {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public void onTokenRefresh() {
        FirebaseInstanceId.getInstance().getToken();

        Log.e("Token ",FirebaseInstanceId.getInstance().getToken());
  sharedPreferences= APp.getIdPrefs();
        editor=sharedPreferences.edit();
        editor.putString("", FirebaseInstanceId.getInstance().getToken());
        editor.commit();


    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
*/
