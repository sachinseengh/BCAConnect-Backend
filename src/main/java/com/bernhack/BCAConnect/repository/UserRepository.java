package com.bernhack.BCAConnect.repository;

import com.bernhack.BCAConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUserName(String sachin);

    Optional<User> findByEmail(String email);
}
