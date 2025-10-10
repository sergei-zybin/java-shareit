package ru.practicum.shareit.misc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }

    private Item createItem(Long id, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item " + id);
        item.setDescription("Description " + id);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    @Test
    void getByOwner_WhenUserExists_ReturnsItems() {
        User owner = createUser(1L);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(createItem(1L, owner)));

        var result = itemService.getByOwner(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).existsById(1L);
    }

    @Test
    void getByOwner_WhenUserNotExists_ThrowsException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.getByOwner(999L));
    }

    @Test
    void getById_WhenItemExists_ReturnsItem() {
        User owner = createUser(1L);
        Item item = createItem(1L, owner);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        var result = itemService.getById(1L, 1L);

        assertNotNull(result);
        verify(itemRepository).findById(1L);
    }

    @Test
    void getById_WhenItemNotExists_ThrowsException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(999L, 1L));
    }

    @Test
    void create_WhenValidData_CreatesItem() {
        User owner = createUser(1L);
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test");
        itemDto.setDescription("Test");
        itemDto.setAvailable(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(createItem(1L, owner));

        var result = itemService.create(itemDto, 1L);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(itemRepository).save(any());
    }

    @Test
    void create_WhenInvalidData_ThrowsException() {
        User owner = createUser(1L);
        ItemDto invalidDto = new ItemDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> itemService.create(invalidDto, 1L));
    }

    @Test
    void update_WhenOwnerUpdates_UpdatesItem() {
        User owner = createUser(1L);
        Item existingItem = createItem(1L, owner);
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any())).thenReturn(existingItem);

        var result = itemService.update(1L, updateDto, 1L);

        assertNotNull(result);
        verify(itemRepository).save(any());
    }

    @Test
    void update_WhenNotOwner_ThrowsException() {
        User owner = createUser(1L);
        Item existingItem = createItem(1L, owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        assertThrows(NotFoundException.class, () -> itemService.update(1L, new ItemDto(), 999L));
    }

    @Test
    void delete_WhenItemExists_DeletesItem() {
        when(itemRepository.existsById(1L)).thenReturn(true);

        itemService.delete(1L);

        verify(itemRepository).deleteById(1L);
    }

    @Test
    void delete_WhenItemNotExists_ThrowsException() {
        when(itemRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.delete(999L));
    }

    @Test
    void search_WithText_ReturnsItems() {
        User owner = createUser(1L);
        when(itemRepository.search("test")).thenReturn(List.of(createItem(1L, owner)));

        var result = itemService.search("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository).search("test");
    }

    @Test
    void search_WithEmptyText_ReturnsEmptyList() {
        var result = itemService.search("");

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(any());
    }
}