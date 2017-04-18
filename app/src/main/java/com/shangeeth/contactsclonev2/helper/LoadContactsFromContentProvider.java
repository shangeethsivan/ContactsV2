package com.shangeeth.contactsclonev2.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.shangeeth.contactsclonev2.db.ContactsDBHelper;
import com.shangeeth.contactsclonev2.db.ContactsMultipleFieldsTable;
import com.shangeeth.contactsclonev2.db.ContactsPrimaryTable;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactsJDO;
import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 17/04/17.
 */

public class LoadContactsFromContentProvider {


    private String TAG = LoadContactsFromContentProvider.class.getSimpleName();

    public void loadContacts(Context context) {

        ContentResolver lContentResolver = context.getContentResolver();
        Cursor cursor = lContentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                PrimaryContactsJDO contactsJDO = new PrimaryContactsJDO();

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


                Cursor imCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                        ContactsContract.Data.MIMETYPE + "=? AND "+ ContactsContract.Data.CONTACT_ID+" =?", new String[]{ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE,id}, null);

                if (imCursor.moveToFirst()) {
                    do {
                        SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                        secondaryContactsJDO.setContactId(imCursor.getString(imCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                        secondaryContactsJDO.setType(ContactsMultipleFieldsTable.Type.IM);
                        secondaryContactsJDO.setData(imCursor.getString(imCursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA)));

                    } while (imCursor.moveToNext());
                }
                imCursor.close();

                SQLiteDatabase sqLiteDatabase = new ContactsDBHelper(context).getWritableDatabase();

                ContactsPrimaryTable table = new ContactsPrimaryTable();
                table.insertRow(sqLiteDatabase, contactsJDO);


                sqLiteDatabase.close();

            } while (cursor.moveToNext());

            cursor.close();

        }

    }

    public void loadDataForSecondaryTable(Context context){

        SQLiteDatabase sqLiteDatabase = new ContactsDBHelper(context).getReadableDatabase();
        ContentResolver lContentResolver = context.getContentResolver();
        ContactsMultipleFieldsTable fieldsTable = new ContactsMultipleFieldsTable();

                /*
                Getting data for the second table
                 */

        Cursor phoneNumberCursor = lContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null, null);

        if (phoneNumberCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsMultipleFieldsTable.Type.PHONE);
                secondaryContactsJDO.setData(phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                fieldsTable.insertRow(sqLiteDatabase, secondaryContactsJDO);
            } while (phoneNumberCursor.moveToNext());
        }
        phoneNumberCursor.close();


        Cursor emailCursor = lContentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null,null, null);

        if (emailCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsMultipleFieldsTable.Type.EMAIL);
                secondaryContactsJDO.setData(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                fieldsTable.insertRow(sqLiteDatabase, secondaryContactsJDO);
            } while (emailCursor.moveToNext());
        }
        emailCursor.close();


        Cursor websiteCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=?", new String[]{ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE}, null);

        if (websiteCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(websiteCursor.getString(websiteCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsMultipleFieldsTable.Type.WEBSITE);
                secondaryContactsJDO.setData(websiteCursor.getString(websiteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                fieldsTable.insertRow(sqLiteDatabase, secondaryContactsJDO);
            } while (websiteCursor.moveToNext());
        }
        websiteCursor.close();

        Cursor imCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=? " , new String[]{ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}, null);

        if (imCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(imCursor.getString(imCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsMultipleFieldsTable.Type.IM);
                secondaryContactsJDO.setData(imCursor.getString(imCursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA)));
                fieldsTable.insertRow(sqLiteDatabase, secondaryContactsJDO);
            } while (imCursor.moveToNext());
        }
        imCursor.close();

        Cursor addressCursor = lContentResolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + "=? " , new String[]{ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}, null);

        if (addressCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO secondaryContactsJDO = new SecondaryContactsJDO();
                secondaryContactsJDO.setContactId(addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
                secondaryContactsJDO.setType(ContactsMultipleFieldsTable.Type.ADDRESS);
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
                fieldsTable.insertRow(sqLiteDatabase, secondaryContactsJDO);
            } while (addressCursor.moveToNext());
        }
        addressCursor.close();
    }


}
