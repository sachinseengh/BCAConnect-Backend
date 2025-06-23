package com.bernhack.BCAConnect.entity;

import lombok.Data;

import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")

@Data
@NoArgsConstructor
public class User {

    @Id
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "users_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_seq_gen", strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(nullable = false)
    private String fullName;

    @Column(nullable=false)
    private String semester;

    @Column(nullable=false,unique = true)
    private String email;

    @Column(nullable = false,unique=true)
    private String userName;

    @Column(nullable = false)
    private String password;


    @ManyToMany
    @JoinTable(
            name="user_roles",
            joinColumns =@JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")

    )
    private List<Role> roles = new ArrayList<>();


    @OneToMany()
    @JoinColumn(name="posts_id")
    private List<Posts> posts = new ArrayList<>();


    @OneToMany(fetch = FetchType.EAGER)
    private List<Notes> notes = new ArrayList<>();


}
