## UTENTE
- ID, Primary Key, int;
- Nome, Not Null, varchar(30);
- Cognome, Not Null, varchar(30);
- Email, Not Null, varchar(40);
- Password, Not Null, varchar(40);      <!-Da cryptare prima dell'inserimento nel->
- Telefono, Not Null, varchar(10);
- Indirizzo, varchar(30);
- Citta, varchar(20);
- Data_Registrazione, Not Null, Date;
- Riferimento, varchar(30);

- Foreign Key  ID_Ruolo --> FROM Ruolo ID;

### RUOLO
- ID, Primary Key, int;
- Ruolo, Not Null, varchar(15);


## ANIMALE
- ID, Primary Key, int;
- Nome, Not Null, varchar(30);
- Specie, varchar(15);
- Razza, varchar(20);
- Sesso, Bool;
- Data_Nascita, Not Null, Date;
- Peso, double;
- Microchip, varchar(15);      <!-Nullabile perchè i conigli non hanno microchip->
- Note, text;

- Foreign Key  ID_Utente --> FROM Utente ID;


## VACCINAZIONI 
- ID, Primary Key, int;
- ID_Tipo, Not Null, int;
- Data_Vaccinazione, Date;
- Lotto, varchar(10);

- Foreign Key  ID_Animale --> FROM Animale ID;
- Foreign Key  ID_Tipo_Vaccino --> FROM Tipo_Vaccino ID;

### TIPO VACCINO
- ID, Primary Key, int;
- Tipologia, NotNull, varchar(40);
- Durata, Not Null, int;                <!-Durata espressa in mesi->
- Note, text;


## VISITA
- ID, Primary Key, int;
- ID_Animale, int, NotNull;
- Data_Visita, Not Null, Date;
- ID_Tipo, Not Null, int;
- ID_Dottore, not Null, int;
- ID_Pagamento, int;
- Stato: enum(PRENOTATA,COMPLETATA,NO_SHOW)
- Note, text;

- Foreign Key  ID_Animale --> FROM Animale ID;
- Foreign Key  ID_Tipo --> FROM Tipi_Visita ID;
- Foreign Key ID_Pagamento --> FROM Pagamenti ID
- Foreign Key ID_Dottore --> FROM Utenti ID

### TIPI VISITA 
- ID, Primary Key, int;
- Durata, int;                          <!-Durata espressa in minuti->
- Descrizione, text;
- Prezzo, Not Null, double;
- ID_Dottore, Not Null, int;


## PAGAMENTI
- ID, Primary Key, int;
- Data, Not Null, Date;
- Tipo_Pagamento, varchar(30);          <!-Oppure bool se pos / contanti->
- Importo_Totale, Not Null, double;

- Foreign Key  ID_Utente --> FROM Utente ID;


## EVENTI APP
- ID_Utente, Primary Key, int;
- ID_Oggetto, Primary Key, int;
- Tipo_Evento, Not Null, varchar(20);
- Ruolo, varchar(30);
- Oggetto_Azione, varchar(30);
- Time_Stamp, timestap();               <!-Data e ora->
- Metadata, text;

- Foreign Key  ID_Utente --> FROM Utente ID;
- Foreign Key  ID_Ruolo --> FROM Ruolo ID;

## ORARIO SETTIMANALE

- ID_Utente, Primary Key, int;
- GiornoSettimana, int, NotNull;    <!-Da 1 a 7->
- MattinaInizio Date, NotNull;
- MattinaFine Date, NotNull;
- PomeriggioInizio Date, NotNull;
- PomeriggioFine Date, NotNull;


