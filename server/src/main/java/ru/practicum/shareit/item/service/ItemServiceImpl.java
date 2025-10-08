package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDtoWithBookings> getByOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id=" + ownerId + " не найден.");
        }

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Comment> allComments = commentRepository.findByItemIdIn(itemIds);

        return items.stream()
                .map(item -> toItemDtoWithBookings(item, ownerId, allComments))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoWithBookings getById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + id + " не найдена."));

        return toItemDtoWithBookings(item, userId, Collections.emptyList());
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден."));

        validateItemDto(itemDto);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id=" + itemDto.getRequestId() + " не найден."));
            item.setRequest(itemRequest);
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + id + " не найдена."));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может редактировать вещь.");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id=" + itemDto.getRequestId() + " не найден."));
            existingItem.setRequest(itemRequest);
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException("Вещь с id=" + id + " не найдена.");
        }
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + authorId + " не найден."));

        List<Booking> userBookings = bookingRepository.findByItemIdAndBookerIdAndEndBefore(
                itemId, authorId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду.");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return toCommentDto(savedComment);
    }

    private ItemDtoWithBookings toItemDtoWithBookings(Item item, Long userId, List<Comment> allComments) {
        ItemDtoWithBookings dto = new ItemDtoWithBookings();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);

        if (item.getOwner().getId().equals(userId)) {
            dto.setLastBooking(getLastBooking(item.getId()));
            dto.setNextBooking(getNextBooking(item.getId()));
        }

        List<CommentDto> itemComments = allComments.stream()
                .filter(comment -> comment.getItem().getId().equals(item.getId()))
                .map(this::toCommentDto)
                .collect(Collectors.toList());

        if (allComments.isEmpty()) {
            itemComments = getCommentsForItem(item.getId());
        }

        dto.setComments(itemComments);
        return dto;
    }

    private BookingShortDto getLastBooking(Long itemId) {
        List<Booking> lastBookings = bookingRepository.findLastBookings(itemId);
        if (!lastBookings.isEmpty()) {
            Booking booking = lastBookings.get(0);
            return new BookingShortDto(booking.getId(), booking.getBooker().getId(),
                    booking.getStart(), booking.getEnd());
        }
        return null;
    }

    private BookingShortDto getNextBooking(Long itemId) {
        List<Booking> nextBookings = bookingRepository.findNextBookings(itemId);
        if (!nextBookings.isEmpty()) {
            Booking booking = nextBookings.get(0);
            return new BookingShortDto(booking.getId(), booking.getBooker().getId(),
                    booking.getStart(), booking.getEnd());
        }
        return null;
    }

    private List<CommentDto> getCommentsForItem(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    private CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым.");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус доступности должен быть указан.");
        }
    }
}