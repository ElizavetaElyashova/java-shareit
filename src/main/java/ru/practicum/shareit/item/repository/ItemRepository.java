package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> findUserItems(long userId);

    Item findById(long id);

    List<Item> findByNameOrDescription(String text);

    Item create(Item item);

    Item update(Item item);
}
