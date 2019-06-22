package com.example.smartminicamper;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartminicamper.webservice.RestWebserviceSettings;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private Button confirmButton;
    private EditText webserviceLinkET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing app elements
        webserviceLinkET = findViewById(R.id.etWebserverLink);
        webserviceLinkET.addTextChangedListener(linkTextWatcher);

        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("Login");

        confirmButton = findViewById(R.id.confirmLinkButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLandingPageActivity();
            }
        });

        webserviceLinkET.setText("http://78.55.65.106:5000");

    }

    private TextWatcher linkTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String linkInput = webserviceLinkET.getText().toString().trim();
            RestWebserviceSettings.setBaseUrl(linkInput);
            confirmButton.setEnabled(!linkInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void openLandingPageActivity(){
        Intent intent = new Intent(MainActivity.this, LandingPageActivity.class);
        startActivityForResult(intent,1);
    }
}