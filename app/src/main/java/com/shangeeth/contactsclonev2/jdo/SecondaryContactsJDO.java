package com.shangeeth.contactsclonev2.jdo;

import java.io.Serializable;

/**
 * Created by user on 18/04/17.
 */

public class SecondaryContactsJDO implements Serializable{

    String contactId;
    String type;
    String data;

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
}
