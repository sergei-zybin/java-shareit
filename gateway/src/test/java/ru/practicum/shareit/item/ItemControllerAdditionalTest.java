package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerAdditionalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Test
    void create_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ \"name\": \"\", \"description\": \"\", \"available\": null }";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithoutUserId_ShouldReturnBadRequest() throws Exception {
        String validJson = "{ \"name\": \"Item\", \"description\": \"Description\", \"available\": true }";

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WithoutUserId_ShouldReturnBadRequest() throws Exception {
        String jsonContent = "{ \"name\": \"Updated Item\" }";

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_WithoutUserId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByOwner_WithoutUserId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_WithEmptyText_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ \"text\": \"\" }";

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_WithoutUserId_ShouldReturnBadRequest() throws Exception {
        String validJson = "{ \"text\": \"Great item!\" }";

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_WithEmptyText_ShouldReturnOk() throws Exception {
        when(itemClient.searchItems(anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk());
    }
}