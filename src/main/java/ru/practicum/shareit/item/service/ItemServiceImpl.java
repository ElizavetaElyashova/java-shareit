package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.booking.model.Booking;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> findUserItems(long userId) {
        userRepository.findById(userId);
        return itemRepository.findItemsByOwnerId(userId).stream()
                .map(item -> ItemMapper.toItemDto(item,
                        bookingRepository.findLastBookingForItem(item.getId(), LocalDateTime.now()),
                        bookingRepository.findNextBookingForItem(item.getId(), LocalDateTime.now()),
                        findItemComments(item.getId())))
                .toList();

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
        Optional<Booking> booking = bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now());
        if (booking.isPresent()) {
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
