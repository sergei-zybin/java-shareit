package ru.practicum.shareit.misc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ValidationTest {

    @Autowired
    private JacksonTester<UserDto> userJson;

    @Autowired
    private JacksonTester<ItemDto> itemJson;

    @Autowired
    private JacksonTester<BookingRequestDto> bookingJson;

    @Test
    void userDtoSerialization_shouldWorkCorrectly() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        String json = userJson.write(user).getJson();

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test User\"");
        assertThat(json).contains("\"email\":\"test@example.com\"");
    }

    @Test
    void userDtoDeserialization_shouldWorkCorrectly() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        UserDto user = userJson.parseObject(json);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void itemDtoSerialization_shouldWorkCorrectly() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setRequestId(5L);

        String json = itemJson.write(item).getJson();

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"description\":\"Test Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":5");
    }

    @Test
    void itemDtoDeserialization_shouldWorkCorrectly() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"requestId\":5}";

        ItemDto item = itemJson.parseObject(json);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Test Item");
        assertThat(item.getDescription()).isEqualTo("Test Description");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getRequestId()).isEqualTo(5L);
    }

    @Test
    void bookingRequestDtoSerialization_shouldWorkCorrectly() throws Exception {
        BookingRequestDto booking = new BookingRequestDto();
        booking.setItemId(1L);
        booking.setStart(LocalDateTime.of(2023, 10, 10, 10, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 11, 10, 0));

        String json = bookingJson.write(booking).getJson();

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"2023-10-10T10:00:00\"");
        assertThat(json).contains("\"end\":\"2023-10-11T10:00:00\"");
    }

    @Test
    void bookingRequestDtoDeserialization_shouldWorkCorrectly() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2023-10-10T10:00:00\",\"end\":\"2023-10-11T10:00:00\"}";

        BookingRequestDto booking = bookingJson.parseObject(json);

        assertThat(booking.getItemId()).isEqualTo(1L);
        assertThat(booking.getStart()).isEqualTo(LocalDateTime.of(2023, 10, 10, 10, 0));
        assertThat(booking.getEnd()).isEqualTo(LocalDateTime.of(2023, 10, 11, 10, 0));
    }

    @Test
    void userDtoWithNullFields_shouldHandleGracefully() throws Exception {
        UserDto user = new UserDto();
        user.setId(null);
        user.setName(null);
        user.setEmail(null);

        String json = userJson.write(user).getJson();

        UserDto parsed = userJson.parseObject(json);

        assertThat(parsed.getId()).isNull();
        assertThat(parsed.getName()).isNull();
        assertThat(parsed.getEmail()).isNull();
    }

    @Test
    void itemDtoWithNullFields_shouldHandleGracefully() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(null);
        item.setName(null);
        item.setDescription(null);
        item.setAvailable(null);
        item.setRequestId(null);

        String json = itemJson.write(item).getJson();

        ItemDto parsed = itemJson.parseObject(json);

        assertThat(parsed.getId()).isNull();
        assertThat(parsed.getName()).isNull();
        assertThat(parsed.getDescription()).isNull();
        assertThat(parsed.getAvailable()).isNull();
        assertThat(parsed.getRequestId()).isNull();
    }
}