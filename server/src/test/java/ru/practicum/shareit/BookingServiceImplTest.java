package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToObject;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Booking booking;


    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(User.builder().name("user1").email("u1@cane.com").build());
        user2 = userRepository.save(User.builder().name("user2").email("u2@gato.com").build());
        userRepository.saveAll(List.of(user1, user2));
        item1 = Item.builder().name("item1").description("bike").available(true).owner(user1).build();
        item2 = Item.builder().name("item2").description("cup").available(true).owner(user1).build();
        itemRepository.saveAll(List.of(item1, item2));
        booking = Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusSeconds(1))
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void create() {
        NewBookingRequestDto newBookingRequestDto = NewBookingRequestDto.builder()
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        BookingDto result = bookingService.create(newBookingRequestDto, booking.getBooker().getId());
        assertThat(result.getStart(), equalToObject(newBookingRequestDto.getStart()));
        assertThat(result.getEnd(), equalToObject(newBookingRequestDto.getEnd()));
        assertThat(result.getItem(), equalToObject(item1));
        assertThat(result.getBooker(), equalToObject(user2));
        assertThat(result.getStatus(), equalToObject(Status.WAITING));
    }

    @Test
    void setStatus() {
        booking = bookingRepository.save(booking);
        BookingDto bookingDto = bookingService.setStatus(true, booking.getId(), booking.getItem().getOwner().getId());
        assertThat(bookingDto.getStatus(), equalToObject(Status.APPROVED));
    }

    @Test
    void findById() {
        booking = bookingRepository.save(booking);
        Booking result = BookingMapper.toBooking(bookingService.findById(booking.getId(), booking.getBooker().getId()));
        assertThat(result, equalToObject(booking));
    }

    @Test
    void findAll() {
        BookingDto booking1 = BookingMapper.toBookingDto(bookingRepository.save(booking));
        Booking newBooking = Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusSeconds(1))
                .item(item2)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        BookingDto booking2 = BookingMapper.toBookingDto(bookingRepository.save(newBooking));
        List<BookingDto> target = Stream.of(booking1, booking2)
                .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                .toList();
        List<BookingDto> result = bookingService.findAll(user2.getId(), State.ALL);
        assertThat(result, equalToObject(target));
    }

    @Test
    void findAllByOwner() {
        BookingDto booking1 = BookingMapper.toBookingDto(bookingRepository.save(booking));
        Booking newBooking = Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusSeconds(1))
                .item(item2)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        BookingDto booking2 = BookingMapper.toBookingDto(bookingRepository.save(newBooking));
        List<BookingDto> target = Stream.of(booking1, booking2)
                .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                .toList();
        List<BookingDto> result = bookingService.findAllByOwner(user1.getId(), State.ALL);
        assertThat(result, equalToObject(target));
    }
}
