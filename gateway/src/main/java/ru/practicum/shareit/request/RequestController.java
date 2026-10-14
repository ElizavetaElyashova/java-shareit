package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody NewItemRequestDto newItemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Create request {} with userId={}", newItemRequestDto, userId);
        return requestClient.postRequest(userId, newItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get user requests with userId={}", userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get other users requests with userId={}", userId);
        return requestClient.getOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable long requestId) {
        log.info("Get request with requestId={}", requestId);
        return requestClient.getRequest(requestId);
    }
}
