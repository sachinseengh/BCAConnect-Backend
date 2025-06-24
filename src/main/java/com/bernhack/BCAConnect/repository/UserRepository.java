package com.bernhack.BCAConnect.repository;

import com.bernhack.BCAConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUserName(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "select * from users u join user_roles ur on u.id=ur.user_id join roles r on r.id=ur.role_id where r.name=:role",nativeQuery = true)
    Optional<List<User>> findModerators(@Param("role") String username);


    @Query(value = "SELECT COUNT(*) > 0 FROM users u " +
            "JOIN  user_roles ur ON u.id = ur.user_id " +
            "JOIN roles r ON r.id = ur.role_id " +
            "WHERE u.user_name = :username " +
            "AND (r.name = 'ADMIN' OR r.name='MODERATOR') ", nativeQuery = true)
    Boolean findUserWithRoleAdminOrModerator(@Param("username") String username);


}
