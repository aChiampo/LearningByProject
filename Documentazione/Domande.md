
### Aziende
- Lavora con aziende?
*per ora no, non escludo in futuro*
-> direi di aggiungere un campo int ad 'Utenti' che riferisce ad una nuova tabella 'Dati aziendali' che contiene le informazioni relative ( in pratica solo quelle utili ad emettere fatture)

### Protocollo Prima visita
- Come funziona la prima visita? 
	Vuole selezionare a priori se accettare nuovi pazienti?
	O solo se è una razza particolare?

*se si aggiunge un'animale strano si fa una richiesta al dottore. se invece è un'animale normale si effettua la prenotazione aggiungendo una descrizione ma senza inserire i dati(viene creato un animale con i dati a 0)*

### Dimensioni DB
va stabilito quanto spazio è necessario su DB / fileSystem ->
	- chiarire il tipo di ogni campo 
	- i referti degli esami sono file da uploadare e downlodare -> 
		- estensioni? 
		- quanti +- per paziente?

*solo pdf*


### quali animali?
 Basic domestici + 1 `Altro` ?
*Cane - Gatto - Conigli - AltroGenerico*

### tipo di visite?
- \[nome],\[durata],\[costo]

*da consegnare*

### Orario appuntamenti?
Flessibile a piacere o fisso con appuntamenti filler?

* Standard 9-13 14-18*

### Statistiche?
Interessano si/no?
- Richieste particolari?


*Usage appuntamenti, collaboratori/ utenti più attivi ,a piacere*


### Aggiungere nuovi dipendenti
- lo fa l'assistenza oppure il propietario dell'admin

### Richieste aggiuntive? Discutere

avvisi in scadenza

reception>notifiche ritardi


### Come gestiamo la possibilità di aggiungere nuovi eventi? 
  esempio un dottore vuole segnare che per 2 ore un lunedì non può ricevere pazienti
  Ora le Visite hanno molti campi non nullable che impedirebbero la creazione di un evento personalizzato
	  - Creiamo dei Animali, tipoVisite "Evento Personalizzato" per simulare l'esistenza di una visita
	  - 
insert into Animali (Nome,Data_Nascita,Microchip,Id_Utente) values('EventoPersonale',01/01/1970,?,?)
? = String uniqueId = UUID.randomUUID().toString().replace("-", "");
?= Id_Dottore	    