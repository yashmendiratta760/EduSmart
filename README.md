# EduSmart – AI-Powered Education Management Platform

EduSmart is an education management application that combines an Android frontend, backend services, and AI-powered features in one platform. It is designed to support academic workflows and make learning assistance more accessible.

## Features

- **Native Android Frontend:** Android application for user interaction with the platform.
- **Backend APIs:** Server-side logic and API endpoints.
- **Teacher and Student APIs:** Separate API functionality for teachers and students.
- **AI Features:** AI-related functionality integrated into the application.
- **Groq Integration:** The repository history indicates that the AI integration was changed to Groq.
- **Rate Limiting:** Backend request rate-limiting functionality.
- **Redis Caching:** Caching support in the backend.

> Note: This feature list is based on the repository folders and visible commit messages. Keep only the features that are implemented in the current codebase.

## Project Structure

```text
EduSmart/
├── .idea/          # IDE configuration
├── .kotlin/        # Kotlin-related project files
├── AI_Features/    # AI features and integrations
├── backend/        # Backend APIs and server-side logic
└── frontend/       # Android frontend application
```

## Architecture

- **Android Frontend (`frontend/`):** The Android application and user interface.
- **Backend (`backend/`):** Server-side functionality, APIs, and application logic.
- **AI Features (`AI_Features/`):** AI-related functionality and integrations.

The repository's visible commit history also mentions separate teacher/student APIs, rate limiting, Redis caching, and a change to Groq. Refer to the source files and configuration in each directory for the exact frameworks, dependencies, and implementation details.

## Getting Started

### 1. Clone the repository

Replace `https://github.com/yashmendiratta760/EduSmart` with your GitHub repository URL.

```bash
git clone https://github.com/yashmendiratta760/EduSmart
cd EduSmart
```

### 2. Run the Android application

1. Open the project in Android Studio.
2. Allow Gradle to sync and download the required dependencies.
3. Configure the Android SDK and an emulator or connected Android device.
4. Build and run the `frontend` Android application using the configuration included in the repository.

The exact Gradle commands and module names depend on the project's current configuration.

### 3. Configure the backend and AI features

Follow the dependency and startup instructions in `backend/` and `AI_Features/`. Configure any required database, Redis instance, or AI provider credentials according to the project's configuration files.

- Do not commit API keys, passwords, tokens, or other secrets.
- Keep local environment files out of version control.
- Use the environment variable names expected by the code.

## Development Notes

- The Android frontend communicates with backend services through the APIs implemented by the project.
- Ensure the backend is reachable from the Android emulator or device. For a local emulator, the host address may differ from `localhost`.
- Configure any required AI provider and supporting services before testing AI functionality.

## Future Improvements

Possible improvements, depending on the current implementation, include:

- Android app screenshots and a demo video.
- Detailed setup instructions for the backend and AI services.
- API documentation with example requests.
- Automated tests and CI workflows.
- Deployment instructions.

## Author

**Yash Mendiratta**
