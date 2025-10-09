package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class BookingShortDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private BookingShortDto bookingShortDto;

    @BeforeEach
    void setUp() {
        bookingShortDto = new BookingShortDto(
                1L,
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
    }

    @Test
    void serializeAndDeserialize_shouldReturnSameDto() throws Exception {
        String json = objectMapper.writeValueAsString(bookingShortDto);
        BookingShortDto deserialized = objectMapper.readValue(json, BookingShortDto.class);

        assertEquals(bookingShortDto.getId(), deserialized.getId());
        assertEquals(bookingShortDto.getBookerId(), deserialized.getBookerId());
        assertEquals(bookingShortDto.getStart(), deserialized.getStart());
        assertEquals(bookingShortDto.getEnd(), deserialized.getEnd());
    }

    @Test
    void deserialize_withPartialData_shouldHandleNull() throws Exception {
        String json = "{\"id\":1,\"bookerId\":2}";
        BookingShortDto deserialized = objectMapper.readValue(json, BookingShortDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals(2L, deserialized.getBookerId());
        assertNull(deserialized.getStart());
        assertNull(deserialized.getEnd());
    }

    @Test
    void deserialize_withAllFields_shouldMapCorrectly() throws Exception {
        String json = "{\"id\":1,\"bookerId\":2,\"start\":\"2023-10-10T10:00:00\",\"end\":\"2023-10-11T10:00:00\"}";
        BookingShortDto deserialized = objectMapper.readValue(json, BookingShortDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals(2L, deserialized.getBookerId());
        assertNotNull(deserialized.getStart());
        assertNotNull(deserialized.getEnd());
    }
}