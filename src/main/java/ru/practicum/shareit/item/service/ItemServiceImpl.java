package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> findUserItems(long userId) {
        userService.findById(userId);
        return itemRepository.findUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto findById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId));
    }

    @Override
    public NewItemDto create(ItemDto itemDto, long userId) {
        User owner = UserMapper.toUser(userService.findById(userId));
        return ItemMapper.toNewItemDto(itemRepository.create(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        User owner = UserMapper.toUser(userService.findById(userId));
        return ItemMapper.toItemDto(itemRepository.update(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.findByNameOrDescription(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
