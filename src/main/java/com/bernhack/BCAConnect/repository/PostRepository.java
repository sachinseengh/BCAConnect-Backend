package com.bernhack.BCAConnect.repository;

import com.bernhack.BCAConnect.entity.Posts;
import com.bernhack.BCAConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Posts,Long> {

@Query(value="select * from posts p order by p.id desc ",nativeQuery = true)
Optional<List<Posts>> getAllPosts();

    Optional<Posts> findById(Long id);

    Optional<List<Posts>> findAllByUserOrderByDateDesc(User user);


}
