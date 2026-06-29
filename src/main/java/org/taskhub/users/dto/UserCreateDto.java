package org.taskhub.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank(message = "Vorname darf nicht leer sein")
        String firstName,

        @NotBlank(message = "Nachname darf nicht leer sein")
        String lastName,

        @Email(message = "Muss eine gültige E-Mail sein")
        @NotBlank
        String email,

        @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein")
        String password
) {}