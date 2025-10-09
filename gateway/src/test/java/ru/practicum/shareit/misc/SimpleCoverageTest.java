package ru.practicum.shareit.misc;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SimpleCoverageTest {

    @Test
    void dtoConstructorsAndBuilders_ShouldWork() {

        ItemDto item1 = new ItemDto();
        ItemDto item2 = new ItemDto(1L, "Item", "Description", true, 1L);
        ItemDto item3 = ItemDto.builder()
                .id(2L).name("Item2").description("Desc2").available(false).requestId(2L)
                .build();

        assertNotNull(item1);
        assertNotNull(item2);
        assertNotNull(item3);
        assertEquals("Item", item2.getName());

        UserDto user1 = new UserDto();
        UserDto user2 = new UserDto(1L, "User", "user@test.com");
        UserDto user3 = UserDto.builder().id(2L).name("User2").email("user2@test.com").build();

        assertNotNull(user1);
        assertNotNull(user2);
        assertNotNull(user3);
        assertEquals("User", user2.getName());

        LocalDateTime now = LocalDateTime.now();
        CommentDto comment1 = new CommentDto();
        CommentDto comment2 = new CommentDto(1L, "Text", "Author", now);
        CommentDto comment3 = CommentDto.builder()
                .id(2L).text("Text2").authorName("Author2").created(now).build();

        assertNotNull(comment1);
        assertNotNull(comment2);
        assertNotNull(comment3);
        assertEquals("Text", comment2.getText());

        ItemRequestDto request1 = new ItemRequestDto();
        ItemRequestDto request2 = new ItemRequestDto(1L, "Description", 1L, now);

        assertNotNull(request1);
        assertNotNull(request2);
        assertEquals("Description", request2.getDescription());
    }

    @Test
    void bookingState_AllMethods_ShouldWork() {

        BookingState[] states = BookingState.values();
        assertTrue(states.length > 0);


        for (BookingState state : states) {
            Optional<BookingState> result = BookingState.from(state.name());
            assertTrue(result.isPresent());
            assertEquals(state, result.get());

            Optional<BookingState> resultLower = BookingState.from(state.name().toLowerCase());
            assertTrue(resultLower.isPresent());
            assertEquals(state, resultLower.get());
        }

        Optional<BookingState> invalid = BookingState.from("INVALID_STATE");
        assertFalse(invalid.isPresent());

        Optional<BookingState> nullResult = BookingState.from(null);
        assertFalse(nullResult.isPresent());
    }

    @Test
    void dtoMethods_ShouldWork() {

        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Test");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setRequestId(1L);

        assertEquals(1L, item.getId());
        assertEquals("Test", item.getName());
        assertEquals("Desc", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(1L, item.getRequestId());

        assertNotNull(item.toString());
        assertTrue(item.toString().contains("Test"));

                ItemDto sameItem = new ItemDto(1L, "Test", "Desc", true, 1L);
        ItemDto differentItem = new ItemDto(2L, "Different", "Desc2", false, 2L);

        assertEquals(item, sameItem);
        assertNotEquals(item, differentItem);
        assertEquals(item.hashCode(), sameItem.hashCode());
    }
}