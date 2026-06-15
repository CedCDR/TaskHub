import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.taskhub.roles.Role;
import org.taskhub.roles.RoleRepository;
import org.taskhub.users.User;
import org.taskhub.users.UserRepository;
import org.taskhub.users.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ==================================
    // Tests für getUserById()
    // ==================================

    @Test
    void getUserById_ReturnsUser_WhenUserExists()
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        //Wenn das Repository nach ID 1 gefragt wird, gib den dummyUser zurück
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Cedric", result.getFirstName());
    }

    @Test
    void getUserById_ThrowsException_WhenUserDoesntExist() {
            // 1. Mockito Setup: Wir lassen das Repository beim Suchen nach ID 99L ein leeres Optional zurückgeben,
            // was in der Regel darauf hindeutet, dass der Benutzer nicht existiert.
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Wir verwenden assertThrows, um zu überprüfen, ob die erwartete Exception (hier RuntimeException als Platzhalter) 
            // geworfen wird, wenn das Optional leer ist.
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                userService.getUserById(99L);
            });

            // Sie können hier zusätzlich überprüfen, ob die Meldung korrekt ist:
            String expectedMessage = "User nicht gefunden mit ID: 99"; // Passen Sie das Format der Meldung an!
            assertTrue(exception.getMessage().contains("User nicht gefunden"));
    }

    // ==================================
    // Tests für createUser()
    // ==================================

    @Test
    void createUser_CreatesAndReturnsUser()
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        when(userRepository.save(dummyUser)).thenReturn(dummyUser);

        User result = userService.createUser(dummyUser);

        verify(userRepository, times(1)).save(dummyUser);
        assertNotNull(result);
        assertEquals("Cedric", result.getFirstName());
    }

    // ==================================
    // Tests für deleteUserById()
    // ==================================

    @Test
    void deleteUser_ReturnsTrueAndDeletes_WhenUserExists()
    {
        when(userRepository.existsById(1L)).thenReturn(true);
        boolean result = userService.deleteUser(1L);

        assertTrue(result);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserById_ThrowsException_WhenUserDoesntExist()
    {
        // 1. Mockito Setup: Wir lassen das Repository beim Suchen nach ID 99L ein leeres Optional zurückgeben,
        // was in der Regel darauf hindeutet, dass der Benutzer nicht existiert.
        when(userRepository.existsById(99L)).thenReturn(false);

        boolean result = userService.deleteUser(99L);

        assertFalse(result);
    }

    // ==================================
    // Tests für assignRoleToUser()
    // ==================================
    @Test
    void assignRoleToUser_AddsRoleToRolesList()
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");

        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(userRepository.save(dummyUser)).thenReturn(dummyUser);

        User result = userService.assignRoleToUser(1L, adminRole);

        assertTrue(result.getRoles().contains(adminRole));
        assertEquals(result.getRoles(), dummyUser.getRoles());
    }

    @Test
    void assignRoleToUser_AddsNothing_WhenRoleAlreadyExists()
    {
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ADMIN");

        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");
        dummyUser.addRole(adminRole);

        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(userRepository.save(dummyUser)).thenReturn(dummyUser);

        User result = userService.assignRoleToUser(1L, adminRole);

        assertEquals(1, dummyUser.getRoles().size());

    }

    @Test
    void assignRoleToUser_AddsNothing_WhenRoleWithSameNameExists() {
        // 1. ARRANGE
        Role existingAdminRole = new Role();
        existingAdminRole.setId(1L);
        existingAdminRole.setName("ADMIN");

        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");
        dummyUser.addRole(existingAdminRole); // Die erste Rolle ist schon im Set

        // Wir erstellen ein ZWEITES, komplett neues Objekt mit demselben Namen
        Role duplicateAdminRole = new Role();
        duplicateAdminRole.setId(99L); // Andere ID!
        duplicateAdminRole.setName("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(userRepository.save(dummyUser)).thenReturn(dummyUser);

        // Wir versuchen, das Duplikat hinzuzufügen
        userService.assignRoleToUser(1L, duplicateAdminRole);

        // 3. ASSERT
        // Das HashSet hat die zweite Rolle abgewehrt, weil der Name identisch ist!
        assertEquals(1, dummyUser.getRoles().size());
    }

    // ==================================
    // Tests für removeRoleFromUser()
    // ==================================

    @Test
    void removeRoleFromUser_ReducesSizeOfRoles()
    {
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ADMIN");

        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");
        dummyUser.addRole(adminRole);

        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(userRepository.save(dummyUser)).thenReturn(dummyUser);

        User result = userService.removeRoleFromUser(1L, adminRole);

        assertEquals(0, dummyUser.getRoles().size());
    }

    @Test
    void removeRoleFromUser_DoesNothing_WhenRoleDoesntExist()
    {
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ADMIN");

        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");
        dummyUser.addRole(adminRole);

        Role employeeRole = new Role();
        employeeRole.setId(99L);
        employeeRole.setName("MITARBEITER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));
        when(userRepository.save(dummyUser)).thenReturn(dummyUser);

        userService.removeRoleFromUser(1L, employeeRole);

        assertEquals(1, dummyUser.getRoles().size());
    }
}
