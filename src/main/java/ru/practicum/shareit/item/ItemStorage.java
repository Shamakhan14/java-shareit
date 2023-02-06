package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    List<Item> getAll(int userId);

    List<Item> get();

    Item getById(int itemId);

    Item update(Item item);

    List<Item> search(String text);
}
