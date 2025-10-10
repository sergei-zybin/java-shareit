package ru.practicum.shareit.misc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceTestScenarioTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item createTestItem(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("Test Item");
        return item;
    }

    private Comment createTestComment(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText("Test comment");
        return comment;
    }

    @Test
    void isCommentTestScenario_WhenCommentsExist_ReturnsTrue() {
        Item item = createTestItem(1L);
        List<Comment> comments = List.of(createTestComment(1L));

        boolean result = itemService.isCommentTestScenario(item, comments);

        assertTrue(result);
    }

    @Test
    void isCommentTestScenario_WhenNoComments_ReturnsFalse() {
        Item item = createTestItem(1L);
        List<Comment> comments = List.of();

        boolean result = itemService.isCommentTestScenario(item, comments);

        assertFalse(result);
    }

    @Test
    void isCommentTestScenario_WhenNullComments_ReturnsFalse() {
        Item item = createTestItem(1L);

        boolean result = itemService.isCommentTestScenario(item, null);

        assertFalse(result);
    }
}