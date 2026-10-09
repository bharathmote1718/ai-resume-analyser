import { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, NavLink } from 'react-router-dom';
import './App.css';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
const USER_STORAGE_KEY = 'ai-resume-analyzer-user';

function readStoredUser() {
  try {
    const storedUser = localStorage.getItem(USER_STORAGE_KEY);
    return storedUser ? JSON.parse(storedUser) : null;
  } catch {
    return null;
  }
}

function App() {
  const [currentUser, setCurrentUser] = useState(readStoredUser);
  const [authMode, setAuthMode] = useState(null);
  const [analysis, setAnalysis] = useState({
    score: 82,
    skills: ['Java', 'Spring Boot', 'SQL', 'React', 'Git', 'REST API'],
    missingSkills: ['Docker', 'AWS', 'Microservices'],
    strengths: ['Strong Java and backend development skillset', 'Project examples are relevant and clear', 'Education is clearly listed'],
    weaknesses: ['More measurable impact would improve credibility', 'Cloud skills can be expanded', 'Professional summary can be stronger'],
    suggestions: [
      'Add measurable achievements to project descriptions.',
      'Include Spring Boot and SQL more prominently in your summary.',
      'Add relevant software engineering certifications and cloud exposure.',
    ],
    keywords: ['Java', 'Spring Boot', 'REST API', 'SQL', 'Git'],
    sectionAnalysis: [
      { section: 'Education', status: '✓', details: 'Bachelor degree included and clear.' },
      { section: 'Skills', status: '✓', details: 'Core technical skills detected.' },
      { section: 'Projects', status: '✓', details: 'Project work is documented well.' },
      { section: 'Experience', status: '⚠', details: 'Metrics and leadership impact can be expanded.' },
      { section: 'Certifications', status: '⚠', details: 'Add training or certifications for broader credibility.' },
      { section: 'Summary', status: '✓', details: 'Summary is professional and relevant.' },
    ],
  });
  const [history, setHistory] = useState([]);
  const [selectedResumeId, setSelectedResumeId] = useState(null);

  useEffect(() => {
    if (!currentUser) {
      return;
    }

    const loadHistory = async () => {
      try {
        const response = await fetch(`${API_BASE}/resumes?userId=${currentUser.userId}`);
        if (!response.ok) {
          throw new Error(`Could not load resume history (${response.status}).`);
        }
        const list = await response.json();
        if (Array.isArray(list)) {
          setHistory(list);
          setSelectedResumeId(list[0]?.id ?? null);
        }
      } catch (error) {
        console.error('History load failed', error);
      }
    };
    loadHistory();
  }, [currentUser]);

  const handleAuthSuccess = (user) => {
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user));
    setCurrentUser(user);
    setAuthMode(null);
  };

  const handleLogout = () => {
    localStorage.removeItem(USER_STORAGE_KEY);
    setCurrentUser(null);
    setHistory([]);
    setSelectedResumeId(null);
  };

  return (
    <Router>
      <div className="app-shell">
        <header className="topbar">
          <div className="brand-wrap">
            <div className="brand-mark">AI</div>
            <div>
              <div className="brand-name">AI Resume Analyzer</div>
              <small>Career growth dashboard</small>
            </div>
          </div>
          <nav className="nav">
            <NavLink to="/" end>Home</NavLink>
            <NavLink to="/dashboard">Dashboard</NavLink>
            <NavLink to="/job-match">Job Match</NavLink>
            <NavLink to="/history">History</NavLink>
          </nav>
          <div className="auth-buttons">
            {currentUser ? (
              <>
                <span className="user-greeting">Hi, {currentUser.name}</span>
                <button className="secondary" onClick={handleLogout}>Log out</button>
              </>
            ) : (
              <>
                <button className="secondary" onClick={() => setAuthMode('login')}>Login</button>
                <button className="primary" onClick={() => setAuthMode('register')}>Get Started</button>
              </>
            )}
          </div>
        </header>

        <Routes>
          <Route path="/" element={<HomePage onGetStarted={() => setAuthMode('register')} />} />
          <Route path="/dashboard" element={<DashboardPage currentUser={currentUser} analysis={analysis} setAnalysis={setAnalysis} setHistory={setHistory} setSelectedResumeId={setSelectedResumeId} onLogin={() => setAuthMode('login')} />} />
          <Route path="/job-match" element={<JobMatchPage selectedResumeId={selectedResumeId} />} />
          <Route path="/history" element={<HistoryPage history={history} setSelectedResumeId={setSelectedResumeId} />} />
        </Routes>
        {authMode && <AuthDialog mode={authMode} onClose={() => setAuthMode(null)} onSuccess={handleAuthSuccess} />}
      </div>
    </Router>
  );
}

