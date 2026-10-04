package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    User findById(long id);

    User create(User user);

    User update(User newUser);

    void remove(long id);

    Optional<User> findByEmail(String email);

}
