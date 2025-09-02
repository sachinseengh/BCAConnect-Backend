package com.bernhack.BCAConnect.service.impl;


import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.dto.note.NotesResponse;
import com.bernhack.BCAConnect.dto.user.UserResponse;
import com.bernhack.BCAConnect.entity.Notes;
import com.bernhack.BCAConnect.repository.NoteRepository;
import com.bernhack.BCAConnect.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;


    @Override
    public List<NotesResponse> getNotesBySemesterAndSubject(String semester, String subject) {


        List<Notes> notes = noteRepository.getNotesBySemesterAndSubject(semester, subject).orElse(Collections.emptyList());
        List<NotesResponse> responses = new ArrayList<>();

        if (notes != null) {
            for (Notes note : notes) {

                List<String> roles = note.getUser().getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

                UserResponse userResponse = new UserResponse(note.getUser()
                        .getId(), note.getUser().getFullName(), note.getUser().getEmail(), note.getUser().getSemester(), roles);

                String fileUrl = null;
                String fileType = null;
                String fileName=null;
                if (note.getFileName() != null) {
                    fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/uploads/")
                            .path(note.getFileName())
                            .toUriString();
                    fileType = note.getFileType();
                    fileName=note.getFileName();

                }

                responses.add(new NotesResponse(note.getId(), note.getDate(), userResponse,note.getSemester(),note.getSubject(), fileUrl, fileType,fileName));
            }
            return responses;
        }
        return responses;
    }


}
