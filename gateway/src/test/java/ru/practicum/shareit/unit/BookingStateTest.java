package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    void from_shouldReturnStateForValidString() {
        Optional<BookingState> result = BookingState.from("all");
        assertTrue(result.isPresent());
        assertEquals(BookingState.ALL, result.get());
    }

    @Test
    void from_shouldReturnStateForUpperCase() {
        Optional<BookingState> result = BookingState.from("CURRENT");
        assertTrue(result.isPresent());
        assertEquals(BookingState.CURRENT, result.get());
    }

    @Test
    void from_shouldReturnEmptyForInvalidString() {
        Optional<BookingState> result = BookingState.from("invalid");
        assertFalse(result.isPresent());
    }

    @Test
    void from_shouldReturnEmptyForNull() {
        Optional<BookingState> result = BookingState.from(null);
        assertFalse(result.isPresent());
    }

    @Test
    void values_shouldReturnAllStates() {
        BookingState[] states = BookingState.values();
        assertEquals(6, states.length);
        assertArrayEquals(new BookingState[]{
                BookingState.ALL,
                BookingState.CURRENT,
                BookingState.FUTURE,
                BookingState.PAST,
                BookingState.REJECTED,
                BookingState.WAITING
        }, states);
    }

    @Test
    void valueOf_shouldReturnCorrectState() {
        BookingState state = BookingState.valueOf("ALL");
        assertEquals(BookingState.ALL, state);
    }
}