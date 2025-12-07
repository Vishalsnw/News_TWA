package com.news.twa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private static final String WEBSITE_URL = "https://news-alpha-two.vercel.app/";

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLayout;
    private Button retryButton;
    private ImageButton homeButton;
    private ImageButton exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupWebView();
        setupSwipeRefresh();
        setupRetryButton();
        setupHomeButton();
        setupExitButton();

        if (isNetworkAvailable()) {
            loadWebsite();
        } else {
            showError();
        }
    }

    private void initViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        errorLayout = findViewById(R.id.errorLayout);
        retryButton = findViewById(R.id.retryButton);
        homeButton = findViewById(R.id.homeButton);
        exitButton = findViewById(R.id.exitButton);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowContentAccess(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        
        webSettings.setTextZoom(100);

        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient());
        
        webView.setInitialScale(1);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryDark
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isNetworkAvailable()) {
                webView.reload();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                showError();
            }
        });
    }

    private void setupRetryButton() {
        retryButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                hideError();
                loadWebsite();
            }
        });
    }

    private void setupHomeButton() {
        homeButton.setOnClickListener(v -> goHome());
    }

    private void setupExitButton() {
        exitButton.setOnClickListener(v -> showExitDialog());
    }

    private void goHome() {
        webView.clearHistory();
        webView.loadUrl(WEBSITE_URL);
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.exit)
            .setMessage(R.string.exit_confirm)
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                finishAffinity();
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }

    private void loadWebsite() {
        webView.loadUrl(WEBSITE_URL);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void showError() {
        webView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void hideError() {
        webView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            goHome();
        }
    }

    private void hideSubscriptionPopups() {
        String js = "javascript:(function() { " +
            "var selectors = [" +
            "'.subscribe-popup', '.subscription-modal', '.paywall', '.subscribe-wall'," +
            "'.modal-overlay', '.subscription-overlay', '.login-wall', '.signin-wall'," +
            "'[class*=\"subscribe\"]', '[class*=\"paywall\"]', '[class*=\"signin\"]'," +
            "'[id*=\"subscribe\"]', '[id*=\"paywall\"]', '[id*=\"modal\"]'" +
            "];" +
            "selectors.forEach(function(sel) {" +
            "  var elements = document.querySelectorAll(sel);" +
            "  elements.forEach(function(el) {" +
            "    if(el.style.position === 'fixed' || el.style.position === 'absolute') {" +
            "      el.style.display = 'none';" +
            "    }" +
            "  });" +
            "});" +
            "document.body.style.overflow = 'auto';" +
            "document.documentElement.style.overflow = 'auto';" +
            "})()";
        webView.evaluateJavascript(js, null);
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            hideError();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            hideSubscriptionPopups();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) {
                showError();
            }
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
        }
    }
}
