package com.shangeeth.contactsclonev2.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 19/04/17.
 */

public class ValidationHelper {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_NUMBER_REGEX = Pattern.compile("^((\\+|00)(\\d{1,3})[\\s-]?)?(\\d{10})$",
            Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_PINCODE_REGEX = Pattern.compile("^[1-9][0-9]{5}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailId) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailId);
        return matcher.find();
    }

    public static boolean validateMobileNumber(String number) {
        Matcher matcher = VALID_NUMBER_REGEX.matcher(number);
        return matcher.find();
    }

    public static boolean validatePincode(int number) {
        Matcher matcher = VALID_PINCODE_REGEX.matcher(String.valueOf(number));
        return matcher.find();
    }

}