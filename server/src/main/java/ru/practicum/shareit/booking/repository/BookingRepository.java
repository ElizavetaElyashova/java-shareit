package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking save(Booking booking);

    @Query("select new ru.practicum.shareit.booking.dto.BookingDatesDto(i.id, b.start, b.end) " +
            "from Booking b " +
            "join b.item as i " +
            "where i.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc " +
            "limit 1")
    BookingDatesDto findLastBookingForItem(long itemId, LocalDateTime now);

    @Query("select b.start, b.end from Booking b " +
            "join b.item as i " +
            "where i.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start asc " +
            "limit 1")
    BookingDatesDto findNextBookingForItem(long itemId, LocalDateTime now);

    @Query("select i.id, b.start, b.end from Booking b " +
            "join b.item as i " +
            "where i.id in ?1 " +
            "and b.start > ?2 ")
    List<BookingDatesDto> findNextBookingForItems(Collection<Long> itemIds, LocalDateTime now);

    @Query("select new ru.practicum.shareit.booking.dto.BookingDatesDto(i. id, b.start, b.end) " +
            "from Booking b " +
            "join b.item as i " +
            "where i.id in ?1 " +
            "and b.end < ?2 ")
    List<BookingDatesDto> findLastBookingForItems(Collection<Long> itemIds, LocalDateTime now);


    Optional<Booking> findById(Long id);

    List<Booking> findByBookerIdAndItemIdAndEndIsBefore(long bookerId, long itemId, LocalDateTime end);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);


    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findByOwner(long ownerId);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerAndStatus(long ownerId, Status status);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerAndStartAfter(long ownerId, LocalDateTime start);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerAndEndBefore(long ownerId, LocalDateTime end);

    @Query("select b from Booking b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?3 " +
            "order by b.start desc")
    List<Booking> findByOwnerAndStartBeforeAndEndAfter(long ownerId, LocalDateTime start, LocalDateTime end);
}
