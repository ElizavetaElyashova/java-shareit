package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(classes = ItemServiceImpl.class)
@ActiveProfiles("test")
public class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void beforeEach() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        user1 = userRepository.save(User.builder().name("user1").email("u1@cane.com").build());
        user2 = userRepository.save(User.builder().name("user2").email("u2@gato.com").build());
        userRepository.saveAll(List.of(user1, user2));
        item1 = Item.builder().name("item1").description("bike").available(false).build();
        item2 = Item.builder().name("item2").description("cup").available(false).build();
    }

    @Test
    void findUserItems() {
        item1.setOwner(user1);
        item2.setOwner(user1);
        itemRepository.saveAll(List.of(item1, item2));
        List<ItemDto> result = itemService.findUserItems(user1.getId());
        assertThat(result, equalToObject(
                Stream.of(item1, item2)
                        .map(item -> ItemMapper.toItemDto(item, null, null, null))
                        .toList()
        ));
    }

    @Test
    void findById() {
        item1.setOwner(user1);
        Item target = itemRepository.save(item1);
        Item result = ItemMapper.toItem(itemService.findById(target.getId(), user1.getId()), user1);
        assertThat(result, equalToObject(target));
    }

    @Test
    void create() {
        item1.setOwner(user1);
        ItemDto newItem = ItemDto.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .build();
        NewItemDto result = itemService.create(newItem, user1.getId());
        NewItemDto target = ItemMapper.toNewItemDto(itemRepository.findById(result.getId()).get());
        assertThat(result, equalToObject(target));
    }

    @Test
    void update() {
        item1.setOwner(user1);
        Item oldItem = itemRepository.save(item1);
        oldItem.setDescription("motorbike");
        oldItem.setAvailable(true);
        oldItem.setName("it1");
        ItemDto oldItemDto = ItemMapper.toItemDto(oldItem, null, null, null);
        ItemDto result = itemService.update(oldItemDto, user1.getId());
        assertThat(result.getId(), equalToObject(oldItemDto.getId()));
        assertThat(result.getName(), equalToObject(oldItemDto.getName()));
        assertThat(result.getDescription(), equalToObject(oldItemDto.getDescription()));
        assertThat(result.getAvailable(), equalToObject(oldItemDto.getAvailable()));
    }

    @Test
    void search() {
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1 = itemRepository.save(item1);
        List<ItemDto> result = itemService.search("bike");
        assertThat(result, hasSize(1));
    }

    @Test
    void addComment() {
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1 = itemRepository.save(item1);
        Booking booking = Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusNanos(1))
                .item(item1)
                .booker(user2)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        CommentDto comment = CommentDto.builder()
                .text("comment")
                .authorName(user2.getName())
                .created(LocalDateTime.now())
                .build();
        comment = itemService.addComment(comment, item1.getId(), user2.getId());
        ItemDto itemDto = itemService.findById(item1.getId(), item1.getOwner().getId());
        assertThat(comment, equalToObject(itemDto.getComments().getFirst()));
    }

}
