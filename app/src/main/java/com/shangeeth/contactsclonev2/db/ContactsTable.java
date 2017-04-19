package com.shangeeth.contactsclonev2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shangeeth.contactsclonev2.jdo.PrimaryContactJDO;

import java.util.ArrayList;

/**
 * Created by user on 17/04/17.
 */

public class ContactsTable {

    private Context mContext;

    public ContactsTable(Context mContext) {
        this.mContext = mContext;
    }

    public static final String TABLE_NAME = "contacts_primary";
    public static final String _ID = "_ID";
    public static final String DISPLAY_NAME = "name";
    public static final String PHONETIC_NAME = "phonetic_name";
    public static final String PHOTO_URI = "photo_uri";
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String NOTE = "note";
    public static final String ORGANIZATION = "organization";

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            _ID + " INT PRIMARY KEY, " +
            DISPLAY_NAME + " TEXT," + PHONETIC_NAME + " TEXT," + ACCOUNT_TYPE + " TEXT," +
            PHOTO_URI + " TEXT," + NOTE + " TEXT," + ORGANIZATION + " TEXT)";


    public void createTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }


    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    public PrimaryContactJDO getContactsForId(String id) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getReadableDatabase();

        PrimaryContactJDO lContactsJDO = new PrimaryContactJDO();

        Cursor lCursor = lSqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + _ID + "=" + id + " ORDER BY " + DISPLAY_NAME + " ASC", null);

        if (lCursor.moveToFirst()) {
            do {
                lContactsJDO.setId(lCursor.getString(lCursor.getColumnIndex(_ID)));
                lContactsJDO.setDisplayName(lCursor.getString(lCursor.getColumnIndex(DISPLAY_NAME)));
                lContactsJDO.setPhoneticName(lCursor.getString(lCursor.getColumnIndex(PHONETIC_NAME)));
                lContactsJDO.setPhotoUri(lCursor.getString(lCursor.getColumnIndex(PHOTO_URI)));
                lContactsJDO.setAccountType(lCursor.getString(lCursor.getColumnIndex(ACCOUNT_TYPE)));
                lContactsJDO.setNote(lCursor.getString(lCursor.getColumnIndex(NOTE)));
                lContactsJDO.setOraganization(lCursor.getString(lCursor.getColumnIndex(ORGANIZATION)));
            } while (lCursor.moveToNext());

        }

        lCursor.close();

        return lContactsJDO;
    }


    public ArrayList<PrimaryContactJDO> getContactsForList() {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getReadableDatabase();


        ArrayList<PrimaryContactJDO> jdoArrayList = new ArrayList<>();

        Cursor cursor = lSqLiteDatabase.query(TABLE_NAME, new String[]{_ID, DISPLAY_NAME, PHOTO_URI}, null, null, null, null, DISPLAY_NAME + " ASC");

        if (cursor.moveToFirst()) {

            do {
                PrimaryContactJDO contactsJDO = new PrimaryContactJDO();

                contactsJDO.setDisplayName(cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
                contactsJDO.setPhotoUri(cursor.getString(cursor.getColumnIndex(PHOTO_URI)));
                contactsJDO.setId(cursor.getString(cursor.getColumnIndex(_ID)));

                jdoArrayList.add(contactsJDO);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return jdoArrayList;
    }


    public void insertDatas(ArrayList<PrimaryContactJDO> pContactJDOs) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        lSqLiteDatabase.beginTransaction();
        try {
            for (PrimaryContactJDO lContactJDO : pContactJDOs) {

                ContentValues values = new ContentValues();
                values.put(_ID, lContactJDO.getId());
                values.put(DISPLAY_NAME, lContactJDO.getDisplayName());
                values.put(PHONETIC_NAME, lContactJDO.getPhoneticName());
                values.put(PHOTO_URI, lContactJDO.getPhotoUri());
                values.put(ACCOUNT_TYPE, lContactJDO.getAccountType());
                values.put(NOTE, lContactJDO.getNote());
                values.put(ORGANIZATION, lContactJDO.getOraganization());

                lSqLiteDatabase.insert(TABLE_NAME, null, values);

            }
            lSqLiteDatabase.setTransactionSuccessful();
        } finally {
            lSqLiteDatabase.endTransaction();
        }

    }

    public void insertRow( PrimaryContactJDO pJDO) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(_ID, pJDO.getId());
        values.put(DISPLAY_NAME, pJDO.getDisplayName());
        values.put(PHONETIC_NAME, pJDO.getPhoneticName());
        values.put(PHOTO_URI, pJDO.getPhotoUri());
        values.put(ACCOUNT_TYPE, pJDO.getAccountType());
        values.put(NOTE, pJDO.getNote());
        values.put(ORGANIZATION, pJDO.getOraganization());

        lSqLiteDatabase.insert(TABLE_NAME, null, values);

    }

    public void updateRow(PrimaryContactJDO pJDO){

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DISPLAY_NAME, pJDO.getDisplayName());
        values.put(NOTE, pJDO.getNote());
        values.put(ORGANIZATION, pJDO.getOraganization());

        lSqLiteDatabase.update(TABLE_NAME,values,_ID+"=?",new String[]{pJDO.getId()});

    }


}
