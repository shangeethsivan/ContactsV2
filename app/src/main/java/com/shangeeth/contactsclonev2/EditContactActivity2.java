package com.shangeeth.contactsclonev2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shangeeth.contactsclonev2.db.ContactsDataTable;
import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;

import java.util.ArrayList;

public class EditContactActivity2 extends AppCompatActivity {

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
    ArrayList<View> mPhoneViews;

    ArrayList<EditText> mEmailEditTexts;
    ArrayList<View> mEmailViews;

    ArrayList<EditText> mWebsiteEditTexts;
    ArrayList<View> mWebsiteViews;

    ArrayList<EditText> mImEditTexts;
    ArrayList<View> mImViews;

    ArrayList<EditText> mAddressEditTexts;
    ArrayList<View> mAddressViews;

    private LayoutInflater mInflater;
    private ArrayList<SecondaryContactsJDO> mContactsJDOs;
    private String mCurrentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact2);


        Intent intent = getIntent();
        mContactsJDOs = (ArrayList<SecondaryContactsJDO>) intent.getSerializableExtra(getString(R.string.contact_data_jdos));
        mCurrentId = intent.getStringExtra(getString(R.string.id_extra));

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


        setOnClickListeners();


        for (SecondaryContactsJDO lSecondaryContactsJDO : mContactsJDOs) {

            String type = lSecondaryContactsJDO.getType();

            if (ContactsDataTable.Type.checkType(type)) {

                if (type.equalsIgnoreCase(ContactsDataTable.Type.PHONE)) {

                    addView(mLinearLayoutPhone, mPhoneEditTexts, mPhoneViews, InputType.TYPE_CLASS_PHONE, "");

                }
            }
        }

    }


    public void setOnClickListeners() {

        mPhoneAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutPhone, mPhoneEditTexts, mPhoneViews, InputType.TYPE_CLASS_PHONE, "");

            }
        });
        mEmailAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutEmail, mEmailEditTexts, mEmailViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "");

            }
        });
        mWebsiteAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addView(mLinearLayoutWebsite, mWebsiteEditTexts, mWebsiteViews, InputType.TYPE_CLASS_TEXT, "");


            }
        });
        mImAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutIM, mImEditTexts, mImViews, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "");


            }
        });
        mAddressAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addView(mLinearLayoutAddress, mAddressEditTexts, mAddressViews, InputType.TYPE_CLASS_TEXT, "");

            }
        });

    }

    public void removeLastViewInPhone(ArrayList<View> pView, LinearLayout pLayout) {

        pLayout.removeView(pView.get(pView.size() - 1));
        pView.remove(pView.get(pView.size() - 1));

    }

    public void setLastViewWithDeleteButton(ArrayList<View> pViews) {

        for (View lView : pViews) {
            ((ImageView) lView.findViewById(R.id.delete_iv)).setVisibility(View.INVISIBLE);
        }
        if (pViews.size() >= 1) {
            ((ImageView) pViews.get(pViews.size() - 1).findViewById(R.id.delete_iv)).setVisibility(View.VISIBLE);
        }

    }


    /**
     * Create a view and add the View to pLinearLayout -- Not USED for now
     *
     * @param pLinearLayout the layout to add the view
     * @param pEditTexts    arrayList of edittexts
     * @param pViews        arraylist of views
     * @param pInputType    the type of
     */
    public void addView(final LinearLayout pLinearLayout, ArrayList<EditText> pEditTexts, final ArrayList<View> pViews, int pInputType, String pData) {

        View lView = mInflater.inflate(R.layout.dynamic_item, pLinearLayout, false);
        EditText editText = (EditText) lView.findViewById(R.id.data_edt);
        editText.setText(pData);
        editText.setInputType(pInputType);
        pEditTexts.add(editText);
        pViews.add(lView);

        ((ImageView) lView.findViewById(R.id.delete_iv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastViewInPhone(pViews, pLinearLayout);
                setLastViewWithDeleteButton(pViews);
            }
        });

        setLastViewWithDeleteButton(pViews);

        pLinearLayout.addView(lView);
    }
}
