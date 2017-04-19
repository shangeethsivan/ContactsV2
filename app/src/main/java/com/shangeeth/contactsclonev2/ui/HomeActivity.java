package com.shangeeth.contactsclonev2.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shangeeth.contactsclonev2.R;
import com.shangeeth.contactsclonev2.adapters.HomeActivityCustomRecyclerViewAdapter;
import com.shangeeth.contactsclonev2.db.ContactsTable;
import com.shangeeth.contactsclonev2.helper.LoadContactsFromContentProvider;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactJDO;
import com.shangeeth.contactsclonev2.util.RecyclerItemClickListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {


    RecyclerView mRecyclerView;
    private SharedPreferences mSharedPreferences;
    HomeActivityCustomRecyclerViewAdapter mRecyclerViewAdapter;
    private ArrayList<PrimaryContactJDO> mContactListJDO;
    private FloatingActionButton maddContactFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        setOnClickListeners();

        mSharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        boolean lAreContactsLoaded = mSharedPreferences.getBoolean(getString(R.string.are_contacts_loaded), false);

        if (lAreContactsLoaded) {

            loadContacts();

        } else {
            loadContactsFromContentProvider();
        }
    }

    private void init() {
        maddContactFab = (FloatingActionButton) findViewById(R.id.add_contact_fab);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_home);

        LinearLayoutManager lLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(lLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), lLayoutManager.getOrientation()));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                startActivityForResult(new Intent(HomeActivity.this, DetailActivity.class).putExtra(getString(R.string.id_extra), mContactListJDO.get(position).getId()), 0);

            }
        }));
    }

    public void setOnClickListeners() {

        maddContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mRecyclerView, "Add Contact", Snackbar.LENGTH_SHORT).show();
            }
        });

    }


    public void loadContacts() {

        ContactsTable lTable = new ContactsTable(this);

        mContactListJDO = lTable.getContactsForList();
        mRecyclerViewAdapter = new HomeActivityCustomRecyclerViewAdapter(this, mContactListJDO);

        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            boolean lPermissionGranted = true;
            for (int lResult : grantResults) {
                if (lResult == PackageManager.PERMISSION_DENIED) {
                    lPermissionGranted = false;
                }
            }
            if (lPermissionGranted) {
                loadContactsFromContentProvider();
            } else {
                finish();
            }
        }
    }

    public void loadContactsFromContentProvider() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);

        } else {
            LoadContactsInBackground lLoadContactsInBackground = new LoadContactsInBackground();
            lLoadContactsInBackground.execute();
        }
    }

    public class LoadContactsInBackground extends AsyncTask<Void, Void, Void> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(HomeActivity.this);
            mProgressDialog.setTitle("Please Wait");
            mProgressDialog.setMessage("Loading Contacts");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //Store contacts in sqlite from content provider
            LoadContactsFromContentProvider loadContactsFromContentProvider = new LoadContactsFromContentProvider();
            loadContactsFromContentProvider.loadContacts(HomeActivity.this);
            loadContactsFromContentProvider.loadDataForSecondaryTable(HomeActivity.this);

            //Writing boolean areContactsLoaded to shared preference
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(getString(R.string.are_contacts_loaded), true);
            editor.apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadContacts();
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 0) {

            if (data != null && data.getBooleanExtra(getString(R.string.is_data_updated), false)) {

                mContactListJDO.clear();

                loadContacts();
                mRecyclerViewAdapter = new HomeActivityCustomRecyclerViewAdapter(this, mContactListJDO);
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
            }
        }
    }
}
