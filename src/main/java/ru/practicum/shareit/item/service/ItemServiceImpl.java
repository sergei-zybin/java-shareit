package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public List<ItemDto> getAll() {
        return itemStorage.getAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemStorage.getById(id);
        if (item == null) {
            throw new NotFoundException("Вещь с id=" + id + " не найдена.");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        if (userStorage.getById(ownerId) == null) {
            throw new NotFoundException("Пользователь с id=" + ownerId + " не найден.");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым.");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус доступности должен быть указан.");
        }

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userStorage.getById(ownerId));
        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemStorage.getById(id);
        if (existingItem == null) {
            throw new NotFoundException("Вещь с id=" + id + " не найдена.");
        }

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может редактировать вещь.");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemStorage.update(existingItem));
    }

    @Override
    public void delete(Long id) {
        itemStorage.delete(id);
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        User owner = userStorage.getById(ownerId);
        if (owner == null) {
            throw new NotFoundException("Пользователь с id=" + ownerId + " не найден.");
        }

        return itemStorage.getByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
