package com.taxi.nanny.views.login_section;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.taxi.nanny.R;
import com.taxi.nanny.utils.location.permission.Permission;
import com.taxi.nanny.utils.location.permission.PermissionGranted;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.login_section.login.LoginActivity;
import com.taxi.nanny.views.login_section.register.RegisterUserTypeActivity;
import java.util.ArrayList;
import butterknife.OnClick;
import io.socket.client.Socket;


public class WelcomeActivity extends BaseActivity implements PermissionGranted
{
    public static String TAG=WelcomeActivity.class.getSimpleName();
    public Socket mSocket;

    @Override
    protected int getContentId()
    {
        return R.layout.activity_welcome;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Permission.checkPermissionLocation(this, this);
        Permission.checkPermission(this, this);
    }

    @OnClick(R.id.btn_login)
    public void loginClicked()
    {
        Intent it = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(it);
    }

    @Override
    public void onBackPressed()
    {
        callExitAlert();
//        super.onBackPressed();

    }

    public void callExitAlert()
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

        tvMsg.setText("Exit the app ?");
        yes.setText("Yes");

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
                finish();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @OnClick(R.id.btn_registration)
    public void registerClicked()
    {
        Intent intent=new Intent(WelcomeActivity.this,RegisterUserTypeActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPermissionAlert(ArrayList<String> permissionList, int code)
    {
        Log.e( "showPermissionAlert: ","Popup" );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Log.e( "showPermissionAlert: ","Inside" );
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {

            case 2:

                for (int i = 0; i < permissions.length; i++)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    {
                       // Toast.makeText(this, "Permitions Allow", Toast.LENGTH_SHORT).show();
                        //  getLocation();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        // checkPermissionLocation(context);
                     //   Toast.makeText(this, "Permitions Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
            {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


}
