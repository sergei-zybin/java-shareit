package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookerDto;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class BookerDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private BookerDto bookerDto;

    @BeforeEach
    void setUp() {
        bookerDto = BookerDto.builder()
                .id(1L)
                .name("Test Booker")
                .email("booker@example.com")
                .build();
    }

    @Test
    void serializeAndDeserialize_shouldReturnSameDto() throws Exception {
        String json = objectMapper.writeValueAsString(bookerDto);
        BookerDto deserialized = objectMapper.readValue(json, BookerDto.class);

        assertEquals(bookerDto.getId(), deserialized.getId());
        assertEquals(bookerDto.getName(), deserialized.getName());
        assertEquals(bookerDto.getEmail(), deserialized.getEmail());
    }

    @Test
    void deserialize_withPartialData_shouldHandleNull() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Booker\"}";
        BookerDto deserialized = objectMapper.readValue(json, BookerDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals("Test Booker", deserialized.getName());
        assertNull(deserialized.getEmail());
    }

    @Test
    void deserialize_withAllFields_shouldMapCorrectly() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Booker\",\"email\":\"booker@example.com\"}";
        BookerDto deserialized = objectMapper.readValue(json, BookerDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals("Test Booker", deserialized.getName());
        assertEquals("booker@example.com", deserialized.getEmail());
    }
}