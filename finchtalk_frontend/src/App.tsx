import React, { useEffect, useMemo, useState } from 'react';

type Lesson = {
  id: number;
  title: string;
  description: string;
};

type AuthRegisterPayload = {
  username: string;
  password: string;
  role: string;
};

type AuthLoginPayload = {
  username: string;
  password: string;
};

type TokenResponse = {
  token: string;
};

type CourseSummary = {
  courseId: number;
  title: string;
  subtitle: string;
  icon: string;
};

type CourseDetail = {
  courseId: number;
  title: string;
  subtitle: string;
  description: string;
  icon: string;
  modules: { moduleId: number; title: string }[];
};

type EnrollmentStatus = {
  courseId: number;
  courseTitle: string;
  percent: number;
  completedModules: number;
  totalModules: number;
  quizAttempted: boolean;
};

type Roadmap = {
  courseId: number;
  courseTitle: string;
  modules: { moduleId: number; title: string; completed: boolean }[];
  completedCount: number;
  totalCount: number;
};

type Quiz = {
  quizId: number;
  questions: {
    questionId: number;
    prompt: string;
    options: string[];
    correctOptionIndex: number;
  }[];
};

type QuizSubmitRequest = {
  answers: { questionId: number; selectedOptionIndex: number }[];
};

type QuizResult = {
  quizId: number;
  courseId: number;
  quizScore: number;
  totalQuestions: number;
};

