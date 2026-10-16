package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(String description, User requestor, LocalDateTime created) {
        return ItemRequest.builder()
                .description(description)
                .requestor(requestor)
                .created(created)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestWithResponsesDto toItemRequestWithResponsesDto(ItemRequest itemRequest, List<ItemRequestResponse> responses) {
        return ItemRequestWithResponsesDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .items(responses)
                .created(itemRequest.getCreated())
                .build();
    }
}
