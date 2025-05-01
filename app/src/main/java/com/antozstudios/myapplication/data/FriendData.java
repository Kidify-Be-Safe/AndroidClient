package com.antozstudios.myapplication.data;

/**
 * Die Klasse {@code FriendData} repräsentiert die Daten eines Freundes
 * in der Anwendung. Sie enthält Informationen zu persönlichen Details,
 * Standortdaten und weiteren Metadaten.
 *
 * <p>Diese Klasse wird typischerweise für die Datenübertragung und
 * -speicherung innerhalb der App verwendet.</p>
 */
public class FriendData {

    /** Benutzer-ID des aktuellen Nutzers. */
    public int b_id;

    /** Freundes-ID des verbundenen Nutzers. */
    public int f_id;

    /** Eindeutige ID für diesen Datensatz. */
    public int id;

    /** Vorname des Freundes. */
    public String vorname;

    /** Nachname des Freundes. */
    public String nachname;

    /** Postleitzahl des Freundes. */
    public int plz;

    /** Wohnort (Ort/Stadt) des Freundes. */
    public String wohnort;

    /** Straße des Wohnorts des Freundes. */
    public String benutzer_strasse;

    /** Pfad oder URL zum Profilbild. */
    public String img;

    /** Gehashte Version der Benutzer-ID. */
    public String b_id_hash;

    /** Breitengrad des letzten bekannten Standorts. */
    public double breitengrad;

    /** Längengrad des letzten bekannten Standorts. */
    public double laengengrad;

    /** Zeitpunkt der letzten Standortaktualisierung. */
    public String zeitpunkt;

    /** Stadt, in der sich der Freund aktuell aufhält. */
    public String stadt;

    /** Straße, an der sich der Freund aktuell befindet. */
    public String strasse;

    /** Postleitzahl des aktuellen Standorts. */
    public int f_plz;

    /** Status der Ampel (z. B. 0 = grün, 1 = gelb, 2 = rot). */
    public int ampel;
}
