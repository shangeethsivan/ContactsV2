package com.shangeeth.contactsclonev2.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shangeeth.contactsclonev2.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String id = getIntent().getStringExtra(getString(R.string.id_extra));
    }
}
