package com.antozstudios.myapplication;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;

import com.antozstudios.myapplication.util.PostHttp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import okhttp3.*;

import java.io.IOException;

public class PostHttpTest {

    @Mock
    private OkHttpClient mockClient;

    @Mock
    private Call mockCall;  // Wir mocken das Call-Objekt

    @Mock
    private Response mockResponse;

    private PostHttp postHttp;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialisiert die Mocks
        postHttp = new PostHttp();
        postHttp.client = mockClient;  // Setzt den gemockten OkHttpClient in PostHttp
    }

    @Test
    public void testPost() throws IOException {
        String url = "http://example.com";
        String json = "{\"key\":\"value\"}";

        // Simuliere das Verhalten des Call-Objekts
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);  // newCall gibt ein Call-Objekt zurück
        when(mockCall.execute()).thenReturn(mockResponse);  // execute gibt die gemockte Antwort zurück

        // Simuliere die Antwort des Servers
        when(mockResponse.code()).thenReturn(200);  // Erfolgreicher Statuscode 200
        when(mockResponse.body()).thenReturn(ResponseBody.create("response", MediaType.get("application/json")));  // Body der Antwort

        // Führe den POST-Request durch
        String result = postHttp.post(url, json);

        // Überprüfe das Ergebnis
        assertEquals("response", result);  // Erwartete Antwort: "response"
        verify(mockClient).newCall(any(Request.class));  // Überprüfe, ob der HTTP-Request durchgeführt wurde
        verify(mockCall).execute();  // Überprüfe, ob execute aufgerufen wurde
    }
}
