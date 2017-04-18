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
    }

    public void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CONTACT_ID + " INT," + TYPE + " TEXT," + DATA + " TEXT)");
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    public void insertRow(SecondaryContactsJDO pJDO) {

        SQLiteDatabase lSqliteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTACT_ID, pJDO.getContactId());
        values.put(TYPE, pJDO.getType());
        values.put(DATA, pJDO.getData());

        lSqliteDatabase.insert(TABLE_NAME, null, values);

    }

    public void insertRows(ArrayList<SecondaryContactsJDO> pContactJDOs) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getWritableDatabase();

        lSqLiteDatabase.beginTransaction();
        try {
            for (SecondaryContactsJDO lContactJDO : pContactJDOs) {
                ContentValues values = new ContentValues();
                values.put(CONTACT_ID, lContactJDO.getContactId());
                values.put(TYPE, lContactJDO.getType());
                values.put(DATA, lContactJDO.getData());

                lSqLiteDatabase.insert(TABLE_NAME, null, values);
            }
            lSqLiteDatabase.setTransactionSuccessful();
        } finally {
            lSqLiteDatabase.endTransaction();
        }
    }


    public ArrayList<SecondaryContactsJDO> getDatasForId(String id) {

        SQLiteDatabase lSqLiteDatabase = new ContactsDBHelper(mContext).getReadableDatabase();

        ArrayList<SecondaryContactsJDO> contactsPOJOs = new ArrayList<>();

        Cursor cursor = lSqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + CONTACT_ID + "=" + id, null);

        if (cursor.moveToFirst()) {
            do {
                SecondaryContactsJDO contactsPOJO = new SecondaryContactsJDO();
                contactsPOJO.setContactId(cursor.getString(cursor.getColumnIndex(CONTACT_ID)));
                contactsPOJO.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
                contactsPOJO.setData(cursor.getString(cursor.getColumnIndex(DATA)));
                contactsPOJOs.add(contactsPOJO);
            } while (cursor.moveToNext());

        }

        cursor.close();

        return contactsPOJOs;
    }


}
