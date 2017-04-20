package com.shangeeth.contactsclonev2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 17/04/17.
 */

public class ContactsDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyContacts.db";
    Context mContext;

    public ContactsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        ContactsTable lTable = new ContactsTable(mContext);
        lTable.createTable(db);

        ContactsDataTable lFieldsTable = new ContactsDataTable(mContext);
        lFieldsTable.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        ContactsTable lTable = new ContactsTable(mContext);
        lTable.dropTable(db);
        lTable.createTable(db);

        ContactsDataTable lDataTable = new ContactsDataTable(mContext);
        lDataTable.dropTable(db);
        lDataTable.createTable(db);
    }


}

