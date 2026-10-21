package com.saucedemo.dataprovider;

import com.saucedemo.utils.ExcelReader;

import java.io.File;
import java.util.Map;

/**
 * Reads SauceDemo data from saucedemoUserdata.xls.
 * Sheet 1 (index 0): valid login. Sheet 2 (index 1): locked user.
 * Sheet 3 (index 2): checkout first name, last name, and zip.
 */
public final class LoginDataProvider {

    private static final String LOGIN_DATA_PATH = System.getProperty("user.dir")
            + File.separator + "src"
            + File.separator + "test"
            + File.separator + "resources"
            + File.separator + "testdata"
            + File.separator + "saucedemoUserdata.xls";

    private LoginDataProvider() {
    }

    public static Map<String, String> validUser() {
        return ExcelReader.getFirstRowByIndex(LOGIN_DATA_PATH, 0);
    }

    public static Map<String, String> lockedUser() {
        return ExcelReader.getFirstRowByIndex(LOGIN_DATA_PATH, 1);
    }

    public static Map<String, String> checkoutCustomer() {
        return ExcelReader.getFirstRowByIndex(LOGIN_DATA_PATH, 2);
    }

    public static String value(Map<String, String> row, String... keys) {
        for (String key : keys) {
            String value = row.get(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        throw new IllegalArgumentException("Missing Excel column. Looked for " + String.join(", ", keys)
                + ". Columns found: " + row.keySet());
    }
}
