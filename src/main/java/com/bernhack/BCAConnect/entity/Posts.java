package com.bernhack.BCAConnect.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Posts {

    @Id
    @SequenceGenerator(name = "posts_seq_gen", sequenceName = "posts_seq", allocationSize = 1)
    @GeneratedValue(generator = "posts_seq_gen", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable=false)
    private String semester;

    @Column(nullable = false)
    private String subject;


    @Column(nullable = false)
    private String caption;

    @Column( nullable = false)
    private String content;

    private LocalDateTime date;


    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

}
