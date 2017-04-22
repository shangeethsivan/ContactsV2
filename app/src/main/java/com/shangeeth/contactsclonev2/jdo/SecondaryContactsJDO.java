package com.shangeeth.contactsclonev2.jdo;

import android.support.annotation.NonNull;

import com.shangeeth.contactsclonev2.util.CommonUtil;

import java.io.Serializable;


/**
 * Used to Store the repeated data which will be present in the {@link com.shangeeth.contactsclonev2.db.ContactsDataTable}
 */
public class SecondaryContactsJDO implements Serializable,Comparable<SecondaryContactsJDO>{

    String contactId;
    String type;
    String data;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int compareTo(@NonNull SecondaryContactsJDO contactsJDO) {

        return CommonUtil.getTypeOrderIndex(getType())- CommonUtil.getTypeOrderIndex(contactsJDO.getType());

    }
}
