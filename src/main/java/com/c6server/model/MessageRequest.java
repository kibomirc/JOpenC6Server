package com.c6server.model;

public class MessageRequest {
    private String nickMittente;
    private String nickDestinatario;
    private String testo;
    private String stile;

    public String getNickMittente() {
        return nickMittente;
    }

    public void setNickMittente(String nickMittente) {
        this.nickMittente = nickMittente;
    }

    public String getNickDestinatario() {
        return nickDestinatario;
    }

    public void setNickDestinatario(String nickDestinatario) {
        this.nickDestinatario = nickDestinatario;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getStile() {
        return stile;
    }

    public void setStile(String stile) {
        this.stile = stile;
    }
}


