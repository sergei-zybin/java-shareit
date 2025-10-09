package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ComprehensiveIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemRequestService itemRequestService;

    private UserDto owner;
    private UserDto booker;
    private ItemDto itemDto;
    private Long ownerId;
    private Long bookerId;
    private Long itemId;
    private Long requestId;

    @BeforeEach
    void setUp() {
        owner = UserDto.builder()
                .name("Item Owner")
                .email("owner@test.com")
                .build();
        UserDto savedOwner = userService.create(owner);
        ownerId = savedOwner.getId();

        booker = UserDto.builder()
                .name("Booker User")
                .email("booker@test.com")
                .build();
        UserDto savedBooker = userService.create(booker);
        bookerId = savedBooker.getId();

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужна дрель для ремонта");
        ItemRequestResponseDto savedRequest = itemRequestService.create(requestDto, bookerId);
        requestId = savedRequest.getId();

        itemDto = ItemDto.builder()
                .name("Дрель")
                .description("Мощная дрель с ударным механизмом")
                .available(true)
                .requestId(requestId)
                .build();
        ItemDto savedItem = itemService.create(itemDto, ownerId);
        itemId = savedItem.getId();
    }

    @Test
    void completeUserJourney_shouldWorkCorrectly() {
        UserDto retrievedOwner = userService.getById(ownerId);
        assertNotNull(retrievedOwner);
        assertEquals("Item Owner", retrievedOwner.getName());
        assertEquals("owner@test.com", retrievedOwner.getEmail());

        UserDto retrievedBooker = userService.getById(bookerId);
        assertNotNull(retrievedBooker);
        assertEquals("Booker User", retrievedBooker.getName());
        assertEquals("booker@test.com", retrievedBooker.getEmail());

        ItemRequestResponseDto retrievedRequest = itemRequestService.getById(requestId, bookerId);
        assertNotNull(retrievedRequest);
        assertEquals("Нужна дрель для ремонта", retrievedRequest.getDescription());
        assertNotNull(retrievedRequest.getCreated());

        ItemDtoWithBookings retrievedItem = itemService.getById(itemId, bookerId);
        assertNotNull(retrievedItem);
        assertEquals("Дрель", retrievedItem.getName());
        assertEquals("Мощная дрель с ударным механизмом", retrievedItem.getDescription());
        assertTrue(retrievedItem.getAvailable());
        assertEquals(requestId, retrievedItem.getRequestId());

        List<ItemDto> searchResults = itemService.search("дрель");
        assertFalse(searchResults.isEmpty());
        assertEquals(itemId, searchResults.get(0).getId());

        BookingRequestDto bookingRequest = new BookingRequestDto();
        bookingRequest.setItemId(itemId);
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto booking = bookingService.create(bookingRequest, bookerId);
        assertNotNull(booking);
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertEquals(bookerId, booking.getBooker().getId());
        assertEquals(itemId, booking.getItem().getId());

        Long bookingId = booking.getId();

        BookingResponseDto approvedBooking = bookingService.updateStatus(bookingId, true, ownerId);
        assertNotNull(approvedBooking);
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());

        List<BookingResponseDto> userBookings = bookingService.getBookingsByBooker(bookerId, "ALL");
        assertFalse(userBookings.isEmpty());

        List<BookingResponseDto> ownerBookings = bookingService.getBookingsByOwner(ownerId, "ALL");
        assertFalse(ownerBookings.isEmpty());

        ItemDto updateDto = ItemDto.builder()
                .name("Дрель профессиональная")
                .description("Обновленное описание")
                .available(false)
                .build();

        ItemDto updatedItem = itemService.update(itemId, updateDto, ownerId);
        assertNotNull(updatedItem);
        assertEquals("Дрель профессиональная", updatedItem.getName());
        assertEquals("Обновленное описание", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());

        List<ItemDtoWithBookings> ownerItems = itemService.getByOwner(ownerId);
        assertFalse(ownerItems.isEmpty());
        assertEquals(itemId, ownerItems.get(0).getId());

        List<UserDto> allUsers = userService.getAll();
        assertTrue(allUsers.size() >= 2);

        UserDto userUpdate = UserDto.builder()
                .name("Updated Booker")
                .email("updated@test.com")
                .build();

        UserDto updatedUser = userService.update(bookerId, userUpdate);
        assertNotNull(updatedUser);
        assertEquals("Updated Booker", updatedUser.getName());
        assertEquals("updated@test.com", updatedUser.getEmail());

        List<ItemRequestResponseDto> userRequests = itemRequestService.getByRequestor(bookerId);
        assertFalse(userRequests.isEmpty());
        assertEquals(requestId, userRequests.get(0).getId());

        List<ItemRequestResponseDto> otherRequests = itemRequestService.getOtherUsersRequests(ownerId, 0, 10);
        assertFalse(otherRequests.isEmpty());

        assertDoesNotThrow(() -> bookingService.getBookingsByBooker(bookerId, "CURRENT"));
        assertDoesNotThrow(() -> bookingService.getBookingsByBooker(bookerId, "PAST"));
        assertDoesNotThrow(() -> bookingService.getBookingsByBooker(bookerId, "FUTURE"));
        assertDoesNotThrow(() -> bookingService.getBookingsByBooker(bookerId, "WAITING"));
        assertDoesNotThrow(() -> bookingService.getBookingsByBooker(bookerId, "REJECTED"));

        BookingResponseDto specificBooking = bookingService.getById(bookingId, bookerId);
        assertNotNull(specificBooking);
        assertEquals(bookingId, specificBooking.getId());

        List<ItemDto> emptySearch = itemService.search("несуществующий предмет");
        assertTrue(emptySearch.isEmpty());

        assertTrue(true);
    }

    @Test
    void edgeCaseScenarios_shouldHandleCorrectly() {
        List<ItemDto> emptySearch = itemService.search("");
        assertTrue(emptySearch.isEmpty());

        List<ItemDto> nullSearch = itemService.search(null);
        assertTrue(nullSearch.isEmpty());

        assertThrows(Exception.class, () -> userService.getById(999999L));

        assertThrows(Exception.class, () -> itemService.getById(999999L, ownerId));

        ItemDto unavailableItem = ItemDto.builder()
                .name("Недоступная вещь")
                .description("Тестовая вещь")
                .available(false)
                .build();
        ItemDto savedUnavailableItem = itemService.create(unavailableItem, ownerId);

        BookingRequestDto bookingForUnavailable = new BookingRequestDto();
        bookingForUnavailable.setItemId(savedUnavailableItem.getId());
        bookingForUnavailable.setStart(LocalDateTime.now().plusDays(1));
        bookingForUnavailable.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(Exception.class, () -> bookingService.create(bookingForUnavailable, bookerId));

        BookingRequestDto ownItemBooking = new BookingRequestDto();
        ownItemBooking.setItemId(itemId);
        ownItemBooking.setStart(LocalDateTime.now().plusDays(1));
        ownItemBooking.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(Exception.class, () -> bookingService.create(ownItemBooking, ownerId));

        assertDoesNotThrow(() -> bookingService.getBookingsByOwner(ownerId, "ALL"));
        assertDoesNotThrow(() -> bookingService.getBookingsByOwner(ownerId, "CURRENT"));
        assertDoesNotThrow(() -> bookingService.getBookingsByOwner(ownerId, "PAST"));
        assertDoesNotThrow(() -> bookingService.getBookingsByOwner(ownerId, "FUTURE"));
        assertDoesNotThrow(() -> bookingService.getBookingsByOwner(ownerId, "WAITING"));
        assertDoesNotThrow(() -> bookingService.getBookingsByOwner(ownerId, "REJECTED"));
    }
}