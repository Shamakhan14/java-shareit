package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class BookingServiceUnitTest {

    private UserRepository mockUserRepository;
    private ItemRepository mockItemRepository;
    private BookingRepository mockBookingRepository;
    private BookingService service;
    private BookingDtoCreate bookingDtoCreate;
    User user;
    Item item;
    Booking booking;

    @BeforeEach
    void setUp() {
        mockUserRepository = Mockito.mock(UserRepository.class);
        mockItemRepository = Mockito.mock(ItemRepository.class);
        mockBookingRepository = Mockito.mock(BookingRepository.class);
        service = new BookingService(mockUserRepository, mockItemRepository, mockBookingRepository);

        bookingDtoCreate = new BookingDtoCreate(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1),
                LocalDateTime.of(2025, 1, 1, 1, 1)
        );

        user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(2L);
        item.setRequest(1L);

        booking = new Booking();
        booking.setId(2L);
        booking.setStart(LocalDateTime.of(2024, 1, 1, 1, 1));
        booking.setEnd(LocalDateTime.of(2025, 1, 1, 1, 1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void createTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockBookingRepository.save(Mockito.any()))
                .thenReturn(booking);

        BookingDtoResponse response = service.create(user.getId(), bookingDtoCreate);

        assertThat(response.getId(), equalTo(booking.getId()));
        assertThat(response.getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.getStatus(), equalTo(booking.getStatus()));
        assertThat(response.getBooker().getId(), equalTo(user.getId()));
        assertThat(response.getBooker().getName(), equalTo(user.getName()));
        assertThat(response.getItem().getId(), equalTo(item.getId()));
        assertThat(response.getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void createUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.create(user.getId(), bookingDtoCreate);
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void createItemNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.create(user.getId(), bookingDtoCreate);
                }).withMessage("Неверный ID вещи.");
    }

    @Test
    void createItemNotAvailableTest() {
        item.setAvailable(false);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(item));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.create(user.getId(), bookingDtoCreate);
                }).withMessage("На данный момент вещь недоступна для бронирования.");
    }

    @Test
    void createInvalidBookingDtoTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(item));
        bookingDtoCreate.setStart(LocalDateTime.of(2025, 1, 1, 1, 1));
        bookingDtoCreate.setEnd(LocalDateTime.of(2024, 1, 1, 1, 1));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.create(user.getId(), bookingDtoCreate);
                }).withMessage("Неверно введены данные времени начала и/или окончания.");
    }

    @Test
    void createBookingByOwnerTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(item));
        item.setOwner(1L);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.create(user.getId(), bookingDtoCreate);
                }).withMessage("Невозможно забронировать свою вещь.");
    }

    @Test
    void updateStatusToApprovedTest() {
        item.setOwner(1L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoResponse response = service.updateStatus(user.getId(), booking.getId(), "true");

        assertThat(response.getId(), equalTo(booking.getId()));
        assertThat(response.getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(response.getBooker().getId(), equalTo(user.getId()));
        assertThat(response.getBooker().getName(), equalTo(user.getName()));
        assertThat(response.getItem().getId(), equalTo(item.getId()));
        assertThat(response.getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void updateStatusToRejectedTest() {
        item.setOwner(1L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoResponse response = service.updateStatus(user.getId(), booking.getId(), "false");

        assertThat(response.getId(), equalTo(booking.getId()));
        assertThat(response.getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.getStatus(), equalTo(BookingStatus.REJECTED));
        assertThat(response.getBooker().getId(), equalTo(user.getId()));
        assertThat(response.getBooker().getName(), equalTo(user.getName()));
        assertThat(response.getItem().getId(), equalTo(item.getId()));
        assertThat(response.getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void updateStatusUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.updateStatus(user.getId(), booking.getId(), "true");
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void updateStatusBookingNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.updateStatus(user.getId(), booking.getId(), "true");
                }).withMessage("Неверный ID бронирования.");
    }

    @Test
    void updateStatusByNotOwnerTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.updateStatus(user.getId(), booking.getId(), "true");
                }).withMessage("Статус бронирования вещи может менять только ее владелец.");
    }

    @Test
    void updateStatusWrongStatusTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.updateStatus(user.getId(), booking.getId(), "text");
                }).withMessage("Неверный статус бронирования.");
    }

    @Test
    void updateStatusOfFinishedBookingTest() {
        item.setOwner(1L);
        booking.setStatus(BookingStatus.APPROVED);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.updateStatus(user.getId(), booking.getId(), "true");
                }).withMessage("Нельзя изменить статус уже завершенного бронирования.");
    }

    @Test
    void getTest() {
        item.setOwner(1L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoResponse response = service.get(user.getId(), booking.getId());

        assertThat(response.getId(), equalTo(booking.getId()));
        assertThat(response.getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.getStatus(), equalTo(booking.getStatus()));
        assertThat(response.getBooker().getId(), equalTo(user.getId()));
        assertThat(response.getBooker().getName(), equalTo(user.getName()));
        assertThat(response.getItem().getId(), equalTo(item.getId()));
        assertThat(response.getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.get(user.getId(), booking.getId());
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getBookingNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.get(user.getId(), booking.getId());
                }).withMessage("Неверный ID бронирования.");
    }

    @Test
    void getByNotOwnerTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    BookingDtoResponse response = service.get(5L, booking.getId());
                }).withMessage("Данные бронирования доступны только арендатору или владельцу вещи.");
    }

    @Test
    void getAllAllTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findByBookerOrderByStartDesc(Mockito.anyLong()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.ALL,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllCurrentTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findCurrent(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.CURRENT,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllPastTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findPast(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.PAST,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllFutureTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findFuture(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.FUTURE,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllWaitingTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findByBookerAndStatusOrderByStartDesc(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.WAITING,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllRejectedTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findByBookerAndStatusOrderByStartDesc(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.REJECTED,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllAllPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findByBookerOrderByStartDescPageable(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.ALL,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllCurrentPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findCurrentPageable(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.CURRENT,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllPastPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findPastPageable(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.PAST,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllFuturePageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findFuturePageable(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.FUTURE,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllWaitingPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findByBookerAndStatusOrderByStartDescPageable(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.WAITING,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllRejectedPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.findByBookerAndStatusOrderByStartDescPageable(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAll(user.getId(), State.REJECTED,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    List<BookingDtoResponse> response = service.getAll(user.getId(), State.REJECTED,
                            Optional.empty(), Optional.empty());
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getAllFromNegativeTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    List<BookingDtoResponse> response = service.getAll(user.getId(), State.ALL,
                            Optional.of(-5), Optional.of(5));
                }).withMessage("Неверный индекс элемента.");
    }

    @Test
    void getAllSizeNegativeTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    List<BookingDtoResponse> response = service.getAll(user.getId(), State.ALL,
                            Optional.of(5), Optional.of(-5));
                }).withMessage("Неверное количество элементов.");
    }

    @Test
    void getAllForItemsAllTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItems(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.ALL,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsCurrentTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsCurrent(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.CURRENT,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsPastTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsPast(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.PAST,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsFutureTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsFuture(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.FUTURE,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsWaitingTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.WAITING,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsRejectedTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.REJECTED,
                Optional.empty(), Optional.empty());

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsAllPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsPageable(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.ALL,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsCurrentPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsCurrentPageable(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.CURRENT,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsPastPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsPastPageable(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.PAST,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsFuturePageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsFuturePageable(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.FUTURE,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsWaitingPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsStatusPageable(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.WAITING,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsRejectedPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findAllForItemsStatusPageable(Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.REJECTED,
                Optional.of(5), Optional.of(5));

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getId(), equalTo(booking.getId()));
        assertThat(response.get(0).getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(response.get(0).getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(response.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(response.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(response.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(response.get(0).getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void getAllForItemsUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.REJECTED,
                            Optional.of(5), Optional.of(5));
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getAllForItemsItemNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> {
                    List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.REJECTED,
                            Optional.of(5), Optional.of(5));
                }).withMessage("У данного пользователя нет вещей.");
    }

    @Test
    void getAllForItemsFromNegativeTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.REJECTED,
                            Optional.of(-5), Optional.of(5));
                }).withMessage("Неверный индекс элемента.");
    }

    @Test
    void getAllForItemsSizeNegativeTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderById(Mockito.anyLong()))
                .thenReturn(List.of(item));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    List<BookingDtoResponse> response = service.getAllForItems(user.getId(), State.REJECTED,
                            Optional.of(5), Optional.of(-5));
                }).withMessage("Неверное количество элементов.");
    }
}
