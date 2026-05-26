package com.finchtalk.smart_learning_application.service;

import com.finchtalk.smart_learning_application.model.Lesson;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    public List<Lesson> getAllLessons() {
        return List.of(
                new Lesson(1L, "Intro to Finchtalk", "Learn the basics of Smart Learning."),
                new Lesson(2L, "React + API", "How frontend calls backend REST APIs."),
                new Lesson(3L, "Spring Boot", "Building REST APIs with Spring Boot.")
        );
    }
}

