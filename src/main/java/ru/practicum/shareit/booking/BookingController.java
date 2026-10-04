package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody @Valid NewBookingRequestDto newBooking, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.create(newBooking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setStatus(@PathVariable long bookingId, @RequestParam boolean approved, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.setStatus(approved, bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable long bookingId) {
        return bookingService.findById(bookingId);
    }

    @GetMapping
    public List<BookingDto> findAll(@RequestParam(required = false, defaultValue = "ALL") State state, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findAll(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestParam(required = false, defaultValue = "ALL") State state, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findAllByOwner(userId, state);
    }

}
