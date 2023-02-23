package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.BookingDtoFotItems;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

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

    public static BookingDtoResponse mapBookingToBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDtoResponse.Booker(
                        booking.getBooker().getId(),
                        booking.getBooker().getName()
                ),
                new BookingDtoResponse.Item(
                        booking.getItem().getId(),
                        booking.getItem().getName()
                )
        );
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

    public static List<BookingDtoResponse> mapBookingsIntoResponse(List<Booking> bookings) {
        List<BookingDtoResponse> responses = new ArrayList<>();
        for (Booking booking: bookings) {
            responses.add(mapBookingToBookingDtoResponse(booking));
        }
        return responses;
    }
}
