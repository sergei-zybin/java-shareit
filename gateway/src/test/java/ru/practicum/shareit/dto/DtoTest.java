package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void commentDto_ShouldWorkWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto dto = new CommentDto(1L, "Great item!", "John", now);

        assertEquals(1L, dto.getId());
        assertEquals("Great item!", dto.getText());
        assertEquals("John", dto.getAuthorName());
        assertEquals(now, dto.getCreated());
    }

    @Test
    void commentDto_ShouldWorkWithBuilder() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("John")
                .created(now)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Great item!", dto.getText());
        assertEquals("John", dto.getAuthorName());
        assertEquals(now, dto.getCreated());
    }

    @Test
    void itemRequestDto_ShouldWorkWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto dto = new ItemRequestDto(1L, "Need a drill", 1L, now);

        assertEquals(1L, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(1L, dto.getRequestorId());
        assertEquals(now, dto.getCreated());
    }

    @Test
    void userDto_ShouldWorkWithBuilder() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    void userDto_ShouldWorkWithAllArgsConstructor() {
        UserDto dto = new UserDto(1L, "John Doe", "john@example.com");

        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
    }
}