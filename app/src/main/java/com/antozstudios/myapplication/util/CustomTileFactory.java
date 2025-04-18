package com.antozstudios.myapplication.util;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

/**
 * Die Klasse {@link CustomTileFactory} stellt benutzerdefinierte Kartenkachelnquellen für die Verwendung in der OpenStreetMap-Integration zur Verfügung.
 * Sie enthält zwei vordefinierte Kartenquellen: Dark und Light.
 * Diese Kachelnquellen werden mit einem spezifischen TileSourcePolicy versehen, der verschiedene Flags definiert,
 * um das Verhalten beim Abrufen der Kacheln zu steuern.
 */
public class CustomTileFactory {

    /**
     * Eine benutzerdefinierte Kartenquelle mit dunklem Design. Diese Quelle verwendet Kacheln im PNG-Format
     * und basiert auf den Dark-Map Tiles von CartoDB.
     *
     * @see <a href="https://basemaps.cartocdn.com/dark_all/">Dark TileSource</a>
     */
    public static final XYTileSource Dark = new XYTileSource("dark_all",
            0, 20, 256, ".png", new String[]{
            "https://basemaps.cartocdn.com/dark_all/"}, "© OpenStreetMap contributors",
            new TileSourcePolicy(2,
                    TileSourcePolicy.FLAG_NO_BULK
                            | TileSourcePolicy.FLAG_NO_PREVENTIVE
                            | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                            | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
            ));
    /**
     * Eine benutzerdefinierte Kartenquelle mit hellem Design. Diese Quelle verwendet Kacheln im PNG-Format
     * und basiert auf den Light-Map Tiles von CartoDB.
     *
     * @see <a href="https://basemaps.cartocdn.com/light_all/">Light TileSource</a>
     */
    public static final XYTileSource Light = new XYTileSource("light_all",
            0, 20, 256, ".png", new String[]{
            "https://basemaps.cartocdn.com/light_all/"}, "© OpenStreetMap contributors",
            new TileSourcePolicy(2,
                    TileSourcePolicy.FLAG_NO_BULK
                            | TileSourcePolicy.FLAG_NO_PREVENTIVE
                            | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                            | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
            ));
}
