package org.taskhub.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.taskhub.roles.Role;
import org.taskhub.roles.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//RequiredArgsConstructor erstellt einen Konstruktor für alle final Attribute
@RequiredArgsConstructor

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User createdUser) {
        return userRepository.save(createdUser);
    }

    public boolean deleteUser(Long id)  {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public User assignRolesToUser(Long userId, Role rolle){
         return userRepository.findById(userId).map(user -> {
             user.addRole(rolle);
             return userRepository.save(user);
         }).orElseThrow(() -> new RuntimeException("User nicht gefunden mit ID: " + userId));
    }
}
