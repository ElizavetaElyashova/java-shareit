package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(NewItemRequestDto itemRequestDto, long requestorId) {
        User requestor = userRepository.findById(requestorId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto.getDescription(),
                requestor, LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithResponsesDto> findUserRequests(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(userId);
        List<Long> ids = requests.stream().map(ItemRequest::getId).toList();
        List<ItemRequestResponse> responses = itemRepository.findByRequestIdIn(ids);
        Map<Long, List<ItemRequestResponse>> responsesByRequest = new HashMap<>();

        for (ItemRequestResponse response : responses) {
            responsesByRequest.put(response.getRequestId(), responsesByRequest.getOrDefault(response.getRequestId(), new ArrayList<>()));
            responsesByRequest.get(response.getRequestId()).add(response);
        }
        List<ItemRequestWithResponsesDto> requestWithResponsesDtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            requestWithResponsesDtos.add(
                    ItemRequestMapper.toItemRequestWithResponsesDto(
                            request,
                            responsesByRequest.get(request.getId()))
            );
        }
        return requestWithResponsesDtos.stream().sorted(Comparator.comparing(ItemRequestWithResponsesDto::getCreated).reversed()).toList();
    }

    @Override
    public List<ItemRequestDto> findOtherUsersRequests(long userId) {
        return itemRequestRepository.findByRequestorIdNot(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }

    @Override
    public ItemRequestWithResponsesDto findRequest(long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NoSuchElementException("Запрос не найден."));
        List<ItemRequestResponse> responses = itemRepository.findByRequestId(requestId);
        return ItemRequestMapper.toItemRequestWithResponsesDto(itemRequest, responses);
    }
}
