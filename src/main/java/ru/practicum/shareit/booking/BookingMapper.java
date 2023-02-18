package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.BookingDtoFotItems;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapBookingDtoCreateToBooking(BookingDtoCreate bookingDtoCreate, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoCreate.getStart());
        booking.setEnd(bookingDtoCreate.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingDtoResponse mapBookingToBookingDtoResponse(Booking booking, User user, Item item) {
        BookingDtoResponse response = new BookingDtoResponse();
        response.setId(booking.getId());
        response.setStart(booking.getStart());
        response.setEnd(booking.getEnd());
        response.setStatus(booking.getStatus());
        BookingDtoResponse.Booker booker = new BookingDtoResponse.Booker(user.getId(), user.getName());
        response.setBooker(booker);
        BookingDtoResponse.Item newItem = new BookingDtoResponse.Item(item.getId(), item.getName());
        response.setItem(newItem);
        return response;
    }

    public static BookingDtoFotItems mapToBookingDtoForItems(Booking booking) {
        BookingDtoFotItems bookingDtoFotItems = new BookingDtoFotItems();
        bookingDtoFotItems.setId(booking.getId());
        bookingDtoFotItems.setStart(booking.getStart());
        bookingDtoFotItems.setEnd(booking.getEnd());
        bookingDtoFotItems.setStatus(booking.getStatus());
        bookingDtoFotItems.setBookerId(booking.getBooker().getId());
        return bookingDtoFotItems;
    }
}
