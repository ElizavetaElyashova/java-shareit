package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryInMemoryImpl implements ItemRepository {
    private final List<Item> items;
    private static long idCount;

    @Override
    public List<Item> findUserItems(long userId) {
        return items.stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    @Override
    public Item findById(long id) {
        return items.stream()
                .filter(item -> item.getId() == id)
                .findAny()
                .orElseThrow();
    }

    @Override
    public List<Item> findByNameOrDescription(String text) {
        if (text.isBlank()) {
            return List.of();
        } else {
            String searchText = text.toUpperCase();
            return items.stream()
                    .filter(item -> item.getName().toUpperCase().contains(searchText) || item.getDescription().toUpperCase().contains(searchText))
                    .filter(Item::isAvailable)
                    .toList();
        }
    }

    @Override
    public Item create(Item item) {
        idCount++;
        item.setId(idCount);
        items.add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item itemToUpdate = findById(item.getId());
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        itemToUpdate.setAvailable(item.isAvailable());
        return itemToUpdate;
    }
}
