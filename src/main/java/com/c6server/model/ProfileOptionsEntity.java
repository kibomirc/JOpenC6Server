package com.c6server.model;

import java.util.List;

public final class ProfileOptionsEntity {

    private ProfileOptionsEntity() { }

    public static final List<String> ETA = List.of(
            "----------", "non definita", "meno di 14 anni", "da 14 a 18 anni", "da 19 a 27 anni",
            "da 28 a 35 anni", "da 36 a 45 anni", "da 45 a 65 anni", "da 65 a 85 anni",
            "da 85 a 130 anni", "da 130 a 200 anni", "da 200 a 500 anni", "Oltre i 500 anni");

    public static final List<String> GENERE = List.of(
            "----------", "non definito", "maschile", "femminile");

    public static final List<String> ORIENTAMENTO = List.of(
            "----------", "non definito", "eterosessuale", "omosessuale", "entrambi");

    public static final List<String> OCCUPAZIONE = List.of(
            "----------", "non definita", "agente di commercio", "analista/programmatore", "architetto",
            "artigiano/a", "avvocato", "bancario/a", "commercialista", "commerciante", "casalingo/a",
            "dirigente", "disoccupato/a", "fotografo/a", "giornalista", "grafico/a", "impiegato/a",
            "imprenditore/trice", "infermiere/a", "ingegnere", "insegnante", "medico", "musicista",
            "notaio", "operaio/a", "operatore/turistico", "pensionato/a", "procuratore legale",
            "ricercatore/trice", "studente/essa", "altra", "agente immobiliare",
            "quadro/funzionario", "farmacista", "istruttore sportivo", "suora", "prete", "filosofo/a");

    public static final List<String> AREA_GEOGRAFICA = List.of(
            "----------", "non definita", "Italia", "Vaticano", "rep. di S. Marino", "Europa", "Africa",
            "America del nord", "America latina", "Asia", "Australia e Nuova Zelanda");

    public static final List<String> REGIONE_PROVINCIA = List.of(
            "----------", "non definita",
            "Abruzzo", "Basilicata", "Calabria", "Campania", "Emilia Romagna", "Friuli-Venezia Giulia",
            "Lazio", "Liguria", "Lombardia", "Marche", "Molise", "Piemonte", "Puglia", "Sardegna",
            "Sicilia", "Toscana", "Trentino-Alto-Adige", "Umbria", "Valle d'Aosta", "Veneto",
            "----------",
            "Agrigento", "Alessandria", "Ancona", "Aosta", "Arezzo", "Ascoli Piceno", "Asti", "Avellino",
            "Bari", "Belluno", "Benevento", "Bergamo", "Biella", "Bologna", "Bolzano", "Brescia",
            "Brindisi", "Cagliari", "Caltanissetta", "Campobasso", "Caserta", "Catania", "Catanzaro",
            "Chieti", "Como", "Cosenza", "Cremona", "Crotone", "Cuneo", "Enna", "Ferrara", "Firenze",
            "Foggia", "Forlì", "Frosinone", "Genova", "Gorizia", "Grosseto", "Imperia", "Isernia",
            "L'Aquila", "La Spezia", "Latina", "Lecce", "Lecco", "Livorno", "Lodi", "Lucca", "Macerata",
            "Mantova", "Massa Carrara", "Matera", "Messina", "Milano", "Modena", "Napoli", "Novara",
            "Nuoro", "Oristano", "Padova", "Palermo", "Parma", "Pavia", "Perugia", "Pesaro", "Pescara",
            "Piacenza", "Pisa", "Pistoia", "Pordenone", "Potenza", "Prato", "Ragusa", "Ravenna",
            "Reggio Calabria", "Reggio Emilia", "Rieti", "Rimini", "Roma", "Rovigo", "Salerno",
            "Sassari", "Savona", "Siena", "Siracusa", "Sondrio", "Taranto", "Teramo", "Terni", "Torino",
            "Trapani", "Trento", "Treviso", "Trieste", "Udine", "Varese", "Venezia", "Verbania",
            "Vercelli", "Verona", "Vibo Valentia", "Vicenza", "Viterbo");

    public static final List<String> HOBBY = List.of(
            "----------", "non definito", "nessuno", "arte/antiquariato", "bricolage", "cinema",
            "collezionismo", "computer", "cucina", "danza", "esoterismo", "filatelia", "fumetti",
            "giardinaggio", "internet", "lettura", "moto/motori", "musica (ascoltarla)",
            "musica (suonarla)", "ozio", "viaggi", "sesso", "sport (praticarlo)", "sport (in tv!)",
            "teatro", "altro", "discoteche", "scienze", "giochi da tavolo", "scrivere");

    public static final List<String> SPORT = List.of(
            "----------", "non definito", "nessuno", "aerobica", "arti marziali", "atletica",
            "automobilismo", "basket", "body building", "calcio/calcetto", "canottaggio/canoa",
            "ciclismo", "equitazione", "golf", "motociclismo", "nuoto", "pallavolo e/o beach volley",
            "pattinaggio", "pesca", "rugby", "sci/snowboard", "sport estremi", "surf",
            "tennis e/o squash", "vela", "altro", "caccia", "trekking");

    public static final List<String> GENERE_MUSICALE = List.of(
            "----------", "non definito", "nessuno", "blues", "classica", "country", "disco (anni '70)",
            "hard rock/ heavy metal", "jazz", "leggera italiana", "lirica", "new age", "pop",
            "rap/hip hop", "reggae", "rock", "techno (e derivate)", "world music", "altro", "tango",
            "punk", "ska", "house", "underground", "rock alternativo");

    public static final List<String> GENERE_CINEMA = List.of(
            "----------", "non definito", "nessuno", "i film di azione/avventura", "i b-movies",
            "i cartoni (e film di animazione)", "i classici in bianco/nero", "le commedie",
            "le commedie all'italiana", "i film drammatici", "i film di fantascienza", "i gialli",
            "i film horror", "i musical", "i western", "altro", "film d'autore", "fantasy");

    public static final List<String> COMUNITA_VIRTUALE = List.of(
            "----------", "non definito", "Max netClub");

    public static final List<String> ODI_CORDIALI = List.of(
            "----------", "non definito", "non odio niente! :-)", "gli addii", "i boxer fantasia",
            "il calcio", "i calzini bianchi corti", "la chirurgia plastica", "il computer",
            "le discoteche", "i fast-food", "i giornali e/o i telegiornali", "internet", "il lavoro",
            "la moda", "la politica", "la pubblicita'", "il reggiseno", "lo slowfood", "la sveglia",
            "la televisione", "Telecom Italia Net", "Titanic (il film)", "gli uomini troppo profumati",
            "le vamp", "i viaggi organizzati", "le gite fuori porta la domenica", "la scuola",
            "i fanatici di internet");
}