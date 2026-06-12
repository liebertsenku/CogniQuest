# CogniQuest 🧠

Welcome to **CogniQuest**, an innovative Android application designed to elevate the learning experience through interactive and engaging quiz sessions. CogniQuest blends modern design principles with intelligent backend systems to provide a comprehensive learning management platform tailored for both students and administrators.

<p align="center">
  <img src="app/src/main/res/mipmap-xxhdpi/ic_launcher.webp" alt="CogniQuest Logo" width="150"/>
</p>

---

## 🌟 Overview

CogniQuest is not just another quiz app. It is a carefully crafted learning ecosystem that allows users to test their knowledge across various subjects (such as Mathematics, Science, Social Studies, English, and Civics). The application features a secure user authentication system, detailed progress tracking, intuitive dashboards, and is powered by state-of-the-art AI integration (Gemini API) to provide intelligent assistance.

Whether you are a student looking to review your lessons or an admin managing educational content, CogniQuest provides a seamless, robust, and interactive experience.

## ✨ Key Features

- **Robust User Authentication:** Secure login and registration flows, ensuring user data privacy.
- **Interactive Dashboard:** A beautifully designed central hub providing quick access to all application features.
- **Dynamic Quiz System:** Engaging quiz sessions organized by subject, with real-time scoring and feedback.
- **Comprehensive Tracking:** Detailed quiz history and performance analytics to track learning progress over time.
- **Intelligent AI Assistant:** Integrated with the Google Gemini API to provide smart hints, explanations, and learning support.
- **Admin Panel (Phase 2):** Dedicated administrative tools for content management, including adding, updating, and deleting quizzes and questions.
- **Modern UI/UX:** Built with a focus on aesthetics and usability, featuring custom iconography and responsive layouts.

## 🛠 Tech Stack

CogniQuest leverages modern Android development practices and technologies:

- **Platform:** Android (Min SDK 24, Target SDK 35)
- **Language:** Java / Kotlin (Mixed implementation)
- **Build System:** Gradle
- **Architecture & UI:** ViewBinding, Navigation Component
- **Networking:** Retrofit, OkHttp, Gson
- **Database:** SQLite (Custom `DatabaseHelper` with structured migrations)
- **AI Integration:** Google Gemini API

## 📋 Prerequisites

To build and run this project, you will need:

- **Android Studio:** Latest stable version is highly recommended.
- **Java Development Kit (JDK):** JDK 11 or higher.
- **Android SDK:** Version 35.

## 🚀 Installation and Setup

Follow these steps to get the project up and running on your local machine:

1. **Clone the Repository:**
   ```bash
   git clone <repository-url>
   ```

2. **Open the Project:**
   Launch Android Studio, select `File > Open`, and navigate to the cloned `CogniQuest` directory. Allow Gradle to sync the project dependencies.

3. **Configure API Keys:**
   CogniQuest uses the Gemini API. You must provide your own API key.
   Create a `local.properties` file in the root directory of the project (if it doesn't exist) and add the following line:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

4. **Build and Run:**
   Once the project has synced successfully, select your target device (Emulator or Physical Device) and click the **Run** button (`Shift + F10`).

## 📂 Project Structure

- `ui/auth/`: Contains components related to user onboarding (Login and Registration).
- `ui/dashboard/`: Houses the main Home Dashboard activity.
- `ui/quiz/`: Manages the interactive Quiz Sessions and Quiz History.
- `ui/profile/`: Handles Profile Statistics, settings, and Edit Profile functionality.
- `ui/admin/`: Encompasses the Admin Dashboard, Manage Quizzes, and Manage Questions screens.
- `database/`: Contains the SQLite database helper, schema definitions, and dummy data seeders.

## 🤝 Contribution Guidelines

We welcome contributions! If you'd like to improve CogniQuest, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bugfix (`git checkout -b feature/your-feature-name`).
3. Commit your changes (`git commit -m 'feat: Add some amazing feature'`).
4. Push to the branch (`git push origin feature/your-feature-name`).
5. Open a Pull Request.

## 📄 License

This project is licensed under the [MIT License](LICENSE). Feel free to use, modify, and distribute as per the terms of the license.

---
*Built with ❤️ by the CogniQuest Team.*
