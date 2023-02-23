package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody @Validated BookingDtoCreate bookingDtoCreate) {
        BookingDtoResponse response = bookingService.create(userId, bookingDtoCreate);
        log.info("Бронирование успешно создано.");
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long bookingId, @RequestParam String approved) {
        BookingDtoResponse response = bookingService.updateStatus(userId, bookingId, approved);
        log.info("Статус бронирования обновлен.");
        return response;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        BookingDtoResponse response = bookingService.get(userId, bookingId);
        log.info("Выведена информация о бронировании: {}.", response.toString());
        return response;
    }

    @GetMapping
    public List<BookingDtoResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") State state) {
        List<BookingDtoResponse> response = bookingService.getAll(userId, state);
        log.info("Выведен список бронирований пользователя.");
        return response;
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllForItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "ALL") State state) {
        List<BookingDtoResponse> response = bookingService.getAllForItems(userId, state);
        log.info("Выведен список бронирований пользователя.");
        return response;
    }
}
