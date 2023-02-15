package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private Boolean available;
    @Column(name = "item_owner")
    private Long owner;
    @Column(name = "request")
    private Long request;
}