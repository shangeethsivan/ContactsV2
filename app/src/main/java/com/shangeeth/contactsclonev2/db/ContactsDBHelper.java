package com.shangeeth.contactsclonev2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shangeeth.contactsclonev2.jdo.PrimaryContactsJDO;

import java.util.ArrayList;

/**
 * Created by user on 17/04/17.
 */

public class ContactsDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyContacts.db";

    public ContactsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        ContactsPrimaryTable table = new ContactsPrimaryTable();
        table.createTable(db);

        ContactsMultipleFieldsTable fieldsTable = new ContactsMultipleFieldsTable();
        fieldsTable.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        ContactsPrimaryTable table = new ContactsPrimaryTable();
        table.dropTable(db);
        table.createTable(db);

        ContactsMultipleFieldsTable fieldsTable = new ContactsMultipleFieldsTable();
        fieldsTable.dropTable(db);
        fieldsTable.createTable(db);
    }


}

