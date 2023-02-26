package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInc;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOut create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody @Valid ItemRequestDtoInc itemRequestDtoInc) {
        ItemRequestDtoOut response = itemRequestService.create(userId, itemRequestDtoInc);
        log.info("Запрос успешно создан.");
        return response;
    }

    @GetMapping
    public List<ItemRequestDtoOut> getOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestDtoOut> response = itemRequestService.getOwn(userId);
        log.info("Выведен список запросов пользователя.");
        return response;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(required = false) Optional<Integer> from,
                                          @RequestParam(required = false) Optional<Integer> size) {
        List<ItemRequestDtoOut> response = itemRequestService.getAll(userId, from, size);
        log.info("Выведен список запросов.");
        return response;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        ItemRequestDtoOut response = itemRequestService.getById(userId, requestId);
        log.info("Выведена информация о запросе.");
        return response;
    }
}
