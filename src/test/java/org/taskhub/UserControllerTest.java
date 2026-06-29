package org.taskhub;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.taskhub.config.SecurityConfig;
import org.taskhub.users.User;
import org.taskhub.users.UserController;
import org.taskhub.users.UserService;
import org.taskhub.users.dto.UserCreateDto;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    // ==================================
    // Tests für getUserById()
    // ==================================

    @Test
    void getUserById_ReturnsUserDetailDto_WhenUserExists() throws Exception
    {
        User dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setFirstName("Cedric");
        dummyUser.setEmail("c.abissa@gmail.com");

        when(userService.getUserById(1L)).thenReturn(dummyUser);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Cedric"))
                .andExpect(jsonPath("$.email").value("c.abissa@gmail.com"));
    }

    @Test
    void getUserById_Returns404_WhenUserDoesntExist() throws Exception
    {
        when(userService.getUserById(99L)).thenThrow(new RuntimeException("User nicht gefunden mit der ID: 99"));

        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound()) // Wir erwarten den 404 Status!
                .andExpect(content().string("User nicht gefunden mit der ID: 99"));
    }

    // =============================
    // Tests für getAllUsers()
    // =============================

    @Test
    void getAllUsers_ReturnsListOfUserSummaryDtos_WhenUsersExist() throws Exception {

        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Spongebob");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Patrick");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                // Prüft, ob das JSON-Array genau 2 Elemente hat
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Spongebob"))
                .andExpect(jsonPath("$[1].firstName").value("Patrick"));
    }

    @Test
    void getAllUsers_ReturnsEmptyList_WhenDatabaseIsEmpty() throws Exception {

        when(userService.getAllUsers()).thenReturn(List.of()); // Leere Liste vom Service

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk()) // Es muss trotzdem ein 200 OK sein!
                .andExpect(jsonPath("$", hasSize(0))); // Das JSON-Array muss leer sein
    }

    // ==========================
    // Tests für createUser()
    // ==========================

    @Test
    void createUser_Returns201AndUserDetailDto_WhenDataIsValid() throws Exception
    {
        UserCreateDto requestDto  = new UserCreateDto(
                "Cedric", "Abissa",
                "c.abissa@gmail.com", "Test1234");

        User savedUser = new User();
        savedUser.setId(67L);
        savedUser.setFirstName("Cedric");
        savedUser.setLastName("Abissa");
        savedUser.setEmail("c.abissa@gmail.com");
        savedUser.setRoles(Set.of());

        when(userService.createUser(any(UserCreateDto.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/v1/users")
                // Das Request ist in JSON-Format
                .contentType(MediaType.APPLICATION_JSON)
                // Objekt in JSON wandeln
                .content(objectMapper.writeValueAsString(requestDto)))
                // Erwarte 201 Created
                .andExpect(status().isCreated())
                // Prüfe den Location Header
                .andExpect(header().string("Location", "/api/v1/users/67"))
                // Prüfe die generierte ID im Body
                .andExpect(jsonPath("$.id").value(67))
                .andExpect(jsonPath("$.firstName").value("Cedric"));

    }

    @Test
    void createUser_Returns400BadRequest_WhenDataIsInvalid() throws Exception
    {
        UserCreateDto invalidDto = new UserCreateDto("Cedric", "Abissa",
                "keine-mail","abc");

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
