package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class BookingResponseDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        BookerDto bookerDto = BookerDto.builder()
                .id(1L)
                .name("Booker Name")
                .email("booker@example.com")
                .build();

        ItemBookingDto itemBookingDto = ItemBookingDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(bookerDto)
                .item(itemBookingDto)
                .build();
    }

    @Test
    void serializeAndDeserialize_shouldReturnSameDto() throws Exception {
        String json = objectMapper.writeValueAsString(bookingResponseDto);
        BookingResponseDto deserialized = objectMapper.readValue(json, BookingResponseDto.class);

        assertEquals(bookingResponseDto.getId(), deserialized.getId());
        assertEquals(bookingResponseDto.getStart(), deserialized.getStart());
        assertEquals(bookingResponseDto.getEnd(), deserialized.getEnd());
        assertEquals(bookingResponseDto.getStatus(), deserialized.getStatus());
        assertEquals(bookingResponseDto.getBooker().getId(), deserialized.getBooker().getId());
        assertEquals(bookingResponseDto.getItem().getId(), deserialized.getItem().getId());
    }

    @Test
    void deserialize_withPartialData_shouldHandleGracefully() throws Exception {
        String json = "{\"id\":1,\"status\":\"APPROVED\"}";
        BookingResponseDto deserialized = objectMapper.readValue(json, BookingResponseDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals(BookingStatus.APPROVED, deserialized.getStatus());
        assertNull(deserialized.getStart());
        assertNull(deserialized.getEnd());
        assertNull(deserialized.getBooker());
        assertNull(deserialized.getItem());
    }

    @Test
    void deserialize_withDifferentStatus_shouldMapCorrectly() throws Exception {
        String json = "{\"id\":1,\"status\":\"REJECTED\"}";
        BookingResponseDto deserialized = objectMapper.readValue(json, BookingResponseDto.class);

        assertEquals(BookingStatus.REJECTED, deserialized.getStatus());
    }
}