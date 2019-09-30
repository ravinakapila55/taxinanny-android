package com.taxi.nanny.views.driver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.login_section.register.RegisterUserDetailsActivity;
import butterknife.BindView;

public class DriverStripeAccountSetup extends BaseActivity
{
    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected int getContentId()
    {
        return R.layout.driver_stripe_account_setup;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        webView.setWebViewClient(new MyBrowser());

        String email="d@gmail.com";

        String url = "https://connect.stripe.com/express/oauth/authorize?redirect_uri=https://connect.stripe.com/" +
                "connect/default_new/oauth/test&client_id=ca_32D88BD1qLklliziD7gYQvctJIhWBSQ7&" +
                "stripe_user[email]=d@gmail.com&stripe_user[business_type]=individual";

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient()
        {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                Log.e("WebViewClientOverride ",request+"");

//                webView.loadUrl(request.getUrl().toString());
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                Log.e("onPageStarted ",url+"");
                super.onPageStarted(view, url, favicon);
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
            }
            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.e("onPageFinished ",url+"");
            }
        });
    }

    private class MyBrowser extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, RegisterUserDetailsActivity.class);
        setResult(56,intent);
        finish();
    }

}
