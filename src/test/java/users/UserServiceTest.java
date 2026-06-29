package users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.taskhub.roles.Role;
import org.taskhub.roles.RoleRepository;
import org.taskhub.users.User;
import org.taskhub.users.dto.UserCreateDto;
import org.taskhub.users.UserRepository;
import org.taskhub.users.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
    void getUserById_ThrowsException_WhenUserDoesntExist()
    {
            // 1. Mockito Setup: Wir lassen das Repository beim Suchen nach ID 99L ein leeres Optional zurückgeben,
            // was in der Regel darauf hindeutet, dass der Benutzer nicht existiert.
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Wir verwenden assertThrows, um zu überprüfen, ob die erwartete Exception (hier RuntimeException als Platzhalter) 
            // geworfen wird, wenn das Optional leer ist.
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                userService.getUserById(99L);
            });

            assertTrue(exception.getMessage().contains("User nicht gefunden"));
    }

    // ==================================
    // Tests für createUser()
    // ==================================

    @Test
    void createUser_CreatesAndReturnsUserDto()
    {
        UserCreateDto dummyUser = new UserCreateDto(
                "Cedric", "Abissa", "c.abissa@gmail.com", "Test1234");

        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setFirstName("Cedric");
        expectedUser.setLastName("Abissa");
        expectedUser.setPassword("GehashtesPasswort");

        when(passwordEncoder.encode("Test1234")).thenReturn("GehashtesPasswort");
        // Da der Service das User-Objekt intern mit 'new User()' frisch erstellt,
        // sagen wir dem Mock: "Egal welches User-Objekt du gleich zum Speichern kriegst,
        // nimm es an und gib unseren expectedSavedUser zurück."
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User result = userService.createUser(dummyUser);

        verify(userRepository, times(1)).save(any(User.class));
        assertNotNull(result);
        assertEquals("Cedric", result.getFirstName());
    }

    @Test
    void createUser_HashesPasswordBeforeSaving()
    {
        UserCreateDto dummyUser = new UserCreateDto(
                "Cedric", "Abissa", "c.abissa@gmail.com", "Test1234");
        when(passwordEncoder.encode("Test1234")).thenReturn("GehashtesPasswort");

        userService.createUser(dummyUser);

        // Wir bereiten eine "Kamera" vor, die speziell darauf eingestellt ist, ein User-Objekt aufzunehmen
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        // Wir prüfen, ob save() aufgerufen wurde. Gleichzeitig fängt .capture() das exakte User-Objekt ab, das der Service in diesem Moment an die Datenbank schickt.
        verify(userRepository).save(userCaptor.capture());

        // Wir holen das abgefangene Objekt aus dem Captor heraus, um seine Werte zu überprüfen
        User capturedUser = userCaptor.getValue();

        assertEquals("GehashtesPasswort", capturedUser.getPassword());
    }

    @Test
    void createUser_ThrowsException_WhenEmailAlreadyExists()
    {
        UserCreateDto dummyUserDto = new UserCreateDto(
                "Cedric", "Abissa", "c.abissa@gmail.com", "Test1234");

        when(userRepository.existsByMail(dummyUserDto.email())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(dummyUserDto);
        });

        assertTrue(exception.getMessage().contains("E-Mail ist bereits vergeben"));
        verify(userRepository, times(0)).save(any(User.class));

    }

    // ==================================
    // Tests für deleteUserById()
    // ==================================

    @Test
    void deleteUser_DeletesUserOnce_WhenUserExists()
    {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserById_ThrowsException_WhenUserDoesntExist()
    {
        // 1. Mockito Setup: Wir lassen das Repository beim Suchen nach ID 99L ein leeres Optional zurückgeben,
        // was in der Regel darauf hindeutet, dass der Benutzer nicht existiert.
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(99L);
        });

        verify(userRepository, times(0)).deleteById(99L);
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
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        User result = userService.assignRoleToUser(1L, 1L);

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
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));


        User result = userService.assignRoleToUser(1L, 1L);

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
        when(roleRepository.findById(99L)).thenReturn(Optional.of(duplicateAdminRole));

        // Wir versuchen, das Duplikat hinzuzufügen
        userService.assignRoleToUser(1L, 99L);

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
