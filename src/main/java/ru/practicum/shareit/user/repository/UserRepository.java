package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    Optional<User> findById(long id);

    User save(User user);

    void deleteById(long id);

    Optional<User> findByEmail(String email);

}
