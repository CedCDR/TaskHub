package org.taskhub;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.taskhub.config.SecurityConfig;
import org.taskhub.users.User;
import org.taskhub.users.UserController;
import org.taskhub.users.UserService;

import static org.mockito.Mockito.when;
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
}
