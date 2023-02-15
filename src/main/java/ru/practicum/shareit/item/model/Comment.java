package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "comment_text")
    private String text;
    @Column(name = "item")
    private Long item;
    @Column(name = "author")
    private Long author;
    @Column(name = "created")
    private LocalDateTime created;
}
