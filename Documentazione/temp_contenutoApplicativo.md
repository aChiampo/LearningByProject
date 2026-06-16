## CONTEXT
I'm going to develop a web app for a Veterinary clinic, for the moment i have to create a simple clickable mockup.

## TASK
Your task is to create a mockup using the instructions in this prompt

## LIMITATIONS
- All the mockup content should be in italian
- You can use only simple html and css
- Inside the `Page`



 Gli utenti saranno di 4 tipi:
 - Super Admin
 - dottore/veterinario
 - receptionist 
 - I clienti dello studio

## PAGES
### homepage
This page should contain:
- A sing in button
- A log in button
- Finctional info about the clinic
### dashboard-super-admin
This page still to be implemented

### dashboard-doctor
Page: **doctor Dashboard**
This dashboard should contain (in order):
- The [navbar](#navbar-doctor)
- A calendar view of the current week (showing the appointments)
  With the possibility of change displayed week (using buttons)
  A button to insert a custom Event
- A search bar to select a specific animal (placeholder="Cerca per: Proprietario o Nome animale")
  After the submit should appear a list of animals with corresponding names
  For every animal should be visible: \[nomeAnimale], \[nomeCliente], \[razza], a button pointing to [Cartella Clinica](#view-record-doctor)
- A list of next appointments (max 10)
  Each should contain:
	- \[animal_name],\[type_of_visit],\[starting_hour],\[ending_hour],
	- A button to create a report
	- A button to delete the appointment
### dashboard-receptionist
Page: **receptionist Dashboard**
This dashboard should contain:
- The [navbar](#navbar-receptionist)
- A calendar view of the current week (showing the appointments)
  With the possibility of change displayed week (using buttons)
  A button to insert a custom Event
- A list of next appointments (max 10)
  Each should contain:
	- \[animal_name],\[type_of_visit],\[starting_hour],\[ending_hour],
	- A button to edit the appointment
	- A button to delete the appointment
### dashboard-client
Page: **client Dashboard**
This dashboard should contain ( in order):
- The [navbar](#navbar-client)
- A list of next appointments
  Each should contain:
	- \[animal_name],\[type_of_visit],\[starting_hour],\[ending_hour],
	- A button to modify the appointment
	- A button to delete the appointment
- A list of animals (near it a button to add a new animal)
	  Each card should contain:
	-  \[nome], \[razza], \[sesso]
	- A button to open [animal record](#view-record-client) view
	- A button to [book an appointments](#booking-client)




### view-record-client
Page: **Medical record client**
This page should show:
- Dati dell'animale: \[nome], \[cognome], \[razza], \[data_nascita], \[sesso], \[peso], \[numero_chip]
- Vaccines : \[list_of_vaccines_done]
- A list of cards containing [visit report](#view-visit-report-client)
- A box to upload a medical record file
### view-record-doctor
Page: **medical record doctor**
This page should show:
- Proprietario: \[nome] , \[cognome]
- Dati dell'animale: \[nome], \[cognome], \[razza], \[data_nascita], \[sesso], \[peso], \[numero_chip]
- Vaccines : \[list_of_vaccines_done]
- A button to edit the data above
- A list of cards containing [visit report](#view-visit-report-doctor)
### view-visit-report-client
This card should show:
- \[type_of_visit], \[date]
- Public note: \[public_note]
- Attachments: \[list_of_file_attachment]
### view-visit-report-doctor
This card should show:
- \[type_of_visit], \[date]
- Public note: \[public_note]
- Private note: \[private_note]
- Attachments: \[list_of_file_attachment]
- A button to edit the visit report

### view-animal-doctor
This page should contain
- A search bar to select a specific animal (placeholder="Cerca per: Proprietario o Nome animale")
- A list of animals cards
  For every animal should be visible: \[nomeAnimale], \[nomeCliente], \[razza], a button pointing to [Cartella Clinica](#view-record-doctor)

### view-appointment-receptionist
Page: **appointments receptionist**
This page should show:
- A search bar to filter the list of appointment (placeholder="Cerca per: Proprietario o Nome animale")
- A list of booked appointment with edit button and delete button
- A button to book a new appointment

### view-clients-receptionist
This page should show:
- A search bar to filter the list of clients (placeholder="Cerca per: Proprietario o Nome animale")
- A list of clients cards with buttons for:
	- edit clients data
	- link to [payments](#view-payment-receptionist)
	- A button to book a new appointment

### booking-client
Page : **booking appointment**
This page should show:
- a form asking for data as:
	- select: Animal name
	- select: type of visit
	- select: doctor
	- select: available time slot


### view-payment-client
Page: **payment page**
This page should contain:
- A list of pending payments with a button (Paga) to pay (of the client)
- A list of previous payments with a button (Scarica) to download the invoice (of the client)
  
### view-payment-receptionist
Page: **payment page**
This page should contain:
- A search bar to filter the list of clients (placeholder="Cerca per: Proprietario")
- A list of pending payments with a button (Invia promemoria) to send email (all clients)  
- A list of previous payments with a button (Scarica) to download the invoice (all clients)

### navbar-receptionist
This navbar should contain links to:
- [Dashboard](#dashboard-receptionist)
- [Manage_appointment](#view-appointment-receptionist)
- [Manage Clients](#view-clients-receptionist)
- [Manage Payments](#view-payment-receptionist)


### navbar-client
This navbar should contain links to:
- [Dashboard](#dashboard-client)
- [Book_appointment](#booking-client)
- [Manage Payments](#view-payment-client)

### navbar-doctor
This navbar should contain links to:
- [Dashboard](#dashboard-doctor)
- [Manage Animals](#view-animal-doctor)