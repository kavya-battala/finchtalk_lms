package com.finchtalk.smart_learning_application.courses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class CoursesController {

    // Use record types from CoursesService (inner records) to avoid DTO type duplication

    private final CoursesService coursesService;


    public CoursesController(CoursesService coursesService) {
        this.coursesService = coursesService;
    }

    @GetMapping("/courses")
    public List<CoursesService.CourseSummary> getCourses() {
        return coursesService.getCourses();
    }


    @GetMapping("/courses/{courseId}")
    public CoursesService.CourseDetail getCourse(@PathVariable Long courseId) {
        return coursesService.getCourse(courseId);
    }


    @PostMapping("/courses/{courseId}/enroll")
    @ResponseStatus(HttpStatus.OK)
    public CoursesService.EnrollmentStatus enroll(@PathVariable Long courseId, @RequestHeader("Authorization") String authorization) {
        return coursesService.enroll(courseId, authorization);
    }


    @GetMapping("/courses/me/enrollments")
    public List<CoursesService.EnrollmentStatus> myEnrollments(@RequestHeader("Authorization") String authorization) {
        return coursesService.getMyEnrollments(authorization);
    }


    @GetMapping("/courses/{courseId}/roadmap")
    public CoursesService.Roadmap getRoadmap(@PathVariable Long courseId, @RequestHeader("Authorization") String authorization) {
        return coursesService.getRoadmap(courseId, authorization);
    }



    @PostMapping("/courses/{courseId}/modules/{moduleId}/complete")
    public CoursesService.ModuleCompletion completeModule(@PathVariable Long courseId,
                                             @PathVariable Long moduleId,
                                             @RequestHeader("Authorization") String authorization) {
        return coursesService.completeModule(courseId, moduleId, authorization);
    }


    @GetMapping("/courses/{courseId}/quiz")
    public CoursesService.Quiz getQuiz(@PathVariable Long courseId, @RequestHeader("Authorization") String authorization) {
        return coursesService.getQuiz(courseId, authorization);
    }


    @PostMapping("/courses/{courseId}/quiz/submit")
    public CoursesService.QuizResult submitQuiz(@PathVariable Long courseId,
                                   @RequestBody CoursesService.QuizSubmitRequest request,
                                   @RequestHeader("Authorization") String authorization) {
        // service method returns QuizResultWithProgress; controller returns only quiz result for now
        CoursesService.QuizResultWithProgress qp = coursesService.submitQuiz(courseId, request, authorization);
        return qp.result();
    }



    @GetMapping("/courses/{courseId}/progress")
    public CoursesService.Progress getProgress(@PathVariable Long courseId, @RequestHeader("Authorization") String authorization) {
        return coursesService.getProgress(courseId, authorization);
    }


}

