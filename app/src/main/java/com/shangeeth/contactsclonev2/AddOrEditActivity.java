package com.shangeeth.contactsclonev2;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shangeeth.contactsclonev2.db.ContactsDataTable;
import com.shangeeth.contactsclonev2.db.ContactsTable;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactJDO;
import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;
import com.shangeeth.contactsclonev2.util.Util;

import java.util.ArrayList;

public class AddOrEditActivity extends AppCompatActivity {

    LinearLayout mLinearLayoutPhone;
    LinearLayout mLinearLayoutEmail;
    LinearLayout mLinearLayoutWebsite;
    LinearLayout mLinearLayoutIM;
    LinearLayout mLinearLayoutAddress;

    EditText mNameEDT;
    EditText mNoteEDT;
    EditText mOrgEDT;

    ImageView mPhoneAddIV;
    ImageView mEmailAddIV;
    ImageView mWebsiteAddIV;
    ImageView mImAddIV;
    ImageView mAddressAddIV;

    ArrayList<EditText> mPhoneEditTexts;
    ArrayList<EditTextAndIdJDO> mPhoneEditTextJDO;
    ArrayList<View> mPhoneViews;

    ArrayList<EditText> mEmailEditTexts;
    ArrayList<View> mEmailViews;
    ArrayList<EditTextAndIdJDO> mEmailEditTextAndIdJDOs;


    ArrayList<EditText> mWebsiteEditTexts;
    ArrayList<View> mWebsiteViews;
    ArrayList<EditTextAndIdJDO> mWebsiteEditTextAndIdJDOs;

    ArrayList<EditText> mImEditTexts;
    ArrayList<View> mImViews;
    ArrayList<EditTextAndIdJDO> mImEditTextAndIdJDOs;

    ArrayList<EditText> mAddressEditTexts;
    ArrayList<View> mAddressViews;
    ArrayList<EditTextAndIdJDO> mAddressEditTextAndIdJDOs;

    private LayoutInflater mInflater;
    private ArrayList<SecondaryContactsJDO> mContactsJDOs;
    private String mCurrentId;

    private FloatingActionButton mFab;
    private String mContactName;

    ArrayList<String> mIdsTobeDeleted;

    private int mRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        mRequestCode = intent.getIntExtra(getString(R.string.request_code), 0);

        mInflater = getLayoutInflater();

        mLinearLayoutPhone = (LinearLayout) findViewById(R.id.phone_container);
        mLinearLayoutEmail = (LinearLayout) findViewById(R.id.email_container);
        mLinearLayoutWebsite = (LinearLayout) findViewById(R.id.wesite_container);
        mLinearLayoutIM = (LinearLayout) findViewById(R.id.im_container);
        mLinearLayoutAddress = (LinearLayout) findViewById(R.id.address_container);

        mPhoneAddIV = (ImageView) findViewById(R.id.add_number);
        mEmailAddIV = (ImageView) findViewById(R.id.add_email);
        mWebsiteAddIV = (ImageView) findViewById(R.id.add_website);
        mImAddIV = (ImageView) findViewById(R.id.add_im);
        mAddressAddIV = (ImageView) findViewById(R.id.add_address);

        mNameEDT = (EditText) findViewById(R.id.name_edt_txt);
        mNoteEDT = (EditText) findViewById(R.id.note_edt);
        mOrgEDT = (EditText) findViewById(R.id.organization_edt);


        mPhoneEditTextJDO = new ArrayList<>();
        mEmailEditTextAndIdJDOs = new ArrayList<>();
        mWebsiteEditTextAndIdJDOs = new ArrayList<>();
        mImEditTextAndIdJDOs = new ArrayList<>();
        mAddressEditTextAndIdJDOs = new ArrayList<>();


        mFab = (FloatingActionButton) findViewById(R.id.save_fab);

        mPhoneEditTexts = new ArrayList<>();
        mPhoneViews = new ArrayList<>();

        mEmailEditTexts = new ArrayList<>();
        mEmailViews = new ArrayList<>();

        mWebsiteEditTexts = new ArrayList<>();
        mWebsiteViews = new ArrayList<>();

        mImEditTexts = new ArrayList<>();
        mImViews = new ArrayList<>();

        mAddressEditTexts = new ArrayList<>();
        mAddressViews = new ArrayList<>();

        mIdsTobeDeleted = new ArrayList<>();

