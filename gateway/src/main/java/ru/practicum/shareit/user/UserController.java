package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get users");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable long userId) {
        log.info("Get user with userId={}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> post(@RequestBody @Valid UserDto userDto) {
        log.info("Post user {}", userDto);
        return userClient.postUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody @Valid UpdateUserRequest updateUserRequest, @PathVariable long userId) {
        updateUserRequest.setId(userId);
        log.info("Update user {} with userId={}", updateUserRequest, userId);
        return userClient.patchUser(userId, updateUserRequest);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> remove(@PathVariable long userId) {
        log.info("Delete user with userId={}", userId);
        return userClient.deleteUser(userId);
    }
}
