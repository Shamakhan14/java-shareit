package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoInc;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest mapIncomingRequestToRequest(User user, ItemRequestDtoInc itemRequestDtoInc) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDtoInc.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static ItemRequestDtoOut mapRequestToOutcomingRequest(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestDtoOut(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated(),
                mapItemsToResponses(items)
        );
    }

    public static List<Response> mapItemsToResponses(List<Item> items) {
        List<Response> responses = new ArrayList<>();
        for (Item item: items) {
            Response response = new Response();
            response.setId(item.getId());
            response.setName(item.getName());
            response.setUserId(item.getOwner());
            response.setDescription(item.getDescription());
            response.setAvailable(item.getAvailable());
            response.setRequestId(item.getRequest());
            responses.add(response);
        }
        return responses;
    }

    public static List<ItemRequestDtoOut> mapRequestsToOutcomingRequests(List<ItemRequest> requests,
                                                                         Map<Long, List<Item>> items) {
        List<ItemRequestDtoOut> response = new ArrayList<>();
        for (ItemRequest itemRequest: requests) {
            response.add(mapRequestToOutcomingRequest(itemRequest, items.getOrDefault(itemRequest.getId(),
                    Collections.emptyList())));
        }
        return response;
    }
}
