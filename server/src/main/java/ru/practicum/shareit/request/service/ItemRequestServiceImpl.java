package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestResponseDto create(ItemRequestDto itemRequestDto, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + requestorId + " не найден."));

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание запроса не может быть пустым");
        }

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return toItemRequestResponseDto(savedRequest);
    }

    @Override
    public List<ItemRequestResponseDto> getByRequestor(Long requestorId) {
        if (!userRepository.existsById(requestorId)) {
            throw new NotFoundException("Пользователь с id=" + requestorId + " не найден.");
        }

        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId).stream()
                .map(this::toItemRequestResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> getOtherUsersRequests(Long requestorId, Integer from, Integer size) {
        if (!userRepository.existsById(requestorId)) {
            throw new NotFoundException("Пользователь с id=" + requestorId + " не найден.");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId, pageable)
                .getContent()
                .stream()
                .map(this::toItemRequestResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getById(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден."));

        return toItemRequestResponseDto(itemRequest);
    }

    private ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest) {
        List<ItemDto> items = itemRepository.findByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}