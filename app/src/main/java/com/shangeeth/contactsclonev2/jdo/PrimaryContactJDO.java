package com.shangeeth.contactsclonev2.jdo;

import java.io.Serializable;


/**
 * This JDO will be Used to store the Unique data of the contact which will be present in {@link com.shangeeth.contactsclonev2.db.ContactsTable}
 */
public class PrimaryContactJDO implements Serializable {

    String id;
    String displayName;
    String phoneticName;
    String photoUri;
    String note;
    String Oraganization;
    String accountType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneticName() {
        return phoneticName;
    }

    public void setPhoneticName(String phoneticName) {
        this.phoneticName = phoneticName;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOraganization() {
        return Oraganization;
    }

    public void setOrganization(String oraganization) {
        Oraganization = oraganization;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "id:"+getId()+ " displauyName: "+getDisplayName()+" Phonetic name: "+getPhoneticName()+" PhotoURI: "+getPhotoUri()+" Note"+getNote()+" Org:"+getOraganization()+" Acc:"+getAccountType();
    }



}
