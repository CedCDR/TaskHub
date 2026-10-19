package org.taskhub.users.dto;

import jakarta.validation.constraints.NotBlank;
import org.taskhub.users.User;

public record UserSummaryDto(
        Long id,

        String firstName,

        String lastName,

        String profilePictureUrl
)
{

    public static UserSummaryDto fromEntity(User user)
    {
        return new UserSummaryDto(user.getId(), user.getFirstName(),
                user.getLastName(), user.getProfilePictureURL());
    }
}
