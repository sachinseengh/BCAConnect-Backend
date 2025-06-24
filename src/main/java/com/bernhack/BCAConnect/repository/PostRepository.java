package com.bernhack.BCAConnect.repository;

import com.bernhack.BCAConnect.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Posts,Long> {


    Optional<Posts> findById(Long id);
}
