package com.shangeeth.contactsclonev2;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shangeeth.contactsclonev2.db.ContactsDataTable;
import com.shangeeth.contactsclonev2.db.ContactsTable;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactJDO;
import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;
import com.shangeeth.contactsclonev2.util.ValidationHelper;

import java.util.ArrayList;

public class EditContactActivity extends AppCompatActivity {


    LinearLayout mLinearLayout;
    ArrayList<EditText> mEditTexts;
    ArrayList<String> mTypes;
    EditText mNameEdt;
    String mCurrentId;

    private FloatingActionButton mSaveFab;
    private ArrayList<SecondaryContactsJDO> mContactsJDOs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        Intent intent = getIntent();
        mContactsJDOs = (ArrayList<SecondaryContactsJDO>) intent.getSerializableExtra(getString(R.string.contact_data_jdos));
        mCurrentId = intent.getStringExtra(getString(R.string.id_extra));

        mLinearLayout = (LinearLayout) findViewById(R.id.container_layout);
        mSaveFab = (FloatingActionButton) findViewById(R.id.save_fab);
        mNameEdt = (EditText) findViewById(R.id.name_edt_txt);

        mEditTexts = new ArrayList<>();

        ((EditText) findViewById(R.id.name_edt_txt)).setText(intent.getStringExtra(getString(R.string.name)));

        for (SecondaryContactsJDO lSecondaryContactsJDO : mContactsJDOs) {

            if (lSecondaryContactsJDO.getData() != null) {

                View lView = getLayoutInflater().inflate(R.layout.field_view, null);

                EditText lEditText = (EditText) lView.findViewById(R.id.data_edt_text);
                if (lSecondaryContactsJDO.getType().equals(ContactsDataTable.Type.PHONE)) {
                    lEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                } else if (lSecondaryContactsJDO.getType().equals(ContactsDataTable.Type.EMAIL)) {
                    lEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                }

                lEditText.setText(lSecondaryContactsJDO.getData());
                ((TextView) lView.findViewById(R.id.label_tv)).setText(lSecondaryContactsJDO.getType());

                //Storing for data retrival while saving.
                mEditTexts.add(lEditText);

                mLinearLayout.addView(lView);
            }

        }


        mSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });


    }


    private void saveContact() {

        boolean lAllDataValid = true;
        PrimaryContactJDO lPrimaryContactJDO = new PrimaryContactJDO();
        lPrimaryContactJDO.setId(mCurrentId);
        lPrimaryContactJDO.setDisplayName(mNameEdt.getText().toString().trim());

        ArrayList<SecondaryContactsJDO> lUpdateJDOs = new ArrayList<SecondaryContactsJDO>();

        for (int i = 0; i < mContactsJDOs.size(); i++) {

            if (mContactsJDOs.get(i).getData() != null) {

                if (ContactsDataTable.Type.checkType(mContactsJDOs.get(i).getType())) {

                    if (mContactsJDOs.get(i).getType().equals(ContactsDataTable.Type.EMAIL)) {

                        if (!ValidationHelper.validateEmail(mEditTexts.get(i).getText().toString())) {
                            lAllDataValid = false;
                        }
                    }

                    SecondaryContactsJDO lJdo = new SecondaryContactsJDO();
                    lJdo.setId(mContactsJDOs.get(i).getId());
                    lJdo.setType(mContactsJDOs.get(i).getType());
                    lJdo.setData(mEditTexts.get(i).getText().toString());

                    lUpdateJDOs.add(lJdo);

                } else if (mContactsJDOs.get(i).getType().equals("Organization")) {
                    lPrimaryContactJDO.setOraganization(mEditTexts.get(i).getText().toString());

                } else if (mContactsJDOs.get(i).getType().equals("Note")) {
                    lPrimaryContactJDO.setNote(mEditTexts.get(i).getText().toString());
                }

            }


        }

        if(lAllDataValid) {
            ContactsTable lContactsTable = new ContactsTable(EditContactActivity.this);
            lContactsTable.updateRow(lPrimaryContactJDO);

            ContactsDataTable dataTable = new ContactsDataTable(EditContactActivity.this);
            dataTable.updateRows(lUpdateJDOs);

            Intent intent = new Intent();
            intent.putExtra(getString(R.string.id_extra), mCurrentId);
            setResult(1, intent);
            finish();
        }
        else {
            Toast.makeText(this, "Invalid data entered", Toast.LENGTH_SHORT).show();
        }

    }


}
