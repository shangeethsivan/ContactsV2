package com.shangeeth.contactsclonev2.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.shangeeth.contactsclonev2.AddOrEditActivity;
import com.shangeeth.contactsclonev2.R;
import com.shangeeth.contactsclonev2.adapters.DetailActivityCustomRecylerViewAdapter;
import com.shangeeth.contactsclonev2.db.ContactsDataTable;
import com.shangeeth.contactsclonev2.db.ContactsTable;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactJDO;
import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class DetailActivity extends AppCompatActivity {

    private ImageView mProfilePicIV;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mToolbarLayout;
    private PrimaryContactJDO mContactsJDO;
    private ContactsTable mContactsTable;
    private RecyclerView mRecyclerView;
    private ArrayList<SecondaryContactsJDO> mContactsDataJDOs;
    private String mCurrentId;
    private boolean mUpdated = false;
    private DetailActivityCustomRecylerViewAdapter mCustomAdapter;
    private int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(DetailActivity.class.getSimpleName(), "onCreate: created" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCurrentId = getIntent().getStringExtra(getString(R.string.id_extra));

        init();

        ensurePermissions();


    }

    private void ensurePermissions() {

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},0);
        }else {
            loadDataFromTable();
        }

    }

    private void loadDataFromTable() {

        mContactsJDO = mContactsTable.getContactsForId(mCurrentId);

        ContactsDataTable lDataTable = new ContactsDataTable(this);

        mContactsDataJDOs = lDataTable.getDatasForId(mCurrentId);

        SecondaryContactsJDO lNoteJDO = new SecondaryContactsJDO();
        lNoteJDO.setContactId(mCurrentId);
        lNoteJDO.setData(mContactsJDO.getNote());
        lNoteJDO.setType("Note");
        mContactsDataJDOs.add(lNoteJDO);

        SecondaryContactsJDO lOrganizationJDO = new SecondaryContactsJDO();
        lOrganizationJDO.setContactId(mCurrentId);
        lOrganizationJDO.setData(mContactsJDO.getOraganization());
        lOrganizationJDO.setType("Organization");

        mContactsDataJDOs.add(lOrganizationJDO);


        /*
        Loading data for title and image
         */
        mToolbarLayout.setTitle(mContactsJDO.getDisplayName());
        Picasso.with(this)
                .load(mContactsJDO.getPhotoUri())
                .placeholder(R.drawable.contact_bg)
                .into(mProfilePicIV);


        Collections.sort(mContactsDataJDOs);
        mCustomAdapter = new DetailActivityCustomRecylerViewAdapter(this, mContactsDataJDOs);
        mRecyclerView.setAdapter(mCustomAdapter);

    }


    public void init() {

        mContactsTable = new ContactsTable(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_detail);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager lLayoutManager = new LinearLayoutManager(this);
        lLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), lLayoutManager.getOrientation()));
        mRecyclerView.setLayoutManager(lLayoutManager);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProfilePicIV = (ImageView) findViewById(R.id.profile_pic_collapsing);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(0,new Intent().putExtra(getString(R.string.is_data_updated),mUpdated));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_contact:

                Intent lIntent = new Intent(this, AddOrEditActivity.class);
                lIntent.putExtra(getString(R.string.contact_data_jdos), mContactsDataJDOs);
                lIntent.putExtra(getString(R.string.name),mContactsJDO.getDisplayName());
                lIntent.putExtra(getString(R.string.id_extra), mCurrentId);
                lIntent.putExtra(getString(R.string.request_code),REQUEST_CODE);

                startActivityForResult(lIntent,REQUEST_CODE);

                break;
            case android.R.id.home:
                setResult(0,new Intent().putExtra(getString(R.string.is_data_updated),mUpdated));
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE && resultCode == 1){

            mUpdated = true;
            loadDataFromTable();

            mCustomAdapter = new DetailActivityCustomRecylerViewAdapter(this,mContactsDataJDOs);
            mRecyclerView.setAdapter(mCustomAdapter);

            Snackbar.make((CoordinatorLayout)findViewById(R.id.coordinator_layout),"Contact Updated Successfully",Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0 ){
            boolean lArePermissionsGranted = true;

            for(int lResult:grantResults){
                if(lResult== PackageManager.PERMISSION_DENIED){
                    lArePermissionsGranted = false;
                }
            }
            if(lArePermissionsGranted){
                loadDataFromTable();
            }
        }
    }
}
