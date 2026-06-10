# UTC2 App Reborn

**UTC2 App Reborn** is a mobile application dedicated to students of the University of Transport and Communications, Campus in Ho Chi Minh City (UTC2). The application helps students easily track their academic information, pay tuition fees, register for dormitories, use public services, and receive the latest notifications from the university.

**Related repositories:**
- [UTC2_Web_Server](https://github.com/Piu-Empire/UTC2_Web_Server) — Backend API & Database
- [UTC2_Web_Admin](https://github.com/Piu-Empire/UTC2_Web_Admin) — Web Admin Dashboard

---

## Screenshots

| Login | Home |
| :---: | :---: |
| ![Login](docs/images/login.png) | ![Home](docs/images/home.png) |

| Student Profile | Tuition |
| :---: | :---: |
| ![Profile](docs/images/profile.png) | ![Tuition](docs/images/tuition.png) |

---

## Demo Video

**Watch here:** https://youtu.be/your-video-id

*(Or replace the above link with a Demo GIF file here)*
<!-- ![Demo](docs/demo.gif) -->

---

## Key Features

The application provides a comprehensive ecosystem with the following functional modules:
1. **Home & News:** Update the latest news and events from the university. Receive push notifications via Firebase.
2. **Schedule:** Track detailed class and exam schedules, supporting grid/canvas display and schedule exporting.
3. **Academic Results:** Look up exam scores and view detailed academic assessments. Integrated tracking for **Academic Warnings**, **Scholarships**, and **Student Leaderboards**.
4. **Assessment:** Support conducting individual training point assessments for each semester.
5. **Tuition:** View and track the detailed payment status of subject tuition fees, dormitory fees, and electronic invoices.
6. **Course Registration:** Support students in registering for courses and selecting classes directly on the app.
7. **Public Services:** Quickly register for administrative procedures such as:
   - Student ID card re-issuance
   - Student loan support
   - Transcript registration
   - Student confirmation letter request
8. **Dormitory:** Manage accommodation information, register for rooms, and track dormitory fees.
9. **AI Chat Assistant:** Integrated with a Semantic Search AI chat tool to help students look up information and get automated answers via the Floating AI Button.
10. **Profile & Settings:** Manage personal information, view training programs, graduation requirements, change passwords, and customize the application.
11. **Comprehensive Search:** Quick search function for all information within the application system.

---

## Tech Stack

- **Language:** Java / Kotlin
- **Architecture:** MVVM (Model-View-ViewModel) combined with Repository Pattern
- **Network:** Retrofit 2, OkHttp3 (with Logging Interceptor), Custom ApiClient
- **Push Notification:** Firebase Cloud Messaging (FCM)
- **Local Storage:** Room Database, SharedPreferences
- **UI:** XML, Custom Views, Material Design Components (MDC), ViewBinding / DataBinding
- **Extended Features:** Integrated automated AI Chat API.

---

## Installation

### For Users (Install APK)
You can directly download the APK installation file to your Android phone:
1. Go to the [Releases](https://github.com/Piu-Empire/UTC2_App_Reborn/releases) page on Github (or use the download link provided by the university).
2. Download the latest `app-release.apk` file.
3. Allow **Install unknown apps** on your phone if prompted.
4. Install the downloaded APK file and start using the app.

### For Developers (Build from Source Code)
1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/Piu-Empire/UTC2_App_Reborn.git
   ```
2. Open the project using **Android Studio**.
3. Sync the project with Gradle files.
4. Run the application on an Emulator or a physical device (Requires Android API 24+).

---

## Architecture

The project is built following the **MVVM (Model-View-ViewModel)** architecture, with clearly divided modules for easy expansion and maintenance.

```text
com.utc2.appreborn
├── base                (Shared BaseActivity, BaseFragment classes)
├── data                (Data Management: API, Database, Repository)
│   ├── local           (Local storage - SharedPreferences, Room DB)
│   ├── remote          (Retrofit API interfaces, DTOs, Network configuration)
│   └── repository      (Repository Pattern - Communication between Data and ViewModel)
├── di                  (Dependency Injection if available)
├── model               (Entity classes - Data classes)
├── network             (Network state handling, Interceptor, Custom ApiClient)
├── service             (Background services, e.g., FcmMessagingService)
├── ui                  (UI Interface & Control Logic - Separated into multiple modules)
│   ├── aichat          (AI Chat Assistant)
│   ├── assessment      (Training point assessment)
│   ├── courseregistration (Course registration)
│   ├── dormitory       (Dormitory)
│   ├── results         (Academic results, Warnings, Leaderboard, Scholarships)
│   ├── schedule        (Class schedules)
│   ├── public_services (Online public services)
│   └── ...             (Other modules: news, tuition, profile, login, home, search...)
└── utils               (Support classes: Constants, Extensions, Helpers)
```

### Connection between Mobile App, Server Backend, and Web Admin

The overall system is designed following a Client-Server model, comprising 3 main components working closely together:

1. **Mobile App (Client):** Uses `Retrofit` to send HTTP Requests to the **RESTful API** system (such as `ApiService`, `AiChatApiService`) to fetch and display data for students (Schedules, grades, tuition...). It also receives real-time notifications (Push Notifications) via **Firebase (FCM)**.
2. **[Web Admin](https://github.com/Piu-Empire/UTC2_Web_Admin):** A web-based administration dashboard for university staff. It supports adding/editing/deleting announcements, approving/rejecting public services, and updating grades and tuition fees. The Web Admin calls APIs to connect directly to the central Backend system.
3. **[Server Backend](https://github.com/Piu-Empire/UTC2_Web_Server) & Database:**
   - Acts as the central data storage and handles complex business logic.
   - Provides all API Endpoints for both the Mobile App and the Web Admin.
   - When the Web Admin triggers a state change (e.g., a staff member approves a form or posts an urgent announcement), the Backend updates the DB and immediately calls the Firebase API to trigger a Push Notification to the student's device running the Mobile App.
