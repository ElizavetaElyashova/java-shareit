package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(NewItemRequestDto itemRequestDto, long requestorId);

    List<ItemRequestWithResponsesDto> findUserRequests(long userId);

    List<ItemRequestDto> findOtherUsersRequests(long userId);

    ItemRequestWithResponsesDto findRequest(long requestId);
}
