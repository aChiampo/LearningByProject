# Tabelle

## UTENTE

| Nome               | TipoDato    | Flag        | ForeignKey           | Commenti                           |
| ------------------ | ----------- | ----------- | -------------------- | ---------------------------------- |
| ID                 | int         | Primary Key |                      |                                    |
| Nome               | varchar(30) | Not Null    |                      |                                    |
| Cognome            | varchar(30) | Not Null    |                      |                                    |
| Email              | varchar(40) | Not Null    |                      |                                    |
| Password           | varchar(40) | Not Null    |                      | Da criptare prima dell'inserimento |
| Telefono           | varchar(10) | Not Null    |                      |                                    |
| Indirizzo          | varchar(30) |             |                      |                                    |
| Citta              | varchar(20) |             |                      |                                    |
| Data_Registrazione | Date        | Not Null    |                      |                                    |
| Riferimento        | varchar(30) |             |                      |                                    |
| ID_Ruolo           | int         |             | [`RUOLO.ID`](#ruolo) |                                    |

### RUOLO

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| ID | int | Primary Key | | |
| Ruolo | varchar(15) | Not Null | | |

## ANIMALE

| Nome         | TipoDato    | Flag        | ForeignKey             | Commenti                                    |
| ------------ | ----------- | ----------- | ---------------------- | ------------------------------------------- |
| ID           | int         | Primary Key |                        |                                             |
| Nome         | varchar(30) | Not Null    |                        |                                             |
| Specie       | varchar(15) |             |                        |                                             |
| Razza        | varchar(20) |             |                        |                                             |
| Sesso        | Bool        |             |                        |                                             |
| Data_Nascita | Date        | Not Null    |                        |                                             |
| Peso         | double      |             |                        |                                             |
| Microchip    | varchar(15) |             |                        | Nullabile: i conigli non hanno il microchip |
| Note         | text        |             |                        |                                             |
| ID_Utente    | int         |             |[`UTENTE.ID`](#utente)  |                                             |


## VACCINAZIONI

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| ID | int | Primary Key | | |
| ID_Tipo | int | Not Null | | |
| Data_Vaccinazione | Date | | | |
| Lotto | varchar(10) | | | |
| ID_Animale | int | | [`ANIMALE.ID`](#animale) | |
| ID_Tipo_Vaccino | int | | [`TIPO VACCINO.ID`](#tipo-vaccino) | |

### TIPO VACCINO

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| ID | int | Primary Key | | |
| Tipologia | varchar(40) | Not Null | | |
| Durata | int | Not Null | | Durata espressa in mesi |
| Note | text | | | |

## VISITA

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| ID | int | Primary Key | | |
| ID_Animale | int | Not Null | [`ANIMALE.ID`](#animale) | |
| Data_Visita | Date | Not Null | | |
| ID_Tipo | int | Not Null | [`TIPI VISITA.ID`](#tipi-visita) | |
| ID_Dottore | int | Not Null | [`UTENTE.ID`](#utente) | |
| ID_Pagamento | int | | [`PAGAMENTI.ID`](#pagamenti) | |
| Note | text | | | |

### TIPI VISITA

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| ID | int | Primary Key | | |
| Durata | int | | | Durata espressa in minuti |
| Descrizione | text | | | |
| Prezzo | double | Not Null | | |
| ID_Dottore | int | Not Null | | |

## PRENOTAZIONI

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| ID | int | Primary Key | | |
| Data_Visita | Date | Not Null | | |
| Tipo_Visita | varchar(30) | | | |
| ID_Utente | int | | [`UTENTE.ID`](#utente) | |
| ID_Tipi_Visita | int | | [`TIPI VISITA.ID`](#tipi-visita) | |

## PAGAMENTI

| Nome           | TipoDato    | Flag        | ForeignKey  | Commenti                                           |
| -------------- | ----------- | ----------- | ----------- | -------------------------------------------------- |
| ID             | int         | Primary Key |             |                                                    |
| Data           | Date        | Not Null    |             |                                                    |
| Tipo_Pagamento | varchar(30) |             |             | In alternativa, usare un booleano per POS/contanti |
| Importo_Totale | double      | Not Null    |             |                                                    |
| ID_Utente      | int         |             | [`UTENTE.ID`](#utente) |                                                    |

## EVENTI APP

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| ID_Utente | int | Primary Key | [`UTENTE.ID`](#utente) | |
| ID_Oggetto | int | Primary Key | | |
| Tipo_Evento | varchar(20) | Not Null | | |
| Ruolo | varchar(30) | | | |
| Oggetto_Azione | varchar(30) | | | |
| Time_Stamp | timestamp | | | Data e ora |
| Metadata | text | | | |
| ID_Ruolo | int | | [`RUOLO.ID`](#ruolo) | |

## ORARIO SETTIMANALE

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| ID_Utente | int | Primary Key | | |
| GiornoSettimana | int | Not Null | | Valore da 1 a 7 |
| MattinaInizio | Date | Not Null | | |
| MattinaFine | Date | Not Null | | |
| PomeriggioInizio | Date | Not Null | | |
| PomeriggioFine | Date | Not Null | | |
