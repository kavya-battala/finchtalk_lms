# Finchtalk LMS (Smart Learning) — Frontend/Backend Integration

This repo contains a simple example of a **React frontend** calling **Spring Boot REST APIs**, receiving **JSON**, and rendering it in the UI.

---

## 1) Basic project setup and integration

### Ports
- **Spring Boot backend** runs on: `http://localhost:8080` (see `finchtalk_backend/src/main/resources/application.properties`)
- **React/Vite frontend dev server** runs on: `http://localhost:3000` (see `finchtalk_frontend/vite.config.ts`)

### Start backend (Spring Boot)
From the repo root:
- Run inside `finchtalk_backend`:
  - `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`)

### Start frontend (React/Vite)
From the repo root:
- Run inside `finchtalk_frontend`:
  - `npm install`
  - `npm run dev`

### Cross-Origin setup (CORS)
Because frontend and backend run on different ports (`3000` vs `8080`), Spring Boot must allow cross-origin calls.
- `LessonController` is annotated with:
  - `@CrossOrigin(origins = "http://localhost:3000")`

---

## 2) How Spring Boot creates REST APIs

### Controller annotations
Backend endpoint(s) are implemented using Spring MVC annotations:

- `@RestController`:
  - Marks the class as a REST controller.
  - **All returned values are written to the HTTP response body** (not rendered as HTML).

- `@RequestMapping("/api")`:
  - Prefixes all routes in the controller with `/api`.

- `@GetMapping("/lessons")`:
  - Creates an HTTP GET endpoint at `/api/lessons`.

### Lesson endpoint
File: `finchtalk_backend/src/main/java/com/finchtalk/smart_learning_application/controller/LessonController.java`

Key behavior:
- `GET /api/lessons` calls:
  - `lessonService.getAllLessons()`
- Returns:
  - `List<Lesson>`



---

## 3) How JSON data is sent from backend to frontend

### Java model -> JSON automatically
Backend model: `Lesson` is a Java record:
- `finchtalk_backend/.../model/Lesson.java`

```java
public record Lesson(Long id, String title, String description) {}
```

Because the controller is a `@RestController`, Spring Boot uses an HTTP message converter (typically Jackson) to serialize Java objects into JSON.

### What the frontend receives
For `/api/lessons`, the response body is JSON shaped like:

```json
[
  { "id": 1, "title": "Intro to Finchtalk", "description": "Learn the basics of Smart Learning." },
  { "id": 2, "title": "React + API", "description": "How frontend calls backend REST APIs." },
  { "id": 3, "title": "Spring Boot", "description": "Building REST APIs with Spring Boot." }
]
```

---

## 4) How frontend calls backend APIs (React)

File: `finchtalk_frontend/src/App.tsx`

The UI loads lessons using the Fetch API:

- `useEffect(() => { ... }, [])` runs once when the component mounts.
- It calls:
  - `fetch('http://localhost:8080/api/lessons')`

Then it:
- checks `res.ok`
- parses JSON via:
  - `await res.json()`
- stores results in React state:
  - `setLessons(data)`

---

## 5) How React displays data on UI

File: `finchtalk_frontend/src/App.tsx`

React state:
- `lessons` holds the array returned from the backend.
- `loading` and `error` control conditional rendering.

Rendering logic:
- While loading: shows `Loading lessons...`
- On error: shows an error message
- When data is available:
  - `lessons.map((l) => ( <li key={l.id}> ... </li> ))`

So the JSON returned by Spring Boot becomes TypeScript objects and is displayed as:
- lesson title (`l.title`)
- lesson description (`l.description`)

---

## 6) API URLs (quick reference)

- `GET http://localhost:8080/api/lessons`

---

## Notes
- If you see CORS errors, confirm that the frontend is running on `http://localhost:3000` (not a different port), since the backend CORS rule is pinned to that origin.

