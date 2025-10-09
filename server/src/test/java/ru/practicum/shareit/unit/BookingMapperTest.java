package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingMapperTest {

    private Booking booking;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        booker.setName("User");
        booker.setEmail("user@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void toBookingDto_shouldMapCorrectly() {
        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getItem().getId(), dto.getItemId());
        assertEquals(booking.getBooker().getId(), dto.getBookerId());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertEquals(booking.getItem().getName(), dto.getItemName());
        assertEquals(booking.getBooker().getName(), dto.getBookerName());
    }

    @Test
    void toBookingResponseDto_shouldMapCorrectly() {
        BookingResponseDto dto = BookingMapper.toBookingResponseDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertNotNull(dto.getBooker());
        assertEquals(booker.getId(), dto.getBooker().getId());
        assertEquals(booker.getName(), dto.getBooker().getName());
        assertEquals(booker.getEmail(), dto.getBooker().getEmail());
        assertNotNull(dto.getItem());
        assertEquals(item.getId(), dto.getItem().getId());
        assertEquals(item.getName(), dto.getItem().getName());
        assertEquals(item.getDescription(), dto.getItem().getDescription());
        assertEquals(item.getAvailable(), dto.getItem().getAvailable());
    }
}