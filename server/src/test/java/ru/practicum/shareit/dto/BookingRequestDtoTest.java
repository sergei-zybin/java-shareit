package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class BookingRequestDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingRequestDto.setItemId(1L);
    }

    @Test
    void serializeAndDeserialize_shouldReturnSameDto() throws Exception {
        String json = objectMapper.writeValueAsString(bookingRequestDto);
        BookingRequestDto deserialized = objectMapper.readValue(json, BookingRequestDto.class);

        assertEquals(bookingRequestDto.getStart(), deserialized.getStart());
        assertEquals(bookingRequestDto.getEnd(), deserialized.getEnd());
        assertEquals(bookingRequestDto.getItemId(), deserialized.getItemId());
    }

    @Test
    void deserialize_withMissingItemId_shouldHandleNull() throws Exception {
        String json = "{\"start\":\"2023-10-10T10:00:00\",\"end\":\"2023-10-11T10:00:00\"}";
        BookingRequestDto deserialized = objectMapper.readValue(json, BookingRequestDto.class);

        assertNotNull(deserialized.getStart());
        assertNotNull(deserialized.getEnd());
        assertNull(deserialized.getItemId());
    }

    @Test
    void deserialize_withMissingStart_shouldHandleNull() throws Exception {
        String json = "{\"itemId\":1,\"end\":\"2023-10-11T10:00:00\"}";
        BookingRequestDto deserialized = objectMapper.readValue(json, BookingRequestDto.class);

        assertEquals(1L, deserialized.getItemId());
        assertNull(deserialized.getStart());
        assertNotNull(deserialized.getEnd());
    }

    @Test
    void deserialize_withMissingEnd_shouldHandleNull() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2023-10-10T10:00:00\"}";
        BookingRequestDto deserialized = objectMapper.readValue(json, BookingRequestDto.class);

        assertEquals(1L, deserialized.getItemId());
        assertNotNull(deserialized.getStart());
        assertNull(deserialized.getEnd());
    }

    @Test
    void deserialize_withPastStart_shouldHandleGracefully() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2020-10-10T10:00:00\",\"end\":\"2023-10-11T10:00:00\"}";
        BookingRequestDto deserialized = objectMapper.readValue(json, BookingRequestDto.class);

        assertEquals(1L, deserialized.getItemId());
        assertNotNull(deserialized.getStart());
        assertNotNull(deserialized.getEnd());
    }

    @Test
    void deserialize_withAllFields_shouldMapCorrectly() throws Exception {
        String json = "{\"start\":\"2023-10-10T10:00:00\",\"end\":\"2023-10-11T10:00:00\",\"itemId\":1}";
        BookingRequestDto deserialized = objectMapper.readValue(json, BookingRequestDto.class);

        assertNotNull(deserialized.getStart());
        assertNotNull(deserialized.getEnd());
        assertEquals(1L, deserialized.getItemId());
    }
}