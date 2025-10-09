package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class ItemRequestResponseDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestResponseDto itemRequestResponseDto;

    @BeforeEach
    void setUp() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        itemRequestResponseDto = new ItemRequestResponseDto();
        itemRequestResponseDto.setId(1L);
        itemRequestResponseDto.setDescription("Test request description");
        itemRequestResponseDto.setCreated(LocalDateTime.now());
        itemRequestResponseDto.setItems(List.of(itemDto));
    }

    @Test
    void serializeAndDeserialize_shouldReturnSameDto() throws Exception {
        String json = objectMapper.writeValueAsString(itemRequestResponseDto);
        ItemRequestResponseDto deserialized = objectMapper.readValue(json, ItemRequestResponseDto.class);

        assertEquals(itemRequestResponseDto.getId(), deserialized.getId());
        assertEquals(itemRequestResponseDto.getDescription(), deserialized.getDescription());
        assertEquals(itemRequestResponseDto.getItems().size(), deserialized.getItems().size());
        assertEquals(itemRequestResponseDto.getItems().get(0).getName(), deserialized.getItems().get(0).getName());
    }

    @Test
    void deserialize_withEmptyItems_shouldHandleCorrectly() throws Exception {
        String json = "{\"id\":1,\"description\":\"Test description\",\"created\":\"2023-10-10T10:00:00\",\"items\":[]}";
        ItemRequestResponseDto deserialized = objectMapper.readValue(json, ItemRequestResponseDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals("Test description", deserialized.getDescription());
        assertTrue(deserialized.getItems().isEmpty());
    }

    @Test
    void deserialize_withNullItems_shouldHandleCorrectly() throws Exception {
        String json = "{\"id\":1,\"description\":\"Test description\",\"created\":\"2023-10-10T10:00:00\"}";
        ItemRequestResponseDto deserialized = objectMapper.readValue(json, ItemRequestResponseDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals("Test description", deserialized.getDescription());
        assertNull(deserialized.getItems());
    }
}