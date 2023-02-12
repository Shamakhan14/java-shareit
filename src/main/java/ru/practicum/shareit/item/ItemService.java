package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto create(int userId, ItemDto itemDto) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        Item item = ItemMapper.mapToNewItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.mapToItemDto(itemStorage.create(item));
    }

    public List<ItemDto> getAll(int userId) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        return ItemMapper.mapToItemDto(itemStorage.getAll(userId));
    }

    public ItemDto getById(int userId, int itemId) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidItemId(itemId)) throw new ItemNotFoundException("Неверный ID вещи.");
        return ItemMapper.mapToItemDto(itemStorage.getById(itemId));
    }

    public ItemDto update(int userId, int itemId, ItemDto itemDto) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidItemId(itemId)) throw new ItemNotFoundException("Неверный ID вещи.");
        if (itemStorage.getById(itemId).getOwner() != userId)
            throw new UserNotFoundException("Вещь не принадлежит данному пользователю.");
        Item item = ItemMapper.mapToNewItem(itemDto);
        item.setId(itemId);
        return ItemMapper.mapToItemDto(itemStorage.update(item));
    }

    public List<ItemDto> search(int userId, String text) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        return ItemMapper.mapToItemDto(itemStorage.search(text));
    }

    private boolean isValidOwner(int userId) {
        if (userStorage.getById(userId) != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidItemId(int itemId) {
        if (itemStorage.getById(itemId) != null) {
            return true;
        } else {
            return false;
        }
    }
}
