package com.antozstudios.myapplication;

import com.antozstudios.myapplication.util.JsonParser;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class JsonParserTest {

    private JsonParser jsonParser;

    @Before
    public void setUp() {

        jsonParser = new JsonParser();
    }

    @Test
    public void testParseValidJson() {
        // JSON-Antwort
        String jsonResponse = "[{\"email\":\"test1@example.com\",\"vorname\":\"Max\",\"nachname\":\"Mustermann\"}," +
                "{\"email\":\"test2@example.com\",\"vorname\":\"Mia\",\"nachname\":\"Meier\"}]";


        jsonParser.jsonResponse = jsonResponse;
        jsonParser.parse();


        assertNotNull(jsonParser.userList);
        assertEquals(2, jsonParser.userList.size());
        assertEquals("test1@example.com", jsonParser.userList.get(0).email);
        assertEquals("test2@example.com", jsonParser.userList.get(1).email);
    }

    @Test
    public void testParseEmptyJson() {

        String jsonResponse = "[]";

        // Setze die jsonResponse und parse sie
        jsonParser.jsonResponse = jsonResponse;
        jsonParser.parse();


        assertNotNull(jsonParser.userList);
        assertEquals(0, jsonParser.userList.size());
    }

    @Test
    public void testGetIndex() {

        String jsonResponse = "[{\"email\":\"test1@example.com\",\"vorname\":\"Max\",\"nachname\":\"Mustermann\"}," +
                "{\"email\":\"test2@example.com\",\"vorname\":\"Mia\",\"nachname\":\"Meier\"}]";

        // Setze die jsonResponse und parse sie
        jsonParser.jsonResponse = jsonResponse;
        jsonParser.parse();


        assertEquals(0, jsonParser.getIndex("test1@example.com"));
        assertEquals(1, jsonParser.getIndex("test2@example.com"));
    }

    @Test
    public void testGetIndexForNonExistentUser() {

        String jsonResponse = "[{\"email\":\"test1@example.com\",\"vorname\":\"Max\",\"nachname\":\"Mustermann\"}," +
                "{\"email\":\"test2@example.com\",\"vorname\":\"Mia\",\"nachname\":\"Meier\"}]";

        // Setze die jsonResponse und parse sie
        jsonParser.jsonResponse = jsonResponse;
        jsonParser.parse();


        assertEquals(-1, jsonParser.getIndex("nonexistent@example.com"));
    }
}
