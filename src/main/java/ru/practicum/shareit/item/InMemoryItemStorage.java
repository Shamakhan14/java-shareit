package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private int ids = 0;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(++ids);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAll(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> get() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item getById(int itemId) {
        return items.get(itemId);
    }

    @Override
    public Item update(Item item) {
        Item oldItem = items.get(item.getId());
        if (item.getAvailable() != null && item.getAvailable() != null) {
            if (Boolean.compare(item.getAvailable(), oldItem.getAvailable()) != 0)
                oldItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null && !item.getName().isBlank()) oldItem.setName(item.getName());
        if (item.getDescription() != null && !item.getDescription().isBlank())
            oldItem.setDescription(item.getDescription());
        return oldItem;
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}
