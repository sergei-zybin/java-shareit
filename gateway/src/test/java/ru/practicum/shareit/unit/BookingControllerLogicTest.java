package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookingControllerLogicTest {

    @Test
    void getBookings_WithInvalidState_ShouldThrowException() {
        BookingClient bookingClient = mock(BookingClient.class);
        BookingController controller = new BookingController(bookingClient);

        assertThrows(IllegalArgumentException.class, () -> {
            controller.getBookings(1L, "INVALID_STATE", 0, 10);
        });
    }

    @Test
    void getBookingsByOwner_WithInvalidState_ShouldThrowException() {
        BookingClient bookingClient = mock(BookingClient.class);
        BookingController controller = new BookingController(bookingClient);

        assertThrows(IllegalArgumentException.class, () -> {
            controller.getBookingsByOwner(1L, "INVALID_STATE", 0, 10);
        });
    }

    @Test
    void getBookings_WithValidState_ShouldCallClient() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(null);

        BookingController controller = new BookingController(bookingClient);

        controller.getBookings(1L, "all", 0, 10);

        verify(bookingClient).getBookings(eq(1L), eq(BookingState.ALL), eq(0), eq(10));
    }
}