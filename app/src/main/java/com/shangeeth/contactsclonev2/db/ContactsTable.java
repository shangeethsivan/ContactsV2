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


    /**
     * Executes the create query
     *
     * @param db the db instance
     */
    public static void createTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }


    /**
     * Executes the drop table query
     */
    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    /**
     * Get the data in the {@link PrimaryContactJDO} form for a specified id
     * @param id the id for the data to be fetched
     * @return returns the data in the {@link PrimaryContactJDO} type
     */
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
                lContactsJDO.setOrganization(lCursor.getString(lCursor.getColumnIndex(ORGANIZATION)));
            } while (lCursor.moveToNext());

        }

        lCursor.close();

        return lContactsJDO;
    }


    /**
     * Gets all the contacts id,name and image to be shown in the home activity
     * @return returns the list of Contacts
     */
    public ArrayList<PrimaryContactJDO> getContactsForList() {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getReadableDatabase();


        ArrayList<PrimaryContactJDO> lContactJDOArrayList = new ArrayList<>();

        Cursor lCursor = lSqLiteDatabase.query(TABLE_NAME, new String[]{_ID, DISPLAY_NAME, PHOTO_URI}, null, null, null, null, DISPLAY_NAME + " ASC");

        if (lCursor.moveToFirst()) {

            do {
                PrimaryContactJDO lContactJDO = new PrimaryContactJDO();

                lContactJDO.setDisplayName(lCursor.getString(lCursor.getColumnIndex(DISPLAY_NAME)));
                lContactJDO.setPhotoUri(lCursor.getString(lCursor.getColumnIndex(PHOTO_URI)));
                lContactJDO.setId(lCursor.getString(lCursor.getColumnIndex(_ID)));

                lContactJDOArrayList.add(lContactJDO);

            } while (lCursor.moveToNext());

        }

        lCursor.close();

        return lContactJDOArrayList;
    }

    /**
     * Deletes the contact in the specified id
     * @param pId the contact to be deleted
     */
    public void deleteContact(String pId){

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        lSqLiteDatabase.delete(TABLE_NAME,_ID+"=?",new String[]{pId});

        lSqLiteDatabase.close();
    }

    /**
     *  Inserts bulk data from the content provider
     * @param pContactJDOs the list of data to be added in {@link PrimaryContactJDO} form
     */
    public void insertData(ArrayList<PrimaryContactJDO> pContactJDOs) {

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


    /**
     * Updates the Particular row in the table
     * @param pJDO
     */
    public void updateRow(PrimaryContactJDO pJDO){

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        ContentValues lValues = new ContentValues();
        lValues.put(DISPLAY_NAME, pJDO.getDisplayName());
        lValues.put(NOTE, pJDO.getNote());
        lValues.put(ORGANIZATION, pJDO.getOraganization());

        lSqLiteDatabase.update(TABLE_NAME,lValues,_ID+"=?",new String[]{pJDO.getId()});

    }

    /**
     * Insert new row of data to the table
     * @param pJDO the data which is present in the {@link PrimaryContactJDO} form
     * @return returns the Id of the stored contact.
     */
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
