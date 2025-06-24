package com.bernhack.BCAConnect.service;


import com.bernhack.BCAConnect.dto.note.NotesResponse;

import java.util.List;

public interface NoteService {


    public List<NotesResponse> getNotesBySemesterAndSubject( String semester,String subject);

    public Long deleteById(Long note_id);

}
