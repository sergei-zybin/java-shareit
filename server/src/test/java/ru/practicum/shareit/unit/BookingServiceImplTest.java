package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private User booker;
    private User owner;
    private User anotherUser;
    private Item item;
    private Item unavailableItem;
    private Booking booking;
    private Booking approvedBooking;
    private Booking rejectedBooking;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking currentBooking;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        unavailableItem = new Item();
        unavailableItem.setId(2L);
        unavailableItem.setName("Unavailable Item");
        unavailableItem.setDescription("Unavailable Description");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(owner);

        LocalDateTime now = LocalDateTime.now();

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(now.plusDays(1));
        booking.setEnd(now.plusDays(2));
        booking.setStatus(BookingStatus.WAITING);

        approvedBooking = new Booking();
        approvedBooking.setId(2L);
        approvedBooking.setBooker(booker);
        approvedBooking.setItem(item);
        approvedBooking.setStart(now.plusDays(3));
        approvedBooking.setEnd(now.plusDays(4));
        approvedBooking.setStatus(BookingStatus.APPROVED);

        rejectedBooking = new Booking();
        rejectedBooking.setId(3L);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setItem(item);
        rejectedBooking.setStart(now.plusDays(5));
        rejectedBooking.setEnd(now.plusDays(6));
        rejectedBooking.setStatus(BookingStatus.REJECTED);

        pastBooking = new Booking();
        pastBooking.setId(4L);
        pastBooking.setBooker(booker);
        pastBooking.setItem(item);
        pastBooking.setStart(now.minusDays(3));
        pastBooking.setEnd(now.minusDays(2));
        pastBooking.setStatus(BookingStatus.APPROVED);

        futureBooking = new Booking();
        futureBooking.setId(5L);
        futureBooking.setBooker(booker);
        futureBooking.setItem(item);
        futureBooking.setStart(now.plusDays(7));
        futureBooking.setEnd(now.plusDays(8));
        futureBooking.setStatus(BookingStatus.WAITING);

        currentBooking = new Booking();
        currentBooking.setId(6L);
        currentBooking.setBooker(booker);
        currentBooking.setItem(item);
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));
        currentBooking.setStatus(BookingStatus.APPROVED);

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_withValidData_shouldReturnBookingDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.create(bookingRequestDto, 1L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingRequestDto, 999L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_withNonExistentItem_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        bookingRequestDto.setItemId(999L);

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingRequestDto, 1L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_withUnavailableItem_shouldThrowValidationException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(unavailableItem));

        bookingRequestDto.setItemId(2L);

        assertThrows(ValidationException.class, () -> bookingService.create(bookingRequestDto, 1L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenOwnerBooksOwnItem_shouldThrowNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingRequestDto, 2L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_withStartDateInPast_shouldThrowValidationException() {
        bookingRequestDto.setStart(LocalDateTime.now().minusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingRequestDto, 1L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_withEndBeforeStart_shouldThrowValidationException() {
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingRequestDto, 1L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_withEqualStartAndEnd_shouldThrowValidationException() {
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        bookingRequestDto.setStart(sameTime);
        bookingRequestDto.setEnd(sameTime);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingRequestDto, 1L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateStatus_withApproval_shouldReturnApprovedBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.updateStatus(1L, true, 2L);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateStatus_withRejection_shouldReturnRejectedBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.updateStatus(1L, false, 2L);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateStatus_withNonExistentBooking_shouldThrowNotFoundException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(999L, true, 2L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateStatus_byNonOwner_shouldThrowForbiddenException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.updateStatus(1L, true, 3L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateStatus_whenStatusNotWaiting_shouldThrowValidationException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(1L, true, 2L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getById_forBooker_shouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getById_forOwner_shouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getById(1L, 2L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getById_withNonExistentBooking_shouldThrowNotFoundException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(999L, 1L));
    }

    @Test
    void getById_byUnauthorizedUser_shouldThrowNotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 3L));
    }

    @Test
    void getBookingsByBooker_withAllState_shouldReturnAllBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L))
                .thenReturn(List.of(booking, approvedBooking, rejectedBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(1L, "ALL");

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void getBookingsByBooker_withCurrentState_shouldReturnCurrentBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findCurrentByBookerId(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(currentBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(1L, "CURRENT");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByBooker_withPastState_shouldReturnPastBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(pastBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(1L, "PAST");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByBooker_withFutureState_shouldReturnFutureBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(futureBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(1L, "FUTURE");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByBooker_withWaitingState_shouldReturnWaitingBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING))
                .thenReturn(List.of(booking, futureBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(1L, "WAITING");

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getBookingsByBooker_withRejectedState_shouldReturnRejectedBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED))
                .thenReturn(List.of(rejectedBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(1L, "REJECTED");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByBooker_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByBooker(999L, "ALL"));
    }

    @Test
    void getBookingsByBooker_withInvalidState_shouldThrowValidationException() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> bookingService.getBookingsByBooker(1L, "INVALID_STATE"));
    }

    @Test
    void getBookingsByBooker_withCaseInsensitiveState_shouldWorkCorrectly() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));

        assertDoesNotThrow(() -> bookingService.getBookingsByBooker(1L, "all"));
        assertDoesNotThrow(() -> bookingService.getBookingsByBooker(1L, "All"));
        assertDoesNotThrow(() -> bookingService.getBookingsByBooker(1L, "ALL"));
    }

    @Test
    void getBookingsByOwner_withAllState_shouldReturnAllBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(2L))
                .thenReturn(List.of(booking, approvedBooking, rejectedBooking, pastBooking, futureBooking, currentBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(2L, "ALL");

        assertNotNull(result);
        assertEquals(6, result.size());
    }

    @Test
    void getBookingsByOwner_withCurrentState_shouldReturnCurrentBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findCurrentByOwnerId(eq(2L), any(LocalDateTime.class)))
                .thenReturn(List.of(currentBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(2L, "CURRENT");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByOwner_withPastState_shouldReturnPastBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(2L), any(LocalDateTime.class)))
                .thenReturn(List.of(pastBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(2L, "PAST");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByOwner_withFutureState_shouldReturnFutureBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(2L), any(LocalDateTime.class)))
                .thenReturn(List.of(futureBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(2L, "FUTURE");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByOwner_withWaitingState_shouldReturnWaitingBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING))
                .thenReturn(List.of(booking, futureBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(2L, "WAITING");

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getBookingsByOwner_withRejectedState_shouldReturnRejectedBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(2L, BookingStatus.REJECTED))
                .thenReturn(List.of(rejectedBooking));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(2L, "REJECTED");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByOwner_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByOwner(999L, "ALL"));
    }

    @Test
    void getBookingsByOwner_withInvalidState_shouldThrowValidationException() {
        when(userRepository.existsById(2L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> bookingService.getBookingsByOwner(2L, "INVALID_STATE"));
    }

    @Test
    void getBookingsByOwner_withEmptyResult_shouldReturnEmptyList() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(2L)).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(2L, "ALL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getBookingsByBooker_withEmptyResult_shouldReturnEmptyList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L)).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.getBookingsByBooker(1L, "ALL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createBooking_withMinimalValidTimeRange_shouldWork() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertDoesNotThrow(() -> bookingService.create(bookingRequestDto, 1L));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateStatus_multipleTimesOnSameBooking_shouldHandleCorrectly() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto firstResult = bookingService.updateStatus(1L, true, 2L);
        assertEquals(BookingStatus.APPROVED, firstResult.getStatus());

        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(1L, false, 2L));
    }

}