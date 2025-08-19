package com.bernhack.BCAConnect.repository;

import com.bernhack.BCAConnect.entity.Posts;
import com.bernhack.BCAConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Posts,Long> {

    @Query(value="select * from posts p where p.is_Verified=true order by p.id desc ",nativeQuery = true)
    Optional<List<Posts>> getAllPosts();

    Optional<Posts> findById(Long id);

    Optional<List<Posts>> findAllByUserOrderByDateDesc(User user);



    @Query(value = "select * from posts p where p.is_verified=false",nativeQuery = true)
    Optional<List<Posts>> findAllUnverifiedPosts();

//
    @Query(value="SELECT * FROM posts p " +
            "JOIN saved_posts sp " +
            " ON p.id=sp.post_id " +
            " WHERE sp.user_id =:userId",nativeQuery = true)

//    The below is jpql
//    @Query("SELECT u.savedPosts FROM User u WHERE u.id = :userId")
    Optional<List<Posts>> findAllSavedPost(@Param("userId") Long userId);



    @Query(value="SELECT  COUNT(*) > 0  FROM " +
            " saved_posts sp " +
            " WHERE sp.user_id =:userId AND sp.post_id=:postId",nativeQuery = true)
    boolean checkPostAlreadySaved(@Param("userId") Long userId,@Param("postId") Long postId);
}
