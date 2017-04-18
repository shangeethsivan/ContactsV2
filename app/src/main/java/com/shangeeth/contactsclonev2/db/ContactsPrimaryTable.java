package com.shangeeth.contactsclonev2.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shangeeth.contactsclonev2.jdo.PrimaryContactsJDO;

import java.util.ArrayList;

/**
 * Created by user on 17/04/17.
 */

public class ContactsPrimaryTable {

    public static final String TABLE_NAME = "contacts_primary";
    public static final String _ID = "_ID";
    public static final String DISPLAY_NAME = "name";
    public static final String PHONETIC_NAME = "phonetic_name";
    public static final String PHOTO_URI = "photo_uri";
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String NOTE = "note";
    public static final String ORGANIZATION = "organization";

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + "("+
                                                    _ID+" INT PRIMARY KEY, "+
                                                    DISPLAY_NAME+" TEXT,"+PHONETIC_NAME+" TEXT,"+ACCOUNT_TYPE+" TEXT,"+
                                                    PHOTO_URI+" TEXT,"+NOTE+" TEXT,"+ORGANIZATION+" TEXT)";


    public void createTable(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }


    public void dropTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }


    public PrimaryContactsJDO getAllContacts(SQLiteDatabase db){

        PrimaryContactsJDO contactsJDO = new PrimaryContactsJDO();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME +" ORDER BY "+DISPLAY_NAME +" ASC",null);

        if(cursor.moveToFirst()){

            do{
                contactsJDO.setId(cursor.getString(cursor.getColumnIndex(_ID)));
                contactsJDO.setDisplayName(cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
                contactsJDO.setPhoneticName(cursor.getString(cursor.getColumnIndex(PHONETIC_NAME)));
                contactsJDO.setPhotoUri(cursor.getString(cursor.getColumnIndex(PHOTO_URI)));
                contactsJDO.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
                contactsJDO.setOraganization(cursor.getString(cursor.getColumnIndex(ORGANIZATION)));

            }   while(cursor.moveToNext());

        }

        cursor.close();

        return contactsJDO;
    }


    public ArrayList<PrimaryContactsJDO> getContactForId(SQLiteDatabase db,String id){

        ArrayList<PrimaryContactsJDO> jdoArrayList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME +" WHERE "+_ID+"="+id+ " ORDER BY "+DISPLAY_NAME +" ASC",null);

        if(cursor.moveToFirst()){
            do{
                PrimaryContactsJDO contactsJDO = new PrimaryContactsJDO();
                contactsJDO.setId(cursor.getString(cursor.getColumnIndex(_ID)));
                contactsJDO.setDisplayName(cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
                contactsJDO.setPhoneticName(cursor.getString(cursor.getColumnIndex(PHONETIC_NAME)));
                contactsJDO.setPhotoUri(cursor.getString(cursor.getColumnIndex(PHOTO_URI)));
                contactsJDO.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
                jdoArrayList.add(contactsJDO);
            }   while(cursor.moveToNext());

        }

        cursor.close();

        return jdoArrayList;
    }



    public ArrayList<PrimaryContactsJDO> getContactsForList(SQLiteDatabase db){

        ArrayList<PrimaryContactsJDO> jdoArrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME,new String[]{_ID,DISPLAY_NAME,PHOTO_URI},null,null,null,null,DISPLAY_NAME+" ASC");

        if(cursor.moveToFirst()){

            do{
                PrimaryContactsJDO contactsJDO = new PrimaryContactsJDO();

                contactsJDO.setDisplayName(cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
                contactsJDO.setPhotoUri(cursor.getString(cursor.getColumnIndex(PHOTO_URI)));
                contactsJDO.setId(cursor.getString(cursor.getColumnIndex(_ID)));

                jdoArrayList.add(contactsJDO);

            }while(cursor.moveToNext());

        }

        cursor.close();

        return jdoArrayList;
    }

    public void insertRow(SQLiteDatabase pDatabase, PrimaryContactsJDO pJDO){

        ContentValues values = new ContentValues();
        values.put(_ID,pJDO.getId());
        values.put(DISPLAY_NAME,pJDO.getDisplayName());
        values.put(PHONETIC_NAME,pJDO.getPhoneticName());
        values.put(PHOTO_URI,pJDO.getPhotoUri());
        values.put(ACCOUNT_TYPE,pJDO.getAccountType());
        values.put(NOTE,pJDO.getNote());
        values.put(ORGANIZATION,pJDO.getOraganization());

        pDatabase.insert(TABLE_NAME,null,values);

    }


}
