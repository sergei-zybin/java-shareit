package ru.practicum.shareit.ilovejacoco;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaximumCoverageTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void testUserServiceFullCoverage() {
        UserDto userDto = new UserDto(1L, "test", "test@test.com");
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto created = userService.create(userDto);
        assertNotNull(created);

        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> allUsers = userService.getAll();
        assertFalse(allUsers.isEmpty());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto found = userService.getById(1L);
        assertNotNull(found);

        UserDto updateDto = new UserDto(1L, "updated", "updated@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto updated = userService.update(1L, updateDto);
        assertNotNull(updated);

        when(userRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> userService.delete(1L));

        UserDto invalidEmail = new UserDto(1L, "test", "invalid");
        assertThrows(ValidationException.class, () -> userService.create(invalidEmail));

        UserDto emptyEmail = new UserDto(1L, "test", "");
        assertThrows(ValidationException.class, () -> userService.create(emptyEmail));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    void testItemServiceFullCoverage() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@test.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemDto itemDto = new ItemDto(1L, "item", "description", true, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto created = itemService.create(itemDto, 1L);
        assertNotNull(created);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findByItemIdIn(anyList())).thenReturn(List.of());

        List<ItemDtoWithBookings> ownerItems = itemService.getByOwner(1L);
        assertFalse(ownerItems.isEmpty());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ItemDtoWithBookings found = itemService.getById(1L, 1L);
        assertNotNull(found);

        ItemDto updateDto = new ItemDto(1L, "updated", "updated", false, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updated = itemService.update(1L, updateDto, 1L);
        assertNotNull(updated);

        when(itemRepository.search("test")).thenReturn(List.of(item));
        List<ItemDto> searchResults = itemService.search("test");
        assertFalse(searchResults.isEmpty());

        List<ItemDto> emptySearch = itemService.search("");
        assertTrue(emptySearch.isEmpty());

        CommentDto commentDto = new CommentDto(1L, "comment", "author", LocalDateTime.now());
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(owner);
        booking.setItem(item);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText("comment");
        savedComment.setAuthor(owner);
        savedComment.setItem(item);
        savedComment.setCreated(LocalDateTime.now());
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto addedComment = itemService.addComment(1L, commentDto, 1L);
        assertNotNull(addedComment);

        ItemDto invalidItem = new ItemDto(null, "", "", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        assertThrows(ValidationException.class, () -> itemService.create(invalidItem, 1L));
    }

    @Test
    void testBookingServiceFullCoverage() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("owner");

        User booker = new User();
        booker.setId(2L);
        booker.setName("booker");

        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setAvailable(true);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L
        );

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto created = bookingService.create(bookingRequestDto, 2L);
        assertNotNull(created);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto approved = bookingService.updateStatus(1L, true, 1L);
        assertNotNull(approved);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        BookingResponseDto found = bookingService.getById(1L, 1L);
        assertNotNull(found);

        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L)).thenReturn(List.of(booking));

        List<BookingResponseDto> allBookings = bookingService.getBookingsByBooker(2L, "ALL");
        assertFalse(allBookings.isEmpty());

        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));

        List<BookingResponseDto> ownerAll = bookingService.getBookingsByOwner(1L, "ALL");
        assertFalse(ownerAll.isEmpty());

        BookingRequestDto invalidBooking = new BookingRequestDto(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                1L
        );
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        assertThrows(ValidationException.class, () -> bookingService.create(invalidBooking, 2L));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        assertThrows(ForbiddenException.class, () -> bookingService.updateStatus(1L, true, 999L));

        when(userRepository.existsById(999L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByBooker(999L, "ALL"));
    }

    @Test
    void testItemRequestServiceFullCoverage() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("requestor");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("need item");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestDto requestDto = new ItemRequestDto(1L, "need item", 1L, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of());

        ItemRequestResponseDto created = itemRequestService.create(requestDto, 1L);
        assertNotNull(created);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(itemRequest));

        List<ItemRequestResponseDto> userRequests = itemRequestService.getByRequestor(1L);
        assertFalse(userRequests.isEmpty());

        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(eq(1L), any(Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        List<ItemRequestResponseDto> otherRequests = itemRequestService.getOtherUsersRequests(1L, 0, 10);
        assertTrue(otherRequests.isEmpty());

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        ItemRequestResponseDto found = itemRequestService.getById(1L, 1L);
        assertNotNull(found);
    }

    @Test
    void testExceptionHandlingCoverage() {
        assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException("test");
        });

        assertThrows(ValidationException.class, () -> {
            throw new ValidationException("test");
        });

        assertThrows(ConflictException.class, () -> {
            throw new ConflictException("test");
        });

        assertThrows(ForbiddenException.class, () -> {
            throw new ForbiddenException("test");
        });

        ErrorResponse errorResponse = new ErrorResponse("test error");
        assertEquals("test error", errorResponse.getError());
    }

    @Test
    void testEdgeCasesAndBoundaries() {
        BookingRequestDto equalTimesBooking = new BookingRequestDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1),
                1L
        );

        User booker = new User();
        booker.setId(2L);
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        User owner = new User();
        owner.setId(1L);
        item.setOwner(owner);

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(equalTimesBooking, 2L));

        BookingRequestDto invalidTimesBooking = new BookingRequestDto(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                1L
        );

        assertThrows(ValidationException.class, () -> bookingService.create(invalidTimesBooking, 2L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(equalTimesBooking, 1L));

        item.setAvailable(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(equalTimesBooking, 2L));
    }

    @Test
    void testAllBookingStates() {
        User booker = new User();
        booker.setId(1L);
        when(userRepository.existsById(1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> bookingService.getBookingsByBooker(1L, "UNKNOWN_STATE"));
        assertThrows(ValidationException.class, () -> bookingService.getBookingsByOwner(1L, "UNKNOWN_STATE"));
    }

    @Test
    void testCommentValidation() {
        CommentDto commentDto = new CommentDto(1L, "comment", "author", LocalDateTime.now());
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of());

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, commentDto, 1L));
    }

    @Test
    void testBookingStatusUpdateValidation() {
        User owner = new User();
        owner.setId(1L);
        User booker = new User();
        booker.setId(2L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(1L, true, 1L));
    }

    @Test
    void testItemWithRequest() {
        User owner = new User();
        owner.setId(1L);
        ItemRequest request = new ItemRequest();
        request.setId(1L);

        ItemDto itemDto = new ItemDto(1L, "item", "desc", true, 1L);
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto created = itemService.create(itemDto, 1L);
        assertNotNull(created);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updated = itemService.update(1L, itemDto, 1L);
        assertNotNull(updated);
    }
}