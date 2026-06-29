package org.taskhub.users.dto;

import jakarta.validation.constraints.NotBlank;

public record UserSummaryDto(
        @NotBlank(message = "Id darf nicht leer sein")
        Long id,

        @NotBlank(message = "Vorname darf nicht leer sein")
        String firstName,

        @NotBlank(message = "Nachname darf nicht leer sein")
        String lastname,

        String profilePictureUrl
) {
}
