package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByItemInTest() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@email.com");
        em.persist(user);

        User newUser = new User();
        newUser.setName("newName");
        newUser.setEmail("newEmail@email.com");
        em.persist(newUser);

        Item item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user.getId());
        em.persist(item);

        Comment comment = new Comment();
        comment.setText("text");
        comment.setItem(item.getId());
        comment.setAuthor(newUser);
        comment.setCreated(LocalDateTime.now());
        em.persist(comment);

        List<Comment> result = repository.findByItemIn(List.of(item.getId()), Sort.by(DESC, "created"));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getText(), equalTo(comment.getText()));
        assertThat(result.get(0).getItem(), equalTo(comment.getItem()));
        assertThat(result.get(0).getAuthor(), equalTo(comment.getAuthor()));
        assertThat(result.get(0).getCreated(), equalTo(comment.getCreated()));
    }
}
