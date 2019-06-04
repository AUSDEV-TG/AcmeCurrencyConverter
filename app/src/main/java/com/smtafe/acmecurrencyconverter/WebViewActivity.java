package com.smtafe.acmecurrencyconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * AcmeCurrencyConverter WebViewActivity
 *
 * @author Tom Green
 * @version 1.0
 */

public class WebViewActivity extends AppCompatActivity {
    //Create the WebViewActivity using the layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        String url = null;

        //Get the url from the extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }

        WebView webView = findViewById(R.id.webview);

        //Load the url in the webView
        webView.loadUrl(url);
    }
}
