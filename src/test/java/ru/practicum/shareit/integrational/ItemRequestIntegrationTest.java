package ru.practicum.shareit.integrational;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDtoInc;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    @Test
    void createTest() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@email.com");
        User entity = UserMapper.mapToNewUser(userDto);
        em.persist(entity);
        em.flush();

        List<ItemRequestDtoInc> incDtos = List.of(
                new ItemRequestDtoInc("d1"),
                new ItemRequestDtoInc("d2"),
                new ItemRequestDtoInc("d3")
        );

        for (ItemRequestDtoInc requestInc: incDtos) {
            ItemRequest request = ItemRequestMapper.mapIncomingRequestToRequest(entity, requestInc);
            em.persist(request);
        }
        em.flush();

        List<ItemRequestDtoOut> outs = itemRequestService.getAll(entity.getId(), Optional.empty(), Optional.empty());

        assertThat(outs, hasSize(incDtos.size()));
    }
}
