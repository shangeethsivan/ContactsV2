package com.shangeeth.contactsclonev2.util;

import com.shangeeth.contactsclonev2.db.ContactsDataTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 19/04/17.
 */

public class CommonUtil {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_NUMBER_REGEX = Pattern.compile("^((\\+|00)(\\d{1,3})[\\s-]?)?(\\d{10})$",
            Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_PINCODE_REGEX = Pattern.compile("^[1-9][0-9]{5}$", Pattern.CASE_INSENSITIVE);
    final static HashMap<String, Integer> typeIndexMap = new HashMap<>();

    public static boolean validateEmail(String emailId) {
        Matcher lMatcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailId);
        return lMatcher.find();
    }

    public static boolean validateMobileNumber(String number) {
        Matcher lMatcher = VALID_NUMBER_REGEX.matcher(number);
        return lMatcher.find();
    }

    public static boolean validatePincode(int number) {
        Matcher lMatcher = VALID_PINCODE_REGEX.matcher(String.valueOf(number));
        return lMatcher.find();
    }

    public static int getTypeOrderIndex(String type) {

        typeIndexMap.put(ContactsDataTable.Type.PHONE, 1);
        typeIndexMap.put(ContactsDataTable.Type.EMAIL, 2);
        typeIndexMap.put(ContactsDataTable.Type.WEBSITE, 3);
        typeIndexMap.put(ContactsDataTable.Type.IM, 4);
        typeIndexMap.put(ContactsDataTable.Type.ADDRESS, 5);
        typeIndexMap.put("Note", 6);
        typeIndexMap.put("Organization", 7);

        return typeIndexMap.get(type);
    }

    public static <T> int getItemIndex(ArrayList<T> t, T object) {

        int index = -1;
        for (int i = 0; i < t.size(); i++) {
            if (t.get(i).hashCode() == object.hashCode()) {
                index = i;
            }
        }
        return index;
    }
}