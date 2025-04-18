package com.antozstudios.myapplication.data;

/**
 * Die Klasse {@code User} repräsentiert einen registrierten Benutzer
 * der Anwendung. Sie enthält persönliche Informationen, Verifizierungsstatus
 * und Premium-Zugehörigkeit.
 *
 * <p>Diese Klasse wird zur Verwaltung und Darstellung von Benutzerdaten genutzt.</p>
 */
public class User {

    /** Eindeutige ID des Benutzers. */
    public int id;

    /** Vorname des Benutzers. */
    public String name;

    /** Nachname des Benutzers. */
    public String nachname;

    /** E-Mail-Adresse des Benutzers. */
    public String email;

    /** Postleitzahl des Wohnorts. */
    public int plz;

    /** Gibt an, ob der Benutzer verifiziert ist. */
    public boolean istverifiziert;

    /** Wohnort (Ort/Stadt) des Benutzers. */
    public String wohnort;

    /** Straße des Wohnorts. */
    public String strasse;

    /** Gibt an, ob der Benutzer ein Premiumkonto besitzt. */
    public boolean ispremium;

    /** Gehashter Wert des Benutzerpassworts. */
    public String passwort_hash;

    /** Gehashte Version der Benutzer-ID (z. B. für Freundesverknüpfungen). */
    public String b_id_hash;
}
