package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("POST /requests - создание запроса: {}, пользователь: {}", itemRequestDto, requestorId);
        return itemRequestClient.createRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("GET /requests - получение запросов пользователя: {}", requestorId);
        return itemRequestClient.getByRequestor(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /requests/all - получение запросов других пользователей, пользователь: {}, from={}, size={}",
                requestorId, from, size);
        return itemRequestClient.getOtherUsersRequests(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/{} - получение запроса, пользователь: {}", requestId, userId);
        return itemRequestClient.getById(requestId, userId);
    }
}