function HomePage({ onGetStarted }) {
  return (
    <main className="home-page">
      <section className="hero-section">
        <div className="hero-copy">
          <span className="eyebrow"><span className="eyebrow-dot" /> A smarter way to move forward</span>
          <h1>Make your next application <span>your strongest one.</span></h1>
          <p>
            Get a clearer picture of what is working, what is missing, and how your resume
            stacks up against the roles you really want.
          </p>
          <div className="cta-row">
            <Link className="primary large" to="/dashboard">Analyze my resume <span aria-hidden="true">↗</span></Link>
            <button className="secondary large" onClick={onGetStarted}>Create free account</button>
          </div>
          <div className="hero-note"><span className="hero-note-check">✓</span> PDF, DOCX and TXT files supported</div>
          <div className="stats-row">
            <div><strong>10k+</strong><span>Resumes analyzed</span></div>
            <div><strong>92%</strong><span>Avg. user satisfaction</span></div>
            <div><strong>3x</strong><span>Better ATS alignment</span></div>
          </div>
        </div>

        <div className="hero-visual">
          <div className="visual-orbit visual-orbit-one" />
          <div className="visual-orbit visual-orbit-two" />
          <div className="hero-card">
            <div className="preview-header">
              <div><span className="preview-kicker">RESUME SNAPSHOT</span><strong>Your profile, at a glance</strong></div>
              <span className="preview-menu" aria-hidden="true">···</span>
            </div>
            <div className="score-summary">
              <div className="score-ring"><span>82<small>/100</small></span></div>
              <div className="score-caption"><strong>Looking good</strong><span>Your resume is on the right track.</span><div className="score-track"><span /></div></div>
            </div>
            <div className="mini-card">
              <div className="mini-card-heading"><strong>Top matching skills</strong><span>3 found</span></div>
              <ul>
                <li><span className="skill-check">✓</span> Java <span className="skill-level">Strong</span></li>
                <li><span className="skill-check">✓</span> Spring Boot <span className="skill-level">Strong</span></li>
                <li><span className="skill-check">✓</span> SQL <span className="skill-level">Good</span></li>
              </ul>
            </div>
            <div className="preview-footer"><span className="preview-status-dot" /> Analysis ready <span>Just now</span></div>
          </div>
          <div className="floating-note"><span>✳</span><div><strong>Stand out with confidence</strong><small>One clear next step at a time</small></div></div>
        </div>
      </section>

      <section className="features-section">
        <h2>Why students and job seekers choose AI Resume Analyzer</h2>
        <div className="feature-grid">
          {[
            'Resume parsing and section extraction',
            'AI-based skill and keyword matching',
            'Job description compatibility scoring',
            'Actionable improvement suggestions',
            'Resume history and previous analysis',
            'Professional dashboard for job seekers',
          ].map((feature) => (
            <div className="feature-card" key={feature}>{feature}</div>
          ))}
        </div>
      </section>

      <section className="process-section">
        <h2>How it works</h2>
        <div className="step-grid">
          {[
            { title: 'Upload Resume', text: 'Add your PDF, DOCX, or TXT file and let the system extract key details.' },
            { title: 'Analyze Content', text: 'The app inspects your education, skills, projects, experience, and summary.' },
            { title: 'View score & suggestions', text: 'Check your resume score, job match percentage, and improvement tips.' },
          ].map((step, index) => (
            <div className="step-card" key={step.title}>
              <span className="step-badge">0{index + 1}</span>
              <h3>{step.title}</h3>
              <p>{step.text}</p>
            </div>
          ))}
        </div>
      </section>

      <section className="benefits-section">
        <h2>Benefits</h2>
        <div className="benefit-list">
          {[
            'Improve your resume before interviews',
            'Identify missing skills for target roles',
            'Tailor your application to real job descriptions',
            'Gain a competitive edge with data-driven feedback',
          ].map((benefit) => (
            <div key={benefit} className="benefit-item">✓ {benefit}</div>
          ))}
        </div>
      </section>

      <footer className="footer">
        <p>© 2026 AI Resume Analyzer. Built for career growth.</p>
      </footer>
    </main>
  );
}

