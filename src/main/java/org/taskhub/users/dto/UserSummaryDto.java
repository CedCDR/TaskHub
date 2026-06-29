package org.taskhub.users.dto;

import jakarta.validation.constraints.NotBlank;

public record UserSummaryDto(
        Long id,

        String firstName,

        String lastname,

        String profilePictureUrl
) {
}
