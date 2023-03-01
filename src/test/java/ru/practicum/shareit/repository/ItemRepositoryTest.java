package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;

    @Test
    void findByOwnerOrderByIdPageableTest() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@email.com");
        em.persist(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user.getId());
        em.persist(item);

        Pageable pageable = PageRequest.of(0, 20, Sort.by(DESC, "id"));

        List<Item> result = repository.findByOwnerOrderByIdPageable(user.getId(), pageable);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getName(), equalTo(item.getName()));
        assertThat(result.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.get(0).getOwner(), equalTo(item.getOwner()));
    }
}
