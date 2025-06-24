package com.bernhack.BCAConnect.controller.note;

import com.bernhack.BCAConnect.controller.BaseController;
import com.bernhack.BCAConnect.dto.GlobalAPIResponse;
import com.bernhack.BCAConnect.service.NoteService;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/note/")
public class NoteController extends BaseController {

    @Autowired
    private NoteService noteService;

    @GetMapping("/getNote")
    public ResponseEntity<GlobalAPIResponse> getNotesBySemesterAndSubject(@RequestParam String semester, @RequestParam String subject){
        return successResponse("Notes fetched",noteService.getNotesBySemesterAndSubject(semester,subject));
    }

    @DeleteMapping("/deleteNote/{id}")
    public ResponseEntity<GlobalAPIResponse> deleteNote(@PathVariable Long id){
        return successResponse("Notes Deleted",noteService.deleteById(id));
    }

}
