package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.UnknownStatusException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public BookingDtoResponse create(Long userId, BookingDtoCreate bookingDtoCreate) {
        if (!isValidRequester(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidItem(bookingDtoCreate.getItemId())) throw new EntityNotFoundException("Неверный ID вещи.");
        if (itemRepository.findById(bookingDtoCreate.getItemId()).get().getAvailable().equals(false)) {
            throw new ValidationException("На данный момент вещь недоступна для бронирования.");
        }
        if (!isValidBookingDto(bookingDtoCreate)) {
            throw new ValidationException("Неверно введены данные времени начала и/или окончания.");
        }
        if (userId == itemRepository.findById(bookingDtoCreate.getItemId()).get().getOwner()) {
            throw new EntityNotFoundException("Невозможно забронировать свою вещь.");
        }
        Booking booking = BookingMapper.mapBookingDtoCreateToBooking(bookingDtoCreate, userId);
        Booking response = bookingRepository.save(booking);
        ItemDto itemDto = ItemMapper.mapToItemDto(itemRepository.findById(response.getItem()).get());
        User user = userRepository.findById(userId).get();
        return BookingMapper.mapBookingToBookingDtoResponse(response, user, itemDto);
    }

    public BookingDtoResponse updateStatus(Long userId, Long bookingId, String status) {
        if (!isValidRequester(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidBooking(bookingId)) throw new EntityNotFoundException("Неверный ID бронирования.");
        if (!status.equals("true") && !status.equals("false")) {
            throw new ValidationException("Неверный статус бронирования.");
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        Item item = itemRepository.findById(booking.getItem()).get();
        if (item.getOwner() != userId) {
            throw new UserNotFoundException("Статус бронирования вещи может менять только ее владелец.");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Нельзя изменить статус уже завершенного бронирования.");
        }
        if (status.equals("true")) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking response = bookingRepository.save(booking);
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        return BookingMapper.mapBookingToBookingDtoResponse(response,
                userRepository.findById(booking.getBooker()).get(), itemDto);
    }

    public BookingDtoResponse get(Long userId, Long bookingId) {
        if (!isValidRequester(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidBooking(bookingId)) throw new EntityNotFoundException("Неверный ID бронирования.");
        Booking booking = bookingRepository.findById(bookingId).get();
        Item item = itemRepository.findById(booking.getItem()).get();
        if (userId != booking.getBooker() && userId != item.getOwner()) {
            throw new UserNotFoundException("Данные бронирования доступны только арендатору или владельцу вещи.");
        }
        return BookingMapper.mapBookingToBookingDtoResponse(booking, userRepository.findById(booking.getBooker()).get(),
                ItemMapper.mapToItemDto(item));
    }

    public List<BookingDtoResponse> getAll(Long userId, String state) {
        if (!isValidRequester(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findByBookerOrderByStartDesc(userId));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findCurrent(userId, LocalDateTime.now()));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findPast(userId, LocalDateTime.now()));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findFuture(userId, LocalDateTime.now()));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findByBookerAndStatusOrderByStartDesc(userId, BookingStatus.WAITING));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findByBookerAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED));
                break;
            default:
                throw new UnknownStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return mapBookingsIntoResponse(bookings);
    }

    public List<BookingDtoResponse> getAllForItems(Long userId, String state) {
        if (!isValidRequester(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (itemRepository.findByOwner(userId).size() == 0) throw new EntityNotFoundException("У данного пользователя" +
                " нет вещей.");
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllForItems(userId));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllForItemsCurrent(userId, LocalDateTime.now()));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllForItemsPast(userId, LocalDateTime.now()));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllForItemsFuture(userId, LocalDateTime.now()));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllForItemsStatus(userId, BookingStatus.WAITING));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllForItemsStatus(userId, BookingStatus.REJECTED));
                break;
            default:
                throw new UnknownStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return mapBookingsIntoResponse(bookings);
    }

    private boolean isValidRequester(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidItem(Long itemId) {
        if (itemRepository.findById(itemId).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidBookingDto(BookingDtoCreate bookingDtoCreate) {
        if (bookingDtoCreate.getStart().isBefore(LocalDateTime.now()) || bookingDtoCreate.getEnd().isBefore(LocalDateTime.now()) ||
                bookingDtoCreate.getStart().isAfter(bookingDtoCreate.getEnd())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidBooking(Long bookingId) {
        if (bookingRepository.findById(bookingId).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private List<BookingDtoResponse> mapBookingsIntoResponse(List<Booking> bookings) {
        List<BookingDtoResponse> response = new ArrayList<>();
        for (Booking booking: bookings) {
            Item item = itemRepository.findById(booking.getItem()).get();
            response.add(BookingMapper.mapBookingToBookingDtoResponse(booking,
                    userRepository.findById(booking.getBooker()).get(),
                    ItemMapper.mapToItemDto(item)));
        }
        return response;
    }
}
