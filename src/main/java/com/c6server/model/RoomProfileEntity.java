package com.c6server.model;

import com.c6server.c6enum.C6EnumRoomPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoomProfileEntity {

    public static final int MAX_PER_CATEGORIA = 3;

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

    private C6EnumRoomPreferences eta;
    private C6EnumRoomPreferences occupazione;
    private C6EnumRoomPreferences genere;
    private C6EnumRoomPreferences orientamento;
    private C6EnumRoomPreferences areaGeografica;
    private C6EnumRoomPreferences regioneProvincia;
    private C6EnumRoomPreferences comunitaVirtuale;

    private final List<C6EnumRoomPreferences> hobby          = new ArrayList<>();
    private final List<C6EnumRoomPreferences> odiCordiali    = new ArrayList<>();
    private final List<C6EnumRoomPreferences> generiMusicali = new ArrayList<>();
    private final List<C6EnumRoomPreferences> generiFilm     = new ArrayList<>();
    private final List<C6EnumRoomPreferences> sport          = new ArrayList<>();

    public C6EnumRoomPreferences getEta() { return eta; }
    public void setEta(C6EnumRoomPreferences p) {
        this.eta = checkIndex(p, IDX_ETA, "eta'");
    }

    public C6EnumRoomPreferences getOccupazione() { return occupazione; }
    public void setOccupazione(C6EnumRoomPreferences p) {
        this.occupazione = checkIndex(p, IDX_OCCUPAZIONE, "occupazione");
    }

    public C6EnumRoomPreferences getGenere() { return genere; }
    public void setGenere(C6EnumRoomPreferences p) {
        this.genere = checkIndex(p, IDX_GENERE, "genere");
    }

    public C6EnumRoomPreferences getOrientamento() { return orientamento; }
    public void setOrientamento(C6EnumRoomPreferences p) {
        this.orientamento = checkIndex(p, IDX_ORIENTAMENTO, "orientamento");
    }

    public C6EnumRoomPreferences getAreaGeografica() { return areaGeografica; }
    public void setAreaGeografica(C6EnumRoomPreferences p) {
        this.areaGeografica = checkIndex(p, IDX_AREA_GEO, "area geografica");
    }

    public C6EnumRoomPreferences getRegioneProvincia() { return regioneProvincia; }
    public void setRegioneProvincia(C6EnumRoomPreferences p) {
        this.regioneProvincia = checkIndex(p, IDX_REGIONE_PROV, "regione/provincia");
    }

    public C6EnumRoomPreferences getComunitaVirtuale() { return comunitaVirtuale; }
    public void setComunitaVirtuale(C6EnumRoomPreferences p) {
        this.comunitaVirtuale = checkIndex(p, IDX_COMUNITA, "comunita' virtuale");
    }

    public List<C6EnumRoomPreferences> getHobby() {
        return Collections.unmodifiableList(hobby);
    }
    public void addHobby(C6EnumRoomPreferences p) {
        add(hobby, p, IDX_HOBBY, "hobby");
    }

    public List<C6EnumRoomPreferences> getOdiCordiali() {
        return Collections.unmodifiableList(odiCordiali);
    }
    public void addOdioCordiale(C6EnumRoomPreferences p) {
        add(odiCordiali, p, IDX_ODI, "odio cordiale");
    }

    public List<C6EnumRoomPreferences> getGeneriMusicali() {
        return Collections.unmodifiableList(generiMusicali);
    }
    public void addGenereMusicale(C6EnumRoomPreferences p) {
        add(generiMusicali, p, IDX_MUSICA, "genere musicale");
    }

    public List<C6EnumRoomPreferences> getGeneriFilm() {
        return Collections.unmodifiableList(generiFilm);
    }
    public void addGenereFilm(C6EnumRoomPreferences p) {
        add(generiFilm, p, IDX_CINEMA, "genere cinematografico");
    }

    public List<C6EnumRoomPreferences> getSport() {
        return Collections.unmodifiableList(sport);
    }
    public void addSport(C6EnumRoomPreferences p) {
        add(sport, p, IDX_SPORT, "sport");
    }

    public void addGeneric(C6EnumRoomPreferences p) {
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

    public List<C6EnumRoomPreferences> toFlatList() {
        List<C6EnumRoomPreferences> tutte = new ArrayList<>();
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

    private static C6EnumRoomPreferences checkIndex(
            C6EnumRoomPreferences p, byte indexAtteso, String nomeCategoria) {
        if (p != null && p.getIndex() != indexAtteso) {
            throw new IllegalArgumentException(
                    p.name() + " non appartiene alla categoria " + nomeCategoria);
        }
        return p;
    }

    private static void add(List<C6EnumRoomPreferences> lista,
                            C6EnumRoomPreferences p,
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
                    "Massimo " + MAX_PER_CATEGORIA + " " + nomeCategoria + " per stanza");
        }
        lista.add(p);
    }

    private static void addIfNotNull(List<C6EnumRoomPreferences> lista,
                                     C6EnumRoomPreferences p) {
        if (p != null) lista.add(p);
    }

    @Override
    public String toString() {
        return "RoomProfileEntity{" +
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