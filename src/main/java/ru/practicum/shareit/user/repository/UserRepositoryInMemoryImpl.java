package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.DuplicatedDataException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryInMemoryImpl implements UserRepository {
    private final List<User> users;
    private static long idCount;

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User findById(long id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findAny()
                .orElseThrow();
    }

    @Override
    public User create(User user) {
        if (findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicatedDataException("Такой email уже используется");
        }
        idCount++;
        user.setId(idCount);
        users.add(user);
        return user;
    }

    @Override
    public User update(User newUser) {
        User userToUpdate = findById(newUser.getId());
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            if (findByEmail(newUser.getEmail()).isPresent()) {
                throw new DuplicatedDataException("Такой email уже используется");
            }
            userToUpdate.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            userToUpdate.setName(newUser.getName());
        }
        return userToUpdate;
    }

    @Override
    public void remove(long id) {
        users.remove(findById(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }
}
