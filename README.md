# CogniQuest

CogniQuest is an Android application designed to provide interactive quiz sessions and learning experiences. It features comprehensive modules for user authentication, taking quizzes, tracking history, managing profiles, and an administrative dashboard for managing content. The application is also integrated with the Gemini API for advanced capabilities.

## Features

- **User Authentication:** Secure login and registration functionality.
- **Dashboard:** A central hub for users to navigate the application.
- **Quiz System:** Interactive quiz sessions and detailed history tracking.
- **User Profiles:** Profile management and statistics viewing.
- **Admin Panel:** Administrative tools to manage quizzes, questions, and view the admin dashboard (Phase 2).
- **AI Integration:** Powered by the Gemini API for enhanced learning and interactions.

## Tech Stack

- **Platform:** Android (Min SDK 24, Target SDK 35)
- **Language:** Kotlin
- **Build System:** Gradle (Kotlin DSL)
- **Architecture & UI:** ViewBinding, Navigation Component
- **Networking:** Retrofit, OkHttp, Gson
- **AI Integration:** Gemini API

## Prerequisites

- Android Studio (Latest Version Recommended)
- JDK 11
- Android SDK 35

## Installation and Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   ```

2. **Open the project:**
   Open Android Studio and select `File > Open`, then navigate to the cloned `CogniQuest` directory.

3. **Configure API Keys:**
   Create a `local.properties` file in the root directory of the project if it does not exist, and add your Gemini API Key:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```

4. **Build and Run:**
   Sync the project with Gradle files and run the application on an emulator or physical device.

## Project Structure

- `ui/auth/`: Contains Login and Registration activities.
- `ui/dashboard/`: Contains the main Home Dashboard activity.
- `ui/quiz/`: Contains Quiz Session and Quiz History activities.
- `ui/profile/`: Contains Profile Statistics and Edit Profile activities.
- `ui/admin/`: Contains Admin Dashboard, Manage Quizzes, and Manage Questions activities.

## License

[Specify License Here]