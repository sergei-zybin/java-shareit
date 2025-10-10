package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_ShouldReturnItemDto() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto createdItem = new ItemDto();
        createdItem.setId(1L);
        createdItem.setName("Test Item");
        createdItem.setDescription("Test Description");
        createdItem.setAvailable(true);

        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(createdItem);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Item");
        updatedItem.setDescription("Test Description");
        updatedItem.setAvailable(true);

        when(itemService.update(eq(1L), any(ItemDto.class), eq(1L))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    void getItemById_ShouldReturnItemWithBookings() throws Exception {

        ItemDtoWithBookings item = new ItemDtoWithBookings();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(List.of());

        when(itemService.getById(eq(1L), eq(1L))).thenReturn(item);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.lastBooking").isEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty());
    }

    @Test
    void getItemById_WithBookings_ShouldReturnItemWithBookings() throws Exception {

        BookingShortDto lastBooking = new BookingShortDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(2L);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        BookingShortDto nextBooking = new BookingShortDto();
        nextBooking.setId(2L);
        nextBooking.setBookerId(3L);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        ItemDtoWithBookings item = new ItemDtoWithBookings();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        item.setComments(List.of());

        when(itemService.getById(eq(1L), eq(1L))).thenReturn(item);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastBooking.id").value(1L))
                .andExpect(jsonPath("$.nextBooking.id").value(2L));
    }

    @Test
    void getItemsByOwner_ShouldReturnItemList() throws Exception {

        ItemDtoWithBookings item1 = new ItemDtoWithBookings();
        item1.setId(1L);
        item1.setName("Item 1");

        ItemDtoWithBookings item2 = new ItemDtoWithBookings();
        item2.setId(2L);
        item2.setName("Item 2");

        when(itemService.getByOwner(eq(1L))).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void searchItems_ShouldReturnMatchingItems() throws Exception {

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Drill");
        item1.setDescription("Powerful drill");

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Hammer Drill");
        item2.setDescription("Professional hammer drill");

        when(itemService.search(eq("drill"))).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].name").value("Hammer Drill"));
    }

    @Test
    void searchItems_WithEmptyText_ShouldReturnEmptyList() throws Exception {

        when(itemService.search(eq(""))).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addComment_ShouldReturnComment() throws Exception {
        // Given
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto createdComment = new CommentDto();
        createdComment.setId(1L);
        createdComment.setText("Great item!");
        createdComment.setAuthorName("Test User");
        createdComment.setCreated(LocalDateTime.now());

        when(itemService.addComment(eq(1L), any(CommentDto.class), eq(1L))).thenReturn(createdComment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("Test User"));
    }
}