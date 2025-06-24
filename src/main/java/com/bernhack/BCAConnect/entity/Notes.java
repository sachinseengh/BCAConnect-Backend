package com.bernhack.BCAConnect.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name="notes")

@Data
@NoArgsConstructor

public class Notes {
    @Id
    @SequenceGenerator(name = "notes_seq_gen", sequenceName = "notes_seq", allocationSize = 1)
    @GeneratedValue(generator = "notes_seq_gen", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private String subject;

    private LocalDateTime date;

    private String fileName;
    private String fileType;

    @ManyToOne()
    @JoinColumn(name="user_id")
    private User user;

}
