package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;

    public Item createItem(int userId, ItemDto itemDto) {
        return itemStorage.createItem(userId, itemDto);
    }

    public List<Item> getAllItems(int userId) {
        return itemStorage.getAllItems(userId);
    }

    public Item getItemById(int itemId) {
        return itemStorage.getItemById(itemId);
    }

    public Item updateItem(int itemId, ItemDto itemDto) {
        return itemStorage.updateItem(itemId, itemDto);
    }

    public List<Item> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
