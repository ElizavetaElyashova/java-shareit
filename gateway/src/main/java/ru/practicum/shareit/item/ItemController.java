package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get user items with userId={}", userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item with itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Create item {} with userId={}", itemDto, userId);
        return itemClient.postItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        itemDto.setId(itemId);
        log.info("Update item {} with userId={}", itemDto, userId);
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text) {
        log.info("Search items with text={}", text);
        return itemClient.seachItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentDto commentDto, @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Post comment {} with itemId={}, userId={}", commentDto, itemId, userId);
        return itemClient.postComment(userId, itemId, commentDto);
    }
}
