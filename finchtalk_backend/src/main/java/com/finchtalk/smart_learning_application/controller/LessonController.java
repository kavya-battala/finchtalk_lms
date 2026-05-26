package com.finchtalk.smart_learning_application.controller;


import com.finchtalk.smart_learning_application.model.Lesson;
import com.finchtalk.smart_learning_application.service.LessonService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/lessons")
    public List<Lesson> getLessons() {
        return lessonService.getAllLessons();
    }
}


