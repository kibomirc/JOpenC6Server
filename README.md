#  JOpenC6Server

[![Java Version](https://shields.io)](https://openjdk.org)
[![Database](https://shields.io)](https://sqlite.org)
[![Status](https://shields.io)](#)

Un'implementazione nativa in Java del backend per il protocollo di messaggistica **C6**.  
L'obiettivo principale del progetto è replicare e supportare accuratamente le specifiche del protocollo utilizzate dalla versione del client ufficiale contenuta nell'installer `c6-4.2.c6tinb426.exe`.

---

## Funzionalità Supportate

Il server implementa **tutti i comandi previsti dal documento ufficiale di OpenC6**, a cura di Alessio Periloso (*BigAlex*), con l'eccezione indicata più sotto..

OpenC6 era orientato soprattutto alla realizzazione del **client**: la gestione delle stanze era demandata al server ufficiale di Atlantide e non è documentata. Il protocollo delle stanze è stato quindi **ricostruito** analizzando con **Wireshark** il traffico del progetto [c6online.it](https://c6online.it) di Daniele *DaxWeb* Ciarapica.

All'inizio degli anni 2000 Alessio Periloso (*BigAlex*) rilasciò una versione **open source del server**, scritta in **Visual Basic 5**. I proprietari del progetto, però, modificavano spesso il protocollo, e quel server è diventato incompatibile con le versioni più recenti del client, compresa la `c6-4.2.c6tinb426.exe`.

-----
### Funzionalità non implementate

- **IP visibile**: nel client l'utente può scegliere di rendere visibile il proprio indirizzo IP, che viene poi mostrato nel suo profilo. Questa funzionalità non è implementata dal server.

---

##  Architettura Tecnica

Il server si interfaccia a un database relazionale leggero ad alte prestazioni:
* **Core Engine:** Sviluppato in Java con architettura multi-thread orientata agli oggetti.
* **Storage:** File database **SQLite**, interfacciato tramite driver JDBC standard

---

## Roadmap & TODO

Le attività attualmente pianificate includono:

- [] **Memorizzazione cambio stato utente**
- [] **Orario dell ultimo accesso**
- [] **Stato del netfriend nel profilo**
- [] **Stato del netfriend cercato**
- [] **Gestione messaggio custom al client**
- [] **Se la stanza già esiste inviare messaggio**
---

Progetto sviluppato a scopo di studio e preservazione digitale dei vecchi protocolli di messaggistica.

Si ringrazia Alessio (*BigAlex*) Periloso per aver effettuato il reversing del protocollo.
Si ringrazia inoltre Daniele (*DaxWeb*) Ciarapica per aver messo online il servizio [c6online.it](https://c6online.it), grazie al quale è stato possibile analizzare le parti del protocollo mancanti.

