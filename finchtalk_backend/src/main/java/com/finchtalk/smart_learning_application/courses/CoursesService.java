package com.finchtalk.smart_learning_application.courses;

import com.finchtalk.smart_learning_application.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CoursesService {

    private final JwtTokenProvider jwtTokenProvider;

    // -------- Static catalog data (in-memory) --------
    private final Map<Long, Course> catalog = new HashMap<>();

    // -------- Per-user progress data (in-memory) --------
    // key: username
    private final Map<String, Set<Long>> enrollmentsByUser = new ConcurrentHashMap<>();

    // key: username -> courseId -> completedModuleIds
    private final Map<String, Map<Long, Set<Long>>> completedModules = new ConcurrentHashMap<>();

    // key: username -> courseId -> quizAttempted? + result
    private final Map<String, Map<Long, QuizAttempt>> quizAttempts = new ConcurrentHashMap<>();

    public CoursesService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        seedCatalog();
    }

    private void seedCatalog() {
        // Course 1: Machine Learning
        Course ml = new Course(1L, "Machine Learning", "Build ML fundamentals and deploy models.",
                "Machine Learning", "🧠");
        ml.modules = List.of(
                new Module(101L, "Introduction to ML", List.of(10101L, 10102L)),
                new Module(102L, "Supervised Learning", List.of(10201L, 10202L)),
                new Module(103L, "Model Evaluation", List.of(10301L, 10302L))
        );
        ml.quiz = new Quiz(
                1001L,
                List.of(
                        new QuizQuestion(201L, "Which metric is suitable for classification imbalance?", List.of("Accuracy", "F1-score", "MSE", "MAE"), 1),
                        new QuizQuestion(202L, "What is overfitting?", List.of("Low variance", "High bias", "Model learns noise", "Under training"), 2),
                        new QuizQuestion(203L, "Cross-validation helps to…", List.of("Leak test data", "Estimate generalization", "Decrease training", "Only tune hyperparameters"), 1)
                )
        );
        catalog.put(ml.id, ml);

        // Course 2: Artificial Intelligence
        Course ai = new Course(2L, "Artificial Intelligence", "From search to decision making.",
                "Artificial Intelligence", "🤖");
        ai.modules = List.of(
                new Module(201L, "AI Basics", List.of(20101L, 20102L)),
                new Module(202L, "Search Algorithms", List.of(20201L, 20202L)),
                new Module(203L, "Planning & Reasoning", List.of(20301L, 20302L))
        );
        ai.quiz = new Quiz(
                2002L,
                List.of(
                        new QuizQuestion(301L, "Which is an example of uninformed search?", List.of("A*", "BFS", "Minimax", "Dijkstra"), 1),
                        new QuizQuestion(302L, "An agent's goal is to…", List.of("Randomly act", "Maximize reward", "Ignore environment", "Only plan offline"), 1),
                        new QuizQuestion(303L, "Planning differs from reflex agents because…", List.of("No lookahead", "Uses internal models", "No states", "No evaluation"), 1)
                )
        );
        catalog.put(ai.id, ai);

        // Course 3: Web Development
        Course web = new Course(3L, "Web Development", "React + backend fundamentals.",
                "Web Development", "🌐");
        web.modules = List.of(
                new Module(301L, "HTML/CSS/JS", List.of(30101L, 30102L)),
                new Module(302L, "React Fundamentals", List.of(30201L, 30202L)),
                new Module(303L, "APIs & Authentication", List.of(30301L, 30302L))
        );
        web.quiz = new Quiz(
                3003L,
                List.of(
                        new QuizQuestion(401L, "React is best described as…", List.of("A server", "A UI library", "A database", "A build tool"), 1),
                        new QuizQuestion(402L, "HTTP method used to fetch data is…", List.of("POST", "GET", "PUT", "DELETE"), 1),
                        new QuizQuestion(403L, "JWT is mainly used for…", List.of("File uploads", "Authentication/authorization", "Caching", "SQL"), 1)
                )
        );
        catalog.put(web.id, web);

        // Course 4: Kafka
        Course kafka = new Course(4L, "Kafka Essentials", "Understand event streaming with Kafka.",
                "Kafka", "📨");
        kafka.modules = List.of(
                new Module(401L, "Producers & Consumers", List.of(40101L, 40102L)),
                new Module(402L, "Topics & Partitions", List.of(40201L, 40202L)),
                new Module(403L, "Offsets & Delivery Guarantees", List.of(40301L, 40302L))
        );
        kafka.quiz = new Quiz(
                4004L,
                List.of(
                        new QuizQuestion(501L, "A topic contains…", List.of("Files", "Messages", "Users", "Executables"), 1),
                        new QuizQuestion(502L, "Partitions help with…", List.of("Single-threading", "Parallelism", "No ordering", "No scaling"), 1),
                        new QuizQuestion(503L, "Offsets track…", List.of("Training epochs", "Read position", "Network speed", "Disk usage"), 1)
                )
        );
        catalog.put(kafka.id, kafka);

        // Course 5: Docker
        Course docker = new Course(5L, "Docker for Developers", "Containerize apps and ship consistently.",
                "Docker", "🐳");
        docker.modules = List.of(
                new Module(501L, "Containers 101", List.of(50101L, 50102L)),
                new Module(502L, "Images & Dockerfile", List.of(50201L, 50202L)),
                new Module(503L, "Compose & Deployment", List.of(50301L, 50302L))
        );
        docker.quiz = new Quiz(
                5005L,
                List.of(
                        new QuizQuestion(601L, "A Docker image is…", List.of("A running container", "A read-only template", "A network cable", "A kernel"), 1),
                        new QuizQuestion(602L, "Dockerfile is used to…", List.of("Store logs", "Build images", "Run only tests", "Encrypt files"), 1),
                        new QuizQuestion(603L, "docker-compose is for…", List.of("Single command", "Multi-container apps", "Only volumes", "Only networking"), 1)
                )
        );
        catalog.put(docker.id, docker);

        // Course 6: Django
        Course django = new Course(6L, "Django Basics", "Build web apps with Django.",
                "Django", "🐍");
        django.modules = List.of(
                new Module(601L, "Django Setup", List.of(60101L, 60102L)),
                new Module(602L, "Models & ORM", List.of(60201L, 60202L)),
                new Module(603L, "Views & Templates", List.of(60301L, 60302L))
        );
        django.quiz = new Quiz(
                6006L,
                List.of(
                        new QuizQuestion(701L, "Django ORM is used for…", List.of("UI rendering", "Database access", "PDF generation", "Email sending"), 1),
                        new QuizQuestion(702L, "Django templates help with…", List.of("Database migrations", "Rendering HTML", "Compiling code", "Networking"), 1),
                        new QuizQuestion(703L, "MVC in Django is often described as…", List.of("MVT", "MLK", "VUI", "CTR"), 0)
                )
        );
        catalog.put(django.id, django);

        // You can add more courses later similarly.
    }

    // -------- Auth helper --------
    private String usernameFromAuthHeader(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing Bearer token");
        }
        String token = authorization.substring("Bearer ".length());
        Claims claims = jwtTokenProvider.parse(token);
        return claims.getSubject();
    }

    // -------- API implementations --------

    public List<CourseSummary> getCourses() {
        return catalog.values().stream()
                .map(c -> new CourseSummary(c.id, c.title, c.subtitle, c.icon))
                .sorted(Comparator.comparingLong(CourseSummary::courseId))
                .toList();
    }

    public CourseDetail getCourse(Long courseId) {
        Course c = requireCourse(courseId);
        return new CourseDetail(c.id, c.title, c.subtitle, c.description, c.icon,
                c.modules.stream().map(m -> new ModuleSummary(m.id, m.title)).toList());
    }

    public EnrollmentStatus enroll(Long courseId, String authorization) {
        String username = usernameFromAuthHeader(authorization);
        requireCourse(courseId);

        enrollmentsByUser.computeIfAbsent(username, u -> ConcurrentHashMap.newKeySet()).add(courseId);
        completedModules.computeIfAbsent(username, u -> new ConcurrentHashMap<>()).computeIfAbsent(courseId, cid -> ConcurrentHashMap.newKeySet());
        quizAttempts.computeIfAbsent(username, u -> new ConcurrentHashMap<>()).putIfAbsent(courseId, QuizAttempt.empty());

        return enrollmentStatus(username, courseId);
    }

    public List<EnrollmentStatus> getMyEnrollments(String authorization) {
        String username = usernameFromAuthHeader(authorization);
        Set<Long> set = enrollmentsByUser.getOrDefault(username, Set.of());
        return set.stream().map(cid -> enrollmentStatus(username, cid)).toList();
    }

    public Roadmap getRoadmap(Long courseId, String authorization) {
        String username = usernameFromAuthHeader(authorization);
        Course c = requireCourse(courseId);
        ensureEnrolled(username, courseId);

        Set<Long> done = completedModules.getOrDefault(username, Map.of()).getOrDefault(courseId, Set.of());
        List<ModuleProgress> modules = c.modules.stream()
                .map(m -> new ModuleProgress(m.id, m.title, done.contains(m.id)))
                .toList();

        int completedCount = (int) modules.stream().filter(ModuleProgress::completed).count();

        return new Roadmap(courseId, c.title, modules, completedCount, c.modules.size());
    }

    public ModuleCompletion completeModule(Long courseId, Long moduleId, String authorization) {
        String username = usernameFromAuthHeader(authorization);
        Course c = requireCourse(courseId);
        ensureEnrolled(username, courseId);
        boolean exists = c.modules.stream().anyMatch(m -> m.id.equals(moduleId));
        if (!exists) throw new IllegalArgumentException("Unknown moduleId");

        Set<Long> done = completedModules.get(username).get(courseId);
        done.add(moduleId);

        Progress progress = computeProgress(username, courseId);
        return new ModuleCompletion(courseId, moduleId, progress.percent);
    }

    public Quiz getQuiz(Long courseId, String authorization) {
        String username = usernameFromAuthHeader(authorization);
        requireCourse(courseId);
        ensureEnrolled(username, courseId);

        Course c = catalog.get(courseId);
        // Quiz contains correctOptionIndex; frontend will use it only for scoring server-side.
        return c.quiz;
    }

    public QuizResultWithProgress submitQuiz(Long courseId, QuizSubmitRequest request, String authorization) {
        String username = usernameFromAuthHeader(authorization);
        requireCourse(courseId);
        ensureEnrolled(username, courseId);

        Course c = catalog.get(courseId);
        QuizAttempt attempt = quizAttempts.get(username).getOrDefault(courseId, QuizAttempt.empty());

        if (attempt.attempted) {
                Progress progress = computeProgress(username, courseId);
                return new QuizResultWithProgress(attempt.result, progress);
        }

        // Score
        Map<Long, Integer> selectedByQuestionId = request.answers.stream()
                .collect(Collectors.toMap(QuizAnswer::questionId, QuizAnswer::selectedOptionIndex));

        int correct = 0;
        int total = c.quiz.questions.size();
        for (QuizQuestion q : c.quiz.questions) {
            Integer selected = selectedByQuestionId.get(q.questionId);
            if (selected != null && selected == q.correctOptionIndex) correct++;
        }

        int quizScore = correct;

        QuizResult result = new QuizResult(c.quiz.quizId, courseId, quizScore, total);
        QuizAttempt newAttempt = QuizAttempt.attempted(result);
        quizAttempts.get(username).put(courseId, newAttempt);

        Progress progress = computeProgress(username, courseId);
        // return with computed progress info
        return new QuizResultWithProgress(result, progress);
    }

    public Progress getProgress(Long courseId, String authorization) {
        String username = usernameFromAuthHeader(authorization);
        requireCourse(courseId);
        ensureEnrolled(username, courseId);
        return computeProgress(username, courseId);
    }

    // -------- helpers --------
    private Course requireCourse(Long courseId) {
        Course c = catalog.get(courseId);
        if (c == null) throw new IllegalArgumentException("Unknown courseId");
        return c;
    }

    private void ensureEnrolled(String username, Long courseId) {
        Set<Long> set = enrollmentsByUser.getOrDefault(username, Set.of());
        if (!set.contains(courseId)) {
            throw new IllegalArgumentException("User is not enrolled in this course");
        }
    }

    private EnrollmentStatus enrollmentStatus(String username, Long courseId) {
        Course c = catalog.get(courseId);
        Set<Long> done = completedModules.getOrDefault(username, Map.of()).getOrDefault(courseId, Set.of());
        Progress p = computeProgress(username, courseId);
        QuizAttempt attempt = quizAttempts.getOrDefault(username, Map.of()).getOrDefault(courseId, QuizAttempt.empty());

        return new EnrollmentStatus(courseId, c.title, p.percent, done.size(), c.modules.size(), attempt.attempted);
    }

    private Progress computeProgress(String username, Long courseId) {
        Course c = catalog.get(courseId);
        Set<Long> done = completedModules.getOrDefault(username, Map.of()).getOrDefault(courseId, Set.of());
        int completedModulesCount = done.size();
        int totalModules = c.modules.size();

        QuizAttempt attempt = quizAttempts.getOrDefault(username, Map.of()).getOrDefault(courseId, QuizAttempt.empty());
        boolean quizAttempted = attempt.attempted;
        Integer quizScore = attempt.attempted ? attempt.result.quizScore : null;
        boolean quizPassed = quizAttempted && quizScore != null && quizScore >= (int) Math.ceil(c.quiz.questions.size() * 0.6);

        int totalSteps = totalModules + 1; // + quiz
        int completedSteps = completedModulesCount + (quizAttempted ? 1 : 0);
        int percent = (int) Math.round((completedSteps * 100.0) / totalSteps);

        return new Progress(courseId, c.title, percent,
                completedModulesCount, totalModules,
                quizScore,
                quizAttempted,
                quizPassed);
    }

    // -------- internal domain models --------

    private static class Course {
        Long id;
        String title;
        String subtitle;
        String description;
        String icon;
        List<Module> modules = List.of();
        Quiz quiz = new Quiz(0L, List.of());

        Course(Long id, String title, String subtitle, String description, String icon) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.description = description;
            this.icon = icon;
        }
    }

    private static class Module {
        Long id;
        String title;
        List<Long> dummyQuizLinks; // placeholder

        Module(Long id, String title, List<Long> dummyQuizLinks) {
            this.id = id;
            this.title = title;
            this.dummyQuizLinks = dummyQuizLinks;
        }
    }

    private static class QuizAttempt {
        boolean attempted;
        QuizResult result;

        static QuizAttempt empty() {
            QuizAttempt a = new QuizAttempt();
            a.attempted = false;
            a.result = null;
            return a;
        }

        static QuizAttempt attempted(QuizResult result) {
            QuizAttempt a = new QuizAttempt();
            a.attempted = true;
            a.result = result;
            return a;
        }
    }

    // -------- DTOs / records --------

    public record CourseSummary(Long courseId, String title, String subtitle, String icon) {}

    public record CourseDetail(Long courseId, String title, String subtitle, String description, String icon, List<ModuleSummary> modules) {}

    public record ModuleSummary(Long moduleId, String title) {}

    public record EnrollmentStatus(Long courseId,
                                     String courseTitle,
                                     int percent,
                                     int completedModules,
                                     int totalModules,
                                     boolean quizAttempted) {}

    public record Roadmap(Long courseId, String courseTitle, List<ModuleProgress> modules, int completedCount, int totalCount) {}

    public record ModuleProgress(Long moduleId, String title, boolean completed) {}

    public record ModuleCompletion(Long courseId, Long moduleId, int percent) {}

    public record Quiz(Long quizId, List<QuizQuestion> questions) {}

    public record QuizQuestion(Long questionId, String prompt, List<String> options, int correctOptionIndex) {}

    public record QuizSubmitRequest(List<QuizAnswer> answers) {}

    public record QuizAnswer(Long questionId, int selectedOptionIndex) {}

    public record QuizResult(Long quizId, Long courseId, int quizScore, int totalQuestions) {}

    public record QuizResultWithProgress(QuizResult result, Progress progress) {}

    public record Progress(Long courseId,
                             String courseTitle,
                             int percent,
                             int completedModules,
                             int totalModules,
                             Integer quizScore,
                             boolean quizAttempted,
                             boolean quizPassed) {}
}

