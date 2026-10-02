package com.c6server.model;

import com.c6server.c6enum.C6EnumUserProfilePreferences;

public class UserProfileSearchEntity {
    private C6EnumUserProfilePreferences eta;
    private C6EnumUserProfilePreferences genere;
    private C6EnumUserProfilePreferences orientamento;
    private C6EnumUserProfilePreferences occupazione;
    private C6EnumUserProfilePreferences areaGeografica;
    private C6EnumUserProfilePreferences regione;
    private C6EnumUserProfilePreferences hobby;
    private C6EnumUserProfilePreferences sport;
    private C6EnumUserProfilePreferences genereMusicale;
    private C6EnumUserProfilePreferences genereCinematografico;
    private C6EnumUserProfilePreferences comunitaVirtuale;
    private C6EnumUserProfilePreferences odiCordiali;

    public C6EnumUserProfilePreferences getEta() { return eta; }
    public void setEta(C6EnumUserProfilePreferences eta) { this.eta = eta; }

    public C6EnumUserProfilePreferences getGenere() { return genere; }
    public void setGenere(C6EnumUserProfilePreferences genere) { this.genere = genere; }

    public C6EnumUserProfilePreferences getOrientamento() { return orientamento; }
    public void setOrientamento(C6EnumUserProfilePreferences orientamento) { this.orientamento = orientamento; }

    public C6EnumUserProfilePreferences getOccupazione() { return occupazione; }
    public void setOccupazione(C6EnumUserProfilePreferences occupazione) { this.occupazione = occupazione; }

    public C6EnumUserProfilePreferences getAreaGeografica() { return areaGeografica; }
    public void setAreaGeografica(C6EnumUserProfilePreferences areaGeografica) { this.areaGeografica = areaGeografica; }

    public C6EnumUserProfilePreferences getRegione() { return regione; }
    public void setRegione(C6EnumUserProfilePreferences regione) { this.regione = regione; }

    public C6EnumUserProfilePreferences getHobby() { return hobby; }
    public void setHobby(C6EnumUserProfilePreferences hobby) { this.hobby = hobby; }

    public C6EnumUserProfilePreferences getSport() { return sport; }
    public void setSport(C6EnumUserProfilePreferences sport) { this.sport = sport; }

    public C6EnumUserProfilePreferences getGenereMusicale() { return genereMusicale; }
    public void setGenereMusicale(C6EnumUserProfilePreferences genereMusicale) { this.genereMusicale = genereMusicale; }

    public C6EnumUserProfilePreferences getGenereCinematografico() { return genereCinematografico; }
    public void setGenereCinematografico(C6EnumUserProfilePreferences genereCinematografico) { this.genereCinematografico = genereCinematografico; }

    public C6EnumUserProfilePreferences getComunitaVirtuale() { return comunitaVirtuale; }
    public void setComunitaVirtuale(C6EnumUserProfilePreferences comunitaVirtuale) { this.comunitaVirtuale = comunitaVirtuale; }

    public C6EnumUserProfilePreferences getOdiCordiali() { return odiCordiali; }
    public void setOdiCordiali(C6EnumUserProfilePreferences odiCordiali) { this.odiCordiali = odiCordiali; }

    @Override
    public String toString() {
        return "UserProfileSearchEntity{" +
                "eta=" + eta +
                ", genere=" + genere +
                ", orientamento=" + orientamento +
                ", occupazione=" + occupazione +
                ", areaGeografica=" + areaGeografica +
                ", regione=" + regione +
                ", hobby=" + hobby +
                ", sport=" + sport +
                ", genereMusicale=" + genereMusicale +
                ", genereCinematografico=" + genereCinematografico +
                ", comunitaVirtuale=" + comunitaVirtuale +
                ", odiCordiali=" + odiCordiali +
                '}';
    }
}
