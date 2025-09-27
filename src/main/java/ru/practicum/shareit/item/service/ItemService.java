package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import java.util.List;

public interface ItemService {
    List<ItemDtoWithBookings> getByOwner(Long ownerId);

    ItemDtoWithBookings getById(Long id, Long userId);

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(Long id, ItemDto itemDto, Long ownerId);

    void delete(Long id);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId);
}