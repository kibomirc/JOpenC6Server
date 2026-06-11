package com.c6server.model;

import com.c6server.c6enum.C6EnumUserProfilePreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model del profilo preferenze di un utente.
 *
 * Vincoli (gli stessi imposti dal DB):
 *  - categorie singole (0..1): occupazione, genere, orientamento,
 *    area geografica, regione/provincia, comunita' virtuale
 *  - categorie multiple (max 3): hobby, odi cordiali, generi musicali,
 *    generi cinematografici, sport
 */
public class UserProfileEntity {

    public static final int MAX_PER_CATEGORIA = 3;

    // pref_index delle categorie
    public static final byte IDX_ETA          = 0x01;
    public static final byte IDX_GENERE       = 0x02;
    public static final byte IDX_ORIENTAMENTO = 0x03;
    public static final byte IDX_OCCUPAZIONE  = 0x04;
    public static final byte IDX_AREA_GEO     = 0x05;
    public static final byte IDX_REGIONE_PROV = 0x06;
    public static final byte IDX_HOBBY        = 0x07;
    public static final byte IDX_SPORT        = 0x08;
    public static final byte IDX_MUSICA       = 0x09;
    public static final byte IDX_CINEMA       = 0x0A;
    public static final byte IDX_COMUNITA     = 0x0B;
    public static final byte IDX_ODI          = 0x0C;

    // ---------- categorie singole (0..1) ----------
    private C6EnumUserProfilePreferences eta;
    private C6EnumUserProfilePreferences occupazione;
    private C6EnumUserProfilePreferences genere;
    private C6EnumUserProfilePreferences orientamento;
    private C6EnumUserProfilePreferences areaGeografica;
    private C6EnumUserProfilePreferences regioneProvincia;
    private C6EnumUserProfilePreferences comunitaVirtuale;

    // ---------- categorie multiple (max 3) ----------
    private final List<C6EnumUserProfilePreferences> hobby          = new ArrayList<>();
    private final List<C6EnumUserProfilePreferences> odiCordiali    = new ArrayList<>();
    private final List<C6EnumUserProfilePreferences> generiMusicali = new ArrayList<>();
    private final List<C6EnumUserProfilePreferences> generiFilm     = new ArrayList<>();
    private final List<C6EnumUserProfilePreferences> sport          = new ArrayList<>();

    // ------------------------------------------------------------------
    // GETTER / SETTER categorie singole (con validazione dell'index)
    // ------------------------------------------------------------------

    public C6EnumUserProfilePreferences getEta() { return eta; }
    public void setEta(C6EnumUserProfilePreferences p) {
        this.eta = checkIndex(p, IDX_ETA, "eta'");
    }

    public C6EnumUserProfilePreferences getOccupazione() { return occupazione; }
    public void setOccupazione(C6EnumUserProfilePreferences p) {
        this.occupazione = checkIndex(p, IDX_OCCUPAZIONE, "occupazione");
    }

    public C6EnumUserProfilePreferences getGenere() { return genere; }
    public void setGenere(C6EnumUserProfilePreferences p) {
        this.genere = checkIndex(p, IDX_GENERE, "genere");
    }

    public C6EnumUserProfilePreferences getOrientamento() { return orientamento; }
    public void setOrientamento(C6EnumUserProfilePreferences p) {
        this.orientamento = checkIndex(p, IDX_ORIENTAMENTO, "orientamento");
    }

    public C6EnumUserProfilePreferences getAreaGeografica() { return areaGeografica; }
    public void setAreaGeografica(C6EnumUserProfilePreferences p) {
        this.areaGeografica = checkIndex(p, IDX_AREA_GEO, "area geografica");
    }

    public C6EnumUserProfilePreferences getRegioneProvincia() { return regioneProvincia; }
    public void setRegioneProvincia(C6EnumUserProfilePreferences p) {
        this.regioneProvincia = checkIndex(p, IDX_REGIONE_PROV, "regione/provincia");
    }

    public C6EnumUserProfilePreferences getComunitaVirtuale() { return comunitaVirtuale; }
    public void setComunitaVirtuale(C6EnumUserProfilePreferences p) {
        this.comunitaVirtuale = checkIndex(p, IDX_COMUNITA, "comunita' virtuale");
    }

    // ------------------------------------------------------------------
    // LISTE (in sola lettura verso l'esterno) + ADD con validazione
    // ------------------------------------------------------------------

    public List<C6EnumUserProfilePreferences> getHobby() {
        return Collections.unmodifiableList(hobby);
    }
    public void addHobby(C6EnumUserProfilePreferences p) {
        add(hobby, p, IDX_HOBBY, "hobby");
    }

    public List<C6EnumUserProfilePreferences> getOdiCordiali() {
        return Collections.unmodifiableList(odiCordiali);
    }
    public void addOdioCordiale(C6EnumUserProfilePreferences p) {
        add(odiCordiali, p, IDX_ODI, "odio cordiale");
    }

    public List<C6EnumUserProfilePreferences> getGeneriMusicali() {
        return Collections.unmodifiableList(generiMusicali);
    }
    public void addGenereMusicale(C6EnumUserProfilePreferences p) {
        add(generiMusicali, p, IDX_MUSICA, "genere musicale");
    }

