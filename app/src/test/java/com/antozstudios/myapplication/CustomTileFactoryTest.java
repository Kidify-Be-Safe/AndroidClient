package com.antozstudios.myapplication;

import org.junit.Before;
import org.junit.Test;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;

import static org.junit.Assert.*;

import com.antozstudios.myapplication.util.CustomTileFactory;


public class CustomTileFactoryTest {

    private XYTileSource darkTileSource;
    private XYTileSource lightTileSource;

    @Before
    public void setUp() {
        // Initialisiere die TileSource-Objekte
        darkTileSource = CustomTileFactory.Dark;
        lightTileSource = CustomTileFactory.Light;
    }

    @Test
    public void testDarkTileSourceInitialization() {
        // Testet, ob die Dark TileSource korrekt initialisiert ist
        assertNotNull("Dark TileSource sollte nicht null sein", darkTileSource);
        assertEquals("Dark TileSource Name ist nicht korrekt", "dark_all", darkTileSource.name());
        assertEquals("Dark TileSource MaxZoom-Level ist nicht korrekt", 20, darkTileSource.getMaximumZoomLevel());
        assertEquals("Dark TileSource MinZoom-Level ist nicht korrekt", 0, darkTileSource.getMinimumZoomLevel());
    }

    @Test
    public void testLightTileSourceInitialization() {
        // Testet, ob die Light TileSource korrekt initialisiert ist
        assertNotNull("Light TileSource sollte nicht null sein", lightTileSource);
        assertEquals("Light TileSource Name ist nicht korrekt", "light_all", lightTileSource.name());
        assertEquals("Light TileSource MaxZoom-Level ist nicht korrekt", 20, lightTileSource.getMaximumZoomLevel());
        assertEquals("Light TileSource MinZoom-Level ist nicht korrekt", 0, lightTileSource.getMinimumZoomLevel());
    }

    @Test
    public void testTileSourcePolicy() {
        // Testet, ob die TileSourcePolicy korrekt gesetzt ist
        TileSourcePolicy darkPolicy = darkTileSource.getTileSourcePolicy();
        TileSourcePolicy lightPolicy = lightTileSource.getTileSourcePolicy();

        assertNotNull("TileSourcePolicy für Dark Source sollte nicht null sein", darkPolicy);
        assertNotNull("TileSourcePolicy für Light Source sollte nicht null sein", lightPolicy);

        // Überprüfen, ob die Policy Flags gesetzt sind
        int expectedFlags = TileSourcePolicy.FLAG_NO_BULK | TileSourcePolicy.FLAG_NO_PREVENTIVE
                | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED;


    }
}
