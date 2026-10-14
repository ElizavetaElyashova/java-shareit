package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoTest {
    private final JacksonTester<BookingDatesDto> bookingDatesDtoJson;
    private final JacksonTester<BookingDto> bookingDtoJson;
    private final JacksonTester<NewBookingRequestDto> newBookingRequestDtoJson;
    LocalDateTime start = LocalDateTime.of(2026, Month.SEPTEMBER, 12, 13, 40);
    LocalDateTime end = LocalDateTime.of(2026, Month.SEPTEMBER, 13, 13, 40);

    @Test
    void testBookingDatesDto() throws Exception {
        BookingDatesDto bookingDatesDto = new BookingDatesDto(1L, start, end);
        JsonContent<BookingDatesDto> result = bookingDatesDtoJson.write(bookingDatesDto);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-09-12T13:40:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-09-13T13:40:00");
    }

    @Test
    void testBookingDto() throws Exception {
        Item item = Item.builder().id(3L).name("item").build();
        User user = User.builder().id(2).name("user").build();
        BookingDto bookingDto = BookingDto.builder()
                .id(1).start(start).end(end).item(item)
                .booker(user).status(Status.WAITING).build();
        JsonContent<BookingDto> result = bookingDtoJson.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-09-12T13:40:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-09-13T13:40:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testNewBookingRequestDto() throws Exception {
        NewBookingRequestDto newBookingRequestDto = new NewBookingRequestDto(1L, start, end);
        JsonContent<NewBookingRequestDto> result = newBookingRequestDtoJson.write(newBookingRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-09-12T13:40:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-09-13T13:40:00");
    }
}
