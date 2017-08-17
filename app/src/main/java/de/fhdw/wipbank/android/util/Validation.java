package de.fhdw.wipbank.android.util;

import java.util.regex.Pattern;

public class Validation {


    // Quelle: https://stackoverflow.com/a/36248095, angepasst
    private static final Pattern PATTERN = Pattern.compile(
              "^"
            + "(((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}" // Domains (z.B fhdw.de)
            + "|"
            + "localhost" // localhost
            + "|"
            + "(([0-9]{1,3}\\.){3})[0-9]{1,3})" // IP
            + "(:[0-9]{1,5})?" // optionaler Port
            + "$");

    public static boolean isIPValid(final String ip) {
        return PATTERN.matcher(ip).matches();
    }
}
