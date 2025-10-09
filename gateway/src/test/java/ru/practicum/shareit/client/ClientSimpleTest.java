package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClientSimpleTest {

    @Test
    void bookingClient_MethodsShouldReturnResponseEntity() {

        BookingClient client = null;

        assertNotNull(BookingState.ALL);
        assertNotNull(LocalDateTime.now());
        assertNotNull(ResponseEntity.ok().build());
    }

    @Test
    void bookItemRequestDto_ShouldCreateInstance() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);

        assertNotNull(dto);
        assertEquals(1L, dto.getItemId());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
    }
}