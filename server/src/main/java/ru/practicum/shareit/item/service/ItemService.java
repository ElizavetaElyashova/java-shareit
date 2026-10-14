package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findUserItems(long userId);

    ItemDto findById(long itemId, long userId);

    NewItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(CommentDto commentDto, long itemId, long userId);
}
