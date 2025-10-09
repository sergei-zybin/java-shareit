package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateLogicTest {

    @Test
    void shouldHandleAllStates() {
        // Проверяем, что все состояния правильно преобразуются из строк
        assertStateConversion("all", BookingState.ALL);
        assertStateConversion("ALL", BookingState.ALL);
        assertStateConversion("current", BookingState.CURRENT);
        assertStateConversion("future", BookingState.FUTURE);
        assertStateConversion("past", BookingState.PAST);
        assertStateConversion("rejected", BookingState.REJECTED);
        assertStateConversion("waiting", BookingState.WAITING);
    }

    @Test
    void shouldHandleInvalidStates() {
        assertInvalidState("invalid_state");
        assertInvalidState("");
        assertInvalidState("PENDING");
    }

    private void assertStateConversion(String input, BookingState expected) {
        Optional<BookingState> result = BookingState.from(input);
        assertTrue(result.isPresent(), "State should be present for input: " + input);
        assertEquals(expected, result.get(), "State should match for input: " + input);
    }

    private void assertInvalidState(String input) {
        Optional<BookingState> result = BookingState.from(input);
        assertFalse(result.isPresent(), "State should not be present for invalid input: " + input);
    }
}