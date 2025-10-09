package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SimpleBookingControllerTest {

    @Test
    void bookingController_shouldCallClientMethods() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        BookingController controller = new BookingController(bookingClient);

        ResponseEntity<Object> response = controller.getBookings(1L, "ALL", 0, 10);

        verify(bookingClient).getBookings(1L, BookingState.ALL, 0, 10);
        assertNotNull(response);
    }
}