    public List<C6EnumUserProfilePreferences> getGeneriFilm() {
        return Collections.unmodifiableList(generiFilm);
    }
    public void addGenereFilm(C6EnumUserProfilePreferences p) {
        add(generiFilm, p, IDX_CINEMA, "genere cinematografico");
    }

    public List<C6EnumUserProfilePreferences> getSport() {
        return Collections.unmodifiableList(sport);
    }
    public void addSport(C6EnumUserProfilePreferences p) {
        add(sport, p, IDX_SPORT, "sport");
    }

    // ------------------------------------------------------------------
    // METODI DI SERVIZIO PER IL DAO
    // ------------------------------------------------------------------

    /**
     * Smista una preferenza generica nel campo giusto in base al suo index.
     * Usato dal DAO in fase di caricamento dal DB.
     */
    public void addGeneric(C6EnumUserProfilePreferences p) {
        switch (p.getIndex()) {
            case IDX_ETA          -> setEta(p);
            case IDX_OCCUPAZIONE  -> setOccupazione(p);
            case IDX_GENERE       -> setGenere(p);
            case IDX_ORIENTAMENTO -> setOrientamento(p);
            case IDX_AREA_GEO     -> setAreaGeografica(p);
            case IDX_REGIONE_PROV -> setRegioneProvincia(p);
            case IDX_COMUNITA     -> setComunitaVirtuale(p);
            case IDX_HOBBY        -> addHobby(p);
            case IDX_SPORT        -> addSport(p);
            case IDX_MUSICA       -> addGenereMusicale(p);
            case IDX_CINEMA       -> addGenereFilm(p);
            case IDX_ODI          -> addOdioCordiale(p);
            default -> throw new IllegalArgumentException(
                    "Categoria non gestita nel profilo: " + p.getIndex());
        }
    }

    /**
     * Restituisce tutte le preferenze del profilo come lista piatta.
     * Usato dal DAO in fase di salvataggio.
     */
    public List<C6EnumUserProfilePreferences> toFlatList() {
        List<C6EnumUserProfilePreferences> tutte = new ArrayList<>();
        addIfNotNull(tutte, eta);
        addIfNotNull(tutte, genere);
        addIfNotNull(tutte, orientamento);
        addIfNotNull(tutte, occupazione);
        addIfNotNull(tutte, areaGeografica);
        addIfNotNull(tutte, regioneProvincia);
        addIfNotNull(tutte, comunitaVirtuale);
        tutte.addAll(hobby);
        tutte.addAll(sport);
        tutte.addAll(generiMusicali);
        tutte.addAll(generiFilm);
        tutte.addAll(odiCordiali);
        return tutte;
    }

    /** Svuota completamente il profilo. */
    public void clear() {
        eta = null;
        occupazione = null;
        genere = null;
        orientamento = null;
        areaGeografica = null;
        regioneProvincia = null;
        comunitaVirtuale = null;
        hobby.clear();
        odiCordiali.clear();
        generiMusicali.clear();
        generiFilm.clear();
        sport.clear();
    }

    // ------------------------------------------------------------------
    // HELPER PRIVATI
    // ------------------------------------------------------------------

    /** Verifica che la preferenza appartenga alla categoria attesa (null ammesso). */
    private static C6EnumUserProfilePreferences checkIndex(
            C6EnumUserProfilePreferences p, byte indexAtteso, String nomeCategoria) {
        if (p != null && p.getIndex() != indexAtteso) {
            throw new IllegalArgumentException(
                    p.name() + " non appartiene alla categoria " + nomeCategoria);
        }
        return p;
    }

    /** Aggiunta a una lista con validazione: categoria giusta, no duplicati, max 3. */
    private static void add(List<C6EnumUserProfilePreferences> lista,
                            C6EnumUserProfilePreferences p,
                            byte indexAtteso,
                            String nomeCategoria) {
        if (p == null) {
            throw new IllegalArgumentException("Preferenza nulla");
        }
        if (p.getIndex() != indexAtteso) {
            throw new IllegalArgumentException(
                    p.name() + " non appartiene alla categoria " + nomeCategoria);
        }
        if (lista.contains(p)) {
            throw new IllegalArgumentException(p.name() + " gia' presente");
        }
        if (lista.size() >= MAX_PER_CATEGORIA) {
            throw new IllegalStateException(
                    "Massimo " + MAX_PER_CATEGORIA + " " + nomeCategoria + " per utente");
        }
        lista.add(p);
    }

    private static void addIfNotNull(List<C6EnumUserProfilePreferences> lista,
                                     C6EnumUserProfilePreferences p) {
        if (p != null) lista.add(p);
    }

    @Override
    public String toString() {
        return "UserProfileEntity{" +
                "eta=" + eta +
                ", genere=" + genere +
                ", orientamento=" + orientamento +
                ", occupazione=" + occupazione +
                ", areaGeografica=" + areaGeografica +
                ", regioneProvincia=" + regioneProvincia +
                ", comunitaVirtuale=" + comunitaVirtuale +
                ", hobby=" + hobby +
                ", sport=" + sport +
                ", generiMusicali=" + generiMusicali +
                ", generiFilm=" + generiFilm +
                ", odiCordiali=" + odiCordiali +
                '}';
    }
}