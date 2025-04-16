package com.antozstudios.myapplication.util;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

public class CustomTileFactory {


    public static final XYTileSource Dark = new XYTileSource("dark_all",
            0, 20, 256, ".png", new String[]{
            "https://basemaps.cartocdn.com/dark_all/"}, "© OpenStreetMap contributors",
            new TileSourcePolicy(2,
                    TileSourcePolicy.FLAG_NO_BULK
                            | TileSourcePolicy.FLAG_NO_PREVENTIVE
                            | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                            | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
            ));

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
