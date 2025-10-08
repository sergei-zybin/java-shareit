package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:test"})
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private Long userId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        UserDto savedUser = userService.create(userDto);
        userId = savedUser.getId();

        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
        ItemDto savedItem = itemService.create(itemDto, userId);
        itemId = savedItem.getId();
    }

    @Test
    void getByOwner_shouldReturnUserItems() {
        List<ItemDtoWithBookings> items = itemService.getByOwner(userId);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Test Item", items.get(0).getName());
    }

    @Test
    void getById_shouldReturnItem() {
        ItemDtoWithBookings item = itemService.getById(itemId, userId);

        assertNotNull(item);
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
    }

    @Test
    void search_shouldFindItemsByText() {
        List<ItemDto> items = itemService.search("test");

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Test Item", items.get(0).getName());
    }

    @Test
    void search_withBlankText_shouldReturnEmptyList() {
        List<ItemDto> items = itemService.search("");

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }
}