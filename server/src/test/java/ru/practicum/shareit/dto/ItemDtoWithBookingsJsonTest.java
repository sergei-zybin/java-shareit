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
    void deserialize_shouldMapAllFieldsCorrectly() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"name\":\"Дрель\"," +
                "\"description\":\"Мощная дрель с ударным механизмом\"," +
                "\"available\":true," +
                "\"requestId\":10," +
                "\"lastBooking\":{" +
                "\"id\":1," +
                "\"bookerId\":2," +
                "\"start\":\"2023-10-10T10:00:00\"," +
                "\"end\":\"2023-10-11T10:00:00\"" +
                "}," +
                "\"nextBooking\":{" +
                "\"id\":2," +
                "\"bookerId\":3," +
                "\"start\":\"2023-10-12T10:00:00\"," +
                "\"end\":\"2023-10-13T10:00:00\"" +
                "}," +
                "\"comments\":[" +
                "{" +
                "\"id\":1," +
                "\"text\":\"Отличная вещь!\"," +
                "\"authorName\":\"Пользователь 1\"," +
                "\"created\":\"2023-10-05T10:00:00\"" +
                "}," +
                "{" +
                "\"id\":2," +
                "\"text\":\"Очень понравилось\"," +
                "\"authorName\":\"Пользователь 2\"," +
                "\"created\":\"2023-10-07T10:00:00\"" +
                "}" +
                "]" +
                "}";

        ItemDtoWithBookings result = objectMapper.readValue(json, ItemDtoWithBookings.class);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Дрель", result.getName());
        assertEquals("Мощная дрель с ударным механизмом", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(10L, result.getRequestId());

        assertNotNull(result.getLastBooking());
        assertEquals(1L, result.getLastBooking().getId());
        assertEquals(2L, result.getLastBooking().getBookerId());

        assertNotNull(result.getNextBooking());
        assertEquals(2L, result.getNextBooking().getId());
        assertEquals(3L, result.getNextBooking().getBookerId());

        assertNotNull(result.getComments());
        assertEquals(2, result.getComments().size());
        assertEquals("Отличная вещь!", result.getComments().get(0).getText());
        assertEquals("Очень понравилось", result.getComments().get(1).getText());
    }

    @Test
    void deserialize_withNullBookings_shouldHandleCorrectly() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"name\":\"Дрель\"," +
                "\"description\":\"Мощная дрель с ударным механизмом\"," +
                "\"available\":true," +
                "\"requestId\":10," +
                "\"lastBooking\":null," +
                "\"nextBooking\":null," +
                "\"comments\":[]" +
                "}";

        ItemDtoWithBookings result = objectMapper.readValue(json, ItemDtoWithBookings.class);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Дрель", result.getName());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void deserialize_withNullComments_shouldHandleCorrectly() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"name\":\"Дрель\"," +
                "\"description\":\"Мощная дрель с ударным механизмом\"," +
                "\"available\":true," +
                "\"requestId\":10," +
                "\"lastBooking\":{" +
                "\"id\":1,\"bookerId\":2,\"start\":\"2023-10-10T10:00:00\",\"end\":\"2023-10-11T10:00:00\"" +
                "}," +
                "\"nextBooking\":null," +
                "\"comments\":null" +
                "}";

        ItemDtoWithBookings result = objectMapper.readValue(json, ItemDtoWithBookings.class);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertNull(result.getComments());
    }

    @Test
    void deserialize_withMissingFields_shouldHandleGracefully() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"name\":\"Дрель\"" +
                "}";

        ItemDtoWithBookings result = objectMapper.readValue(json, ItemDtoWithBookings.class);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Дрель", result.getName());
        assertNull(result.getDescription());
        assertNull(result.getAvailable());
        assertNull(result.getRequestId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertNull(result.getComments());
    }

    @Test
    void deserialize_withEmptyCommentsArray_shouldHandleCorrectly() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"name\":\"Дрель\"," +
                "\"description\":\"Мощная дрель с ударным механизмом\"," +
                "\"available\":true," +
                "\"requestId\":10," +
                "\"lastBooking\":null," +
                "\"nextBooking\":null," +
                "\"comments\":[]" +
                "}";

        ItemDtoWithBookings result = objectMapper.readValue(json, ItemDtoWithBookings.class);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());
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