package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int item;
    private int booker;
    private BookingStatus status;
}