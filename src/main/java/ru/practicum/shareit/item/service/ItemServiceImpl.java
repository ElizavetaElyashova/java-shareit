package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.exception.AccessForbiddenException;
import ru.practicum.shareit.item.exception.IllegalOperationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NESTED)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> findUserItems(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        Map<Long, Item> items = itemRepository.findItemsByOwnerId(userId).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Set<Long> itemsIds = items.keySet();
        Map<Long, BookingDatesDto> lastBookings = bookingRepository.findLastBookingForItems(itemsIds, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(BookingDatesDto::getItemId, Function.identity()));
        Map<Long, BookingDatesDto> nextBookings = bookingRepository.findNextBookingForItems(itemsIds, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(BookingDatesDto::getItemId, Function.identity()));
        List<Comment> comments = commentRepository.findByItemIdIn(itemsIds);
        Map<Long, List<CommentDto>> commentsByItem = new HashMap<>();
        for (Comment comment : comments) {
            commentsByItem.getOrDefault(comment.getItem().getId(), new ArrayList<>()).add(CommentDto.from(comment));
        }
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Long id : itemsIds) {
            itemDtos.add(
                    ItemMapper.toItemDto(
                            items.get(id),
                            lastBookings.get(id),
                            nextBookings.get(id),
                            commentsByItem.get(id))
            );
        }
        return itemDtos;

    }

    @Override
    public ItemDto findById(long itemId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException(("Вещь не найдена")));
        BookingDatesDto lastBooking = null;
        BookingDatesDto nextBooking = null;
        if (item.getOwner().equals(user)) {
            lastBooking = bookingRepository.findLastBookingForItem(itemId, LocalDateTime.now());
            nextBooking = bookingRepository.findNextBookingForItem(itemId, LocalDateTime.now());
        }
        return ItemMapper.toItemDto(item, lastBooking, nextBooking, findItemComments(itemId));
    }

    @Override
    public NewItemDto create(ItemDto itemDto, long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        return ItemMapper.toNewItemDto(itemRepository.save(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(NoSuchElementException::new);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        if (!item.getOwner().equals(owner)) {
            throw new AccessForbiddenException("Пользователь не является владельцем вещи");
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item),
                bookingRepository.findLastBookingForItem(item.getId(), LocalDateTime.now()),
                bookingRepository.findNextBookingForItem(item.getId(), LocalDateTime.now()),
                findItemComments(item.getId()));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameOrDescription(text).stream()
                .map(item -> ItemMapper.toItemDto(item,
                        bookingRepository.findLastBookingForItem(item.getId(), LocalDateTime.now()),
                        bookingRepository.findNextBookingForItem(item.getId(), LocalDateTime.now()),
                        findItemComments(item.getId())))
                .toList();
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, long itemId, long userId) {
        User author = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now());
        Optional<Booking> booking = bookings.stream()
                .filter(b -> !b.getStatus().equals(Status.REJECTED) && !b.getStatus().equals(Status.WAITING))
                .findAny();
        if (booking.isPresent()
                && !booking.get().getStatus().equals(Status.REJECTED)
                && !booking.get().getStatus().equals(Status.WAITING)) {
            Comment comment = Comment.builder()
                    .text(commentDto.getText())
                    .author(author)
                    .item(item)
                    .created(LocalDateTime.now())
                    .build();
            return CommentDto.from(commentRepository.save(comment));
        } else {
            throw new IllegalOperationException("Пользователь не может оставить комментарий к этой вещи.");
        }
    }

    private List<CommentDto> findItemComments(long itemId) {
        return commentRepository.findByItemId(itemId).stream().map(CommentDto::from).toList();
    }
}
