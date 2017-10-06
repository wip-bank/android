package de.fhdw.wipbank.android.util;

import java.util.regex.Pattern;

/**
 * Bietet allgemeine Funktionen zur Validierung an.
 * @author Daniel Sawenko
 * @author Alexander Sawenko
 */
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

    /**
     * Prüft ob die übergebene IP valide ist.
     *
     * @param ip zu prüfende IP
     * @return true = IP valid, false = IP invalid
     */
    public static boolean isIPValid(final String ip) {
        return PATTERN.matcher(ip).matches();
    }
}
