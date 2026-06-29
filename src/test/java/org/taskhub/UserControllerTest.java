package org.taskhub;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.taskhub.config.SecurityConfig;
import org.taskhub.users.User;
import org.taskhub.users.UserController;
import org.taskhub.users.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}
