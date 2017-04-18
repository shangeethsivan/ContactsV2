package com.shangeeth.contactsclonev2;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;

import java.util.ArrayList;

public class EditContactActivity extends AppCompatActivity {


    LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        Intent intent = getIntent();
        ArrayList<SecondaryContactsJDO> contactsJDOs = (ArrayList<SecondaryContactsJDO>) intent.getSerializableExtra(getString(R.string.contact_data_jdos));
        mLinearLayout = (LinearLayout) findViewById(R.id.container_layout);

        ((EditText)findViewById(R.id.name_edt_txt)).setText(intent.getStringExtra(getString(R.string.name)));

        for(SecondaryContactsJDO lSecondaryContactsJDO:contactsJDOs){

            if(lSecondaryContactsJDO.getData()!=null) {
                View view = getLayoutInflater().inflate(R.layout.field_view, null);

                ((EditText) view.findViewById(R.id.data_edt_text)).setText(lSecondaryContactsJDO.getData());
                ((TextView) view.findViewById(R.id.label_tv)).setText(lSecondaryContactsJDO.getType());

                mLinearLayout.addView(view);
            }

        }




    }
}
