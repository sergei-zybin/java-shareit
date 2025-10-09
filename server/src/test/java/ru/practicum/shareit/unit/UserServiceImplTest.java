package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private UserDto userDto;
    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
    }

    @Test
    void createUser_withValidData_shouldReturnUserDto() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void createUser_withDuplicateEmail_shouldThrowConflictException() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    void createUser_withInvalidEmail_shouldThrowValidationException() {
        UserDto invalidUserDto = UserDto.builder()
                .name("Test User")
                .email("invalid-email")
                .build();

        assertThrows(ValidationException.class, () -> userService.create(invalidUserDto));
    }

    @Test
    void createUser_withEmptyEmail_shouldThrowValidationException() {
        UserDto invalidUserDto = UserDto.builder()
                .name("Test User")
                .email("")
                .build();

        assertThrows(ValidationException.class, () -> userService.create(invalidUserDto));
    }

    @Test
    void updateUser_withValidData_shouldReturnUpdatedUser() {
        UserDto updateDto = UserDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_withDuplicateEmail_shouldThrowConflictException() {
        UserDto updateDto = UserDto.builder()
                .email("another@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(anotherUser));

        assertThrows(ConflictException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void updateUser_withSameEmail_shouldUpdateSuccessfully() {
        UserDto updateDto = UserDto.builder()
                .name("Updated User")
                .email("test@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_withNonExistentUser_shouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserDto updateDto = UserDto.builder().name("Updated User").build();

        assertThrows(NotFoundException.class, () -> userService.update(999L, updateDto));
    }

    @Test
    void updateUser_withPartialData_shouldUpdateOnlyProvidedFields() {
        UserDto updateDto = UserDto.builder()
                .name("Updated User")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated User", result.getName());
        assertEquals("test@example.com", result.getEmail()); // Email remains unchanged
    }

    @Test
    void getUserById_withValidId_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getUserById_withNonExistentId_shouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(999L));
    }

    @Test
    void getAllUsers_shouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user, anotherUser));

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllUsers_whenNoUsers_shouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUser_withValidId_shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_withNonExistentId_shouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.delete(999L));
    }
}