export default function App() {
  const [lessons, setLessons] = useState<Lesson[]>([]);
  const [lessonsLoading, setLessonsLoading] = useState(true);
  const [lessonsError, setLessonsError] = useState<string | null>(null);

  const API_BASE = 'http://localhost:8080';

  // token + auth
  const [token, setToken] = useState<string>(() => localStorage.getItem('token') ?? '');
  const authHeader = useMemo(() => (token ? `Bearer ${token}` : ''), [token]);

  // auth forms
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('STUDENT');
  const [authMsg, setAuthMsg] = useState<string | null>(null);

  // courses dashboard
  const [coursesLoading, setCoursesLoading] = useState(false);
  const [coursesError, setCoursesError] = useState<string | null>(null);
  const [courses, setCourses] = useState<CourseSummary[]>([]);

  const [selectedCourseId, setSelectedCourseId] = useState<number | ''>('');
  const [selectedCourseLoading, setSelectedCourseLoading] = useState(false);
  const [selectedCourse, setSelectedCourse] = useState<CourseDetail | null>(null);

  const [enrollmentsLoading, setEnrollmentsLoading] = useState(false);
  const [enrollments, setEnrollments] = useState<EnrollmentStatus[]>([]);

  const [roadmapLoading, setRoadmapLoading] = useState(false);
  const [roadmap, setRoadmap] = useState<Roadmap | null>(null);

  // quiz
  const [quizLoading, setQuizLoading] = useState(false);
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [quizAnswers, setQuizAnswers] = useState<Record<number, number>>({});
  const [quizResult, setQuizResult] = useState<QuizResult | null>(null);

  useEffect(() => {
    const fetchLessons = async () => {
      try {
        setLessonsLoading(true);
        setLessonsError(null);
        const res = await fetch(`${API_BASE}/api/lessons`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        setLessons((await res.json()) as Lesson[]);
      } catch (e) {
        setLessonsError(e instanceof Error ? e.message : 'Unknown error');
      } finally {
        setLessonsLoading(false);
      }
    };

    fetchLessons();
  }, []);

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }, [token]);

  async function register() {
    setAuthMsg(null);
    const payload: AuthRegisterPayload = { username, password, role };
    const res = await fetch(`${API_BASE}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const txt = await res.text();
      throw new Error(txt || `HTTP ${res.status}`);
    }
    const data = (await res.json()) as TokenResponse;
    setToken(data.token);
    setAuthMsg('Registered + logged in.');
  }

  async function login() {
    setAuthMsg(null);
    const payload: AuthLoginPayload = { username, password };
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const txt = await res.text();
      throw new Error(txt || `HTTP ${res.status}`);
    }
    const data = (await res.json()) as TokenResponse;
    setToken(data.token);
    setAuthMsg('Logged in.');
  }

  async function loadCourses() {
    setCoursesLoading(true);
    setCoursesError(null);
    try {
      const res = await fetch(`${API_BASE}/api/courses`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      setCourses((await res.json()) as CourseSummary[]);
    } catch (e) {
      setCoursesError(e instanceof Error ? e.message : 'Unknown error');
    } finally {
      setCoursesLoading(false);
    }
  }

  async function loadCourseDetail(courseId: number) {
    setSelectedCourseLoading(true);
    setSelectedCourse(null);
    try {
      const res = await fetch(`${API_BASE}/api/courses/${courseId}`);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      setSelectedCourse((await res.json()) as CourseDetail);
    } catch (e) {
      setCoursesError(e instanceof Error ? e.message : 'Unknown error');
    } finally {
      setSelectedCourseLoading(false);
    }
  }

  async function loadMyEnrollments() {
    if (!token) return;
    setEnrollmentsLoading(true);
    try {
      const res = await fetch(`${API_BASE}/api/courses/me/enrollments`, {
        headers: { Authorization: authHeader },
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      setEnrollments((await res.json()) as EnrollmentStatus[]);
    } catch (e) {
      setCoursesError(e instanceof Error ? e.message : 'Unknown error');
    } finally {
      setEnrollmentsLoading(false);
    }
  }

  async function enroll(courseId: number) {
    if (!token) return;
    const res = await fetch(`${API_BASE}/api/courses/${courseId}/enroll`, {
      method: 'POST',
      headers: { Authorization: authHeader },
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    await loadMyEnrollments();
    await loadRoadmap(courseId);
  }

  async function completeModule(courseId: number, moduleId: number) {
    if (!token) return;
    const res = await fetch(
      `${API_BASE}/api/courses/${courseId}/modules/${moduleId}/complete`,
      {
        method: 'POST',
        headers: { Authorization: authHeader },
      }
    );
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    await loadRoadmap(courseId);
  }

  async function loadRoadmap(courseId: number | '') {
    if (!token || !courseId) return;
    setRoadmapLoading(true);
    setRoadmap(null);
    try {
      const res = await fetch(`${API_BASE}/api/courses/${courseId}/roadmap`, {
        headers: { Authorization: authHeader },
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      setRoadmap((await res.json()) as Roadmap);
    } catch (e) {
      setCoursesError(e instanceof Error ? e.message : 'Unknown error');
    } finally {
      setRoadmapLoading(false);
    }
  }

  async function loadQuiz(courseId: number) {
    if (!token) return;
    setQuizLoading(true);
    setQuiz(null);
    setQuizResult(null);
    setQuizAnswers({});
    try {
      const res = await fetch(`${API_BASE}/api/courses/${courseId}/quiz`, {
        headers: { Authorization: authHeader },
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      setQuiz((await res.json()) as Quiz);
    } catch (e) {
      setCoursesError(e instanceof Error ? e.message : 'Unknown error');
    } finally {
      setQuizLoading(false);
    }
  }

  function submitQuiz(courseId: number) {
    if (!token || !quiz) return;

    const answers: QuizSubmitRequest['answers'] = quiz.questions.map((q) => ({
      questionId: q.questionId,
      selectedOptionIndex: quizAnswers[q.questionId] ?? 0,
    }));

    const payload: QuizSubmitRequest = { answers };

    return fetch(`${API_BASE}/api/courses/${courseId}/quiz/submit`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: authHeader,
      },
      body: JSON.stringify(payload),
    }).then(async (res) => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      setQuizResult((await res.json()) as QuizResult);
      await loadRoadmap(courseId);
    });
  }

  async function doLoadEverything() {
    await loadCourses();
    await loadMyEnrollments();
  }

  return (
    <div style={{ maxWidth: 1000, margin: '20px auto', fontFamily: 'Arial, sans-serif' }}>
      <h1>Finchtalk Smart Learning</h1>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
        <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8 }}>
          <h2>Lessons (public)</h2>
          {lessonsLoading && <p>Loading lessons...</p>}
          {lessonsError && <p style={{ color: 'crimson' }}>Failed: {lessonsError}</p>}
          {!lessonsLoading && !lessonsError && (
            <ul>
              {lessons.map((l) => (
                <li key={l.id} style={{ marginBottom: 12 }}>
                  <strong>{l.title}</strong>
                  <div style={{ color: '#444' }}>{l.description}</div>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8 }}>
          <h2>Auth</h2>
          <div style={{ display: 'grid', gap: 8 }}>
            <label>
              Username
              <input value={username} onChange={(e) => setUsername(e.target.value)} style={{ width: '100%' }} />
            </label>
            <label>
              Password
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} style={{ width: '100%' }} />
            </label>
            <label>
              Role (register only)
              <input value={role} onChange={(e) => setRole(e.target.value)} style={{ width: '100%' }} />
            </label>

            <div style={{ display: 'flex', gap: 8 }}>
              <button
                onClick={async () => {
                  try {
                    await register();
                  } catch (e) {
                    setAuthMsg(e instanceof Error ? e.message : 'Register failed');
                  }
                }}
              >
                Register
              </button>
              <button
                onClick={async () => {
                  try {
                    await login();
                  } catch (e) {
                    setAuthMsg(e instanceof Error ? e.message : 'Login failed');
                  }
                }}
              >
                Login
              </button>
              <button
                disabled={!token}
                onClick={() => {
                  setToken('');
                  setAuthMsg('Logged out.');
                }}
              >
                Logout
              </button>
            </div>

            {authMsg && <p>{authMsg}</p>}
            {token && <p style={{ fontSize: 12, wordBreak: 'break-all' }}>Token set ✅</p>}
          </div>

          <hr style={{ margin: '16px 0' }} />

          <button
            disabled={!token}
            onClick={async () => {
              try {
                setCoursesError(null);
                await doLoadEverything();
              } catch (e) {
                setCoursesError(e instanceof Error ? e.message : 'Failed');
              }
            }}
          >
            Load Courses + My Enrollments
          </button>

          {coursesError && <p style={{ color: 'crimson' }}>{coursesError}</p>}
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginTop: 16 }}>
        <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8 }}>
          <h2>Courses</h2>
          <button onClick={loadCourses} disabled={coursesLoading}>
            {coursesLoading ? 'Loading...' : 'Refresh courses'}
          </button>
          {coursesError && <p style={{ color: 'crimson' }}>{coursesError}</p>}
          <ul>
            {courses.map((c) => (
              <li key={c.courseId} style={{ marginBottom: 12 }}>
                <button
                  onClick={() => {
                    setSelectedCourseId(c.courseId);
                    loadCourseDetail(c.courseId);
                    loadRoadmap(c.courseId);
                    loadQuiz(c.courseId);
                  }}
                >
                  {c.icon} {c.title}
                </button>
              </li>
            ))}
          </ul>
        </div>

        <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8 }}>
          <h2>Course Dashboard</h2>

          <label>
            Selected courseId
            <input
              value={selectedCourseId}
              onChange={(e) => setSelectedCourseId(e.target.value ? Number(e.target.value) : '')}
              style={{ width: '100%' }}
              placeholder="Select a course"
            />
          </label>

          <div style={{ display: 'flex', gap: 8, marginTop: 8, flexWrap: 'wrap' }}>
            <button disabled={!token || !selectedCourseId} onClick={() => loadRoadmap(selectedCourseId)}>
              Load roadmap
            </button>
            <button disabled={!token || !selectedCourseId} onClick={() => enroll(selectedCourseId as number)}>
              Enroll
            </button>
            <button disabled={!token || !selectedCourseId} onClick={() => loadQuiz(selectedCourseId as number)}>
              Load quiz
            </button>
          </div>

          {selectedCourseLoading && <p>Loading details...</p>}
          {selectedCourse && (
            <div style={{ marginTop: 12 }}>
              <h3>{selectedCourse.icon} {selectedCourse.title}</h3>
              <p>{selectedCourse.description}</p>
              <p style={{ fontSize: 12, color: '#555' }}>Modules: {selectedCourse.modules.length}</p>
            </div>
          )}

          <hr style={{ margin: '16px 0' }} />

          <h3>My Enrollments</h3>
          <button disabled={!token} onClick={loadMyEnrollments}>
            {enrollmentsLoading ? 'Loading...' : 'Refresh my enrollments'}
          </button>
          <ul>
            {enrollments.map((e) => (
              <li key={e.courseId} style={{ marginBottom: 8 }}>
                <strong>{e.courseTitle}</strong> — {e.percent}% (modules {e.completedModules}/{e.totalModules})
                {e.quizAttempted ? ' — quiz attempted' : ''}
              </li>
            ))}
          </ul>

          <hr style={{ margin: '16px 0' }} />

          <h3>Roadmap</h3>
          {roadmapLoading && <p>Loading roadmap...</p>}
          {!roadmapLoading && roadmap && (
            <div>
              <p>
                {roadmap.courseTitle}: {roadmap.completedCount}/{roadmap.totalCount} complete
              </p>
              <ul>
                {roadmap.modules.map((m) => (
                  <li key={m.moduleId} style={{ marginBottom: 8 }}>
                    {m.completed ? '✅' : '⬜️'} {m.title}
                    <div>
                      <button disabled={!token || roadmapLoading || m.completed} onClick={() => completeModule(roadmap.courseId, m.moduleId)}>
                        Mark complete
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      </div>

      <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8, marginTop: 16 }}>
        <h2>Quiz</h2>
        {quizLoading && <p>Loading quiz...</p>}
        {quiz && (
          <div>
            <ul>
              {quiz.questions.map((q) => (
                <li key={q.questionId} style={{ marginBottom: 16 }}>
                  <strong>{q.prompt}</strong>
                  <div>
                    {q.options.map((opt, idx) => (
                      <label key={opt} style={{ display: 'block' }}>
                        <input
                          type="radio"
                          name={`q-${q.questionId}`}
                          checked={(quizAnswers[q.questionId] ?? 0) === idx}
                          onChange={() => setQuizAnswers((prev) => ({ ...prev, [q.questionId]: idx }))}
                        />
                        {opt}
                      </label>
                    ))}
                  </div>
                </li>
              ))}
            </ul>

            <button
              disabled={!token || !selectedCourseId}
              onClick={() => submitQuiz(selectedCourseId as number)}
            >
              Submit quiz
            </button>

            {quizResult && (
              <p>
                Score: {quizResult.quizScore}/{quizResult.totalQuestions}
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}


