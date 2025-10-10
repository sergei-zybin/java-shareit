package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ItemDtoWithBookingsJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDtoWithBookings itemDtoWithBookings;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private CommentDto comment1;
    private CommentDto comment2;

    @BeforeEach
    void setUp() {

        lastBooking = new BookingShortDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(2L);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        nextBooking = new BookingShortDto();
        nextBooking.setId(2L);
        nextBooking.setBookerId(3L);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        comment1 = new CommentDto();
        comment1.setId(1L);
        comment1.setText("Отличная вещь!");
        comment1.setAuthorName("Пользователь 1");
        comment1.setCreated(LocalDateTime.now().minusDays(5));

        comment2 = new CommentDto();
        comment2.setId(2L);
        comment2.setText("Очень понравилось");
        comment2.setAuthorName("Пользователь 2");
        comment2.setCreated(LocalDateTime.now().minusDays(3));

        itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setId(1L);
        itemDtoWithBookings.setName("Дрель");
        itemDtoWithBookings.setDescription("Мощная дрель с ударным механизмом");
        itemDtoWithBookings.setAvailable(true);
        itemDtoWithBookings.setRequestId(10L);
        itemDtoWithBookings.setLastBooking(lastBooking);
        itemDtoWithBookings.setNextBooking(nextBooking);
        itemDtoWithBookings.setComments(List.of(comment1, comment2));
    }

    @Test
    void serialize_shouldIncludeAllFields() throws Exception {
        String json = objectMapper.writeValueAsString(itemDtoWithBookings);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"Дрель\""));
        assertTrue(json.contains("\"description\":\"Мощная дрель с ударным механизмом\""));
        assertTrue(json.contains("\"available\":true"));
        assertTrue(json.contains("\"requestId\":10"));
        assertTrue(json.contains("\"lastBooking\""));
        assertTrue(json.contains("\"nextBooking\""));
        assertTrue(json.contains("\"comments\""));
    }

    @Test
    void serialize_withNullFields_shouldHandleCorrectly() throws Exception {
        ItemDtoWithBookings item = new ItemDtoWithBookings();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription(null);
        item.setAvailable(null);
        item.setRequestId(null);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(null);

        String json = objectMapper.writeValueAsString(item);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"Дрель\""));
        assertTrue(json.contains("\"description\":null") || !json.contains("\"description\""));
        assertTrue(json.contains("\"available\":null") || !json.contains("\"available\""));
        assertTrue(json.contains("\"requestId\":null") || !json.contains("\"requestId\""));
        assertTrue(json.contains("\"lastBooking\":null") || !json.contains("\"lastBooking\""));
        assertTrue(json.contains("\"nextBooking\":null") || !json.contains("\"nextBooking\""));
        assertTrue(json.contains("\"comments\":null") || !json.contains("\"comments\""));
    }
}