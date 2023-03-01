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

    public static List<RequestResponse> mapItemsToResponses(List<Item> items) {
        List<RequestResponse> respons = new ArrayList<>();
        for (Item item: items) {
            RequestResponse requestResponse = new RequestResponse();
            requestResponse.setId(item.getId());
            requestResponse.setName(item.getName());
            requestResponse.setUserId(item.getOwner());
            requestResponse.setDescription(item.getDescription());
            requestResponse.setAvailable(item.getAvailable());
            requestResponse.setRequestId(item.getRequest());
            respons.add(requestResponse);
        }
        return respons;
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
