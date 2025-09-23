package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    List<ItemDto> getAll();

    ItemDto getById(Long id);

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(Long id, ItemDto itemDto, Long ownerId);

    void delete(Long id);

    List<ItemDto> getByOwner(Long ownerId);

    List<ItemDto> search(String text);
}