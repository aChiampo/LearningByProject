package com.WW.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FirstAppointmentRequestDTO(

        @NotBlank(message = "Il nome e cognome è obbligatorio") @Size(max = 100) String clientName,

        @NotBlank(message = "L'email è obbligatoria") @Email(message = "Formato email non valido") String email,

        @NotBlank(message = "Il telefono è obbligatorio") @Pattern(regexp = "^[+]?[0-9\\s]{6,20}$", message = "Formato telefono non valido") String phone,

        @NotBlank(message = "Il nome dell'animale è obbligatorio") @Size(max = 50) String petName,

        @NotBlank(message = "Seleziona un animale") String animalType, // e.g. "Cane", "Gatto"

        @NotBlank(message = "Seleziona il motivo della visita") String visitReason, // e.g. "Controllo generale"

        @Size(max = 500, message = "Le note non possono superare i 500 caratteri") String notes // optional

) {
}
