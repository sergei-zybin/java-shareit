package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto bookingRequestDto, Long bookerId);

    BookingResponseDto updateStatus(Long bookingId, Boolean approved, Long ownerId);

    BookingResponseDto getById(Long bookingId, Long userId);

    List<BookingResponseDto> getBookingsByBooker(Long bookerId, String state);

    List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state);
}