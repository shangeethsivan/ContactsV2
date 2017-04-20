package com.shangeeth.contactsclonev2.util;

import com.shangeeth.contactsclonev2.db.ContactsDataTable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 19/04/17.
 */

public class Util {

    final static HashMap<String, Integer> typeIndexMap = new HashMap<>();

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
