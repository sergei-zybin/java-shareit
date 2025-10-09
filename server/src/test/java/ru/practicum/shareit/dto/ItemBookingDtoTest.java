package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.ItemBookingDto;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class ItemBookingDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private ItemBookingDto itemBookingDto;

    @BeforeEach
    void setUp() {
        itemBookingDto = ItemBookingDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    @Test
    void serializeAndDeserialize_shouldReturnSameDto() throws Exception {
        String json = objectMapper.writeValueAsString(itemBookingDto);
        ItemBookingDto deserialized = objectMapper.readValue(json, ItemBookingDto.class);

        assertEquals(itemBookingDto.getId(), deserialized.getId());
        assertEquals(itemBookingDto.getName(), deserialized.getName());
        assertEquals(itemBookingDto.getDescription(), deserialized.getDescription());
        assertEquals(itemBookingDto.getAvailable(), deserialized.getAvailable());
    }

    @Test
    void deserialize_withMissingFields_shouldHandleNull() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Item\"}";
        ItemBookingDto deserialized = objectMapper.readValue(json, ItemBookingDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals("Test Item", deserialized.getName());
        assertNull(deserialized.getDescription());
        assertNull(deserialized.getAvailable());
    }

    @Test
    void deserialize_withAllFields_shouldMapCorrectly() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true}";
        ItemBookingDto deserialized = objectMapper.readValue(json, ItemBookingDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals("Test Item", deserialized.getName());
        assertEquals("Test Description", deserialized.getDescription());
        assertTrue(deserialized.getAvailable());
    }
}