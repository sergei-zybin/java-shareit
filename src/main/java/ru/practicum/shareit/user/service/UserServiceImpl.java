package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден."));
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

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Пользователь с таким email уже существует.");
        }

        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден."));

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!isValidEmail(userDto.getEmail())) {
                throw new ValidationException("Некорректный формат email.");
            }

            userRepository.findByEmail(userDto.getEmail())
                    .ifPresent(user -> {
                        if (!user.getId().equals(id)) {
                            throw new ConflictException("Пользователь с таким email уже существует.");
                        }
                    });
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userRepository.save(existingUser));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден.");
        }
        userRepository.deleteById(id);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") < email.lastIndexOf(".");
    }
}