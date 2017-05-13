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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shangeeth.contactsclonev2.R;
import com.shangeeth.contactsclonev2.adapters.ContactsListAdapter;
import com.shangeeth.contactsclonev2.db.ContactsTable;
import com.shangeeth.contactsclonev2.helper.LoadContactsFromContentProvider;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactJDO;
import com.shangeeth.contactsclonev2.listeners.CustomOnItemTouchListener;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {


    RecyclerView mRecyclerView;
    private SharedPreferences mSharedPreferences;
    ContactsListAdapter mRecyclerViewAdapter;
    private ArrayList<PrimaryContactJDO> mContactListJDO;
    private FloatingActionButton maddContactFab;
    public static final int REQUEST_CODE = 100;

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

    /**
     * Initializing the required variables
     */
    private void init() {
        maddContactFab = (FloatingActionButton) findViewById(R.id.add_contact_fab);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_home);

        LinearLayoutManager lLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(lLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), lLayoutManager.getOrientation()));

        ItemTouchHelper lItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                mRecyclerViewAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d("ContactListAdapter", "onSwiped: "+direction+" Left:"+ItemTouchHelper.ANIMATION_TYPE_SWIPE_CANCEL);
                mRecyclerViewAdapter.onItemRemoved(viewHolder.getAdapterPosition());
            }
        });

        lItemTouchHelper.attachToRecyclerView(mRecyclerView);
//
//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                startActivityForResult(new Intent(ContactListActivity.this, DetailActivity.class).putExtra(getString(R.string.id_extra), mContactListJDO.get(position).getId()), 0);
//                overridePendingTransition(R.anim.from_right,R.anim.to_left);
//            }
//        }));


        mRecyclerView.addOnItemTouchListener(new CustomOnItemTouchListener(this, new CustomOnItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick() {
                Toast.makeText(ContactListActivity.this, "Long press ocured", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    /**
     * Adding onClick Listeners
     */
    public void setOnClickListeners() {

        maddContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ContactListActivity.this, AddOrEditActivity.class)
                        .putExtra(getString(R.string.request_code),REQUEST_CODE),REQUEST_CODE);
                overridePendingTransition(R.anim.from_bottom,R.anim.to_up);

            }
        });

    }


    /**
     * Loading list of Contacts from the local Sqlite Database using the ContactsTable instance
     */
    public void loadContacts() {

        ContactsTable lTable = new ContactsTable(this);

        mContactListJDO = lTable.getContactsForList();
        mRecyclerViewAdapter = new ContactsListAdapter(this, mContactListJDO);

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

    /**
     * If the app is opened for the first time check runtime permissions and load contacts from the content provider using the AsyncTask class
     */
    public void loadContactsFromContentProvider() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);

        } else {
            LoadContactsInBackground lLoadContactsInBackground = new LoadContactsInBackground();
            lLoadContactsInBackground.execute();
        }
    }

    /**
     * Loading all contacts in background
     */
    private class LoadContactsInBackground extends AsyncTask<Void, Void, Void> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ContactListActivity.this);
            mProgressDialog.setTitle("Please Wait");
            mProgressDialog.setMessage("Loading Contacts");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //Store contacts in sqlite from content provider
            LoadContactsFromContentProvider loadContactsFromContentProvider = new LoadContactsFromContentProvider();
            loadContactsFromContentProvider.loadContactsTable(ContactListActivity.this);
            loadContactsFromContentProvider.loadContactsDataTable(ContactListActivity.this);

            //Writing boolean areContactsLoaded to shared preference
            SharedPreferences.Editor lEditor = mSharedPreferences.edit();
            lEditor.putBoolean(getString(R.string.are_contacts_loaded), true);
            lEditor.apply();

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
                mRecyclerViewAdapter = new ContactsListAdapter(this, mContactListJDO);
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                if(data.getBooleanExtra(getString(R.string.contact_added),false)){
                    Snackbar.make(mRecyclerView,"Contact Added Successfully",Snackbar.LENGTH_SHORT).show();
                }
                if(data.getBooleanExtra(getString(R.string.contact_deleted),false)){
                    Snackbar.make(mRecyclerView,"Contact Deleted Successfully",Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }
}
