package com.shangeeth.contactsclonev2.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.shangeeth.contactsclonev2.db.ContactsDBHelper;
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

    public void loadContacts(Context context) {

        ArrayList<PrimaryContactJDO> primaryContactJDOs = new ArrayList<>();

        ContentResolver lContentResolver = context.getContentResolver();
        Cursor cursor = lContentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                PrimaryContactJDO contactsJDO = new PrimaryContactJDO();

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                contactsJDO.setId(id);
                contactsJDO.setDisplayName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                contactsJDO.setPhoneticName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHONETIC_NAME)));
                contactsJDO.setPhotoUri(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));


                Cursor rawContactsCursor = lContentResolver.query(ContactsContract.RawContacts.CONTENT_URI, null,
                        ContactsContract.RawContacts.CONTACT_ID + "=?", new String[]{id}, null);
                if (rawContactsCursor.moveToFirst()) {
                    contactsJDO.setAccountType(rawContactsCursor.getString(rawContactsCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE)));
                }
                rawContactsCursor.close();


                Cursor noteCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                        ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data.CONTACT_ID + "=?", new String[]{ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE, id}, null);
                if (noteCursor.moveToFirst()) {
                    contactsJDO.setNote(noteCursor.getString(noteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE)));
                }
                noteCursor.close();
                
                Cursor organizationCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                        ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data.CONTACT_ID + "=?", new String[]{ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE, id}, null);
                if (organizationCursor.moveToFirst()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Organisation Data", organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)));
                        jsonObject.put("Organisation Title", organizationCursor.getString(organizationCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contactsJDO.setOraganization(jsonObject.toString());
                }
                organizationCursor.close();

                primaryContactJDOs.add(contactsJDO);

            } while (cursor.moveToNext());

            cursor.close();


            ContactsTable table = new ContactsTable(context);
            table.insertDatas(primaryContactJDOs);



        }

    }

    public void loadDataForSecondaryTable(Context pContext){

        SQLiteDatabase sqLiteDatabase = new ContactsDBHelper(pContext).getReadableDatabase();
        ContentResolver lContentResolver = pContext.getContentResolver();

        ArrayList<SecondaryContactsJDO> lSecondaryContactsJDOs = new ArrayList<>();
                /*
                Getting data for the second table
                 */

        Cursor phoneNumberCursor = lContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null, null);

        if (phoneNumberCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsDataTable.Type.PHONE);
                secondaryContactsJDO.setData(phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                lSecondaryContactsJDOs.add(secondaryContactsJDO);

            } while (phoneNumberCursor.moveToNext());
        }
        phoneNumberCursor.close();


        Cursor emailCursor = lContentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null,null, null);

        if (emailCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsDataTable.Type.EMAIL);
                secondaryContactsJDO.setData(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));

                lSecondaryContactsJDOs.add(secondaryContactsJDO);

            } while (emailCursor.moveToNext());
        }
        emailCursor.close();


        Cursor websiteCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=?", new String[]{ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE}, null);

        if (websiteCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(websiteCursor.getString(websiteCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsDataTable.Type.WEBSITE);
                secondaryContactsJDO.setData(websiteCursor.getString(websiteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));

                lSecondaryContactsJDOs.add(secondaryContactsJDO);

            } while (websiteCursor.moveToNext());
        }
        websiteCursor.close();

        Cursor imCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=? " , new String[]{ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}, null);

        if (imCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(imCursor.getString(imCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsDataTable.Type.IM);
                secondaryContactsJDO.setData(imCursor.getString(imCursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA)));

                lSecondaryContactsJDOs.add(secondaryContactsJDO);

            } while (imCursor.moveToNext());
        }
        imCursor.close();

        Cursor addressCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=? " , new String[]{ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}, null);

        if (addressCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsDataTable.Type.ADDRESS);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("POBOX", addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX)));
                    jsonObject.put("STREET", addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)));
                    jsonObject.put("CITY", addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)));
                    jsonObject.put("REGION", addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION)));
                    jsonObject.put("POSTCODE", addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)));
                    jsonObject.put("COUNTRY", addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                secondaryContactsJDO.setData(jsonObject.toString());

                lSecondaryContactsJDOs.add(secondaryContactsJDO);

            } while (addressCursor.moveToNext());
        }
        addressCursor.close();

        ContactsDataTable fieldsTable = new ContactsDataTable(pContext);
        fieldsTable.insertRows(lSecondaryContactsJDOs);


    }


}
