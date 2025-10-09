package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingState;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientMethodSignatureTest {

    @Test
    void itemClient_Methods_ShouldExist() throws Exception {

        Class<?> clazz = Class.forName("ru.practicum.shareit.item.ItemClient");

        Method[] methods = clazz.getDeclaredMethods();
        String[] expectedMethods = {"getItems", "getItem", "createItem", "updateItem", "deleteItem", "searchItems", "addComment"};

        for (String expected : expectedMethods) {
            boolean found = Arrays.stream(methods)
                    .anyMatch(m -> m.getName().equals(expected));
            assertTrue(found, "Method " + expected + " should exist in ItemClient");
        }
    }

    @Test
    void userClient_Methods_ShouldExist() throws Exception {
        Class<?> clazz = Class.forName("ru.practicum.shareit.user.UserClient");

        Method[] methods = clazz.getDeclaredMethods();
        String[] expectedMethods = {"getUsers", "getUser", "createUser", "updateUser", "deleteUser"};

        for (String expected : expectedMethods) {
            boolean found = Arrays.stream(methods)
                    .anyMatch(m -> m.getName().equals(expected));
            assertTrue(found, "Method " + expected + " should exist in UserClient");
        }
    }

    @Test
    void bookingClient_Methods_ShouldExist() throws Exception {
        Class<?> clazz = Class.forName("ru.practicum.shareit.booking.BookingClient");

        Method[] methods = clazz.getDeclaredMethods();
        String[] expectedMethods = {"getBookings", "bookItem", "getBooking", "getBookingsByOwner", "updateStatus"};

        for (String expected : expectedMethods) {
            boolean found = Arrays.stream(methods)
                    .anyMatch(m -> m.getName().equals(expected));
            assertTrue(found, "Method " + expected + " should exist in BookingClient");
        }
    }

    @Test
    void bookingState_AllValues_ShouldExist() {

        BookingState[] states = BookingState.values();
        assertTrue(states.length >= 6); // ALL, CURRENT, FUTURE, PAST, REJECTED, WAITING

        assertTrue(Arrays.stream(states).anyMatch(s -> s.name().equals("ALL")));
        assertTrue(Arrays.stream(states).anyMatch(s -> s.name().equals("CURRENT")));
        assertTrue(Arrays.stream(states).anyMatch(s -> s.name().equals("FUTURE")));
        assertTrue(Arrays.stream(states).anyMatch(s -> s.name().equals("PAST")));
        assertTrue(Arrays.stream(states).anyMatch(s -> s.name().equals("REJECTED")));
        assertTrue(Arrays.stream(states).anyMatch(s -> s.name().equals("WAITING")));
    }
}