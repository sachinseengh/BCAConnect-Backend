package com.bernhack.BCAConnect.repository;


import com.bernhack.BCAConnect.entity.Posts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Repository
public interface ModeratorRepository {

    @Query(value = "select * from posts p where p.is_verified=false",nativeQuery = true)
    List<Posts> findAllUnverifiedPosts();

}
