# Tabelle

## UTENTI

| Nome               | TipoDato     | Flag             | ForeignKey             | Commenti                                                                                                                                 |
| ------------------ | ------------ | ---------------- | ---------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| ID                 | int          | Primary Key      |                        |                                                                                                                                          |
| Nome               | varchar(30)  | Not Null         |                        |                                                                                                                                          |
| Cognome            | varchar(30)  | Not Null         |                        |                                                                                                                                          |
| Email              | varchar(100) | not null, UNIQUE |                        |                                                                                                                                          |
| Password           | varchar(255) | Not Null         |                        | Da criptare prima dell'inserimento                                                                                                       |
| Telefono           | varchar(20)  | Not Null         |                        |                                                                                                                                          |
| Indirizzo          | varchar(30)  |                  |                        |                                                                                                                                          |
| Citta              | varchar(20)  |                  |                        |                                                                                                                                          |
| COdiceFiscale      | varchar(16)  | unique           |                        |                                                                                                                                          |
| Data_Registrazione | DateTime     | Not Null         |                        |                                                                                                                                          |
| IDAzienda          | int          |                  | [AZIENDE.ID](#aziende) | Se esiste questo campo, questo utente è un azienda, quindi per i pagamenti bisonga prendere i dati necessari all creazione della fattura |
| ID_Ruolo           | int          | not null         | [`RUOLI.ID`](#ruoli)   |                                                                                                                                          |
| isDeleted          | boolean      | Not Null         |                        |                                                                                                                                          |

### AZIENDE

| Nome           | TipoDato     | Flag             | ForeignKey | Commenti |
| -------------- | ------------ | ---------------- | ---------- | -------- |
| ID             | int          | Primary Key      |            |          |
| RagioneSociale | varchar(100) | not null         |            |          |
| Partita_IVA    | varchar(100) | not null, unique |            |          |
| FormaGiuridica | varchar(100) | not null         |            |          |
| isDeleted      | boolean      | Not Null         |            |          |

### RUOLI

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| ID | int | Primary Key | | |
| Ruolo | varchar(15) | Not Null, UNIQUE | | |

## ANIMALI

| Nome         | TipoDato     | Flag        | ForeignKey             | Commenti                                    |
| ------------ | ------------ | ----------- | ---------------------- | ------------------------------------------- |
| ID           | int          | Primary Key |                        |                                             |
| Nome         | varchar(30)  | Not Null    |                        |                                             |
| Specie       | varchar(15)  |             |                        |                                             |
| Razza        | varchar(20)  |             | [RAZZE.ID](#razze)     |                                             |
| Sesso        | varchar(10)  |             |                        |                                             |
| Data_Nascita | Date         | Not Null    |                        |                                             |
| Peso         | decimal(5,2) |             |                        |                                             |
| Microchip    | varchar(15)  | unique      |                        | Nullabile: i conigli non hanno il microchip |
| Note         | text         |             |                        |                                             |
| ID_Utente    | int          | NOT null    | [`UTENTI.ID`](#utenti) |                                             |
| isDeleted    | boolean      | Not Null    |                        |                                             |

## SPECIE

| Nome      | TipoDato    | Flag        | ForeignKey | Commenti |
| --------- | ----------- | ----------- | ---------- | -------- |
| ID        | int         | Primary Key |            |          |
| Nome      | varchar(30) | Not Null    |            |          |
| isDeleted | boolean     | Not Null    |            |          |

## RAZZE

| Nome      | TipoDato    | Flag        | ForeignKey           | Commenti |
| --------- | ----------- | ----------- | -------------------- | -------- |
| ID        | int         | Primary Key |                      |          |
| Nome      | varchar(30) | Not Null    |                      |          |
| ID_Specie | int         | Not Null    | [SPECIE.ID](#specie) |          |
| isDeleted | boolean     | Not Null    |                      |          |



## VACCINAZIONI

| Nome              | TipoDato    | Flag        | ForeignKey                         | Commenti |
| ----------------- | ----------- | ----------- | ---------------------------------- | -------- |
| ID                | int         | Primary Key |                                    |          |
| ID_Tipo_Vaccino   | int         | Not Null    | [`TIPI_VACCINO.ID`](#tipi_vaccino) |          |
| Data_Vaccinazione | Date        |             |                                    |          |
| Lotto             | varchar(10) |             |                                    |          |
| ID_Animale        | int         | not null    | [`ANIMALI.ID`](#animali)           |          |
| isDeleted         | boolean     | Not Null    |                                    |          |


### TIPI_VACCINO

| Nome      | TipoDato    | Flag        | ForeignKey | Commenti                |
| --------- | ----------- | ----------- | ---------- | ----------------------- |
| ID        | int         | Primary Key |            |                         |
| Tipologia | varchar(40) | Not Null    |            |                         |
| Durata    | int         | Not Null    |            | Durata espressa in mesi |
| Note      | text        |             |            |                         |
| isDeleted | boolean     | Not Null    |            |                         |

## VISITE

| Nome         | TipoDato  | Flag        | ForeignKey                       | Commenti               |
| ------------ | --------- | ----------- | -------------------------------- | ---------------------- |
| ID           | int       | Primary Key |                                  |                        |
| ID_Animale   | int       | Not Null    | [`ANIMALI.ID`](#animali)         |                        |
| Data_Visita  | timestamp | Not Null    |                                  |                        |
| ID_Tipo      | int       | Not Null    | [`TIPI_VISITE.ID`](#tipi_visite) |                        |
| ID_Dottore   | int       | Not Null    | [`UTENTI.ID`](#utenti)           |                        |
| ID_Pagamento | int       |             | [`PAGAMENTI.ID`](#pagamenti)     |                        |
| Note         | text      |             |                                  |                        |
| Nota Privata | text      |             |                                  |                        |
| Stato        | enum      |             |                                  | (PRENOTATA,COMPLETATA) |
| isDeleted    | boolean   | Not Null    |                                  |                        |

## TIPI_VISITE

| Nome         | TipoDato     | Flag                   | ForeignKey                               | Commenti                                                            |
| ------------ | ------------ | ---------------------- | ---------------------------------------- | ------------------------------------------------------------------- |
| ID           | int          | Primary Key            |                                          |                                                                     |
| Nome         | varchar(80)  | not null               |                                          |                                                                     |
| Durata       | int          | not null               |                                          | Durata espressa in minuti                                           |
| ID_Categoria | int          |                        | [CATEGORIE_VISITE.ID](#categorie_visite) |                                                                     |
| Prezzo       | decimal(8,2) | Not Null               |                                          |                                                                     |
| attivo       | BOOLEAN      | Not Null, defaul= True |                                          | defaul true - usato per disabilitare le visite da parte del dottore |
| ID_Dottore   | int          | Not Null               | [UTENTI.ID](#utenti)                     | ogni dottore ha le proprie visite                                   |
| isDeleted    | boolean      | Not Null               |                                          |                                                                     |


## CATEGORIE_VISITE  
| Nome      | TipoDato     | Flag        | ForeignKey | Commenti                           |
| --------- | ------------ | ----------- | ---------- | ---------------------------------- |
| ID        | int          | Primary Key |            |                                    |
| Nome      | varchar(100) | not null    |            | prendere dati dal file del cliente |
| isDeleted | boolean      | Not Null    |            |                                    |


## PAGAMENTI

| Nome            | TipoDato     | Flag        | ForeignKey                           | Commenti                                           |
| --------------- | ------------ | ----------- | ------------------------------------ | -------------------------------------------------- |
| ID              | int          | Primary Key |                                      |                                                    |
| Data            | Date         | Not Null    |                                      |                                                    |
| Tipo_Pagamento  | varchar(30)  |             |                                      | In alternativa, usare un booleano per POS/contanti |
| Importo_Totale  | decimal(8,2) | Not Null    |                                      |                                                    |
| ID_Utente       | int          |             | [`UTENTI.ID`](#utenti)               |                                                    |
| RiferimentoFile | int          | Not Null    | [FILEREFERENCES.ID](#filereferences) |                                                    |
| isDeleted       | boolean      | Not Null    |                                      |                                                    |

## EVENTI_APP

| Nome           | TipoDato    | Flag        | ForeignKey             | Commenti    |
| -------------- | ----------- | ----------- | ---------------------- | ----------- |
| ID             | int         | Primary Key |                        |             |
| ID_Utente      | int         | not null    | [`UTENTI.ID`](#utenti) |             |
| ID_Ruolo       | int         | not null    | [`RUOLI.ID`](#ruoli)   |             |
| Tipo_Evento    | varchar(20) | Not Null    |                        |             |
| ID_Oggetto     | int         |             |                        |             |
| Oggetto_Azione | varchar(30) |             |                        |             |
| Time_Stamp     | timestamp   |             |                        | Data e ora  |
| Metadata       | jsonb       |             |                        | json format |

## ORARIO_SETTIMANALE

| Nome             | TipoDato | Flag        | ForeignKey             | Commenti        |
| ---------------- | -------- | ----------- | ---------------------- | --------------- |
| ID               | int      | Primary Key |                        |                 |
| ID_Utente        | int      | not null    | [`UTENTI.ID`](#utenti) |                 |
| GiornoSettimana  | int      | Not Null    |                        | Valore da 1 a 7 |
| MattinaInizio    | time     | Not Null    |                        |                 |
| MattinaFine      | time     | Not Null    |                        |                 |
| PomeriggioInizio | time     | Not Null    |                        |                 |
| PomeriggioFine   | time     | Not Null    |                        |                 |

## FILEREFERENCES

| Nome             | TipoDato      | Flag        | ForeignKey | Commenti                                  |
| ---------------- | ------------- | ----------- | ---------- | ----------------------------------------- |
| ID               | int           | Primary Key |            |                                           |
| OriginalFileName | varchar(100)  | Not Null    |            |                                           |
| TipoFile         | varchar(100)  | Not Null    |            | ('FATTURA','VISITE','RICETTE','IMMAGINI') |
| MimeType         | varchar(100)  | Not Null    |            |                                           |
| Size             | Bigint        | Not Null    |            |                                           |
| StoragePath      | varchar(300)  | Not Null    |            |                                           |
| Owner            | int           | Not Null    | ID_UTENTE  |                                           |
| UploadDate       | LocalDateTime | Not Null    |            |                                           |
| isDeleted        | boolean       | Not Null    |            |                                           |



