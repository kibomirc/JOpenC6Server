#  JOpenC6Server

[![Java Version](https://shields.io)](https://openjdk.org)
[![Database](https://shields.io)](https://sqlite.org)
[![Status](https://shields.io)](#)

Un'implementazione nativa in Java del backend per il protocollo di messaggistica **C6**.  
L'obiettivo principale del progetto è replicare e supportare accuratamente le specifiche del protocollo utilizzate dalla versione del client ufficiale contenuta nell'installer `c6-4.2.c6tinb426.exe`.

---

## Funzionalità Supportate

Attualmente il server implementa i seguenti componenti core del protocollo:

* Sicurezza & Autenticazione**
    * Codifica dei pacchetti nativi del protocollo C6.
    * Gestione e crittografia dell'handshake per Nickname e Password.
* Flusso di Connessione**
    * Gestione delle richieste di tipo `infoserverlogin`.
    * Invio del messaggio di benvenuto iniziale (`welcomeMessage` / MOTD).
* Sincronizzazione & Stato**
    * Gestione dei pulsanti e dell'interfaccia: elaborazione delle richieste `reqPuls` e invio dei dati `sendPuls` per l'aggiornamento dei componenti grafici.
    * Gestione della lista contatti: elaborazione della richiesta di elenco (`reqUsers`) e distribuzione degli utenti connessi (`sendUsers`) in tempo reale.

---

##  Architettura Tecnica

Il server si interfaccia a un database relazionale leggero ad alte prestazioni:
* **Core Engine:** Sviluppato in Java con architettura multi-thread orientata agli oggetti.
* **Storage:** File database **SQLite**, interfacciato tramite driver JDBC standard e ottimizzato con chiamate native via **JNI** per garantire latenze minime in produzione.

---

## Roadmap & TODO

Le attività attualmente pianificate includono:

- [X] **Refactoring strutturale:** Ottimizzazione e scomposizione della classe logica principale.
- [X] **Allineamento standard:** Revisione globale del codice sorgente per l'adeguamento alla corretta *naming convention* di Java.
- [ ] **Messaggistica Privata (`SND_MSG`):** Implementazione delle funzionalità per l'invio e il recapito dei messaggi privati per consentire la comunicazione tra utenti in conversazioni singole.

---

Progetto sviluppato a scopo di studio e preservazione digitale dei vecchi protocolli di messaggistica.
Si ringrazia Alessio (BigAlex) Periloso per aver effettuato il reversing del protocollo.

