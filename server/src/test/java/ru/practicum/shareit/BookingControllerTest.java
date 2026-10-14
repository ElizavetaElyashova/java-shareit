package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final User user = User.builder().id(1).build();
    private final Item item = Item.builder().id(1L).available(true).build();
    private final BookingDto bookingDto = BookingDto.builder()
            .id(1).start(LocalDateTime.now()).end(LocalDateTime.now().plusHours(1))
            .item(item).booker(user).status(Status.WAITING).build();

    @Test
    void create() throws Exception {
        NewBookingRequestDto newBookingRequestDto = new NewBookingRequestDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        Mockito.when(bookingService.create(newBookingRequestDto, 1))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(newBookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));
    }

    @Test
    void setStatus() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        Mockito.when(bookingService.setStatus(true, bookingDto.getId(), user.getId()))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));
    }

    @Test
    void findById() throws Exception {
        Mockito.when(bookingService.findById(1, 1))
                .thenReturn(bookingDto);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));
    }

    @Test
    void findAll() throws Exception {
        Mockito.when(bookingService.findAll(1, State.ALL))
                .thenReturn(List.of());
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

    }

    @Test
    void findAllByOwner() throws Exception {
        Mockito.when(bookingService.findAllByOwner(1, State.ALL))
                .thenReturn(List.of());
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

    }
}
