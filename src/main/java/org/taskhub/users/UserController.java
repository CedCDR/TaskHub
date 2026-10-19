package org.taskhub.users;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.taskhub.roles.Role;
import org.taskhub.roles.UserRoleUpdateDto;
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
        UserDetailDto response  = UserDetailDto.fromEntity(user);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getAllUsers()
    {
        List<User> users = userService.getAllUsers();

        List<UserSummaryDto> response = users.stream()
                .map(UserSummaryDto::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserDetailDto> createUser(@Valid @RequestBody UserCreateDto dto)
    {
        // Service erstellt eine User-Entität in der Datenbank
        User createdUser = userService.createUser(dto);

        URI location = URI.create("/api/v1/users/" + createdUser.getId());

        // Erstellte User-Entität in DTO umwandeln
        UserDetailDto responseDto = UserDetailDto.fromEntity(createdUser);

        return ResponseEntity.created(location).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id)
    {
        // Dank GlobalExceptionHandler wird hier 404 abgefangen und returned
        // Wenn der Funktionsaufruf eine RuntimeException auslöst
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDetailDto> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {

        User updatedUser = userService.assignRoleToUser(userId, roleId);

        UserDetailDto responseDto = UserDetailDto.fromEntity(updatedUser);

        // Status 200 OK mit dem aktualisierten Profil zurückgeben
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDetailDto> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {

        User updatedUser = userService.removeRoleFromUser(userId, roleId);

        UserDetailDto responseDto = UserDetailDto.fromEntity(updatedUser);

        return ResponseEntity.ok(responseDto);
    }
}
