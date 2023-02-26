package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoInc;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDtoOut create(Long userId, ItemRequestDtoInc itemRequestDtoInc) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Неверный ID пользователя."));
        ItemRequest itemRequest = ItemRequestMapper.mapIncomingRequestToRequest(user, itemRequestDtoInc);
        ItemRequest response = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapRequestToOutcomingRequest(response, Collections.emptyList());
    }

    public List<ItemRequestDtoOut> getOwn(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Неверный ID пользователя."));
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(toList());
        Map<Long, List<Item>> responses = itemRepository.findByRequestInOrderByIdDesc(requestIds).stream()
                .collect(groupingBy(Item::getRequest, toList()));
        return ItemRequestMapper.mapRequestsToOutcomingRequests(requests, responses);
    }

    public List<ItemRequestDtoOut> getAll(Long userId, Optional<Integer> from, Optional<Integer> size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Неверный ID пользователя."));
        if ((from.isEmpty() && size.isPresent()) || (from.isPresent() && size.isEmpty())) {
            throw new ValidationException("Должны присутствовать оба параметра.");
        }
        List<ItemRequest> requests;
        if (from.isEmpty() && size.isEmpty()) {
            requests = itemRequestRepository.findAllWithoutUser(userId, Sort.by(DESC, "created"));

        } else {
            if (from.get() < 0) {
                throw new ValidationException("Индекс не может быть меньше 0.");
            }
            if (size.get() <= 0) {
                throw new ValidationException("Количество элементов не может быть меньше или равно 0.");
            }
            Pageable pageable = PageRequest.of(from.get() / size.get(), size.get(), Sort.by(DESC, "created"));
            requests = itemRequestRepository.findAllPageable(userId, pageable);
        }
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(toList());
        Map<Long, List<Item>> responses = itemRepository.findByRequestInOrderByIdDesc(requestIds).stream()
                .collect(groupingBy(Item::getRequest, toList()));
        return ItemRequestMapper.mapRequestsToOutcomingRequests(requests, responses);
    }

    public ItemRequestDtoOut getById(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Неверный ID пользователя."));
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Неверный ID запроса."));
        List<Item> items = itemRepository.findByRequestOrderByIdDesc(requestId);
        return ItemRequestMapper.mapRequestToOutcomingRequest(request, items);
    }
}
