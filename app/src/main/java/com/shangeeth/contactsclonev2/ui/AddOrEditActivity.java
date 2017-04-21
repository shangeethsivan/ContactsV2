package com.shangeeth.contactsclonev2.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shangeeth.contactsclonev2.R;
import com.shangeeth.contactsclonev2.db.ContactsDataTable;
import com.shangeeth.contactsclonev2.db.ContactsTable;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactJDO;
import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;
import com.shangeeth.contactsclonev2.util.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddOrEditActivity extends AppCompatActivity {

    private LinearLayout mLinearLayoutPhone;
    LinearLayout mLinearLayoutEmail;
    LinearLayout mLinearLayoutWebsite;
    LinearLayout mLinearLayoutIM;
    LinearLayout mLinearLayoutAddress;

    EditText mNameEDT;
    EditText mNoteEDT;
    EditText mOrgTitleEDT;
    EditText mOrgNameEDT;

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
    ArrayList<EditTextAndIdAddressJDO> mAddressEditTextAndIdJDOs;

    private LayoutInflater mInflater;
    private ArrayList<SecondaryContactsJDO> mContactsJDOs;
    private String mCurrentId;


    private String mContactName;

    private ArrayList<String> mIdsTobeDeleted;

    private int mRequestCode;


    private boolean mIsEditActivity = false;
    private String mUnEditedData = "";
    private int mUnEditedDataIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent lIntent = getIntent();

        mRequestCode = lIntent.getIntExtra(getString(R.string.request_code), 0);

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
        mOrgTitleEDT = (EditText) findViewById(R.id.org_title);
        mOrgNameEDT = (EditText) findViewById(R.id.org_name);


        mPhoneEditTextJDO = new ArrayList<>();
        mEmailEditTextAndIdJDOs = new ArrayList<>();
        mWebsiteEditTextAndIdJDOs = new ArrayList<>();
        mImEditTextAndIdJDOs = new ArrayList<>();
        mAddressEditTextAndIdJDOs = new ArrayList<>();


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

        ActionBar mActionBar = getSupportActionBar();
        if (mRequestCode == 101) {
            mActionBar.setTitle("Edit Contact");
            mIsEditActivity = true;
            mContactsJDOs = (ArrayList<SecondaryContactsJDO>) lIntent.getSerializableExtra(getString(R.string.contact_data_jdos));
            mCurrentId = lIntent.getStringExtra(getString(R.string.id_extra));
            mContactName = lIntent.getStringExtra(getString(R.string.name));
            loadAllDatas();

        } else {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            mActionBar.setTitle("Add Contact");
            loadEmptyFields();
        }

        mNameEDT.requestFocus();

    }


    public void loadAllDatas() {

        mNameEDT.setText(mContactName);

        addToUnEditedData(mContactName);

        for (SecondaryContactsJDO lSecondaryContactsJDO : mContactsJDOs) {

            if (!lSecondaryContactsJDO.getData().trim().equals("")) {
                addToUnEditedData(lSecondaryContactsJDO.getData());
            }
            String lType = lSecondaryContactsJDO.getType();

            if (ContactsDataTable.Type.checkType(lType)) {

                if (lType.equalsIgnoreCase(ContactsDataTable.Type.PHONE)) {

                    addView(mLinearLayoutPhone, mPhoneEditTextJDO, mPhoneViews, InputType.TYPE_CLASS_PHONE, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId(), ContactsDataTable.Type.PHONE);

                } else if (lType.equalsIgnoreCase(ContactsDataTable.Type.EMAIL)) {

                    addView(mLinearLayoutEmail, mEmailEditTextAndIdJDOs, mEmailViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId(), ContactsDataTable.Type.EMAIL);

                } else if (lType.equalsIgnoreCase(ContactsDataTable.Type.WEBSITE)) {

                    addView(mLinearLayoutWebsite, mWebsiteEditTextAndIdJDOs, mWebsiteViews, InputType.TYPE_CLASS_TEXT, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId(), ContactsDataTable.Type.WEBSITE);

                } else if (lType.equalsIgnoreCase(ContactsDataTable.Type.IM)) {

                    addView(mLinearLayoutIM, mImEditTextAndIdJDOs, mImViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId(), ContactsDataTable.Type.IM);

                } else if (lType.equalsIgnoreCase(ContactsDataTable.Type.ADDRESS)) {

                    addView(mLinearLayoutAddress, null, mAddressViews, InputType.TYPE_CLASS_TEXT, lSecondaryContactsJDO.getData(), lSecondaryContactsJDO.getId(), ContactsDataTable.Type.ADDRESS);

                }
            } else if (lType.equalsIgnoreCase("Note")) {
                mNoteEDT.setText(lSecondaryContactsJDO.getData());
            } else if (lType.equalsIgnoreCase("Organization")) {

                try {
                    JSONObject lJsonObject = new JSONObject(lSecondaryContactsJDO.getData());
                    mOrgTitleEDT.setText(lJsonObject.getString("Organisation Title"));
                    mOrgNameEDT.setText(lJsonObject.getString("Organisation Data"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addToUnEditedData(String pData) {
        try {
            JSONObject jsonObject;
            if (mUnEditedData.equals(""))
                jsonObject = new JSONObject();
            else
                jsonObject = new JSONObject(mUnEditedData);

            jsonObject.put(String.valueOf(mUnEditedDataIndex++), pData);
            mUnEditedData = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadEmptyFields() {

        addView(mLinearLayoutPhone, mPhoneEditTextJDO, mPhoneViews, InputType.TYPE_CLASS_PHONE, "", "newData", ContactsDataTable.Type.PHONE);

        addView(mLinearLayoutEmail, mEmailEditTextAndIdJDOs, mEmailViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "", "newData", ContactsDataTable.Type.EMAIL);

        addView(mLinearLayoutWebsite, mWebsiteEditTextAndIdJDOs, mWebsiteViews, InputType.TYPE_CLASS_TEXT, "", "newData", ContactsDataTable.Type.WEBSITE);

        addView(mLinearLayoutIM, mImEditTextAndIdJDOs, mImViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "", "newData", ContactsDataTable.Type.IM);

        addView(mLinearLayoutAddress, null, mAddressViews, InputType.TYPE_CLASS_TEXT, "", "newData", ContactsDataTable.Type.ADDRESS);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_or_edit_menu, menu);
        return true;
    }


    public void setOnClickListeners() {

        mPhoneAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutPhone, mPhoneEditTextJDO, mPhoneViews, InputType.TYPE_CLASS_PHONE, "", "newData", ContactsDataTable.Type.PHONE);

            }
        });
        mEmailAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutEmail, mEmailEditTextAndIdJDOs, mEmailViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "", "newData", ContactsDataTable.Type.EMAIL);

            }
        });
        mWebsiteAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addView(mLinearLayoutWebsite, mWebsiteEditTextAndIdJDOs, mWebsiteViews, InputType.TYPE_CLASS_TEXT, "", "newData", ContactsDataTable.Type.WEBSITE);


            }
        });
        mImAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutIM, mImEditTextAndIdJDOs, mImViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "", "newData", ContactsDataTable.Type.IM);


            }
        });
        mAddressAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutAddress, null, mAddressViews, InputType.TYPE_CLASS_TEXT, "", "newData", ContactsDataTable.Type.ADDRESS);

            }
        });

    }

    private void saveContacts() {

        if (!mNameEDT.getText().toString().trim().equals("") && !areAllFieldsEmpty()) {
            // Update the First table
            PrimaryContactJDO lPrimaryContactJDO = new PrimaryContactJDO();
            lPrimaryContactJDO.setId(mCurrentId);
            lPrimaryContactJDO.setDisplayName(mNameEDT.getText().toString().trim());
            lPrimaryContactJDO.setNote(mNoteEDT.getText().toString());
            if (!mOrgNameEDT.getText().toString().trim().equals("") && !mOrgTitleEDT.getText().toString().trim().equals("")) {
                JSONObject lJsonObjectOrg = new JSONObject();
                try {
                    lJsonObjectOrg.put("Organisation Data", mOrgNameEDT.getText().toString().trim());
                    lJsonObjectOrg.put("Organisation Title", mOrgTitleEDT.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lPrimaryContactJDO.setOrganization(lJsonObjectOrg.toString());
            }
            ContactsTable lTable = new ContactsTable(this);
            //If adding new Contacts add the contacts and update the Current ID.
            if (mCurrentId == null || mCurrentId.equals("")) {
                mCurrentId = String.valueOf(lTable.insertNewRow(lPrimaryContactJDO));
            } else {
                lTable.updateRow(lPrimaryContactJDO);
            }

            ContactsDataTable lDataTable = new ContactsDataTable(this);


            //Delete if there is any id to be deleted
            //Delete if there is any id to be deleted

            if (mIdsTobeDeleted.size() > 0) {
                lDataTable.deleteDataForIds(mIdsTobeDeleted);
            }

            //Get all the second table data

            ArrayList<SecondaryContactsJDO> lDataToBeAdded = new ArrayList<>();

            for (EditTextAndIdJDO lEditTextAndIdJDO : mPhoneEditTextJDO) {

                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals("")) {
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.PHONE);
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    lDataToBeAdded.add(lSecondaryContactsJDO);
                }
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mEmailEditTextAndIdJDOs) {

                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals("")) {
                    if (!CommonUtil.validateEmail(lEditTextAndIdJDO.getmEditText().getText().toString().trim())) {
                        Snackbar.make((LinearLayout) findViewById(R.id.container_layout), "Email Id Should be valid", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.EMAIL);
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    lDataToBeAdded.add(lSecondaryContactsJDO);
                }
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mWebsiteEditTextAndIdJDOs) {

                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals("")) {
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.WEBSITE);
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    lDataToBeAdded.add(lSecondaryContactsJDO);
                }
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mImEditTextAndIdJDOs) {

                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals("")) {
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.IM);
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    lDataToBeAdded.add(lSecondaryContactsJDO);
                }
            }
            for (EditTextAndIdAddressJDO lEditTextAndIdJDO : mAddressEditTextAndIdJDOs) {

                if (!lEditTextAndIdJDO.getmStateEdt().getText().toString().equals("")) {
                    SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                    lSecondaryContactsJDO.setType(ContactsDataTable.Type.ADDRESS);
                    lSecondaryContactsJDO.setId(lEditTextAndIdJDO.getmId());
                    lSecondaryContactsJDO.setContactId(mCurrentId);
                    JSONObject lJsonObjectAddress = new JSONObject();

                    //{"STREET":"334","CITY":"Fggg","REGION":"Tn","POSTCODE":"666005","COUNTRY":"In"}
                    try {
                        lJsonObjectAddress.put("STREET", lEditTextAndIdJDO.getmStreetEdt().getText().toString().trim());
                        lJsonObjectAddress.put("CITY", lEditTextAndIdJDO.getmCityEdt().getText().toString().trim());
                        lJsonObjectAddress.put("REGION", lEditTextAndIdJDO.getmStateEdt().getText().toString().trim());
                        lJsonObjectAddress.put("POSTCODE", lEditTextAndIdJDO.getmPicodeEdt().getText().toString().trim());
                        lJsonObjectAddress.put("COUNTRY", lEditTextAndIdJDO.getmCountryEdt().getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    lSecondaryContactsJDO.setData(lJsonObjectAddress.toString());

//                    lSecondaryContactsJDO.setData(lEditTextAndIdJDO.getmEditText().getText().toString().trim());
                    lDataToBeAdded.add(lSecondaryContactsJDO);
                }
            }


            //Inserting or Updating new Data Based on Ids
            lDataTable.insertOrUpdateData(lDataToBeAdded);


        /*
        Setting the intent to update the datas in the DetailActivity
         */

            if (mRequestCode == 101) {

                Intent lIntent = new Intent();
                lIntent.putExtra(getString(R.string.id_extra), mCurrentId);
                setResult(1, lIntent);
                finish();
                overridePendingTransition(R.anim.from_left, R.anim.to_right);

            } else {

                Intent lIntent = new Intent();
                lIntent.putExtra(getString(R.string.is_data_updated), true);
                lIntent.putExtra(getString(R.string.contact_added), true);
                setResult(0, lIntent);
                finish();
                overridePendingTransition(R.anim.from_up, R.anim.to_bottom);


            }
        } else {
            Snackbar.make((LinearLayout) findViewById(R.id.container_layout), "You have to enter Name and a Field to save the Contacts", Snackbar.LENGTH_SHORT).show();
        }


    }


    public boolean areAllFieldsEmpty() {

        for (EditTextAndIdJDO lEditTextAndIdJDO : mPhoneEditTextJDO) {

            if (!lEditTextAndIdJDO.getmEditText().getText().toString().trim().equals("")) {
                return false;
            }

        }

        for (EditTextAndIdJDO lEditTextAndIdJDO : mEmailEditTextAndIdJDOs) {

            if (!lEditTextAndIdJDO.getmEditText().getText().toString().trim().equals("")) {
                return false;
            }

        }

        for (EditTextAndIdJDO lEditTextAndIdJDO : mWebsiteEditTextAndIdJDOs) {

            if (!lEditTextAndIdJDO.getmEditText().getText().toString().trim().equals("")) {
                return false;
            }

        }

        for (EditTextAndIdJDO lEditTextAndIdJDO : mImEditTextAndIdJDOs) {

            if (!lEditTextAndIdJDO.getmEditText().getText().toString().trim().equals("")) {
                return false;
            }

        }

        for (EditTextAndIdAddressJDO lEditTextAndIdJDO : mAddressEditTextAndIdJDOs) {

            if (!lEditTextAndIdJDO.getmStreetEdt().getText().toString().trim().equals("") || !lEditTextAndIdJDO.getmCityEdt().getText().toString().trim().equals("")
                    || !lEditTextAndIdJDO.getmCityEdt().getText().toString().trim().equals("") || !lEditTextAndIdJDO.getmCountryEdt().getText().toString().trim().equals("")
                    || !lEditTextAndIdJDO.getmPicodeEdt().getText().toString().trim().equals("")) {
                return false;
            }


        }


        if (!mNoteEDT.getText().toString().trim().equals("")) {
            return false;
        } else if (!mOrgTitleEDT.getText().toString().trim().equals("") || !mOrgNameEDT.getText().toString().trim().equals("")) {
            return false;
        }

        return true;
    }

    /**
     * @param pLinearLayout
     * @param pEditTextAndIdJDOs
     * @param pViews
     * @param pInputType
     * @param pData
     * @param pId
     */
    public void addView(final LinearLayout pLinearLayout, final ArrayList<EditTextAndIdJDO> pEditTextAndIdJDOs, final ArrayList<View> pViews, int pInputType, String pData, String pId, final String pType) {

        // If the data is normal leave it else handle them separately


        if (pType.equals(ContactsDataTable.Type.ADDRESS)) {
            View lView = mInflater.inflate(R.layout.dynamic_item_address, pLinearLayout, false);
            EditText lStreetEdt = (EditText) lView.findViewById(R.id.street_edt);
            EditText lCityEdt = (EditText) lView.findViewById(R.id.city_edt);
            EditText lPincodeEdt = (EditText) lView.findViewById(R.id.zipcode_edt);
            EditText lStateEdt = (EditText) lView.findViewById(R.id.state_edt);
            EditText lCountryEdt = (EditText) lView.findViewById(R.id.country_edt);
            lPincodeEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
            lPincodeEdt.setMaxEms(7);


            try {
                JSONObject lObject = new JSONObject(pData);

                lStreetEdt.setText(lObject.getString("STREET"));
                lCityEdt.setText(lObject.getString("CITY"));
                lStateEdt.setText(lObject.getString("REGION"));
                lPincodeEdt.setText(lObject.getString("POSTCODE"));
                lCountryEdt.setText(lObject.getString("COUNTRY"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            lStreetEdt.requestFocus();
            mAddressEditTextAndIdJDOs.add(new EditTextAndIdAddressJDO(lStreetEdt, lCityEdt, lStateEdt, lPincodeEdt, lCountryEdt, pId));
            pViews.add(lView);


            ((ImageView) lView.findViewById(R.id.delete_iv)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Get the parent View and send it to remove method .
                    removeView((View) v.getParent(), pViews, pLinearLayout, pEditTextAndIdJDOs, pType);

                }
            });

            pLinearLayout.addView(lView);


        } else {

            View lView = mInflater.inflate(R.layout.dynamic_item, pLinearLayout, false);
            EditText lEditText = (EditText) lView.findViewById(R.id.data_edt);
            lEditText.setText(pData);
            lEditText.setInputType(pInputType);

            if (pType.equals(ContactsDataTable.Type.PHONE)) {
                lEditText.setMaxEms(12);
            }

            lEditText.setHint(pType);
            lEditText.requestFocus();
            pEditTextAndIdJDOs.add(new EditTextAndIdJDO(lEditText, pId));
            pViews.add(lView);

            ((ImageView) lView.findViewById(R.id.delete_iv)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Get the parent View and send it to remove method .
                    removeView((View) v.getParent(), pViews, pLinearLayout, pEditTextAndIdJDOs, pType);

                }
            });
            pLinearLayout.addView(lView);
        }

    }

    private void removeView(View pView, ArrayList<View> pViews, LinearLayout pLinearLayout, ArrayList<EditTextAndIdJDO> pEditTextAndIdJDOs, String pType) {

        pLinearLayout.removeView(pView);
        int lIndex = CommonUtil.getItemIndex(pViews, pView);
        pViews.remove(pView);
        String lId;
        if (pType.equals(ContactsDataTable.Type.ADDRESS)) {
            lId = mAddressEditTextAndIdJDOs.get(lIndex).getmId();
            mAddressEditTextAndIdJDOs.remove(lIndex);
        } else {
            lId = pEditTextAndIdJDOs.get(lIndex).getmId();
            pEditTextAndIdJDOs.remove(lIndex);
        }
        if (!lId.equals("-1")) {
            mIdsTobeDeleted.add(lId);
        }


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

    public class EditTextAndIdAddressJDO {
        EditText mStreetEdt;
        EditText mCityEdt;
        EditText mStateEdt;
        EditText mPicodeEdt;
        EditText mCountryEdt;
        String mId;

        public EditTextAndIdAddressJDO(EditText mStreetEdt, EditText mCityEdt, EditText mStateEdt, EditText mPicodeEdt, EditText mCountryEdt, String mId) {
            this.mStreetEdt = mStreetEdt;
            this.mCityEdt = mCityEdt;
            this.mStateEdt = mStateEdt;
            this.mPicodeEdt = mPicodeEdt;
            this.mCountryEdt = mCountryEdt;
            this.mId = mId;
        }

        public EditText getmStreetEdt() {
            return mStreetEdt;
        }

        public EditText getmCityEdt() {
            return mCityEdt;
        }

        public EditText getmStateEdt() {
            return mStateEdt;
        }

        public EditText getmPicodeEdt() {
            return mPicodeEdt;
        }

        public EditText getmCountryEdt() {
            return mCountryEdt;
        }

        public String getmId() {
            return mId;
        }
    }

    @Override
    public void onBackPressed() {

        //Create a new Edited Data and compare with the unedited data
        if (isNewDataSame()) {
            finish();
            if (!mIsEditActivity)
                overridePendingTransition(R.anim.from_up, R.anim.to_bottom);
            else
                overridePendingTransition(R.anim.from_left, R.anim.to_right);
        } else {
            AlertDialog.Builder lBuilder = new AlertDialog.Builder(this);
            lBuilder.setTitle("Discard Changes");
            lBuilder.setMessage("Do you want to discard the changes you made");
            lBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!mIsEditActivity) {
                        finish();
                        overridePendingTransition(R.anim.from_up, R.anim.to_bottom);
                    } else {
                        finish();
                        overridePendingTransition(R.anim.from_left, R.anim.to_right);
                    }
                }
            });
            lBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            lBuilder.show();
        }

    }

    private boolean isNewDataSame() {

        int lTempIndex = 1;
        JSONObject lJsonObject = new JSONObject();
        try {
            if (!mNameEDT.getText().toString().trim().equals(""))
                lJsonObject.put("0", mNameEDT.getText().toString().trim());
            for (EditTextAndIdJDO lEditTextAndIdJDO : mPhoneEditTextJDO) {
                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals(""))
                    lJsonObject.put(String.valueOf(lTempIndex++), lEditTextAndIdJDO.getmEditText().getText().toString().trim());
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mEmailEditTextAndIdJDOs) {
                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals(""))
                    lJsonObject.put(String.valueOf(lTempIndex++), lEditTextAndIdJDO.getmEditText().getText().toString().trim());
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mWebsiteEditTextAndIdJDOs) {
                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals(""))
                    lJsonObject.put(String.valueOf(lTempIndex++), lEditTextAndIdJDO.getmEditText().getText().toString().trim());
            }
            for (EditTextAndIdJDO lEditTextAndIdJDO : mImEditTextAndIdJDOs) {
                if (!lEditTextAndIdJDO.getmEditText().getText().toString().equals(""))
                    lJsonObject.put(String.valueOf(lTempIndex++), lEditTextAndIdJDO.getmEditText().getText().toString().trim());
            }
            for (EditTextAndIdAddressJDO lEditTextAndIdJDO : mAddressEditTextAndIdJDOs) {

                String lTempString = lEditTextAndIdJDO.getmStreetEdt().getText().toString().trim()
                        + lEditTextAndIdJDO.getmCityEdt().getText().toString().trim()
                        + lEditTextAndIdJDO.getmStateEdt().getText().toString().trim()
                        + lEditTextAndIdJDO.getmPicodeEdt().getText().toString().trim()
                        + lEditTextAndIdJDO.getmCountryEdt().getText().toString().trim();

                if (!lTempString.equals("")) {
                    JSONObject lAddressJsonObject = new JSONObject();
                    //{"STREET":"334","CITY":"Fggg","REGION":"Tn","POSTCODE":"666005","COUNTRY":"In"}
                    lAddressJsonObject.put("STREET", lEditTextAndIdJDO.getmStreetEdt().getText().toString().trim());
                    lAddressJsonObject.put("CITY", lEditTextAndIdJDO.getmCityEdt().getText().toString().trim());
                    lAddressJsonObject.put("REGION", lEditTextAndIdJDO.getmStateEdt().getText().toString().trim());
                    lAddressJsonObject.put("POSTCODE", lEditTextAndIdJDO.getmPicodeEdt().getText().toString().trim());
                    lAddressJsonObject.put("COUNTRY", lEditTextAndIdJDO.getmCountryEdt().getText().toString().trim());

                    lJsonObject.put(String.valueOf(lTempIndex++), lAddressJsonObject.toString());
                }
            }

            if (!mNoteEDT.getText().toString().trim().equals(""))
                lJsonObject.put(String.valueOf(lTempIndex++), mNoteEDT.getText().toString().trim());

            if (!mOrgNameEDT.getText().toString().trim().equals("") || !mOrgTitleEDT.getText().toString().trim().equals("")) {
                JSONObject lOrgJsonObject = new JSONObject();
                lOrgJsonObject.put("Organisation Data", mOrgNameEDT.getText().toString().trim());
                lOrgJsonObject.put("Organisation Title", mOrgTitleEDT.getText().toString().trim());
                lJsonObject.put(String.valueOf(lTempIndex++), lOrgJsonObject.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (lJsonObject.toString().equals(mUnEditedData) || lJsonObject.toString().equals("{}")) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.save_menu:
                saveContacts();
                break;
        }
        return true;
    }
}
