package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    private BookingMapper() {
    }

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
}