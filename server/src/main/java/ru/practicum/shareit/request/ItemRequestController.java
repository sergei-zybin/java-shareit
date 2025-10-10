package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto create(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("POST /requests - создание запроса: {}, пользователь: {}", itemRequestDto, requestorId);
        return itemRequestService.create(itemRequestDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("GET /requests - получение запросов пользователя: {}", requestorId);
        return itemRequestService.getByRequestor(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /requests/all - получение запросов других пользователей, пользователь: {}, from={}, size={}",
                requestorId, from, size);
        return itemRequestService.getOtherUsersRequests(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(@PathVariable Long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/{} - получение запроса, пользователь: {}", requestId, userId);
        return itemRequestService.getById(requestId, userId);
    }
}