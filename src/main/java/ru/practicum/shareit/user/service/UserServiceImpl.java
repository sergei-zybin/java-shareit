package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден.");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Email не может быть пустым.");
        }

        if (!isValidEmail(userDto.getEmail())) {
            throw new ValidationException("Некорректный формат email.");
        }

        for (User existingUser : userStorage.getAll()) {
            if (existingUser.getEmail().equals(userDto.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует.");
            }
        }

        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existingUser = userStorage.getById(id);
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден.");
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!isValidEmail(userDto.getEmail())) {
                throw new ValidationException("Некорректный формат email.");
            }

            for (User user : userStorage.getAll()) {
                if (user.getEmail().equals(userDto.getEmail()) && !user.getId().equals(id)) {
                    throw new ConflictException("Пользователь с таким email уже существует.");
                }
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userStorage.update(existingUser));
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") < email.lastIndexOf(".");
    }
}