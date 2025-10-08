package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getAll();

    Item getById(Long id);

    Item create(Item item);

    Item update(Item item);

    void delete(Long id);

    List<Item> getByOwner(Long ownerId);

    List<Item> search(String text);
}