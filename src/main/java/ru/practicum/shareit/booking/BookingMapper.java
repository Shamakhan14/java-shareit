package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.BookingDtoFotItems;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapBookingDtoCreateToBooking(BookingDtoCreate bookingDtoCreate, Long userId) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoCreate.getStart());
        booking.setEnd(bookingDtoCreate.getEnd());
        booking.setItem(bookingDtoCreate.getItemId());
        booking.setBooker(userId);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingDtoResponse mapBookingToBookingDtoResponse(Booking booking, User user, ItemDto itemDto) {
        BookingDtoResponse response = new BookingDtoResponse();
        response.setId(booking.getId());
        response.setStart(booking.getStart());
        response.setEnd(booking.getEnd());
        response.setStatus(booking.getStatus());
        response.setBooker(user);
        response.setItem(itemDto);
        return response;
    }

    public static BookingDtoFotItems mapToBookingDtoForItems(Booking booking) {
        BookingDtoFotItems bookingDtoFotItems = new BookingDtoFotItems();
        bookingDtoFotItems.setId(booking.getId());
        bookingDtoFotItems.setStart(booking.getStart());
        bookingDtoFotItems.setEnd(booking.getEnd());
        bookingDtoFotItems.setStatus(booking.getStatus());
        bookingDtoFotItems.setBookerId(booking.getBooker());
        return bookingDtoFotItems;
    }
}
