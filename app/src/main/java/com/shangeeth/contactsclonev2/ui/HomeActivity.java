package com.shangeeth.contactsclonev2.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shangeeth.contactsclonev2.R;
import com.shangeeth.contactsclonev2.adapters.CustomRecyclerViewAdapter;
import com.shangeeth.contactsclonev2.db.ContactsDBHelper;
import com.shangeeth.contactsclonev2.db.ContactsPrimaryTable;
import com.shangeeth.contactsclonev2.helper.LoadContactsFromContentProvider;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactsJDO;
import com.shangeeth.contactsclonev2.util.RecyclerItemClickListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {


    RecyclerView mRecyclerView;
    private SharedPreferences mSharedPreferences;
    CustomRecyclerViewAdapter mRecyclerViewAdapter;
    private ArrayList<PrimaryContactsJDO> mContactListJDO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

        mSharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        boolean areContactsLoaded = mSharedPreferences.getBoolean(getString(R.string.are_contacts_loaded), false);

        if (areContactsLoaded) {

            loadContacts();

        } else {
            loadContactsFromContentProvider();
        }
    }

    private void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_home);
    }


    public void loadContacts() {

        SQLiteDatabase sqLiteDatabase = new ContactsDBHelper(this).getReadableDatabase();
        ContactsPrimaryTable table = new ContactsPrimaryTable();

        mContactListJDO = table.getContactsForList(sqLiteDatabase);
        mRecyclerViewAdapter = new CustomRecyclerViewAdapter(this, mContactListJDO);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                    startActivity(new Intent(HomeActivity.this, DetailActivity.class).putExtra(getString(R.string.id_extra), mContactListJDO.get(position).getId()));

            }
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            boolean permissionGranted = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    permissionGranted = false;
                }
            }
            if (permissionGranted) {
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
            LoadContactsInBackground loadContactsInBackground = new LoadContactsInBackground();
            loadContactsInBackground.execute();
        }
    }

    public class LoadContactsInBackground extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Loading Contacts");
            progressDialog.setCancelable(false);
            progressDialog.show();
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
            progressDialog.dismiss();
        }
    }

}
