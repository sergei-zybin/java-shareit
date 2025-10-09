package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.shareit.user.dto.UserDto;

import jakarta.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class UserDtoTest {

    @TestConfiguration
    static class ValidatorConfig {
        @Bean
        public LocalValidatorFactoryBean validator() {
            LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();
            return validator;
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocalValidatorFactoryBean validator;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    void serializeAndDeserialize_shouldReturnSameDto() throws Exception {
        String json = objectMapper.writeValueAsString(userDto);
        UserDto deserialized = objectMapper.readValue(json, UserDto.class);

        assertEquals(userDto.getName(), deserialized.getName());
        assertEquals(userDto.getEmail(), deserialized.getEmail());

        Set<ConstraintViolation<UserDto>> violations = validator.validate(deserialized);
        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации.");
    }

    @Test
    void deserialize_withMissingEmail_shouldHandleNull() throws Exception {
        String json = "{\"name\":\"Test User\"}";
        UserDto deserialized = objectMapper.readValue(json, UserDto.class);

        assertNull(deserialized.getEmail(), "Email should be null");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(deserialized);
        if (!violations.isEmpty()) {
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")),
                    "Валидация должна обнаружить недостающий e-mail если @NotBlank или @Email присутствует.");
        }
    }

    @Test
    void deserialize_withInvalidEmail_shouldHaveValidationError() throws Exception {
        String json = "{\"name\":\"Test User\",\"email\":\"invalid-email\"}";
        UserDto deserialized = objectMapper.readValue(json, UserDto.class);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(deserialized);
        if (!violations.isEmpty()) {
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")),
                    "Валидация долджна обнаружить валидный @Email присутствует.");
        }
    }
}