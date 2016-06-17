package com.born2go.lazzybee.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.born2go.lazzybee.R;

public class TestYourVoca extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_your_voca);

        _initToolBar();

        final WebView mWebViewTestYourVoca = (WebView) findViewById(R.id.mWebViewTestYourVoca);
        mWebViewTestYourVoca.getSettings().setJavaScriptEnabled(true);
        mWebViewTestYourVoca.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        mWebViewTestYourVoca.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebViewTestYourVoca.loadUrl(getString(R.string.url_test_your_voca));
                progressDialog.dismiss();
            }
        }, 2000);

    }

    private void _initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.drawer_test_your_voca));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
