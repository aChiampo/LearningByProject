## CONTEXT
I'm going to develop a web app for a veterinary clinic. At the moment, I need to create a simple clickable mockup.
In my application there will be users of 4 types:
- Super Admin
- doctor/veterinarian
- receptionist
- the clinic's clients
Each have different logic privileges
 
## TASK
Your task is to create a mockup using the instructions in this prompt.
If anything inside this document or about my request is UNCLEAR you can ask me for specification, in these cases propose to me the different options.
- Inside the [PAGES](#pages) section you can find how is structured the web application, follow that schema, use the internal anchor to read the structure properly.
- Add the possibility to switch the user type (just to presentation purposes)
- Add the possibility to switch primary and secondary colour of the pages (just to presentation purposes)
	- Use 2 separate control switches 
	- For each should be possible to choose between 6 different colors 

## LIMITATIONS
- All mockup content should be in Italian.
- You can use only simple HTML and CSS.
 
## PAGES
### homepage
This page should contain:
- A sign-in button
- A log-in button
- Fictional information about the clinic

### dashboard-super-admin
This page still needs to be implemented.

### dashboard-doctor
Page: **Doctor Dashboard**
This dashboard should contain, in order:
- The [navbar](#navbar-doctor)
- A calendar view of the current week, showing the appointments
  with the option to change the displayed week using buttons
  and a button to insert a custom event
- A search bar to select a specific animal (placeholder="Search by: Owner or Animal name")
  After submission, a list of animals with the corresponding names should appear.
  For every animal, the following should be visible: \[animalName], \[clientName], \[breed], and a button pointing to [Medical Record](#view-record-doctor)
- A list of upcoming appointments (max 10)
  Each should contain:
	- \[animal_name], \[type_of_visit], \[starting_hour], \[ending_hour]
	- A button to create a report
	- A button to delete the appointment

### dashboard-receptionist
Page: **Receptionist Dashboard**
This dashboard should contain:
- The [navbar](#navbar-receptionist)
- A calendar view of the current week, showing the appointments
  with the option to change the displayed week using buttons
  and a button to insert a custom event
- A list of upcoming appointments (max 10)
  Each should contain:
	- \[animal_name], \[type_of_visit], \[starting_hour], \[ending_hour]
	- A button to edit the appointment
	- A button to delete the appointment

### dashboard-client
Page: **Client Dashboard**
This dashboard should contain, in order:
- The [navbar](#navbar-client)
- A list of upcoming appointments
  Each should contain:
	- \[animal_name], \[type_of_visit], \[starting_hour], \[ending_hour]
	- A button to modify the appointment
	- A button to delete the appointment
- A list of animals, with a button nearby to add a new animal
  Each card should contain:
	- \[name], \[breed], \[sex]
	- A button to open the [animal record](#view-record-client) view
	- A button to [book an appointment](#booking-client)

### view-record-client
Page: **Client Medical Record**
This page should show:
- Animal data: \[name], \[surname], \[breed], \[birth_date], \[sex], \[weight], \[chip_number]
- Vaccines: \[list_of_vaccines_done]
- A list of cards containing [visit reports](#view-visit-report-client)
- A box to upload a medical record file

### view-record-doctor
Page: **Doctor Medical Record**
This page should show:
- Owner: \[name], \[surname]
- Animal data: \[name], \[surname], \[breed], \[birth_date], \[sex], \[weight], \[chip_number]
- Vaccines: \[list_of_vaccines_done]
- A button to edit the data above
- A list of cards containing [visit reports](#view-visit-report-doctor)

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
This page should contain:
- A search bar to select a specific animal (placeholder="Search by: Owner or Animal name")
- A list of animal cards
  For every animal, the following should be visible: \[animalName], \[clientName], \[breed], and a button pointing to [Medical Record](#view-record-doctor)

### view-appointment-receptionist
Page: **Receptionist Appointments**
This page should show:
- A search bar to filter the list of appointments (placeholder="Search by: Owner or Animal name")
- A list of booked appointments with an edit button and a delete button
- A button to book a new appointment

### view-clients-receptionist
This page should show:
- A search bar to filter the list of clients (placeholder="Search by: Owner or Animal name")
- A list of client cards with buttons for:
	- editing client data
	- linking to [payments](#view-payment-receptionist)
	- booking a new appointment

### booking-client
Page: **Booking Appointment**
This page should show:
- A form asking for the following data:
	- select: animal name
	- select: type of visit
	- select: doctor
	- select: available time slot

### view-payment-client
Page: **Payment Page**
This page should contain:
- A list of pending payments with a button (Pay) for the client to pay
- A list of previous payments with a button (Download) for the client to download the invoice

### view-payment-receptionist
Page: **Payment Page**
This page should contain:
- A search bar to filter the list of clients (placeholder="Search by: Owner")
- A list of pending payments with a button (Send reminder) to send an email to all clients
- A list of previous payments with a button (Download) to download the invoice for all clients

### navbar-receptionist
This navbar should contain links to:
- [Dashboard](#dashboard-receptionist)
- [Manage Appointment](#view-appointment-receptionist)
- [Manage Clients](#view-clients-receptionist)
- [Manage Payments](#view-payment-receptionist)

### navbar-client
This navbar should contain links to:
- [Dashboard](#dashboard-client)
- [Book Appointment](#booking-client)
- [Manage Payments](#view-payment-client)

### navbar-doctor
This navbar should contain links to:
- [Dashboard](#dashboard-doctor)
- [Manage Animals](#view-animal-doctor)
