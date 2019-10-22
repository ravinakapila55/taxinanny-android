package com.taxi.nanny.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.utils.Constant;
import com.taxi.nanny.utils.SharedPrefUtil;
import com.taxi.nanny.utils.retrofit.RetrofitResponse;
import com.taxi.nanny.utils.retrofit.RetrofitService;
import com.taxi.nanny.views.booking.history.BookingHistory;
import com.taxi.nanny.views.login_section.WelcomeActivity;
import com.taxi.nanny.views.login_section.login.LoginActivity;
import com.taxi.nanny.views.payment.PaymentList;
import com.taxi.nanny.views.profile.EditProfile;
import com.taxi.nanny.views.settings.SettingsActivity;
import org.json.JSONException;
import org.json.JSONObject;
import de.hdodenhof.circleimageview.CircleImageView;


public class Drawer extends AppCompatActivity implements RetrofitResponse {
    public View view, hview;
    FrameLayout frame;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;
    private CircleImageView ivheader;
    private TextView tvname, tvEdit,tvHeaderEmail;
    public Spinner spinner;
    public TextView tvtitle;
    Menu menu;
    RelativeLayout rl_header;
    Menu nav_menu;
    public static String TAG = "Drawer";
    public MenuItem home,payment,history,notifications,settings,help,logout;
    SharedPrefUtil sharedPrefUtil;

    @Override
    public void setContentView(@LayoutRes int layoutResID)
    {
        view = getLayoutInflater().inflate(R.layout.drawer, null);
        frame = (FrameLayout) view.findViewById(R.id.frame);
        getLayoutInflater().inflate(layoutResID, frame, true);

        super.setContentView(view);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        hview = navigationView.getHeaderView(0);

        sharedPrefUtil=SharedPrefUtil.getInstance();

        ivheader = (CircleImageView) hview.findViewById(R.id.ivheader);
        tvname = (TextView) hview.findViewById(R.id.tvname);
        tvEdit = (TextView) hview.findViewById(R.id.tvEdit);
        tvHeaderEmail = (TextView) hview.findViewById(R.id.tvHeaderEmail);
        rl_header = (RelativeLayout) hview.findViewById(R.id.rl_header);

        navigationView.inflateMenu(R.menu.menu_drawer);
        menu = navigationView.getMenu();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvtitle = (TextView) findViewById(R.id.tvtitle);

        menu = navigationView.getMenu();
        home = (MenuItem) menu.findItem(R.id.home);
        payment = (MenuItem) menu.findItem(R.id.payment);
        history = (MenuItem) menu.findItem(R.id.history);
        settings = (MenuItem) menu.findItem(R.id.settings);
        notifications = (MenuItem) menu.findItem(R.id.notifications);
        help = (MenuItem) menu.findItem(R.id.help);
        logout = (MenuItem) menu.findItem(R.id.logout);

        toolbar.setNavigationIcon(R.drawable.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(false);//when using our custom drawer icon
        drawer.setDrawerListener(drawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();
        initializeView();

        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        nav_menu = navigationView.getMenu();
    }

    protected boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        tvname.setText(sharedPrefUtil.getString(SharedPrefUtil.FNAME,"")+" "+
                sharedPrefUtil.getString(SharedPrefUtil.LNAME,""));

        tvEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawer.closeDrawers();
                Intent intent=new Intent(Drawer.this, EditProfile.class);
                startActivity(intent);
            }
        });

        ivheader.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawer.closeDrawers();
                Intent intent=new Intent(Drawer.this, EditProfile.class);
                startActivity(intent);
            }
        });

        if (!sharedPrefUtil.getString(SharedPrefUtil.IMAGE,"").equalsIgnoreCase("") )
        {
            Picasso.with(this).load(sharedPrefUtil.getString(SharedPrefUtil.IMAGE,"")).
                    placeholder(getResources().getDrawable(R.drawable.pic_dummy_user)).into(ivheader);
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

        tvMsg.setText("Are you sure ?");
        yes.setText("Logout");

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                callLogout();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    private void initializeView()
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                drawer.closeDrawers();
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.home:
                        break;

                        case R.id.payment:
                           Intent intent5=new Intent(Drawer.this, PaymentList.class);
                           startActivity(intent5);

                        break;

                        case R.id.settings:
                            Intent intent=new Intent(Drawer.this, SettingsActivity.class);
                            startActivity(intent);
                            break;

                      case R.id.history:

                       Intent intent1=new Intent(Drawer.this, BookingHistory.class);
                       startActivity(intent1);

                      break;

                        case R.id.notifications:
                        break;

                        case R.id.help:
                         Intent in=new Intent(Drawer.this,Help.class);
                         startActivity(in);
                        break;

                        case R.id.logout:
                        callAlert();
                        break;

                }
                return true;
            }
        });
    }

    public void callLogout()
    {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("device_token", SharedPrefUtil.getInstance().getString(SharedPrefUtil.DEVICE_FCM_TOKEN,""));

            Log.e("LogoutParams",jsonObject.toString());
            new RetrofitService(this, this,
                    Constant.API_LOGOUT,jsonObject, 700, 2,"1").
            callService(true);
            SharedPrefUtil.getInstance().onLogout();
            Intent intent = new Intent(Drawer.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode) {
            case 700:
                Log.e(TAG, "onResponse: Logout " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("true"))
                    {
                        SharedPrefUtil.getInstance().onLogout();
                        Intent intent = new Intent(Drawer.this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else
                    {
                        Log.e(TAG, "onResponse: LogoutElse " + "");
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
