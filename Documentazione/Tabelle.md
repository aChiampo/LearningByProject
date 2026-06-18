## UTENTE
- ID, Primary Key, int;
- Nome, Not Null, varchar(30);
- Cognome, Not Null, varchar(30);
- Email, Not Null, varchar(40);
- Telefono, Not Null, varchar(10);
- Indirizzo, varchar(30);
- Citta, varchar(20);
- Data_Registrazione, Not Null, Date;
- Riferimento, varchar(30);

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
- Microchip, Not Null, int;
- Note, text;


## VACCINAZIONI 
- ID, Primary Key, int;
- ID_Tipo, Not Null, varchar(30);
- Data_Vaccinazione, Date;
- Lotto, varchar(10);
- Stato, Not Null, bool;

### TIPO VACCINO
- ID, Primary Key, int;
- Durata, Not Null, int; <!--Durata espressa in mesi-->
- Note, text;


## VISITA
- ID, Primary Key, int; 
- Data_Visita, Not Null, Date;
- ID_Tipo, Not Null, int;
- Note, text;
- ID_Pagamento int null **FK**

### TIPI VISITA 
- ID, Primary Key, int;
- Durata, int; <!--Durata espressa in minuti-->
- Descrizione, text;
- Prezzo, Not Null, double;
- ID_Dottore, Not Null, int;


## PRENOTAZIONI
- ID, Primary Key, int;
- Data_Visita, Not Null, Date;
- Tipo_Visita, varchar(30);


## PAGAMENTI
- ID, Primary Key, int;
- Data, Not Null, Date;
- Tipo_Pagamento, varchar(30); <!--Oppure bool se pos / contanti-->
- Importo_Totale, Not Null, double;