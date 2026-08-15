package org.taskhub.roles;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record UserRoleUpdateDto(
        @NotNull(message = "Die Rollen-Liste darf nicht null sein")
        Set<String> roleNames
) {}