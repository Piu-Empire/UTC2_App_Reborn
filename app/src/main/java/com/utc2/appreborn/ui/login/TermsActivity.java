package com.utc2.appreborn.ui.login;

import android.os.Bundle;
import com.utc2.appreborn.utils.LocaleHelper;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;

public class TermsActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}