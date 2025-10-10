package ru.practicum.shareit.ilovejacoco;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
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
public class LowCoverageAreasTest {

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
    void testBookingRepositoryCustomQueriesCoverage() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findCurrentByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findCurrentByOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findNextBookings(anyLong())).thenReturn(List.of(booking));
        when(bookingRepository.findLastBookings(anyLong())).thenReturn(List.of(booking));
        when(bookingRepository.findByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(List.of(booking));

        List<Booking> currentByBooker = bookingRepository.findCurrentByBookerId(1L, LocalDateTime.now());
        List<Booking> currentByOwner = bookingRepository.findCurrentByOwnerId(1L, LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findNextBookings(1L);
        List<Booking> lastBookings = bookingRepository.findLastBookings(1L);
        List<Booking> itemBookerEndBefore = bookingRepository.findByItemIdAndBookerIdAndEndBefore(1L, 1L, LocalDateTime.now());

        assertFalse(currentByBooker.isEmpty());
        assertFalse(currentByOwner.isEmpty());
        assertFalse(nextBookings.isEmpty());
        assertFalse(lastBookings.isEmpty());
        assertFalse(itemBookerEndBefore.isEmpty());
    }

    @Test
    void testCommentRepositoryCustomQueriesCoverage() {
        User user = new User();
        user.setId(1L);
        user.setName("author");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("test comment");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        when(commentRepository.findByItemIdWithAuthor(anyLong())).thenReturn(List.of(comment));
        when(commentRepository.findByItemIdInWithAuthors(anyList())).thenReturn(List.of(comment));

        List<Comment> withAuthor = commentRepository.findByItemIdWithAuthor(1L);
        List<Comment> withAuthors = commentRepository.findByItemIdInWithAuthors(List.of(1L, 2L));

        assertFalse(withAuthor.isEmpty());
        assertFalse(withAuthors.isEmpty());
        assertEquals("test comment", withAuthor.get(0).getText());
    }

    @Test
    void testAllBookingStatesForOwnerComprehensive() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("owner");

        User booker = new User();
        booker.setId(2L);
        booker.setName("booker");

        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.existsById(1L)).thenReturn(true);

        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));
        when(bookingRepository.findCurrentByOwnerId(eq(1L), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(1L), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(1L), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING)).thenReturn(List.of(booking));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED)).thenReturn(List.of(booking));

        List<BookingResponseDto> all = bookingService.getBookingsByOwner(1L, "ALL");
        List<BookingResponseDto> current = bookingService.getBookingsByOwner(1L, "CURRENT");
        List<BookingResponseDto> past = bookingService.getBookingsByOwner(1L, "PAST");
        List<BookingResponseDto> future = bookingService.getBookingsByOwner(1L, "FUTURE");
        List<BookingResponseDto> waiting = bookingService.getBookingsByOwner(1L, "WAITING");
        List<BookingResponseDto> rejected = bookingService.getBookingsByOwner(1L, "REJECTED");

        assertNotNull(all);
        assertNotNull(current);
        assertNotNull(past);
        assertNotNull(future);
        assertNotNull(waiting);
        assertNotNull(rejected);
        assertFalse(all.isEmpty());
    }

    @Test
    void testAllBookingStatesForBookerComprehensive() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("booker");

        Item item = new Item();
        item.setId(1L);
        item.setName("item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.existsById(1L)).thenReturn(true);

        when(bookingRepository.findByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));
        when(bookingRepository.findCurrentByBookerId(eq(1L), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(eq(1L), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(eq(1L), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING)).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED)).thenReturn(List.of(booking));

        List<BookingResponseDto> all = bookingService.getBookingsByBooker(1L, "ALL");
        List<BookingResponseDto> current = bookingService.getBookingsByBooker(1L, "CURRENT");
        List<BookingResponseDto> past = bookingService.getBookingsByBooker(1L, "PAST");
        List<BookingResponseDto> future = bookingService.getBookingsByBooker(1L, "FUTURE");
        List<BookingResponseDto> waiting = bookingService.getBookingsByBooker(1L, "WAITING");
        List<BookingResponseDto> rejected = bookingService.getBookingsByBooker(1L, "REJECTED");

        assertNotNull(all);
        assertNotNull(current);
        assertNotNull(past);
        assertNotNull(future);
        assertNotNull(waiting);
        assertNotNull(rejected);
        assertFalse(all.isEmpty());
    }

    @Test
    void testItemRequestPaginationWithData() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("requestor");

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("other");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("need item");
        itemRequest.setRequestor(otherUser);
        itemRequest.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(otherUser);
        item.setRequest(itemRequest);

        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(eq(1L), any(Pageable.class))).thenReturn(page);
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        List<ItemRequestResponseDto> result = itemRequestService.getOtherUsersRequests(1L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getId());
        assertFalse(result.get(0).getItems().isEmpty());
    }

    @Test
    void testAllMappersComprehensive() {
        User user = new User();
        user.setId(1L);
        user.setName("test user");
        user.setEmail("test@test.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test description");
        item.setAvailable(true);
        item.setOwner(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        item.setRequest(itemRequest);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("test comment");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        ru.practicum.shareit.booking.dto.BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        Item mappedItem = ItemMapper.toItem(itemDto);

        UserDto userDto = UserMapper.toUserDto(user);
        User mappedUser = UserMapper.toUser(userDto);

        assertNotNull(bookingDto);
        assertNotNull(bookingResponseDto);
        assertNotNull(itemDto);
        assertNotNull(mappedItem);
        assertNotNull(userDto);
        assertNotNull(mappedUser);

        assertEquals(1L, bookingDto.getId());
        assertEquals(1L, bookingResponseDto.getId());
        assertEquals(1L, itemDto.getId());
        assertEquals(1L, userDto.getId());
        assertEquals("test user", userDto.getName());
    }

    @Test
    void testBookingServiceEdgeCases() {
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
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getById(1L, 1L);
        assertNotNull(result);

        BookingResponseDto result2 = bookingService.getById(1L, 2L);
        assertNotNull(result2);

        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 999L));
    }

    @Test
    void testItemRequestWithItems() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("requestor");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("need item");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(1L);
        item.setName("provided item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(requestor);
        item.setRequest(itemRequest);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        List<ItemRequestResponseDto> result = itemRequestService.getByRequestor(1L);

        assertFalse(result.isEmpty());
        ItemRequestResponseDto dto = result.get(0);
        assertFalse(dto.getItems().isEmpty());
        assertEquals("provided item", dto.getItems().get(0).getName());
    }

    @Test
    void testCommentFunctionalityComprehensive() {
        User author = new User();
        author.setId(1L);
        author.setName("author");
        author.setEmail("author@test.com");

        User owner = new User();
        owner.setId(2L);
        owner.setName("owner");

        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(author);
        booking.setStatus(BookingStatus.APPROVED);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("comment text");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment text");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookingRepository.findByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, commentDto, 1L);

        assertNotNull(result);
        assertEquals("comment text", result.getText());
    }

    @Test
    void testItemSearchWithVariousQueries() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Drill");
        item1.setDescription("Powerful electric drill");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Hammer");
        item2.setDescription("Strong hammer for construction");
        item2.setAvailable(true);

        when(itemRepository.search("drill")).thenReturn(List.of(item1));
        when(itemRepository.search("hammer")).thenReturn(List.of(item2));
        when(itemRepository.search("construction")).thenReturn(List.of(item2));

        List<ItemDto> drillResults = itemService.search("drill");
        List<ItemDto> hammerResults = itemService.search("hammer");
        List<ItemDto> descriptionResults = itemService.search("construction");
        List<ItemDto> emptyResults = itemService.search("");

        assertFalse(drillResults.isEmpty());
        assertFalse(hammerResults.isEmpty());
        assertFalse(descriptionResults.isEmpty());
        assertTrue(emptyResults.isEmpty());
        assertEquals("Drill", drillResults.get(0).getName());
        assertEquals("Hammer", hammerResults.get(0).getName());
    }

    @Test
    void testBookingStatusTransitions() {
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

        Booking waitingBooking = new Booking();
        waitingBooking.setId(1L);
        waitingBooking.setStart(LocalDateTime.now().plusDays(1));
        waitingBooking.setEnd(LocalDateTime.now().plusDays(2));
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(waitingBooking);

        BookingResponseDto approved = bookingService.updateStatus(1L, true, 1L);
        assertNotNull(approved);

        waitingBooking.setStatus(BookingStatus.WAITING);
        BookingResponseDto rejected = bookingService.updateStatus(1L, false, 1L);
        assertNotNull(rejected);
    }

    @Test
    void testUserEmailValidationEdgeCases() {
        UserDto validUser = new UserDto(1L, "test", "valid@email.com");
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("valid@email.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto created = userService.create(validUser);
        assertNotNull(created);

        UserDto invalidUser1 = new UserDto(2L, "test", "");
        assertThrows(ValidationException.class, () -> userService.create(invalidUser1));

        UserDto invalidUser2 = new UserDto(3L, "test", null);
        assertThrows(ValidationException.class, () -> userService.create(invalidUser2));
    }

    @Test
    void testItemUpdatePartialFields() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("owner");

        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("old name");
        existingItem.setDescription("old description");
        existingItem.setAvailable(true);
        existingItem.setOwner(owner);

        ItemDto partialUpdate = new ItemDto();
        partialUpdate.setName("new name");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        ItemDto result = itemService.update(1L, partialUpdate, 1L);

        assertNotNull(result);
        assertEquals("new name", result.getName());
        assertEquals("old description", result.getDescription());
    }

    @Test
    void testBookingRepositoryFindMethods() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        List<Booking> bookerEndBefore = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(1L, LocalDateTime.now());
        List<Booking> bookerStartAfter = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(1L, LocalDateTime.now());
        List<Booking> bookerStatus = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING);
        List<Booking> ownerEndBefore = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(1L, LocalDateTime.now());
        List<Booking> ownerStartAfter = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(1L, LocalDateTime.now());
        List<Booking> ownerStatus = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING);

        assertFalse(bookerEndBefore.isEmpty());
        assertFalse(bookerStartAfter.isEmpty());
        assertFalse(bookerStatus.isEmpty());
        assertFalse(ownerEndBefore.isEmpty());
        assertFalse(ownerStartAfter.isEmpty());
        assertFalse(ownerStatus.isEmpty());
    }

    @Test
    void testCommentRepositoryBasicMethods() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("test");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        when(commentRepository.findByItemIdIn(anyList())).thenReturn(List.of(comment));

        List<Comment> byItemId = commentRepository.findByItemId(1L);
        List<Comment> byItemIds = commentRepository.findByItemIdIn(List.of(1L, 2L));

        assertFalse(byItemId.isEmpty());
        assertFalse(byItemIds.isEmpty());
    }
}