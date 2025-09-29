package ru.practicum.shareit.booking.storage;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .itemName(booking.getItem().getName())
                .bookerName(booking.getBooker().getName())
                .build();
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(BookerDto.builder()
                        .id(booking.getBooker().getId())
                        .name(booking.getBooker().getName())
                        .email(booking.getBooker().getEmail())
                        .build())
                .item(ItemBookingDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .description(booking.getItem().getDescription())
                        .available(booking.getItem().getAvailable())
                        .build())
                .build();
    }
}