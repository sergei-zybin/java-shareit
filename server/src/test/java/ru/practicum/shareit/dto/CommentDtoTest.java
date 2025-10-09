package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = CommentDto.builder()
                .text("Test comment")
                .authorName("Test Author")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void serializeAndDeserialize_shouldReturnSameDto() throws Exception {
        String json = objectMapper.writeValueAsString(commentDto);
        CommentDto deserialized = objectMapper.readValue(json, CommentDto.class);

        assertEquals(commentDto.getText(), deserialized.getText());
        assertEquals(commentDto.getAuthorName(), deserialized.getAuthorName());
    }

    @Test
    void deserialize_withMissingText_shouldHandleNull() throws Exception {
        String json = "{\"authorName\":\"Test Author\",\"created\":\"2023-10-10T10:00:00\"}";
        CommentDto deserialized = objectMapper.readValue(json, CommentDto.class);

        assertNull(deserialized.getText());
        assertEquals("Test Author", deserialized.getAuthorName());
        assertNotNull(deserialized.getCreated());
    }

    @Test
    void deserialize_withEmptyText_shouldHandleEmptyString() throws Exception {
        String json = "{\"text\":\"\",\"authorName\":\"Test Author\",\"created\":\"2023-10-10T10:00:00\"}";
        CommentDto deserialized = objectMapper.readValue(json, CommentDto.class);

        assertEquals("", deserialized.getText());
        assertEquals("Test Author", deserialized.getAuthorName());
    }

    @Test
    void deserialize_withNullFields_shouldHandleGracefully() throws Exception {
        String json = "{\"text\":\"Test comment\"}";
        CommentDto deserialized = objectMapper.readValue(json, CommentDto.class);

        assertEquals("Test comment", deserialized.getText());
        assertNull(deserialized.getAuthorName());
        assertNull(deserialized.getCreated());
    }

    @Test
    void deserialize_withAllFields_shouldMapCorrectly() throws Exception {
        String json = "{\"id\":1,\"text\":\"Test comment\",\"authorName\":\"Test Author\",\"created\":\"2023-10-10T10:00:00\"}";
        CommentDto deserialized = objectMapper.readValue(json, CommentDto.class);

        assertEquals(1L, deserialized.getId());
        assertEquals("Test comment", deserialized.getText());
        assertEquals("Test Author", deserialized.getAuthorName());
        assertNotNull(deserialized.getCreated());
    }
}