package ru.practicum.shareit.ilovejacoco;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ModelCoverageTest {

    @Test
    void testBookingStatusEnum() {

        BookingStatus[] statuses = BookingStatus.values();
        assertEquals(4, statuses.length);

        assertEquals(BookingStatus.WAITING, BookingStatus.valueOf("WAITING"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.valueOf("APPROVED"));
        assertEquals(BookingStatus.REJECTED, BookingStatus.valueOf("REJECTED"));
        assertEquals(BookingStatus.CANCELED, BookingStatus.valueOf("CANCELED"));
    }

    @Test
    void testBookingModelComprehensive() {
        Booking booking = new Booking();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Item item = new Item();
        User booker = new User();

        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());

        String toString = booking.toString();
        assertNotNull(toString);
        assertFalse(toString.contains("item=ru.practicum.shareit.item.model.Item"));
        assertFalse(toString.contains("booker=ru.practicum.shareit.user.model.User"));
    }

    @Test
    void testUserModelComprehensive() {
        User user = new User();

        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());

        assertNotNull(user.toString());
    }

    @Test
    void testItemRequestModelComprehensive() {
        ItemRequest request = new ItemRequest();
        User requestor = new User();
        LocalDateTime created = LocalDateTime.now();

        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(created);

        assertEquals(1L, request.getId());
        assertEquals("Need a drill", request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertEquals(created, request.getCreated());

        String toString = request.toString();
        assertNotNull(toString);
        assertFalse(toString.contains("requestor=ru.practicum.shareit.user.model.User"));
    }
}