package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private int ids = 0;
    private final HashMap<Integer, Item> items = new HashMap<>();

    @Override
    public Item createItem(int userId, ItemDto itemDto) {
        Item item = new Item(++ids, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId, null);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAllItems(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item getItemById(int itemId) {
        return items.get(itemId);
    }

    @Override
    public Item updateItem(int itemId, ItemDto itemDto) {
        Item item = items.get(itemId);
        if (itemDto.getAvailable() != null && item.getAvailable() != null) {
            if (Boolean.compare(itemDto.getAvailable(), item.getAvailable()) != 0)
                item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        return item;
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}
