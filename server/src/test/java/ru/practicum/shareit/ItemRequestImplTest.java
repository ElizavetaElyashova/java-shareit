package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@ActiveProfiles("test")
public class ItemRequestImplTest {
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User user1;
    private User user2;
    private Item item1;

    @BeforeEach
    void beforeEach() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        user1 = userRepository.save(User.builder().name("user1").email("u1@cane.com").build());
        user2 = userRepository.save(User.builder().name("user2").email("u2@gato.com").build());
        userRepository.saveAll(List.of(user1, user2));
    }

    @Test
    void create() {
        ItemRequestDto result = itemRequestService.create(new NewItemRequestDto("item"), user1.getId());
        ItemRequest target = itemRequestRepository.findById(result.getId()).get();
        assertThat(result.getId(), equalToObject(target.getId()));
        assertThat(result.getDescription(), equalToObject(target.getDescription()));
        assertThat(result.getCreated(), equalToObject(target.getCreated()));
    }

    @Test
    void findUserRequests() {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("item1")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("item2")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();
        itemRequest1 = itemRequestRepository.save(itemRequest1);
        itemRequest2 = itemRequestRepository.save(itemRequest2);
        item1 = Item.builder().name("item1").description("bike").available(true).owner(user2).requestId(itemRequest1.getId()).build();
        item1 = itemRepository.save(item1);
        List<ItemRequestWithResponsesDto> result = itemRequestService.findUserRequests(user1.getId());
        assertThat(result, hasSize(2));
        assertThat(result.getLast().getItems().getFirst().getId(), equalToObject(item1.getId()));
    }

    @Test
    void findOtherUsersRequests() {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("item1")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("item2")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.saveAll(List.of(itemRequest1, itemRequest2));
        List<ItemRequestDto> target = Stream.of(itemRequest1, itemRequest2)
                .map(ItemRequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
        List<ItemRequestDto> result = itemRequestService.findOtherUsersRequests(user2.getId());
        assertThat(result, equalToObject(target));
    }

    @Test
    void findRequest() {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .description("item1")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();
        itemRequest1 = itemRequestRepository.save(itemRequest1);
        item1 = Item.builder().name("item1").description("bike").available(true).owner(user2).requestId(itemRequest1.getId()).build();
        item1 = itemRepository.save(item1);

        ItemRequestWithResponsesDto result = itemRequestService.findRequest(itemRequest1.getId());
        assertThat(result.getId(), equalToObject(itemRequest1.getId()));
        assertThat(result.getDescription(), equalToObject(itemRequest1.getDescription()));
        assertThat(result.getCreated(), equalToObject(itemRequest1.getCreated()));
        assertThat(result.getItems(), hasSize(1));
        assertThat(result.getItems().getFirst().getId(), equalToObject(item1.getId()));
        assertThat(result.getItems().getFirst().getRequestId(), equalToObject(itemRequest1.getId()));
    }
}
