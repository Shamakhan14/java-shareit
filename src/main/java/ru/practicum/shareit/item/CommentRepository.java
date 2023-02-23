package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItem(Long itemId);

    @Query(value = "select c from Comment c " +
            "where c.item in ?1 " +
            "group by c.id")
    List<Comment> findByItemIn(List<Long> itemIds, Sort sort);
}
