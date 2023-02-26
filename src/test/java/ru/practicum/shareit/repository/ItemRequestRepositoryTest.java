package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    ItemRequestRepository repository;
    @Autowired
    UserRepository userRepository;

    @Test
    void findAllWithoutUserTest() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@email.com");
        em.persist(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        em.persist(itemRequest);

        List<ItemRequest> result = repository.findAllWithoutUser(2L, Sort.by(DESC, "created"));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(result.get(0).getRequestor(), equalTo(itemRequest.getRequestor()));
        assertThat(result.get(0).getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void findAllPageableTest() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@email.com");
        em.persist(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        em.persist(itemRequest);

        Pageable pageable = PageRequest.of(0, 20, Sort.by(DESC, "id"));

        List<ItemRequest> result = repository.findAllPageable(2L, pageable);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(result.get(0).getRequestor(), equalTo(itemRequest.getRequestor()));
        assertThat(result.get(0).getCreated(), equalTo(itemRequest.getCreated()));
    }
}
