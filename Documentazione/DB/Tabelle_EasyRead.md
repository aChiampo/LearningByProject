# Tabelle

> Documento aggiornato dal database PostgreSQL live. I nomi delle colonne seguono lo schema `public` corrente.

## UTENTI

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| nome | varchar(30) | Not Null |  |  |
| cognome | varchar(30) | Not Null |  |  |
| email | varchar(100) | Not Null, UNIQUE |  |  |
| password | varchar(255) | Not Null |  |  |
| telefono | varchar(20) | Not Null |  |  |
| indirizzo | varchar(30) |  |  |  |
| citta | varchar(20) |  |  |  |
| data_registrazione | timestamp | Not Null |  |  |
| id_azienda | integer |  | [`AZIENDE.id`](#aziende) |  |
| codice_fiscale | varchar(16) | Not Null, UNIQUE |  |  |
| id_ruolo | integer | Not Null | [`RUOLI.id`](#ruoli) |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## AZIENDE

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| ragione_sociale | varchar(100) | Not Null |  |  |
| partita_iva | varchar(11) | Not Null, UNIQUE |  |  |
| forma_giuridica | varchar(100) | Not Null |  |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## RUOLI

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| ruolo | varchar(15) | Not Null, UNIQUE |  |  |

## ANIMALI

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| nome | varchar(30) | Not Null |  |  |
| specie | varchar(15) |  |  |  |
| razza | varchar(20) |  |  |  |
| sesso | varchar(10) |  |  |  |
| data_nascita | date | Not Null |  |  |
| peso | double precision |  |  |  |
| microchip | varchar(15) | UNIQUE |  |  |
| note | text |  |  |  |
| id_utente | integer | Not Null | [`UTENTI.id`](#utenti) |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## SPECIE

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| nome | varchar(255) | Not Null |  |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## RAZZE

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| nome | varchar(255) | Not Null |  |  |
| id_specie | integer | Not Null | [`SPECIE.id`](#specie) |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## VACCINAZIONI

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| id_tipo | integer | Not Null | [`TIPI_VACCINO.id`](#tipi_vaccino) |  |
| data_vaccinazione | timestamp |  |  |  |
| lotto | varchar(255) |  |  |  |
| id_animale | integer | Not Null | [`ANIMALI.id`](#animali) |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## TIPI_VACCINO

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| tipologia | varchar(255) | Not Null |  |  |
| durata | integer | Not Null |  |  |
| note | varchar(255) |  |  |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## VISITA

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| data_visita | timestamp | Not Null |  |  |
| id_tipo_visita | integer | Not Null | [`TIPI_VISITE.id`](#tipi_visite) |  |
| id_animale | integer | Not Null | [`ANIMALI.id`](#animali) |  |
| id_veterinario | integer | Not Null | [`UTENTI.id`](#utenti) |  |
| id_pagamento | integer | UNIQUE | [`PAGAMENTI.id`](#pagamenti) |  |
| note | varchar(255) |  |  |  |
| is_deleted | boolean | Not Null, Default false |  |  |
| nota_privata | text |  |  |  |
| stato | enum stato_visita | Not Null |  | (PRENOTATA, COMPLETATA) |

## TIPI_VISITE

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| nome | varchar(80) | Not Null |  |  |
| durata | integer | Not Null |  |  |
| id_categoria | integer |  | [`CATEGORIE_VISITE.id`](#categorie_visite) |  |
| prezzo | numeric(8,2) | Not Null |  |  |
| id_dottore | integer | Not Null | [`UTENTI.id`](#utenti) |  |
| attivo | boolean | Not Null, Default true |  |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## CATEGORIE_VISITE

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| nome | varchar(100) | Not Null |  |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## PAGAMENTI

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| data | date | Not Null |  |  |
| tipo_pagamento | varchar(30) |  |  |  |
| importo_totale | numeric(8,2) | Not Null |  |  |
| id_utente | integer |  | [`UTENTI.id`](#utenti) |  |
| riferimento_file | integer |  | [`FILE_REFERENCE.id`](#file_reference) |  |
| is_deleted | boolean | Not Null, Default false |  |  |

## EVENTI_APP

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| id_utente | integer | Not Null | [`UTENTI.id`](#utenti) |  |
| id_ruolo | integer | Not Null | [`RUOLI.id`](#ruoli) |  |
| tipo_evento | varchar(20) | Not Null |  |  |
| id_oggetto | integer |  |  |  |
| oggetto_azione | varchar(30) |  |  |  |
| time_stamp | timestamp |  |  |  |
| metadata | jsonb |  |  |  |
| is_deleted | boolean |  |  |  |

## ORARIO_SETTIMANALE

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| giorno_settimana | integer | Not Null |  | Valore da 1 a 7 |
| mattina_inizio | integer | Not Null |  |  |
| mattina_fine | integer | Not Null |  |  |
| pomeriggio_inizio | integer | Not Null |  |  |
| pomeriggio_fine | integer | Not Null |  |  |
| id_utente | integer | Not Null | [`UTENTI.id`](#utenti) |  |

## FILE_REFERENCE

| Nome | TipoDato | Flag | ForeignKey | Commenti |
| --- | --- | --- | --- | --- |
| id | integer | Primary Key, Not Null |  |  |
| original_file_name | varchar(255) | Not Null |  |  |
| stored_file_name | varchar(255) | Not Null |  |  |
| mime_type | varchar(255) | Not Null |  |  |
| ssize | bigint | Not Null |  |  |
| storage_path | varchar(255) | Not Null |  |  |
| owner | integer | Not Null | [`UTENTI.id`](#utenti) |  |
| upload_date | timestamp | Not Null |  |  |
| is_deleted | boolean | Not Null, Default false |  |  |

