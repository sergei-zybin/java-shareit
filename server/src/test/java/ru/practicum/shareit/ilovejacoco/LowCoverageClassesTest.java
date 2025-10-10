package ru.practicum.shareit.ilovejacoco;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {BookingController.class, ItemRequestController.class, UserController.class})
public class LowCoverageClassesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private UserService userService;

    @Test
    void testBookingModel() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(1));
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setId(1L);

        Booking booking3 = new Booking();
        booking3.setId(2L);

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertNotEquals(booking1, null);
        assertNotEquals(booking1, "string");

        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertNotEquals(booking1.hashCode(), booking3.hashCode());

        assertNotNull(booking1.toString());

        Item item = new Item();
        User booker = new User();

        booking1.setItem(item);
        booking1.setBooker(booker);

        assertEquals(item, booking1.getItem());
        assertEquals(booker, booking1.getBooker());
        assertEquals(BookingStatus.WAITING, booking1.getStatus());
    }

    @Test
    void testUserModel() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("test");
        user1.setEmail("test@test.com");

        User user2 = new User();
        user2.setId(1L);

        User user3 = new User();
        user3.setId(2L);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, "string");

        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());

        assertNotNull(user1.toString());

        assertEquals("test", user1.getName());
        assertEquals("test@test.com", user1.getEmail());
    }

    @Test
    void testItemRequestModel() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("test");
        request1.setCreated(LocalDateTime.now());

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        ItemRequest request3 = new ItemRequest();
        request3.setId(2L);

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertNotEquals(request1, null);
        assertNotEquals(request1, "string");

        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());

        assertNotNull(request1.toString());

        User requestor = new User();
        request1.setRequestor(requestor);
        assertEquals(requestor, request1.getRequestor());
        assertEquals("test", request1.getDescription());
        assertNotNull(request1.getCreated());
    }


    @Test
    void testBookingControllerAllMethods() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);

        when(bookingService.create(any(BookingRequestDto.class), anyLong())).thenReturn(responseDto);
        when(bookingService.updateStatus(anyLong(), anyBoolean(), anyLong())).thenReturn(responseDto);
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(responseDto);
        when(bookingService.getBookingsByBooker(anyLong(), anyString())).thenReturn(List.of(responseDto));
        when(bookingService.getBookingsByOwner(anyLong(), anyString())).thenReturn(List.of(responseDto));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content("{\"start\":\"2023-01-01T10:00:00\",\"end\":\"2023-01-02T10:00:00\",\"itemId\":1}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void testItemRequestControllerAllMethods() throws Exception {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();
        responseDto.setId(1L);

        when(itemRequestService.create(any(ItemRequestDto.class), anyLong())).thenReturn(responseDto);
        when(itemRequestService.getByRequestor(anyLong())).thenReturn(List.of(responseDto));
        when(itemRequestService.getOtherUsersRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(responseDto));
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content("{\"description\":\"test request\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testUserControllerAllMethods() throws Exception {
        UserDto userDto = new UserDto(1L, "test", "test@test.com");

        when(userService.getAll()).thenReturn(List.of(userDto));
        when(userService.getById(anyLong())).thenReturn(userDto);
        when(userService.create(any(UserDto.class))).thenReturn(userDto);
        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"name\":\"test\",\"email\":\"test@test.com\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content("{\"name\":\"updated\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}


