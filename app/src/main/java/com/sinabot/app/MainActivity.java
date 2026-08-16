package com.sinabot.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private static final String DASHBOARD_URL = "http://76.13.78.123:5000";
    private static final String PREFS = "sinabot";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setMediaPlaybackRequiresUserGesture(false);

        // --- fit / zoom fixes ---
        s.setTextZoom(100);              // ignore the phone's big system font
        s.setSupportZoom(true);          // allow pinch-to-zoom as a fallback
        s.setBuiltInZoomControls(true);
        s.setDisplayZoomControls(false); // no ugly +/- buttons on screen

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler,
                                                  String host, String realm) {
                SharedPreferences p = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                String u = p.getString("auth_user", null);
                String pw = p.getString("auth_pass", null);
                if (handler.useHttpAuthUsernamePassword() && u != null && pw != null) {
                    handler.proceed(u, pw);
                } else {
                    promptCredentials(handler);
                }
            }
        });

        if (savedInstanceState == null) {
            webView.loadUrl(DASHBOARD_URL);
        }
    }

    private void promptCredentials(final HttpAuthHandler handler) {
        final EditText user = new EditText(this);
        user.setHint("username");
        user.setInputType(InputType.TYPE_CLASS_TEXT);

        final EditText pass = new EditText(this);
        pass.setHint("password");
        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        box.setPadding(pad, pad, pad, pad);
        box.addView(user);
        box.addView(pass);

        new AlertDialog.Builder(this)
                .setTitle("SINABOT login")
                .setView(box)
                .setCancelable(false)
                .setPositiveButton("Login", (d, w) -> {
                    String u = user.getText().toString().trim();
                    String pw = pass.getText().toString();
                    getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                            .putString("auth_user", u)
                            .putString("auth_pass", pw)
                            .apply();
                    handler.proceed(u, pw);
                })
                .setNegativeButton("Cancel", (d, w) -> handler.cancel())
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
