package com.antozstudios.myapplication.data;

import java.util.ArrayList;

/**
 * Die Klasse {@code GeoCoding} repräsentiert das Ergebnis einer
 * Geocoding-Anfrage. Sie enthält eine Adresse mit verschiedenen
 * geografischen Informationen.
 *
 * <p>Wird beispielsweise bei der Umwandlung von Koordinaten in
 * menschenlesbare Adressen verwendet.</p>
 */
public class GeoCoding {

    /** Die Adresse, die dem Geocoding-Ergebnis entspricht. */
    public Address address;

    /**
     * Die Klasse {@code Address} enthält detaillierte Informationen
     * über eine geografische Adresse.
     */
    public static class Address {

        /** Straßenname. */
        public String road;

        /** Stadtteil oder Viertel. */
        public String suburb;

        /** Stadt oder Gemeinde. */
        public String town;

        /** Landkreis oder Bezirk. */
        public String county;

        /** Bundesland oder Region. */
        public String state;

        /** Postleitzahl. */
        public String postcode;

        /** Land. */
        public String country;

        /** Ländercode (z. B. "de" für Deutschland). */
        public String country_code;
    }
}
