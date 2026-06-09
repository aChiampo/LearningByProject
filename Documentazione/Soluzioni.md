
## Entities
### Animali
- idUnivoco_chipCode
- propetario
- nomeAnimale
- Eta
- idProp FK
	- **CANI**
	- **GATTI**
	- **PICCOLI ANIMALI STRANI**


## Cartella medica
- idAnimale PK
- idProp FK
- idDoc FK
- storico visite from report text-file
- note text
- esami fatti\[]
- vaccini\[]
- farmaci che prende\[]

## Utente APP
- id
- nome
- cognome
- mail
- telefono
- Ruolo:
	- [Cliente](#Cliente)
	- Doc
	- admin
	- receptionist
	  
## Cliente
- Prop Animali\[[id](#Animali)]

## Dottore

## Receptionist

## Vaccini
- id 
- nome
- tipo

## Visite
- id
- data 
- ora