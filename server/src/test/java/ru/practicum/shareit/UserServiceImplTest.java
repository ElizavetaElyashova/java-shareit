package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder().name("user1").email("u1@cane.com").build();
        user2 = User.builder().name("user2").email("u2@gato.com").build();
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAll() {
        userRepository.saveAll(List.of(user1, user2));
        List<UserDto> result = userService.findAll();
        assertThat(result, hasSize(2));
        assertThat(result, equalToObject(Stream.of(user1, user2).map(UserMapper::toUserDto).toList()));
    }

    @Test
    void findById() {
        userRepository.saveAll(List.of(user1, user2));
        UserDto target = UserMapper.toUserDto(userRepository.findAll().getFirst());
        UserDto result = UserMapper.toUserDto(userRepository.findById(target.getId()).get());
        assertThat(result, equalToObject(target));
    }

    @Test
    void create() {
        UserDto result = userService.create(UserMapper.toUserDto(user1));
        UserDto target = UserMapper.toUserDto(userRepository.findById(result.getId()).get());
        assertThat(result, equalToObject(target));
    }

    @Test
    void update() {
        User oldUser = userRepository.save(user1);
        oldUser.setName("u1");
        oldUser.setEmail("u1@lobo.com");
        UserDto newUser = userService.update(new UpdateUserRequest(oldUser.getId(), oldUser.getName(), oldUser.getEmail()));
        assertThat(newUser, equalToObject(UserMapper.toUserDto(oldUser)));
    }

    @Test
    void remove() {
        long id = userRepository.save(user1).getId();
        assertThat(userRepository.findAll(), hasSize(1));
        userService.remove(id);
        assertThat(userRepository.findAll(), hasSize(0));
    }
}
