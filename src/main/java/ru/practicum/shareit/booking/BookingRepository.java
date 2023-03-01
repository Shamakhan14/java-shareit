package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.booker.id = ?2 and " +
            "b.status = ?3 and " +
            "b.end < ?4")
    List<Booking> findByItemAndValidBooker(Long itemId, Long userId, BookingStatus status, LocalDateTime time);

    @Query(value = "select b from Booking b " +
            "where b.item.id in ?1 and " +
            "b.status = ?2 " +
            "group by b.id")
    List<Booking> findByItemInAndStatus(List<Long> itemIds, BookingStatus status, Sort sort);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findByBookerOrderByStartDescPageable(Long bookerId, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and " +
            "?2 between b.start and b.end " +
            "group by b.id")
    List<Booking> findCurrentPageable(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and " +
            "b.end <= ?2 " +
            "group by b.id")
    List<Booking> findPastPageable(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and " +
            "b.start >= ?2 " +
            "group by b.id")
    List<Booking> findFuturePageable(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and " +
            "b.status = ?2 " +
            "group by b.id " +
            "order by b.start desc")
    List<Booking> findByBookerAndStatusOrderByStartDescPageable(Long bookerId, BookingStatus status, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item.id = i.id " +
            "where i.owner = ?1 " +
            "group by b.id")
    List<Booking> findAllForItemsPageable(Long userId, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item.id = i.id " +
            "where i.owner = ?1 and " +
            "?2 between b.start and b.end " +
            "group by b.id")
    List<Booking> findAllForItemsCurrentPageable(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item.id = i.id " +
            "where i.owner = ?1 and " +
            "b.end < ?2 " +
            "group by b.id")
    List<Booking> findAllForItemsPastPageable(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item.id = i.id " +
            "where i.owner = ?1 and " +
            "b.start > ?2 " +
            "group by b.id")
    List<Booking> findAllForItemsFuturePageable(Long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join Item i on b.item.id = i.id " +
            "where i.owner = ?1 and " +
            "b.status = ?2 " +
            "group by b.id")
    List<Booking> findAllForItemsStatusPageable(Long userId, BookingStatus status, Pageable pageable);
}
