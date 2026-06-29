package org.taskhub.users;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.taskhub.roles.Role;
import org.taskhub.users.dto.UserCreateDto;
import org.taskhub.users.dto.UserDetailDto;
import org.taskhub.users.dto.UserSummaryDto;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailDto> getUserById(@PathVariable Long id)
    {
        User user = userService.getUserById(id);

        //UserDetailDto benutzt Strings, um die Rollen darzustellen
        Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        UserDetailDto response  = new UserDetailDto(
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPhoneNumber(), user.getProfilePictureURL(), roleNames);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getAllUsers()
    {
        List<User> users = userService.getAllUsers();

        List<UserSummaryDto> response = users.stream()
                .map(user -> new UserSummaryDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getProfilePictureURL()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserDetailDto> createUser(@Valid @RequestBody UserCreateDto dto)
    {
        // Service erstellt eine User-Entität in der Datenbank
        User createdUser = userService.createUser(dto);

        URI location = URI.create("/api/v1/users/" + createdUser.getId());

        // User-Entität in DTO umwandeln
        Set<String> roleNames = createdUser.getRoles().stream()
                .map(Role::getName).collect(Collectors.toSet());

        UserDetailDto responseDto = new UserDetailDto(
                createdUser.getId(),
                createdUser.getFirstName(),
                createdUser.getLastName(),
                createdUser.getEmail(),
                createdUser.getPhoneNumber(),
                createdUser.getProfilePictureURL(),
                roleNames
        );

        return ResponseEntity.created(location).body(responseDto);
    }
}
