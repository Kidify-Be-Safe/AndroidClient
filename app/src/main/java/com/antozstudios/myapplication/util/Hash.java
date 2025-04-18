package com.antozstudios.myapplication.util;

import java.security.MessageDigest;


/**
 * Diese Klasse enthält eine Methode zur Berechnung eines SHA-256-Hashes für eine gegebene Eingabe.
 */
public class Hash {

    /**
     * Berechnet den SHA-256-Hash eines gegebenen Eingabestrings.
     *
     * @param input Der Eingabestring, dessen SHA-256-Hash berechnet werden soll.
     * @return Der SHA-256-Hash des Eingabestrings als Hexadezimal-String.
     * @throws RuntimeException Wenn ein Fehler bei der Hash-Berechnung auftritt.
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