        setOnClickListeners();

        if (mRequestCode == 101) {

            mContactsJDOs = (ArrayList<SecondaryContactsJDO>) intent.getSerializableExtra(getString(R.string.contact_data_jdos));
            mCurrentId = intent.getStringExtra(getString(R.string.id_extra));
            mContactName = intent.getStringExtra(getString(R.string.name));
            loadAllDatas();

        } else {
            loadEmptyFields();
        }

    }


    public void loadAllDatas() {

        mNameEDT.setText(mContactName);

        for (SecondaryContactsJDO lSecondaryContactsJDO : mContactsJDOs) {

            String type = lSecondaryContactsJDO.getType();

            if (ContactsDataTable.Type.checkType(type)) {

                if (type.equalsIgnoreCase(ContactsDataTable.Type.PHONE)) {

                    addView(mLinearLayoutPhone, mPhoneEditTextJDO, mPhoneViews, InputType.TYPE_CLASS_PHONE, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId());

                } else if (type.equalsIgnoreCase(ContactsDataTable.Type.EMAIL)) {

                    addView(mLinearLayoutEmail, mEmailEditTextAndIdJDOs, mEmailViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId());

                } else if (type.equalsIgnoreCase(ContactsDataTable.Type.WEBSITE)) {

                    addView(mLinearLayoutWebsite, mWebsiteEditTextAndIdJDOs, mWebsiteViews, InputType.TYPE_CLASS_TEXT, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId());

                } else if (type.equalsIgnoreCase(ContactsDataTable.Type.IM)) {

                    addView(mLinearLayoutIM, mImEditTextAndIdJDOs, mImViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId());

                } else if (type.equalsIgnoreCase(ContactsDataTable.Type.ADDRESS)) {

                    addView(mLinearLayoutAddress, mAddressEditTextAndIdJDOs, mAddressViews, InputType.TYPE_CLASS_TEXT, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId());

                }
            } else if (type.equalsIgnoreCase("Note")) {
                mNoteEDT.setText(lSecondaryContactsJDO.getData());
            } else if (type.equalsIgnoreCase("Organization")) {
                mOrgEDT.setText(lSecondaryContactsJDO.getData());
            }
        }
    }

    public void loadEmptyFields() {

        addView(mLinearLayoutPhone, mPhoneEditTextJDO, mPhoneViews, InputType.TYPE_CLASS_PHONE, "", "-1");

        addView(mLinearLayoutEmail, mEmailEditTextAndIdJDOs, mEmailViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "", "-1");

        addView(mLinearLayoutWebsite, mWebsiteEditTextAndIdJDOs, mWebsiteViews, InputType.TYPE_CLASS_TEXT, "", "-1");

        addView(mLinearLayoutIM, mImEditTextAndIdJDOs, mImViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "", "-1");

        addView(mLinearLayoutAddress, mAddressEditTextAndIdJDOs, mAddressViews, InputType.TYPE_CLASS_TEXT, "", "-1");

    }

    public void setOnClickListeners() {

        mPhoneAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutPhone, mPhoneEditTextJDO, mPhoneViews, InputType.TYPE_CLASS_PHONE, "", "-1");

            }
        });
        mEmailAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutEmail, mEmailEditTextAndIdJDOs, mEmailViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "", "-1");

            }
        });
        mWebsiteAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addView(mLinearLayoutWebsite, mWebsiteEditTextAndIdJDOs, mWebsiteViews, InputType.TYPE_CLASS_TEXT, "", "-1");


            }
        });
        mImAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutIM, mImEditTextAndIdJDOs, mImViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "", "-1");


            }
        });
        mAddressAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutAddress, mAddressEditTextAndIdJDOs, mAddressViews, InputType.TYPE_CLASS_TEXT, "", "-1");

            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContacts();
            }
        });

    }

    private void saveContacts() {

        if (!mNameEDT.getText().toString().trim().equals("")) {
            // Update the First table
            PrimaryContactJDO lPrimaryContactJDO = new PrimaryContactJDO();
            lPrimaryContactJDO.setId(mCurrentId);
            lPrimaryContactJDO.setDisplayName(mNameEDT.getText().toString().trim());
            lPrimaryContactJDO.setNote(mNoteEDT.getText().toString());
            lPrimaryContactJDO.setOraganization(mOrgEDT.getText().toString());

            ContactsTable table = new ContactsTable(this);
            //If adding new Contacts add the contacts and update the Current ID.
            if (mCurrentId == null || mCurrentId.equals("")) {
                mCurrentId = String.valueOf(table.insertNewRow(lPrimaryContactJDO));
            } else {
                table.updateRow(lPrimaryContactJDO);
            }

            ContactsDataTable dataTable = new ContactsDataTable(this);


            //Delete if there is any id to be deleted

            if (mIdsTobeDeleted.size() > 0) {
                dataTable.deleteDataForIds(mIdsTobeDeleted);
            }

            //Get all the second table data

            ArrayList<SecondaryContactsJDO> dataTobeAdded = new ArrayList<>();

            for (EditTextAndIdJDO lEditTextAndIdJDO : mPhoneEditTextJDO) {

                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals("")) {
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.PHONE);
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    dataTobeAdded.add(lSecondaryContactsJDO);
                }
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mEmailEditTextAndIdJDOs) {

                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals("")) {
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.EMAIL);
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    dataTobeAdded.add(lSecondaryContactsJDO);
                }
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mWebsiteEditTextAndIdJDOs) {

                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals("")) {
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.WEBSITE);
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    dataTobeAdded.add(lSecondaryContactsJDO);
                }
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mImEditTextAndIdJDOs) {

                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals("")) {
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.IM);
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    dataTobeAdded.add(lSecondaryContactsJDO);
                }
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mAddressEditTextAndIdJDOs) {

                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals("")) {
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.ADDRESS);
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    dataTobeAdded.add(lSecondaryContactsJDO);
                }
            }

            //Inserting or Updating new Data Based on Ids
            dataTable.insertOrUpdateData(dataTobeAdded);



        /*
        Setting the intent to update the datas in the DetailActivity
         */

            if (mRequestCode == 101) {

                Intent intent = new Intent();
                intent.putExtra(getString(R.string.id_extra), mCurrentId);
                setResult(1, intent);
                finish();

            } else {

                Intent lIntent = new Intent();
                lIntent.putExtra(getString(R.string.is_data_updated),true);
                lIntent.putExtra(getString(R.string.data_updated),true);
                setResult(0, lIntent);
                finish();

            }
        } else {
            Snackbar.make((LinearLayout) findViewById(R.id.container_layout), "You have to enter name to save Contacts", Snackbar.LENGTH_SHORT).show();
        }


    }



    /**
     * @param pLinearLayout
     * @param pEditTextAndIdJDOs
     * @param pViews
     * @param pInputType
     * @param pData
     * @param pId
     */
    public void addView(final LinearLayout pLinearLayout, final ArrayList<EditTextAndIdJDO> pEditTextAndIdJDOs, final ArrayList<View> pViews, int pInputType, String pData, String pId) {

        View lView = mInflater.inflate(R.layout.dynamic_item, pLinearLayout, false);
        EditText editText = (EditText) lView.findViewById(R.id.data_edt);
        editText.setText(pData);
        editText.setInputType(pInputType);

        pEditTextAndIdJDOs.add(new EditTextAndIdJDO(editText, pId));

//        pEditTexts.add(editText);
        pViews.add(lView);

        ((ImageView) lView.findViewById(R.id.delete_iv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get the parent View and send it to remove method .

                removeView((View)v.getParent(),pViews,pLinearLayout,pEditTextAndIdJDOs);

            }
        });


        pLinearLayout.addView(lView);
    }

    private void removeView(View pView,ArrayList<View> pViews, LinearLayout pLinearLayout, ArrayList<EditTextAndIdJDO> pEditTextAndIdJDOs) {

        pLinearLayout.removeView(pView);
        int lIndex = Util.getItemIndex(pViews,pView);
        pViews.remove(pView);

        String lId = pEditTextAndIdJDOs.get(lIndex).getmId();
        if (!lId.equals("-1")) {
            mIdsTobeDeleted.add(lId);
        }

        pEditTextAndIdJDOs.remove(lIndex);

    }


    public class EditTextAndIdJDO {
        EditText mEditText;
        String mId;

        public EditTextAndIdJDO(EditText mEditText, String mId) {
            this.mEditText = mEditText;
            this.mId = mId;
        }

        public EditText getmEditText() {
            return mEditText;
        }

        public String getmId() {
            return mId;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
