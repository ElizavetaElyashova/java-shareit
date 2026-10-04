package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    List<ItemDto> findUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findUserItems(userId);
    }

    @GetMapping("/{itemId}")
    ItemDto findById(@PathVariable long itemId) {
        return itemService.findById(itemId);
    }

    @PostMapping
    NewItemDto create(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto update(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }

    @GetMapping("/search")
    List<ItemDto> search(@RequestParam("text") String text) {
        return itemService.search(text);
    }
}
