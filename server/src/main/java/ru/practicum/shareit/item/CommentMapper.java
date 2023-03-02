package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoInc;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static List<CommentDto> mapToCommentDtos(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment: comments) {
            commentDtos.add(mapToCommentDto(comment));
        }
        return commentDtos;
    }

    public static Comment mapToComment(CommentDtoInc commentDtoInc, Long itemId, User user) {
        Comment comment = new Comment();
        comment.setText(commentDtoInc.getText());
        comment.setItem(itemId);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}
