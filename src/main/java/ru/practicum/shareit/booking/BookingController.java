package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestBody BookingRequestDto bookingRequestDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /bookings - создание бронирования: {}, пользователь: {}", bookingRequestDto, userId);
        return bookingService.create(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateStatus(@PathVariable Long bookingId,
                                           @RequestParam Boolean approved,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /bookings/{}?approved={} - обновление статуса, пользователь: {}",
                bookingId, approved, userId);
        return bookingService.updateStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@PathVariable Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/{} - получение бронирования, пользователь: {}", bookingId, userId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings?state={} - получение бронирований пользователя: {}", state, userId);
        return bookingService.getBookingsByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner?state={} - получение бронирований владельца: {}", state, userId);
        return bookingService.getBookingsByOwner(userId, state);
    }
}