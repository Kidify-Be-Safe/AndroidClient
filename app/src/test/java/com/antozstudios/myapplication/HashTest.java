package com.antozstudios.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

import com.antozstudios.myapplication.util.Hash;

public class HashTest {

    @Test
    public void testSha256() {
        // Eingabewert für den Test
        String input = "testInput";

        // Erwarteter SHA-256-Hash-Wert für "testInput"
        String expectedHash = "620ae460798e1f4cab44c44f3085620284f0960a276bbc3f0bd416449df14dbe";

        // Berechnen des SHA-256-Hashs der Eingabe
        String result = Hash.sha256(input);

        // Überprüfen, ob der berechnete Hash dem erwarteten Hash entspricht
        assertEquals(expectedHash, result);
    }

    @Test
    public void testSha256EmptyString() {
        // Testen mit einem leeren String
        String input = "";

        // Erwarteter SHA-256-Hash-Wert für den leeren String
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        String result = Hash.sha256(input);

        // Überprüfen, ob der berechnete Hash dem erwarteten Hash entspricht
        assertEquals(expectedHash, result);
    }

    @Test(expected = RuntimeException.class)
    public void testSha256Exception() {

        String input = null;

        // Sollte eine RuntimeException werfen
        Hash.sha256(input);
    }
}
