package ru.practicum.shareit.misc;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void testCommentCreation() {

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.now());

        assertEquals(1L, comment.getId());
        assertEquals("Test comment", comment.getText());
        assertNotNull(comment.getCreated());
    }

    @Test
    void testCommentWithItemAndAuthor() {

        Comment comment = new Comment();
        Item item = new Item();
        item.setId(10L);

        User author = new User();
        author.setId(20L);
        author.setName("Test User");

        comment.setId(1L);
        comment.setText("Test comment");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.of(2023, 10, 10, 12, 0));

        assertEquals(1L, comment.getId());
        assertEquals("Test comment", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals(LocalDateTime.of(2023, 10, 10, 12, 0), comment.getCreated());
    }

    @Test
    void testCommentEqualsAndHashCode() {

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Comment 1");

        Comment comment2 = new Comment();
        comment2.setId(1L);
        comment2.setText("Comment 2");

        Comment comment3 = new Comment();
        comment3.setId(2L);
        comment3.setText("Comment 1");

        assertEquals(comment1, comment2); // Same ID
        assertNotEquals(comment1, comment3); // Different ID
        assertEquals(comment1.hashCode(), comment2.hashCode());
        assertNotEquals(comment1.hashCode(), comment3.hashCode());
    }

    @Test
    void testCommentEqualsWithNull() {

        Comment comment = new Comment();
        comment.setId(1L);

        assertNotEquals(null, comment);
        assertNotEquals(comment, new Object());
    }

    @Test
    void testCommentToString() {

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.of(2023, 10, 10, 12, 0));

        String toString = comment.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("text=Test comment"));
        assertTrue(toString.contains("created=2023-10-10T12:00"));
    }
}