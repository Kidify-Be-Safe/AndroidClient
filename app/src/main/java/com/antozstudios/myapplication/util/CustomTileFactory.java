package com.antozstudios.myapplication.util;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

public class CustomTileFactory {


    public static final XYTileSource Dark = new XYTileSource("Dark",
            0, 20, 256, ".png", new String[]{
            "https://basemaps.cartocdn.com/dark_nolabels/"}, "© OpenStreetMap contributors",
            new TileSourcePolicy(2,
                    TileSourcePolicy.FLAG_NO_BULK
                            | TileSourcePolicy.FLAG_NO_PREVENTIVE
                            | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                            | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
            ));
}
