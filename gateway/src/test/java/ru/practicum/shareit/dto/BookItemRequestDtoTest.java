package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookItemRequestDtoTest {

    @Test
    void bookItemRequestDto_shouldCreateWithAllArgsConstructor() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);

        assertNotNull(dto);
        assertEquals(1L, dto.getItemId());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
    }

    @Test
    void bookItemRequestDto_shouldCreateWithNoArgsConstructor() {
        BookItemRequestDto dto = new BookItemRequestDto();

        assertNotNull(dto);
    }
}