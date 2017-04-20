package com.shangeeth.contactsclonev2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;

import java.util.ArrayList;


public class ContactsDataTable {

    private Context mContext;

    public ContactsDataTable(Context mContext) {
        this.mContext = mContext;
    }

    public static final String TABLE_NAME = "contacts_secondary";
    public static final String _ID = "id";
    public static final String CONTACT_ID = "contact_id";
    public static final String TYPE = "type";
    public static final String DATA = "data";

    public static class Type {
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
        public static final String WEBSITE = "website";
        public static final String IM = "im";
        public static final String ADDRESS = "address";

        public static boolean checkType(String pType) {
            if (pType.equals(PHONE) || pType.equals(EMAIL) || pType.equals(WEBSITE) || pType.equals(IM) || pType.equals(ADDRESS))
                return true;
            else
                return false;
        }
    }

    public void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CONTACT_ID + " INT," + TYPE + " TEXT," + DATA + " TEXT)");
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    public void insertRow(SecondaryContactsJDO pJDO) {

        SQLiteDatabase lSqliteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        ContentValues lValues = new ContentValues();
        lValues.put(CONTACT_ID, pJDO.getContactId());
        lValues.put(TYPE, pJDO.getType());
        lValues.put(DATA, pJDO.getData());

        lSqliteDatabase.insert(TABLE_NAME, null, lValues);
        lSqliteDatabase.close();
    }

    public void insertRows(ArrayList<SecondaryContactsJDO> pContactJDOs) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        lSqLiteDatabase.beginTransaction();
        try {
            for (SecondaryContactsJDO lContactJDO : pContactJDOs) {
                ContentValues lValues = new ContentValues();
                lValues.put(CONTACT_ID, lContactJDO.getContactId());
                lValues.put(TYPE, lContactJDO.getType());
                lValues.put(DATA, lContactJDO.getData());

                lSqLiteDatabase.insert(TABLE_NAME, null, lValues);
            }
            lSqLiteDatabase.setTransactionSuccessful();
        } finally {
            lSqLiteDatabase.endTransaction();
        }
    }

    public void updateRows(ArrayList<SecondaryContactsJDO> pContactJDOs) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        lSqLiteDatabase.beginTransaction();

        try {
            for (SecondaryContactsJDO pContactJDO : pContactJDOs) {

                ContentValues values = new ContentValues();
                values.put(TYPE, pContactJDO.getType());
                values.put(DATA, pContactJDO.getData());

                lSqLiteDatabase.update(TABLE_NAME, values, _ID + "=?", new String[]{pContactJDO.getId()});
            }
            lSqLiteDatabase.setTransactionSuccessful();
        } finally {
            lSqLiteDatabase.endTransaction();
            lSqLiteDatabase.close();
        }
    }


    public ArrayList<SecondaryContactsJDO> getDatasForId(String id) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getReadableDatabase();

        ArrayList<SecondaryContactsJDO> lContactsJDOs = new ArrayList<>();

        Cursor lCursor = lSqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + CONTACT_ID + "=" + id, null);

        if (lCursor.moveToFirst()) {
            do {
                SecondaryContactsJDO lContactsJDO = new SecondaryContactsJDO();
                lContactsJDO.setContactId(lCursor.getString(lCursor.getColumnIndex(CONTACT_ID)));
                lContactsJDO.setType(lCursor.getString(lCursor.getColumnIndex(TYPE)));
                lContactsJDO.setData(lCursor.getString(lCursor.getColumnIndex(DATA)));
                lContactsJDO.setId(lCursor.getString(lCursor.getColumnIndex(_ID)));
                lContactsJDOs.add(lContactsJDO);
            } while (lCursor.moveToNext());

        }

        lCursor.close();

        lSqLiteDatabase.close();
        return lContactsJDOs;
    }

    public void deleteDataForIds(ArrayList<String> pIds) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getReadableDatabase();

        try {
            lSqLiteDatabase.beginTransaction();

            for (String lId : pIds) {
                lSqLiteDatabase.delete(TABLE_NAME, _ID + "=?", new String[]{lId});
            }

            lSqLiteDatabase.setTransactionSuccessful();

        } finally {
            lSqLiteDatabase.endTransaction();
        }
    }
    public void insertOrUpdateData(ArrayList<SecondaryContactsJDO> pSecondaryContactsJDOs) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getReadableDatabase();

        try {
            lSqLiteDatabase.beginTransaction();

            for(SecondaryContactsJDO lSecondaryContactsJDO:pSecondaryContactsJDOs){

                ContentValues lContentValues = new ContentValues();
                if(!lSecondaryContactsJDO.getId().equals("-1")){
                    lContentValues.put(DATA,lSecondaryContactsJDO.getData());
                    lSqLiteDatabase.update(TABLE_NAME,lContentValues,_ID+"=?",new String[]{lSecondaryContactsJDO.getId()});
                }else {
                    lContentValues.put(CONTACT_ID,lSecondaryContactsJDO.getContactId());
                    lContentValues.put(TYPE,lSecondaryContactsJDO.getType());
                    lContentValues.put(DATA,lSecondaryContactsJDO.getData());
                    lSqLiteDatabase.insert(TABLE_NAME,null,lContentValues);
                }
            }

            lSqLiteDatabase.setTransactionSuccessful();

        } finally {
            lSqLiteDatabase.endTransaction();
        }
    }

}
