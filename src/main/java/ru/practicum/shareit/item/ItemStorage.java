package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(int userId, ItemDto itemDto);
    List<Item> getAllItems(int userId);
    List<Item> getItems();
    Item getItemById(int itemId);
    Item updateItem(int itemId, ItemDto itemDto);
    List<Item> searchItems(String text);
}