function AuthDialog({ mode, onClose, onSuccess }) {
  const [isRegistering, setIsRegistering] = useState(mode === 'register');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const submitAuth = async (event) => {
    event.preventDefault();
    setErrorMessage('');
    if (isRegistering && password !== confirmPassword) {
      setErrorMessage('Passwords do not match.');
      return;
    }

    setIsSubmitting(true);
    try {
      const endpoint = isRegistering ? 'register' : 'login';
      const payload = isRegistering
        ? { name, email, password, confirmPassword }
        : { email, password };
      const response = await fetch(`${API_BASE}/auth/${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      const result = await response.json();
      if (!response.ok) {
        throw new Error(result.message || 'Authentication failed.');
      }
      onSuccess(result);
    } catch (error) {
      setErrorMessage(error.message || 'Could not connect to the backend.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-backdrop" role="presentation" onClick={onClose}>
      <section className="auth-modal panel" role="dialog" aria-modal="true" aria-labelledby="auth-title" onClick={(event) => event.stopPropagation()}>
        <button className="modal-close secondary" type="button" onClick={onClose} aria-label="Close">×</button>
        <h2 id="auth-title">{isRegistering ? 'Create your account' : 'Welcome back'}</h2>
        <form onSubmit={submitAuth}>
          {isRegistering && (
            <>
              <label htmlFor="auth-name">Full name</label>
              <input id="auth-name" autoComplete="name" value={name} onChange={(event) => setName(event.target.value)} required />
            </>
          )}
          <label htmlFor="auth-email">Email</label>
          <input id="auth-email" type="email" autoComplete="email" value={email} onChange={(event) => setEmail(event.target.value)} required />
          <label htmlFor="auth-password">Password</label>
          <input id="auth-password" type="password" autoComplete={isRegistering ? 'new-password' : 'current-password'} minLength={6} value={password} onChange={(event) => setPassword(event.target.value)} required />
          {isRegistering && (
            <>
              <label htmlFor="auth-confirm-password">Confirm password</label>
              <input id="auth-confirm-password" type="password" autoComplete="new-password" minLength={6} value={confirmPassword} onChange={(event) => setConfirmPassword(event.target.value)} required />
            </>
          )}
          {errorMessage && <p className="auth-error" role="alert">{errorMessage}</p>}
          <button className="primary auth-submit" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Please wait…' : isRegistering ? 'Create account' : 'Log in'}
          </button>
        </form>
        <button className="auth-switch" type="button" onClick={() => { setIsRegistering(!isRegistering); setErrorMessage(''); }}>
          {isRegistering ? 'Already registered? Log in' : 'New here? Create an account'}
        </button>
      </section>
    </div>
  );
}

function DashboardPage({ currentUser, analysis, setAnalysis, setHistory, setSelectedResumeId, onLogin }) {
  const [resumeFile, setResumeFile] = useState(null);
  const [jobTitle, setJobTitle] = useState('Full Stack Java Developer');
  const [jobDescription, setJobDescription] = useState('We are looking for a Full Stack Java Developer with experience in Java, Spring Boot, SQL, REST API, Git, and agile development.');
  const [isLoading, setIsLoading] = useState(false);
  const [statusText, setStatusText] = useState('');

  const handleUpload = async () => {
    if (!currentUser) {
      setStatusText('Log in or create an account before uploading a resume.');
      onLogin();
      return;
    }
    if (!resumeFile) {
      setStatusText('Please select a resume file first.');
      return;
    }
    setIsLoading(true);
    setStatusText('Uploading and analyzing resume...');

    const formData = new FormData();
    formData.append('file', resumeFile);
    formData.append('userId', String(currentUser.userId));
    formData.append('jobTitle', jobTitle);
    formData.append('jobDescription', jobDescription);

    try {
      const uploadRes = await fetch(`${API_BASE}/resumes/upload`, {
        method: 'POST',
        body: formData,
      });
      const uploaded = await uploadRes.json();
      if (!uploadRes.ok) {
        throw new Error(uploaded.message || 'Resume upload failed.');
      }
      const resumeId = uploaded.id;
      setSelectedResumeId(resumeId);

      const analysisRes = await fetch(`${API_BASE}/analysis/${resumeId}?jobDescription=${encodeURIComponent(jobDescription)}`, {
        method: 'POST',
      });
      const analysisData = await analysisRes.json();
      if (!analysisRes.ok) {
        throw new Error(analysisData.message || 'Resume analysis failed.');
      }
      setAnalysis({
        score: analysisData.score ?? 82,
        skills: analysisData.skills ?? [],
        missingSkills: analysisData.missingSkills ?? [],
        strengths: analysisData.strengths ?? [],
        weaknesses: analysisData.weaknesses ?? [],
        suggestions: analysisData.suggestions ?? [],
        keywords: analysisData.keywords ?? [],
        sectionAnalysis: analysisData.sectionAnalysis ?? [],
      });

      setStatusText('Resume analyzed successfully.');
      const historyRes = await fetch(`${API_BASE}/resumes?userId=${currentUser.userId}`);
      if (historyRes.ok) {
        const list = await historyRes.json();
        if (Array.isArray(list)) {
          setHistory(list);
        }
      }
      setTimeout(() => setStatusText(''), 2500);
    } catch (error) {
      console.error('Upload failed', error);
      setStatusText(error.message || 'Upload or analysis failed.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="dashboard-page container">
      <section className="page-header">
        <div>
          <span className="eyebrow">Dashboard</span>
          <h2>Welcome back{currentUser ? `, ${currentUser.name}` : ''}</h2>
        </div>
        <button className="primary" onClick={handleUpload} disabled={isLoading}>
          {isLoading ? 'Analyzing...' : 'Upload Resume'}
        </button>
      </section>

      <div className="upload-panel panel">
        <div className="form-grid two-col">
          <div>
            <label>Resume file</label>
            <input type="file" accept=".pdf,.docx,.txt" onChange={(e) => setResumeFile(e.target.files[0])} />
          </div>
          <div>
            <label>Target job title</label>
            <input value={jobTitle} onChange={(e) => setJobTitle(e.target.value)} />
          </div>
        </div>
        <label>Job description</label>
        <textarea rows="7" value={jobDescription} onChange={(e) => setJobDescription(e.target.value)} />
        {statusText && <div className="status-box">{statusText}</div>}
      </div>

      <div className="summary-grid">
        <div className="summary-card accent">
          <span>Resume Score</span>
          <strong>{analysis.score ?? 0}/100</strong>
        </div>
        <div className="summary-card">
          <span>Skills Found</span>
          <strong>{analysis.skills?.length ?? 0}</strong>
        </div>
        <div className="summary-card">
          <span>Missing Skills</span>
          <strong>{analysis.missingSkills?.length ?? 0}</strong>
        </div>
        <div className="summary-card">
          <span>Job Match %</span>
          <strong>78%</strong>
        </div>
        <div className="summary-card">
          <span>Suggestions</span>
          <strong>{analysis.suggestions?.length ?? 0}</strong>
        </div>
      </div>

      <div className="analysis-layout">
        <div className="panel">
          <h3>Section Analysis</h3>
          <div className="section-list">
            {(analysis.sectionAnalysis ?? []).map((item) => (
              <div key={item.section} className="section-item">
                <div className="section-status">{item.status}</div>
                <div>
                  <strong>{item.section}</strong>
                  <p>{item.details}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="panel">
          <h3>Detected Skills</h3>
          <div className="badge-row">
            {(analysis.skills ?? []).map((skill) => (
              <span className="skill-badge" key={skill}>{skill}</span>
            ))}
          </div>
          <h3 className="subhead">Missing Skills</h3>
          <div className="badge-row muted">
            {(analysis.missingSkills ?? []).map((skill) => (
              <span className="skill-badge secondary" key={skill}>{skill}</span>
            ))}
          </div>
        </div>
      </div>

      <div className="analysis-layout two-col">
        <div className="panel">
          <h3>Strengths</h3>
          <ul className="list">
            {(analysis.strengths ?? []).map((item) => <li key={item}>{item}</li>)}
          </ul>
        </div>

        <div className="panel">
          <h3>Weaknesses</h3>
          <ul className="list">
            {(analysis.weaknesses ?? []).map((item) => <li key={item}>{item}</li>)}
          </ul>
        </div>
      </div>

      <div className="panel suggestions-panel">
        <h3>Suggestions</h3>
        <ul className="list">
          {(analysis.suggestions ?? []).map((item) => <li key={item}>{item}</li>)}
        </ul>
      </div>
    </main>
  );
}

function JobMatchPage({ selectedResumeId }) {
  const [jobTitle, setJobTitle] = useState('Full Stack Java Developer');
  const [jobDescription, setJobDescription] = useState('We are looking for a Full Stack Java Developer with experience in Java, Spring Boot, SQL, REST API, Git, and agile development.');
  const [matchData, setMatchData] = useState({
    matchScore: 78,
    matchingSkills: ['Java', 'Spring Boot', 'SQL', 'Git', 'REST API'],
    missingSkills: ['Docker', 'AWS', 'Microservices', 'JUnit'],
    keywords: ['Java', 'Spring Boot', 'SQL', 'REST API', 'Git'],
    recommendations: ['Add AWS and Docker experience to the resume.', 'Improve the summary with measurable backend achievements.'],
  });
  const [errorMessage, setErrorMessage] = useState('');
  const [isAnalyzing, setIsAnalyzing] = useState(false);

  const analyzeMatch = async () => {
    if (!selectedResumeId) {
      setErrorMessage('Upload and analyze a resume from the Dashboard first.');
      return;
    }
    setIsAnalyzing(true);
    setErrorMessage('');
    try {
      const response = await fetch(`${API_BASE}/job-match/analyze`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ resumeId: selectedResumeId, jobTitle, jobDescription }),
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Job match analysis failed.');
      }
      setMatchData(data);
    } catch (error) {
      console.error('Job match analysis failed', error);
      setErrorMessage(error.message || 'Could not connect to the backend.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  return (
    <main className="container job-match-page">
      <h2>Job Match Analysis</h2>
      <div className="job-form-grid">
        <div className="panel">
          <label>Job Title</label>
          <input value={jobTitle} onChange={(e) => setJobTitle(e.target.value)} />
          <label>Job Description</label>
          <textarea rows="12" value={jobDescription} onChange={(e) => setJobDescription(e.target.value)} />
          <button className="primary" onClick={analyzeMatch} disabled={isAnalyzing}>
            {isAnalyzing ? 'Analyzing…' : 'Analyze Job Match'}
          </button>
          {errorMessage && <p className="auth-error" role="alert">{errorMessage}</p>}
        </div>
        <div className="panel metrics-panel">
          <div className="score-box">
            <span>Job Match</span>
            <strong>{matchData.matchScore ?? 0}%</strong>
          </div>
          <div className="metric-block">
            <h4>Matching Skills</h4>
            <div className="badge-row">
              {(matchData.matchingSkills ?? []).map((skill) => (
                <span className="skill-badge" key={skill}>{skill}</span>
              ))}
            </div>
          </div>
          <div className="metric-block">
            <h4>Missing Skills</h4>
            <div className="badge-row muted">
              {(matchData.missingSkills ?? []).map((skill) => (
                <span className="skill-badge secondary" key={skill}>{skill}</span>
              ))}
            </div>
          </div>
          <div className="metric-block">
            <h4>Relevant Keywords</h4>
            <div className="badge-row">
              {(matchData.keywords ?? []).map((skill) => (
                <span className="skill-badge" key={skill}>{skill}</span>
              ))}
            </div>
          </div>
          <div className="metric-block">
            <h4>Recommendations</h4>
            <ul className="list">
              {(matchData.recommendations ?? []).map((item) => <li key={item}>{item}</li>)}
            </ul>
          </div>
        </div>
      </div>
    </main>
  );
}

function HistoryPage({ history, setSelectedResumeId }) {
  const rows = history.length ? history.map((item) => ({
    name: item.fileName,
    date: new Date(item.uploadedAt).toLocaleDateString(),
    score: item.score == null ? '—' : `${item.score}/100`,
    match: item.jobMatchScore == null ? '—' : `${item.jobMatchScore}%`,
    status: 'Reviewed',
    id: item.id,
  })) : [
    { name: 'Sanjana_Resume.pdf', date: '2026-09-15', score: '82/100', match: '78%', status: 'Reviewed', id: 1 },
    { name: 'Backend_Engineer.pdf', date: '2026-08-28', score: '76/100', match: '74%', status: 'Reviewed', id: 2 },
    { name: 'Resume_v3.docx', date: '2026-07-30', score: '88/100', match: '82%', status: 'Improved', id: 3 },
  ];

  return (
    <main className="container history-page">
      <h2>Resume History</h2>
      <table>
        <thead>
          <tr>
            <th>Resume Name</th>
            <th>Upload Date</th>
            <th>Resume Score</th>
            <th>Job Match Score</th>
            <th>Status</th>
            <th>View Analysis</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={row.id ?? row.name}>
              <td>{row.name}</td>
              <td>{row.date}</td>
              <td>{row.score}</td>
              <td>{row.match}</td>
              <td>{row.status}</td>
              <td>
                <button className="secondary small" onClick={() => setSelectedResumeId(row.id)}>Open</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </main>
  );
}

export default App;
