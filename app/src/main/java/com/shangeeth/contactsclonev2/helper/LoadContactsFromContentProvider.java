package com.shangeeth.contactsclonev2.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.shangeeth.contactsclonev2.db.ContactsDataTable;
import com.shangeeth.contactsclonev2.db.ContactsTable;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactJDO;
import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 17/04/17.
 */

public class LoadContactsFromContentProvider {


    private String TAG = LoadContactsFromContentProvider.class.getSimpleName();

    public void loadContactsTable(Context context) {

        ArrayList<PrimaryContactJDO> lPrimaryContactJDOs = new ArrayList<>();

        ContentResolver lContentResolver = context.getContentResolver();
        Cursor lCursor = lContentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (lCursor.moveToFirst()) {
            do {
                PrimaryContactJDO contactsJDO = new PrimaryContactJDO();

                String lId = lCursor.getString(lCursor.getColumnIndex(ContactsContract.Contacts._ID));
                contactsJDO.setId(lId);
                contactsJDO.setDisplayName(lCursor.getString(lCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                contactsJDO.setPhoneticName(lCursor.getString(lCursor.getColumnIndex(ContactsContract.Contacts.PHONETIC_NAME)));
                contactsJDO.setPhotoUri(lCursor.getString(lCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));


                Cursor lRawContactsCursor = lContentResolver.query(ContactsContract.RawContacts.CONTENT_URI, null,
                        ContactsContract.RawContacts.CONTACT_ID + "=?", new String[]{lId}, null);
                if (lRawContactsCursor.moveToFirst()) {
                    contactsJDO.setAccountType(lRawContactsCursor.getString(lRawContactsCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)));
                }
                lRawContactsCursor.close();


                Cursor lNoteCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                        ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data.CONTACT_ID + "=?", new String[]{ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE, lId}, null);
                if (lNoteCursor.moveToFirst()) {
                    contactsJDO.setNote(lNoteCursor.getString(lNoteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE)));
                }
                lNoteCursor.close();
                
                Cursor lOrganizationCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                        ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data.CONTACT_ID + "=?", new String[]{ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE, lId}, null);
                if (lOrganizationCursor.moveToFirst()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Organisation Data", lOrganizationCursor.getString(lOrganizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)));
                        jsonObject.put("Organisation Title", lOrganizationCursor.getString(lOrganizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contactsJDO.setOrganization(jsonObject.toString());
                }
                lOrganizationCursor.close();

                lPrimaryContactJDOs.add(contactsJDO);

            } while (lCursor.moveToNext());

            lCursor.close();


            ContactsTable lTable = new ContactsTable(context);
            lTable.insertData(lPrimaryContactJDOs);



        }

    }

    public void loadContactsDataTable(Context pContext){

        ContentResolver lContentResolver = pContext.getContentResolver();

        ArrayList<SecondaryContactsJDO> lSecondaryContactsJDOs = new ArrayList<>();
                /*
                Getting data for the second table
                 */

        Cursor lPhoneNumberCursor = lContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null, null);

        if (lPhoneNumberCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                lSecondaryContactsJDO.setContactId(lPhoneNumberCursor.getString(lPhoneNumberCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                lSecondaryContactsJDO.setType(ContactsDataTable.Type.PHONE);
                lSecondaryContactsJDO.setData(lPhoneNumberCursor.getString(lPhoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                lSecondaryContactsJDOs.add(lSecondaryContactsJDO);

            } while (lPhoneNumberCursor.moveToNext());
        }
        lPhoneNumberCursor.close();


        Cursor lEmailCursor = lContentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null,null, null);

        if (lEmailCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                lSecondaryContactsJDO.setContactId(lEmailCursor.getString(lEmailCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                lSecondaryContactsJDO.setType(ContactsDataTable.Type.EMAIL);
                lSecondaryContactsJDO.setData(lEmailCursor.getString(lEmailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));

                lSecondaryContactsJDOs.add(lSecondaryContactsJDO);

            } while (lEmailCursor.moveToNext());
        }
        lEmailCursor.close();


        Cursor lWebsiteCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=?", new String[]{ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE}, null);

        if (lWebsiteCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                lSecondaryContactsJDO.setContactId(lWebsiteCursor.getString(lWebsiteCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                lSecondaryContactsJDO.setType(ContactsDataTable.Type.WEBSITE);
                lSecondaryContactsJDO.setData(lWebsiteCursor.getString(lWebsiteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));

                lSecondaryContactsJDOs.add(lSecondaryContactsJDO);

            } while (lWebsiteCursor.moveToNext());
        }
        lWebsiteCursor.close();

        Cursor lImCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=? " , new String[]{ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}, null);

        if (lImCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                lSecondaryContactsJDO.setContactId(lImCursor.getString(lImCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                lSecondaryContactsJDO.setType(ContactsDataTable.Type.IM);
                lSecondaryContactsJDO.setData(lImCursor.getString(lImCursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA)));

                lSecondaryContactsJDOs.add(lSecondaryContactsJDO);

            } while (lImCursor.moveToNext());
        }
        lImCursor.close();

        Cursor lAddressCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=? " , new String[]{ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}, null);

        if (lAddressCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO lSecondaryContactsJDO = new SecondaryContactsJDO();
                lSecondaryContactsJDO.setContactId(lAddressCursor.getString(lAddressCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                lSecondaryContactsJDO.setType(ContactsDataTable.Type.ADDRESS);
                JSONObject lJsonObject = new JSONObject();
                try {
                    lJsonObject.put("POBOX", lAddressCursor.getString(lAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX)));
                    lJsonObject.put("STREET", lAddressCursor.getString(lAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)));
                    lJsonObject.put("CITY", lAddressCursor.getString(lAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)));
                    lJsonObject.put("REGION", lAddressCursor.getString(lAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION)));
                    lJsonObject.put("POSTCODE", lAddressCursor.getString(lAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)));
                    lJsonObject.put("COUNTRY", lAddressCursor.getString(lAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lSecondaryContactsJDO.setData(lJsonObject.toString());

                lSecondaryContactsJDOs.add(lSecondaryContactsJDO);

            } while (lAddressCursor.moveToNext());
        }
        lAddressCursor.close();

        ContactsDataTable lFieldsTable = new ContactsDataTable(pContext);
        lFieldsTable.insertRows(lSecondaryContactsJDOs);


    }


}
