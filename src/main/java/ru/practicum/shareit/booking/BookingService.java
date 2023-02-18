package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.UnknownStatusException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public BookingDtoResponse create(Long userId, BookingDtoCreate bookingDtoCreate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Неверный ID пользователя."));
        Item item = itemRepository.findById(bookingDtoCreate.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Неверный ID вещи."));
        if (item.getAvailable().equals(false)) {
            throw new ValidationException("На данный момент вещь недоступна для бронирования.");
        }
        if (!isValidBookingDto(bookingDtoCreate)) {
            throw new ValidationException("Неверно введены данные времени начала и/или окончания.");
        }
        if (userId.equals(item.getOwner())) {
            throw new EntityNotFoundException("Невозможно забронировать свою вещь.");
        }
        Booking booking = BookingMapper.mapBookingDtoCreateToBooking(bookingDtoCreate, item, user);
        Booking response = bookingRepository.save(booking);
        return BookingMapper.mapBookingToBookingDtoResponse(response, user, item);
    }

    @Transactional
    public BookingDtoResponse updateStatus(Long userId, Long bookingId, String status) {
        if (!isValidRequester(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Неверный ID бронирования."));
        if (!status.equals("true") && !status.equals("false")) {
            throw new ValidationException("Неверный статус бронирования.");
        }
        Item item = booking.getItem();
        if (!item.getOwner().equals(userId)) {
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
        return BookingMapper.mapBookingToBookingDtoResponse(booking, booking.getBooker(), item);
    }

    public BookingDtoResponse get(Long userId, Long bookingId) {
        if (!isValidRequester(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Неверный ID бронирования."));
        Item item = booking.getItem();
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(item.getOwner())) {
            throw new UserNotFoundException("Данные бронирования доступны только арендатору или владельцу вещи.");
        }
        return BookingMapper.mapBookingToBookingDtoResponse(booking, booking.getBooker(), item);
    }

    public List<BookingDtoResponse> getAll(Long userId, State state) {
        if (!isValidRequester(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrent(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findPast(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findFuture(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new UnknownStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return mapBookingsIntoResponse(bookings);
    }

    public List<BookingDtoResponse> getAllForItems(Long userId, State state) {
        if (!isValidRequester(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (itemRepository.findByOwner(userId).size() == 0) throw new EntityNotFoundException("У данного пользователя" +
                " нет вещей.");
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllForItems(userId,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllForItemsCurrent(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllForItemsPast(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllForItemsFuture(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findAllForItemsStatus(userId, BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllForItemsStatus(userId, BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start"));
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

    private boolean isValidBookingDto(BookingDtoCreate bookingDtoCreate) {
        if (bookingDtoCreate.getStart().isAfter(bookingDtoCreate.getEnd()) ||
            bookingDtoCreate.getStart().equals(bookingDtoCreate.getEnd())) {
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
        List<Long> itemIds = bookings.stream()
                .map((booking) -> booking.getItem().getId())
                .collect(Collectors.toList());
        List<Long> bookerIds = bookings.stream()
                .map((booking) -> booking.getBooker().getId())
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findByIdIn(itemIds);
        List<User> bookers = userRepository.findByIdIn(bookerIds);
        for (Booking booking: bookings) {
            for (Item item: items) {
                if (booking.getItem().getId().equals(item.getId())) {
                    for (User user: bookers) {
                        if (booking.getBooker().getId().equals(user.getId())) {
                            response.add(BookingMapper.mapBookingToBookingDtoResponse(booking, user, item));
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return response;
    }
}
