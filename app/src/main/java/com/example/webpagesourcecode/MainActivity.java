package com.example.webpagesourcecode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    private EditText txtEditURL;
    private TextView textPageSource;
    private RadioButton radioHttp, radioHttps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEditURL = findViewById(R.id.txtEditURL);
        textPageSource = findViewById(R.id.textPageSource);
        radioHttp = findViewById(R.id.radioHttp);
        radioHttps = findViewById(R.id.radioHttps);

        //to reconnect to the loader, if the loader already exists
        if(LoaderManager.getInstance(this).getLoader(0) != null) {
            LoaderManager.getInstance(this).initLoader(0,null,this);
        }
    }

    public void btnGetTheSourceClicked(View view) {
        boolean isHttps = radioHttps.isChecked();
        String urlString = txtEditURL.getText().toString();
        String fullUrlString = "";
        if (isHttps) {
            fullUrlString = "https://" + urlString;
        } else {
            fullUrlString = "http://" + urlString;
        }
        // Hide the soft keyboard
        InputMethodManager inputManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null ) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        // Create ConnectivityManager and NetworkInfo
        ConnectivityManager connMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        // Check network connection if OK then call the FetchBook.excute
        if (networkInfo != null && networkInfo.isConnected() && urlString.length() != 0) {
            // Create a new object for FetchBook class and call execute method
            Bundle queryBundle = new Bundle();
            queryBundle.putString("urlString", fullUrlString);
            LoaderManager.getInstance(this).restartLoader(0, queryBundle, this);
            // Change the title text to loading...
            textPageSource.setText("Loading . . .");
        } else {
            if (urlString.length() == 0) {
                textPageSource.setText("Please enter a url");
            } else {
                textPageSource.setText("No network connection!");
            }
        }

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String url = "";
        if (args != null) {
            url = args.getString("urlString");
        }
        return new WebPageLoader(this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data != null) {
            textPageSource.setText(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}