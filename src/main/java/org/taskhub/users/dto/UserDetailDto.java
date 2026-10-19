package org.taskhub.users.dto;

import org.taskhub.roles.Role;
import org.taskhub.users.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserDetailDto(
        Long id,

        String firstName,

        String lastName,

        String email,

        String phoneNumber,

        String profilePictureURL,

        //Die Rollen werden dem Frontend als String geschickt, um JSON Endlosschleife zu verhindern
        Set<String> roles
)
{
    public static UserDetailDto fromEntity(User user)
    {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName).collect(Collectors.toSet());

        return new UserDetailDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
        user.getPhoneNumber(), user.getProfilePictureURL(), roleNames);
    }
}
