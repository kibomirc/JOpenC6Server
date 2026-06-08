package com.c6server.c6enum;

public enum C6EnumRoomPreferences {
    // ETA (01)
    ETA_NON_DEFINITO    (0x01, 0x01),
    ETA_MENO_DI_14_ANNI (0x01, 0x02),
    ETA_14_18           (0x01, 0x03),
    ETA_19_27           (0x01, 0x04),
    ETA_28_35           (0x01, 0x05),
    ETA_36_45           (0x01, 0x06),
    ETA_45_65           (0x01, 0x07),
    ETA_65_85           (0x01, 0x08),
    ETA_85_130          (0x01, 0x09),
    ETA_130_200         (0x01, 0x0A),
    ETA_200_500         (0x01, 0x0B),
    ETA_OLTRE_500       (0x01, 0x0C),

    // GENERE (02)
    GENERE_NON_DEFINITO (0x02, 0x01),
    MASCHILE            (0x02, 0x02),
    FEMMINILE           (0x02, 0x03),

    // AREA GEOGRAFICA (05)
    AREA_GEOGRAFICA_NON_DEFINITA    (0x05, 0x01),
    ITALIA                          (0x05, 0x02),
    VATICANO                        (0x05, 0x03),
    REP_SAN_MARINO                  (0x05, 0x04),
    EUROPA                          (0x05, 0x05),
    AFRICA                          (0x05, 0x06),
    AMERICA_NORD                    (0x05, 0x07),
    AMERICA_SUD                     (0x05, 0x08),
    ASIA                            (0x05, 0x09),
    AUSTRALIA_NUOVA_ZELANDA         (0x05, 0x0A),

    // ORIENTAMENTO (03)
    ORIENTAMENTO_NON_DEFINITO   (0x03, 0x01),
    ORIENTAMENTO_ETERO          (0x03, 0x02),
    ORIENTAMENTO_OMOSESSUALE    (0x03, 0x03),
    ORIENTAMENTO_ENTRAMBI       (0x03, 0x04),

    // OCCUPAZIONE (04)
    OCCUPAZIONE_NON_DEFINITO            (0x04, 0x01),
    OCCUPAZIONE_AGENTE_DI_COMMERCIO     (0x04, 0x02),
    OCCUPAZIONE_ANALISTA_PROGRAMMATORE  (0x04, 0x03),
    OCCUPAZIONE_ARCHITETTO              (0x04, 0x04),
    OCCUPAZIONE_ARTIGIANO_A             (0x04, 0x05),
    OCCUPAZIONE_AVVOCATO                (0x04, 0x06),
    OCCUPAZIONE_BANCARIO_A              (0x04, 0x07),
    OCCUPAZIONE_COMMERCIALISTA          (0x04, 0x08),
    OCCUPAZIONE_COMMERCIANTE            (0x04, 0x09),
    OCCUPAZIONE_CASALINGO_A             (0x04, 0x0A),
    OCCUPAZIONE_DIRIGENTE               (0x04, 0x0B),
    OCCUPAZIONE_DISOCCUPATO_A           (0x04, 0x0C),
    OCCUPAZIONE_FOTOGRAFO_A             (0x04, 0x0D),
    OCCUPAZIONE_GIORNALISTA             (0x04, 0x0E),
    OCCUPAZIONE_GRAFICO_A               (0x04, 0x0F),
    OCCUPAZIONE_IMPIEGATO_A             (0x04, 0x10),
    OCCUPAZIONE_IMPRENDITORE_TRICE      (0x04, 0x11),
    OCCUPAZIONE_INFERMIERE_A            (0x04, 0x12),
    OCCUPAZIONE_INGEGNERE               (0x04, 0x13),
    OCCUPAZIONE_INSEGNANTE              (0x04, 0x14),
    OCCUPAZIONE_MEDICO                  (0x04, 0x15),
    OCCUPAZIONE_MUSICISTA               (0x04, 0x16),
    OCCUPAZIONE_NOTAIO                  (0x04, 0x17),
    OCCUPAZIONE_OPERAIO_A               (0x04, 0x18),
    OCCUPAZIONE_OPERATORE_TURISTICO     (0x04, 0x19),
    OCCUPAZIONE_PENSIONATO_A            (0x04, 0x1A),
    OCCUPAZIONE_PROCURATORE_LEGALE      (0x04, 0x1B),
    OCCUPAZIONE_RICERCATORE_TRICE       (0x04, 0x1C),
    OCCUPAZIONE_STUDENTE_ESSA           (0x04, 0x1D),
    OCCUPAZIONE_ALTRA                   (0x04, 0x1E),
    OCCUPAZIONE_AGENTE_IMMIBILIARE      (0x04, 0x1F),
    OCCUPAZIONE_QUADRO_FUNZIONARIO      (0x04, 0x20),
    OCCUPAZIONE_FARMACISTA              (0x04, 0x21),
    OCCUPAZIONE_ISTRUTTORE_SPORTIVO     (0x04, 0x22),
    OCCUPAZIONE_SUORA                   (0x04, 0x23),
    OCCUPAZIONE_PRETE                   (0x04, 0x24),
    OCCUPAZIONE_FILOSOFO_A              (0x04, 0x25),

    // HOBBY


    // SPORT (08)
    SPORT_NON_DEFINITO          (0x08, 0x01),
    SPORT_NESSUNO               (0x08, 0x02),
    SPORT_AEROBICA              (0x08, 0x03),
    SPORT_ARTI_MARZIALI         (0x08, 0x04),
    SPORT_ATLETICA              (0x08, 0x05),
    SPORT_AUTOMOBILISMO         (0x08, 0x06),
    SPORT_BASKET                (0x08, 0x07),
    SPORT_BODY_BUILDING         (0x08, 0x08),
    SPORT_CALCIO_CALCETTO       (0x08, 0x09),
    SPORT_CANOTTAGGIO_CANOA     (0x08, 0x0A),
    SPORT_CICLISMO              (0x08, 0x0B),
    SPORT_EQUITAZIONE           (0x08, 0x0C),
    SPORT_GOLF                  (0x08, 0x0D),
    SPORT_MOTOCICLISMO          (0x08, 0x0E),
    SPORT_NUOTO                 (0x08, 0x0F),
    SPORT_PALLA_A_VOLO_BEACH_VOLLEY (0x08, 0x10),
    SPORT_PATTINAGGIO           (0x08, 0x11),
    SPORT_PESCA                 (0x08, 0x12),
    SPORT_RUGBY                 (0x08, 0x13),
    SPORT_SCI_SNOWBOARD         (0x08, 0x14),
    SPORT_ESTREMI               (0x08, 0x15),
    SPORT_SURF                  (0x08, 0x16),
    SPORT_TENNIS_SQUASH         (0x08, 0x17),
    SPORT_VELA                  (0x08, 0x18),
    SPORT_ALTRO                 (0x08, 0x19),
    SPORT_CACCIA                (0x08, 0x1A),
    SPORT_TREKKING              (0x08, 0x1B);




    private final byte index;
    private final byte val;

    C6EnumRoomPreferences(int index, int val) {
        this.index = (byte) index;
        this.val   = (byte) val;
    }

    public byte getIndex() { return index; }
    public byte getVal()   { return val; }

    public static C6EnumRoomPreferences fromBytes(byte index, byte val) {
        for (C6EnumRoomPreferences p : C6EnumRoomPreferences.values()) {
            if (p.index == index && p.val == val) return p;
        }
        throw new IllegalArgumentException("Preferenza sconosciuta: " + index + " " + val);
    }
}
