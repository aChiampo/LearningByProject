
## Dashboard - Veterinario

| EndPoint                            | Funzionalità                                                                     | Descrizione Service                                                                                                                                      | Accessibile da                 | Extra | Esiste |
| ----------------------------------- | -------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------ | ----- | ------ |
| api/visite/future                   | Raccoglie tutti dati sulle visite future in base al ruolo di chi fa le richieste | [ottieniVisiteProgrammateVet](#service-visite)<br>[ottieniVisiteProgrammateCliente](#service-visite)<br>[ottieniTutteVisiteProgrammate](#service-visite) | ADMIN, VET, CLIENTE, RECEPTION |       | NO     |
| api/animali/{nomeAnimale}           | Cerca un animale per nome animale                                                | [ottieniAnimaleByNome](#service-animali)                                                                                                                 | ADMIN, VET, CLIENTE, RECEPTION |       |        |
| api/animali/{nomeCliente}           | Cerca gli animali di un cliente                                                  | [ottieniAnimaleByPropietario](#service-animali)                                                                                                          |                                |       |        |
|                                     | Creare un nuovo Evento custom                                                    |                                                                                                                                                          |                                |       |        |
| api/visite/creaResoconto/{idVisita} | Crea il resoconto della visita: il dottore aggiunge note e allegati              |                                                                                                                                                          |                                |       |        |
| api/visite/modifica/{idVisita}      |                                                                                  |                                                                                                                                                          |                                |       |        |
 
## Service-Visite 
| Nome Metodo Service             | Funzionalità                                                              | QUERY                                                                                               | Extra | Esiste |
| ------------------------------- | ------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------- | ----- | ------ |
| ottieniVisiteProgrammateVet     |                                                                           |                                                                                                     |       |        |
| ottieniVisiteProgrammateCliente | Cerca le visite dove idAnimale è associato all'idUtente passato dal RUOLO | select from VISITE<br>join Animali on idAnimale<br>where an.idUtente = IDUtente(ottenuto dal ruolo) |       |        |
| ottieniTutteVisiteProgrammate   |                                                                           |                                                                                                     |       |        |
|                                 |                                                                           |                                                                                                     |       |        |
## Service-Animale

| Nome Service                | Funzionalità | Descrizione Service | Extra | Esiste |
| --------------------------- | ------------ | ------------------- | ----- | ------ |
| ottieniAnimaleByPropietario |              |                     |       |        |
| ottieniAnimaleByNome        |              |                     |       |        |
