package com.shangeeth.contactsclonev2.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.shangeeth.contactsclonev2.jdo.PrimaryContactsJDO;
import com.shangeeth.contactsclonev2.jdo.SecondaryContactsJDO;


public class ContactsMultipleFieldsTable {


    public static final String TABLE_NAME = "contacts_secondary";
    public static final String _ID = "id";
    public static final String CONTACT_ID = "name";
    public static final String TYPE = "type";
    public static final String DATA = "data";

    public static class Type{
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
        public static final String WEBSITE = "website";
        public static final String IM = "im";
        public static final String ADDRESS = "address";
    }

    public void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("+_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ CONTACT_ID + " INT," + TYPE + " TEXT," + DATA + " TEXT)");
    }

    public void dropTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }


    public void insertRow(SQLiteDatabase pDatabase, SecondaryContactsJDO pJDO){

        ContentValues values = new ContentValues();
        values.put(CONTACT_ID,pJDO.getContactId());
        values.put(TYPE,pJDO.getType());
        values.put(DATA,pJDO.getData());

        pDatabase.insert(TABLE_NAME,null,values);

    }

}
