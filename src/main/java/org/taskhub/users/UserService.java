package org.taskhub.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.taskhub.roles.Role;
import org.taskhub.roles.RoleRepository;
import org.taskhub.tasks.Task;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//RequiredArgsConstructor erstellt einen Konstruktor für alle final Attribute
@RequiredArgsConstructor

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException( "User nicht gefunden mit der ID: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User createdUser) {
        return userRepository.save(createdUser);
    }

    public void deleteUser(Long id)
    {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User nicht gefunden  mit ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public User assignRoleToUser(Long userId, Long roleId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden mit der ID: " + userId));

        Role rolle = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rolle nicht gefunden mit der ID: " + roleId));

        user.addRole(rolle);
        return userRepository.save(user);
    }

    public User removeRoleFromUser(Long userId, Role rolle)
    {
        return userRepository.findById(userId).map(user  -> {
            user.removeRole(rolle);
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User nicht gefunden mit der ID: " + userId));
    }
}
