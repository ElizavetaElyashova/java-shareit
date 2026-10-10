package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDto create(NewBookingRequestDto newBooking, long userId);

    BookingDto setStatus(boolean approved, long bookingId, long userId);

    BookingDto findById(long bookingId, long userId);

    List<BookingDto> findAll(long userId, State state);

    List<BookingDto> findAllByOwner(long ownerId, State state);

}
