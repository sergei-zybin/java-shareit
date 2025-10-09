package ru.practicum.shareit.misc;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DtoCoverageTest {

    @Test
    void allDtos_ShouldWorkWithAllConstructorsAndBuilders() {

        ItemDto item1 = new ItemDto();
        ItemDto item2 = new ItemDto(1L, "Item", "Desc", true, 1L);
        ItemDto item3 = ItemDto.builder()
                .id(2L).name("Item2").description("Desc2").available(false).requestId(2L)
                .build();

        assertNotNull(item1);
        assertNotNull(item2);
        assertNotNull(item3);
        assertEquals("Item", item2.getName());

        UserDto user1 = new UserDto();
        UserDto user2 = new UserDto(1L, "User", "user@test.com");
        UserDto user3 = UserDto.builder()
                .id(2L).name("User2").email("user2@test.com")
                .build();

        assertNotNull(user1);
        assertNotNull(user2);
        assertNotNull(user3);
        assertEquals("User", user2.getName());

        LocalDateTime now = LocalDateTime.now();
        CommentDto comment1 = new CommentDto();
        CommentDto comment2 = new CommentDto(1L, "Text", "Author", now);
        CommentDto comment3 = CommentDto.builder()
                .id(2L).text("Text2").authorName("Author2").created(now)
                .build();

        assertNotNull(comment1);
        assertNotNull(comment2);
        assertNotNull(comment3);
        assertEquals("Text", comment2.getText());

        ItemRequestDto request1 = new ItemRequestDto();
        ItemRequestDto request2 = new ItemRequestDto(1L, "Description", 1L, now);

        assertNotNull(request1);
        assertNotNull(request2);
        assertEquals("Description", request2.getDescription());

        BookItemRequestDto booking1 = new BookItemRequestDto();
        BookItemRequestDto booking2 = new BookItemRequestDto(1L, now.plusDays(1), now.plusDays(2));

        assertNotNull(booking1);
        assertNotNull(booking2);
        assertEquals(1L, booking2.getItemId());
    }

    @Test
    void dtoEqualsAndHashCode_ShouldWork() {
        ItemDto item1 = new ItemDto(1L, "Item", "Desc", true, 1L);
        ItemDto item2 = new ItemDto(1L, "Item", "Desc", true, 1L);
        ItemDto item3 = new ItemDto(2L, "Item2", "Desc2", false, 2L);

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void dtoToString_ShouldWork() {
        ItemDto item = new ItemDto(1L, "Item", "Desc", true, 1L);
        UserDto user = new UserDto(1L, "User", "user@test.com");

        assertNotNull(item.toString());
        assertNotNull(user.toString());
        assertTrue(item.toString().contains("Item"));
        assertTrue(user.toString().contains("User"));
    }
}