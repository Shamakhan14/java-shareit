package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @PostMapping
    public Item createItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidItemDto(itemDto)) throw new ValidationException("Неверные данные вещи.");
        Item item = itemService.createItem(userId, itemDto);
        log.info("Вещь " + itemDto.getName() + " успешно добавлена.");
        return item;
    }

    @GetMapping
    public List<Item> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        log.info("Запрошен список вещей.");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidItemId(itemId)) throw new UserNotFoundException("Неверный ID вещи.");
        Item item = itemService.getItemById(itemId);
        log.info("Выведена информация по вещи " + item.getName() + ".");
        return item;
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                            @RequestBody ItemDto itemDto) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidItemId(itemId)) throw new UserNotFoundException("Неверный ID вещи.");
        if (itemStorage.getItemById(itemId).getOwner() != userId)
            throw new UserNotFoundException("Вещь не принадлежит данному пользователю.");
        Item item = itemService.updateItem(itemId, itemDto);
        log.info("Информация о вещи " + item.getName() + " обновлена.");
        return item;
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam String text) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (text.isEmpty()) return new ArrayList<>();
        List<Item> items = itemService.searchItems(text);
        log.info("Выполнен поиск по описанию: " + text + ".");
        return items;
    }

    private boolean isValidOwner(int userId) {
        for (User user: userStorage.getAllUsers()) {
            if (userId == user.getId()) return true;
        }
        return false;
    }

    private boolean isValidItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null ||
            itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty() ||
            itemDto.getAvailable() == null) return false;
        return true;
    }

    private boolean isValidItemId(int itemId) {
        for (Item item: itemStorage.getItems()) {
            if (item.getId() == itemId) return true;
        }
        return false;
    }
}
