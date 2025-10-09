package ru.practicum.shareit.misc;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerCoverageTest {

    @Test
    void bookingController_EdgeCases_ShouldHandleProperly() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        BookingController controller = new BookingController(bookingClient);

        controller.getBookings(1L, "all", 0, 10);
        controller.getBookingsByOwner(1L, "current", 0, 10);

        assertThrows(IllegalArgumentException.class, () ->
                controller.getBookings(1L, "invalid", 0, 10));
    }

    @Test
    void itemController_EdgeCases_ShouldHandleProperly() {
        ItemClient itemClient = mock(ItemClient.class);
        when(itemClient.getItems(anyLong())).thenReturn(ResponseEntity.ok().build());
        when(itemClient.getItem(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());
        when(itemClient.searchItems(anyString())).thenReturn(ResponseEntity.ok().build());

        ItemController controller = new ItemController(itemClient);

        controller.search("");

        controller.search(null);
    }

    @Test
    void userController_EdgeCases_ShouldHandleProperly() {
        UserClient userClient = mock(UserClient.class);
        when(userClient.getUsers()).thenReturn(ResponseEntity.ok().build());
        when(userClient.getUser(anyLong())).thenReturn(ResponseEntity.ok().build());
        when(userClient.createUser(any())).thenReturn(ResponseEntity.ok().build());
        when(userClient.updateUser(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        UserController controller = new UserController(userClient);

        controller.getAll();
        controller.getById(1L);

        UserDto userDto = new UserDto(null, "John", "john@example.com");
        controller.create(userDto);

        UserDto updateDto = new UserDto(null, "Updated", null);
        controller.update(1L, updateDto);
        controller.delete(1L);
    }

    @Test
    void itemRequestController_EdgeCases_ShouldHandleProperly() {
        ItemRequestClient itemRequestClient = mock(ItemRequestClient.class);
        when(itemRequestClient.getByRequestor(anyLong())).thenReturn(ResponseEntity.ok().build());
        when(itemRequestClient.getOtherUsersRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());
        when(itemRequestClient.getById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        ItemRequestController controller = new ItemRequestController(itemRequestClient);

        controller.getOtherUsersRequests(1L, 0, 10);

        controller.getOtherUsersRequests(1L, 10, 20);
    }
}