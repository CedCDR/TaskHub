package org.taskhub.users.dto;

import java.util.Set;

public record UserDetailDto(
        Long id,

        String firstName,

        String lastName,

        String email,

        String phoneNumber,

        String profilePictureURL,

        //Die Rollen werden dem Frontend als String geschickt, um JSON Endlosschleife zu verhindern
        Set<String> roles
) {

}
