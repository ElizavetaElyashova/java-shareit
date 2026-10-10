package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.exception.IllegalDateException;
import ru.practicum.shareit.booking.exception.UnavailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.AccessForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(NewBookingRequestDto newBooking, long userId) {
        Item item = itemRepository.findById(newBooking.getItemId()).orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));
        User booker = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        if (newBooking.getStart().equals(newBooking.getEnd())
                || newBooking.getStart().isAfter(newBooking.getEnd())) {
            throw new IllegalDateException("Дата конца бронирования не может совпадать с датой начала.");
        }
        if (!item.getAvailable()) {
            throw new UnavailableItemException("Вещь недоступна для бронирования.");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(newBooking, item, booker, Status.WAITING)));
    }

    @Override
    public BookingDto setStatus(boolean approved, long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchElementException("Бронирование не найдено."));
        if (booking.getItem().getOwner().getId() == userId) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new AccessForbiddenException("Пользователь не является владельцем вещи");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchElementException("Бронирование не найдено."));
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        if (!booking.getBooker().equals(user) && !booking.getItem().getOwner().equals(user)) {
            throw new AccessForbiddenException("Бронирование может посмотреть только владелец вещи или автор бронирования.");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAll(long userId, State state) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL -> bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case PAST ->
                    bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case CURRENT ->
                    bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case WAITING ->
                    bookings = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING);
            case REJECTED ->
                    bookings = bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, Status.REJECTED);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> findAllByOwner(long ownerId, State state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL -> bookings = bookingRepository.findByOwner(ownerId);
            case PAST -> bookings = bookingRepository.findByOwnerAndEndBefore(ownerId, LocalDateTime.now());
            case FUTURE -> bookings = bookingRepository.findByOwnerAndStartAfter(ownerId, LocalDateTime.now());
            case CURRENT ->
                    bookings = bookingRepository.findByOwnerAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now(), LocalDateTime.now());
            case WAITING -> bookings = bookingRepository.findByOwnerAndStatus(ownerId, Status.WAITING);
            case REJECTED -> bookings = bookingRepository.findByOwnerAndStatus(ownerId, Status.REJECTED);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }
}
