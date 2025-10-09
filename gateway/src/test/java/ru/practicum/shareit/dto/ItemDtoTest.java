package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    @Test
    void itemDto_shouldCreateWithBuilder() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(1L, dto.getRequestId());
    }

    @Test
    void itemDto_shouldCreateWithAllArgsConstructor() {
        ItemDto dto = new ItemDto(1L, "Item", "Description", true, 1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }
}