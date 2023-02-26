package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerOrderById(Long userId);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String text, String str,
                                                                                              Boolean available);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String text, String str,
                                                                              Boolean available, Pageable pageable);

    List<Item> findByRequestInOrderByIdDesc(List<Long> requests);

    List<Item> findByRequestOrderByIdDesc(Long requestId);

    @Query(value = "select i from Item i " +
            "where i.owner = ?1 " +
            "group by i.id")
    List<Item> findByOwnerOrderByIdPageable(Long userId, Pageable pageable);
}
