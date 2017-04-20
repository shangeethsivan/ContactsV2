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
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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


        ArrayList<PrimaryContactJDO> lContactJDOArrayList = new ArrayList<>();

        Cursor lCursor = lSqLiteDatabase.query(TABLE_NAME, new String[]{_ID, DISPLAY_NAME, PHOTO_URI}, null, null, null, null, DISPLAY_NAME + " ASC");

        if (lCursor.moveToFirst()) {

            do {
                PrimaryContactJDO contactsJDO = new PrimaryContactJDO();

                contactsJDO.setDisplayName(lCursor.getString(lCursor.getColumnIndex(DISPLAY_NAME)));
                contactsJDO.setPhotoUri(lCursor.getString(lCursor.getColumnIndex(PHOTO_URI)));
                contactsJDO.setId(lCursor.getString(lCursor.getColumnIndex(_ID)));

                lContactJDOArrayList.add(contactsJDO);

            } while (lCursor.moveToNext());

        }

        lCursor.close();

        return lContactJDOArrayList;
    }

    public void deleteContact(String pId){

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        lSqLiteDatabase.delete(TABLE_NAME,_ID+"=?",new String[]{pId});

        lSqLiteDatabase.close();
    }

    public void insertDatas(ArrayList<PrimaryContactJDO> pContactJDOs) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        lSqLiteDatabase.beginTransaction();
        try {
            for (PrimaryContactJDO lContactJDO : pContactJDOs) {

                ContentValues lValues = new ContentValues();
                lValues.put(_ID, lContactJDO.getId());
                lValues.put(DISPLAY_NAME, lContactJDO.getDisplayName());
                lValues.put(PHONETIC_NAME, lContactJDO.getPhoneticName());
                lValues.put(PHOTO_URI, lContactJDO.getPhotoUri());
                lValues.put(ACCOUNT_TYPE, lContactJDO.getAccountType());
                lValues.put(NOTE, lContactJDO.getNote());
                lValues.put(ORGANIZATION, lContactJDO.getOraganization());

                lSqLiteDatabase.insert(TABLE_NAME, null, lValues);

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

        ContentValues lValues = new ContentValues();
        lValues.put(DISPLAY_NAME, pJDO.getDisplayName());
        lValues.put(NOTE, pJDO.getNote());
        lValues.put(ORGANIZATION, pJDO.getOraganization());

        lSqLiteDatabase.update(TABLE_NAME,lValues,_ID+"=?",new String[]{pJDO.getId()});

    }

    public int insertNewRow(PrimaryContactJDO pJDO){

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        ContentValues lValues = new ContentValues();
        lValues.put(DISPLAY_NAME, pJDO.getDisplayName());
        lValues.put(NOTE, pJDO.getNote());
        lValues.put(ORGANIZATION, pJDO.getOraganization());

        long lId = lSqLiteDatabase.insert(TABLE_NAME,null,lValues);

        return (int) lId;

    }




}
