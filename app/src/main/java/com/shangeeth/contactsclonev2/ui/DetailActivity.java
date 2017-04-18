package com.shangeeth.contactsclonev2.ui;

import android.content.Intent;
import android.media.Image;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.shangeeth.contactsclonev2.EditContactActivity;
import com.shangeeth.contactsclonev2.R;
import com.shangeeth.contactsclonev2.adapters.DetailActivityCustomRecylerViewAdapter;
import com.shangeeth.contactsclonev2.db.ContactsDataTable;
import com.shangeeth.contactsclonev2.db.ContactsTable;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactJDO;
import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ImageView mProfilePicIV;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mToolbarLayout;
    private PrimaryContactJDO mContactsJDO;
    private ContactsTable mContactsTable;
    private RecyclerView mRecyclerView;
    private ArrayList<SecondaryContactsJDO> mContactsDataJDOs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String lId = getIntent().getStringExtra(getString(R.string.id_extra));

        init();

        mContactsJDO = mContactsTable.getContactForId(lId);

        ContactsDataTable lDataTable = new ContactsDataTable(this);

        mContactsDataJDOs = lDataTable.getDatasForId(lId);

        SecondaryContactsJDO noteJDO = new SecondaryContactsJDO();
        noteJDO.setContactId(lId);
        noteJDO.setData(mContactsJDO.getNote());
        noteJDO.setType("Note");
        mContactsDataJDOs.add(noteJDO);

        SecondaryContactsJDO organizationJDO = new SecondaryContactsJDO();
        organizationJDO.setContactId(lId);
        organizationJDO.setData(mContactsJDO.getOraganization());
        organizationJDO.setType("Organization");

        mContactsDataJDOs.add(organizationJDO);

        mRecyclerView.setAdapter(new DetailActivityCustomRecylerViewAdapter(this, mContactsDataJDOs));

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_UP){
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        mToolbarLayout.setTitle(mContactsJDO.getDisplayName());
        Picasso.with(this)
                .load(mContactsJDO.getPhotoUri())
                .placeholder(R.drawable.contact_bg)
                .into(mProfilePicIV);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_contact:
                Snackbar.make(mRecyclerView, "Edit Clicked", Snackbar.LENGTH_SHORT).show();

                Intent lIntent = new Intent(this, EditContactActivity.class);
                lIntent.putExtra(getString(R.string.contact_data_jdos), mContactsDataJDOs);
                lIntent.putExtra(getString(R.string.name),mContactsJDO.getDisplayName());

                startActivity(lIntent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
