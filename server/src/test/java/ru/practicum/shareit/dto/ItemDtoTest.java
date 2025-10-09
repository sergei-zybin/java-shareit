package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    @Test
    void serializeAndDeserialize_shouldReturnSameDto() throws Exception {
        String json = objectMapper.writeValueAsString(itemDto);
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);

        assertEquals(itemDto.getName(), deserialized.getName());
        assertEquals(itemDto.getDescription(), deserialized.getDescription());
        assertEquals(itemDto.getAvailable(), deserialized.getAvailable());
    }

    @Test
    void deserialize_withMissingName_shouldHandleNull() throws Exception {
        String json = "{\"description\":\"Test Description\",\"available\":true}";
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);

        assertNull(deserialized.getName());
        assertEquals("Test Description", deserialized.getDescription());
        assertTrue(deserialized.getAvailable());
    }

    @Test
    void deserialize_withMissingDescription_shouldHandleNull() throws Exception {
        String json = "{\"name\":\"Test Item\",\"available\":true}";
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);

        assertEquals("Test Item", deserialized.getName());
        assertNull(deserialized.getDescription());
        assertTrue(deserialized.getAvailable());
    }

    @Test
    void deserialize_withMissingAvailable_shouldHandleNull() throws Exception {
        String json = "{\"name\":\"Test Item\",\"description\":\"Test Description\"}";
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);

        assertEquals("Test Item", deserialized.getName());
        assertEquals("Test Description", deserialized.getDescription());
        assertNull(deserialized.getAvailable());
    }

    @Test
    void deserialize_withEmptyName_shouldHandleEmptyString() throws Exception {
        String json = "{\"name\":\"\",\"description\":\"Test Description\",\"available\":true}";
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);

        assertEquals("", deserialized.getName());
        assertEquals("Test Description", deserialized.getDescription());
        assertTrue(deserialized.getAvailable());
    }

    @Test
    void deserialize_withRequestId_shouldHandleCorrectly() throws Exception {
        String json = "{\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"requestId\":1}";
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);

        assertEquals("Test Item", deserialized.getName());
        assertEquals(1L, deserialized.getRequestId());
    }

    @Test
    void deserialize_withAllFields_shouldMapAllValues() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"requestId\":2}";
        ItemDto deserialized = objectMapper.readValue(json, ItemDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals("Test Item", deserialized.getName());
        assertEquals("Test Description", deserialized.getDescription());
        assertTrue(deserialized.getAvailable());
        assertEquals(2L, deserialized.getRequestId());
    }
}