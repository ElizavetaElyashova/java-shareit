package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody NewItemRequestDto newItemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.create(newItemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestWithResponsesDto> findUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithResponsesDto findRequest(@PathVariable long requestId) {
        return itemRequestService.findRequest(requestId);
    }
}
