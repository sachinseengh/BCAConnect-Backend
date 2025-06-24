package com.bernhack.BCAConnect.repository;

import com.bernhack.BCAConnect.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Notes,Long> {


    @Query("SELECT n FROM Notes n JOIN FETCH n.user u JOIN FETCH u.roles WHERE n.semester = :semester AND n.subject = :subject")
    Optional<List<Notes>> getNotesBySemesterAndSubject(@Param("semester") String semester, @Param("subject") String subject);


    Optional<Notes> findById(Long id);

}
