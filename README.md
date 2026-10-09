# AI Resume Analyzer

AI Resume Analyzer is a full-stack Java + React project designed for students and job seekers to upload resumes, extract content, analyze skills, compare against a job description, and receive improvement suggestions.

## Features

- Professional landing page
- User registration and login flow
- Resume upload for PDF, DOCX, and TXT files
- Text extraction with PDFBox and Apache POI
- Resume scoring and section analysis
- Skills and missing-skill detection
- Job description comparison
- Resume history dashboard
- Local rule-based AI fallback when no external API key is configured

## Tech stack

Frontend
- React
- Vite
- CSS3

Backend
- Java
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL-ready config
- H2 dev profile for local testing

## Project structure

- `backend/` — Spring Boot application
- `ai-resume-analyzer-frontend/` — React frontend

## Prerequisites

- Java 17+
- Maven
- Node.js 18+
- Optional: MySQL if you want to use the production DB profile

## Run the backend

```bash
cd backend
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

The app will run on:
- http://localhost:8080

Optional MySQL configuration can be enabled by editing `backend/src/main/resources/application.properties` and setting the datasource details.

## Run the frontend

```bash
cd ai-resume-analyzer-frontend
cp .env.example .env
npm install
npm run dev
```

The app will run on:
- http://localhost:5173

## API examples

Register user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"secret123","confirmPassword":"secret123"}'
```

Upload resume

```bash
curl -X POST http://localhost:8080/api/resumes/upload \
  -F "userId=1" \
  -F "jobTitle=Java Developer" \
  -F "jobDescription=Java Spring Boot SQL REST API" \
  -F "file=@sample_resume.txt"
```

Analyze resume

```bash
curl -X POST "http://localhost:8080/api/analysis/1?jobDescription=Java%20Spring%20Boot%20SQL%20REST%20API%20Git"
```

## Notes

- The app uses a rule-based fallback analysis so it can work without an external AI key.
- Place your own API key in an environment variable if you later connect a real AI provider.

## Demo status

This project is structured as a complete Java Full Stack college-level resume analysis application and is ready for extension or submission.
