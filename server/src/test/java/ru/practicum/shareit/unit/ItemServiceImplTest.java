package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

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

    private ItemDto itemDto;
    private User user;
    private User owner;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need item");

        itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(1L)
                .build();

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
    }

    @Test
    void createItem_withValidData_shouldReturnItemDto() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(itemDto, 2L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
    }

    @Test
    void createItem_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 999L));
    }

    @Test
    void createItem_withInvalidData_shouldThrowValidationException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));

        ItemDto invalidItemDto = ItemDto.builder()
                .name("")
                .description("")
                .available(null)
                .build();

        assertThrows(ValidationException.class, () -> itemService.create(invalidItemDto, 2L));
    }

    @Test
    void updateItem_withValidData_shouldReturnUpdatedItem() {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(1L, updateDto, 2L);

        assertNotNull(result);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_byNonOwner_shouldThrowNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto updateDto = ItemDto.builder().name("Updated Item").build();

        assertThrows(NotFoundException.class, () -> itemService.update(1L, updateDto, 999L));
    }

    @Test
    void updateItem_withNonExistentItem_shouldThrowNotFoundException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        ItemDto updateDto = ItemDto.builder().name("Updated Item").build();

        assertThrows(NotFoundException.class, () -> itemService.update(999L, updateDto, 2L));
    }

    @Test
    void getItemById_forOwner_shouldReturnItemWithBookings() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookings(1L)).thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBookings(1L)).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(1L)).thenReturn(Collections.emptyList());

        ItemDtoWithBookings result = itemService.getById(1L, 2L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void getItemsByOwner_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.getByOwner(999L));
    }

    @Test
    void searchItems_withValidText_shouldReturnMatchingItems() {
        when(itemRepository.search("test")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("test");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void searchItems_withEmptyText_shouldReturnEmptyList() {
        List<ItemDto> result = itemService.search("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_withValidData_shouldReturnComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setEnd(LocalDateTime.now().minusDays(1));

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText("Great item!");
        savedComment.setItem(item);
        savedComment.setAuthor(user);
        savedComment.setCreated(LocalDateTime.now());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.addComment(1L, commentDto, 1L);

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
        assertEquals("Test User", result.getAuthorName());
    }

    @Test
    void addComment_byUserWithoutBooking_shouldThrowValidationException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, commentDto, 1L));
    }
}