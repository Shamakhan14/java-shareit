package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(Long userId);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable(String text, String str,
                                                                                              Boolean available);
}
