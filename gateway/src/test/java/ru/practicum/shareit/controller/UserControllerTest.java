package ru.practicum.shareit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    void getAll_shouldReturnOk() throws Exception {
        when(userClient.getUsers()).thenReturn(null);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(userClient.getUser(anyLong())).thenReturn(null);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturnOk() throws Exception {
        when(userClient.createUser(any())).thenReturn(null);

        String jsonContent = "{ \"name\": \"John Doe\", \"email\": \"john@example.com\" }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        when(userClient.updateUser(anyLong(), any())).thenReturn(null);

        String jsonContent = "{ \"name\": \"John Updated\", \"email\": \"john.updated@example.com\" }";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnOk() throws Exception {
        when(userClient.deleteUser(anyLong())).thenReturn(null);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}