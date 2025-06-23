package com.bernhack.BCAConnect.repository;

import com.bernhack.BCAConnect.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Notes,Long> {
}
