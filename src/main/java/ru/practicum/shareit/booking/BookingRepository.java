package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerOrderByStartDesc(Long bookerId);

    @Query(value = "select b from Booking b " +
            "where b.booker = ?1 and " +
            "b.start <= ?2 and " +
            "b.end >= ?2 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findCurrent(Long bookerId, LocalDateTime dateTime);

    @Query(value = "select b from Booking b " +
            "where b.booker = ?1 and " +
            "b.end <= ?2 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findPast(Long bookerId, LocalDateTime dateTime);

    @Query(value = "select b from Booking b " +
            "where b.booker = ?1 and " +
            "b.start >= ?2 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findFuture(Long bookerId, LocalDateTime dateTime);

    List<Booking> findByBookerAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner = ?1 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findAllForItems(Long userId);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findAllForItemsCurrent(Long userId, LocalDateTime dateTime);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner = ?1 and " +
            "b.end < ?2 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findAllForItemsPast(Long userId, LocalDateTime dateTime);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner = ?1 and " +
            "b.start > ?2 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findAllForItemsFuture(Long userId, LocalDateTime dateTime);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item = i.id " +
            "where i.owner = ?1 and " +
            "b.status = ?2 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findAllForItemsStatus(Long userId, BookingStatus status);

    List<Booking> findByItem(Long itemId);

    List<Booking> findByItemIn(List<Long> itemIds);
}
