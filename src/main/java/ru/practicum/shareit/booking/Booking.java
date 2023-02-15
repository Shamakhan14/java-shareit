package ru.practicum.shareit.booking;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "res_start")
    private LocalDateTime start;
    @Column(name = "res_end")
    private LocalDateTime end;
    @Column(name = "item")
    private Long item;
    @Column(name = "booker")
    private Long booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
