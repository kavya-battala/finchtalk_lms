# Finchtalk Courses/Enrollment/Quiz/Progress (static in-memory)

## User auth
- Existing endpoints used:
  - POST /api/auth/register
  - POST /api/auth/login
- Frontend stores returned JWT token and calls APIs with:
  - Authorization: Bearer <token>

## Quiz attempt policy
- Option A selected: per-course single attempt only.

## Planned API contracts (backend)
All endpoints are under /api and require JWT (except login/register).

1) Catalog
- GET /api/courses
  -> CourseSummary[]
- GET /api/courses/{courseId}
  -> CourseDetail

2) Enrollment
- POST /api/courses/{courseId}/enroll
  -> EnrollmentStatus
- GET /api/courses/me/enrollments
  -> EnrollmentStatus[]

3) Roadmap / modules
- GET /api/courses/{courseId}/roadmap
  -> Roadmap { modules: [{moduleId,title,completed}], completedCount, totalCount }
- POST /api/courses/{courseId}/modules/{moduleId}/complete
  -> Progress

4) Quiz
- GET /api/courses/{courseId}/quiz
  -> Quiz { quizId, questions: [{questionId,prompt,options}] }
- POST /api/courses/{courseId}/quiz/submit
  body: { answers: [{questionId, selectedOptionIndex}] }
  -> QuizResult + Progress

5) Progress
- GET /api/courses/{courseId}/progress
  -> Progress { percent, completedModules, totalModules, quizScore, quizAttempted, quizPassed }

