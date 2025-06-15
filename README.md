# 💪 Fitly - Personal Fitness Planner

> _Your personal home fitness coach, always in your pocket!_

---

## 📱 Project Description

**Fitly** is a mobile application that generates a fully personalized workout plan based on:

- Age
- Gender
- Height
- Weight
- Fitness goals
- Selected muscle groups
- Training difficulty level

The app automatically builds your training plan with:

- Individual exercise sets
- Dynamic difficulty adjustments
- Daily training cycle management
- Rest days detection & control
- Data persistence using `SharedPreferences`
- Daily reminders via notifications

---

## Screenshots

![About You](screenshots/Screenshot_20250615_223805.png)
![Fitnes Goals](screenshots/screenshots/Screenshot_20250615_223818.png
![Workout Plan ](screenshots/Screenshot_20250615_223828.png)
![Notification](screenshots/Screenshot_20250615_223838.png)
![Today Workout](screenshots/Screenshot_20250615_223848.png)
![Settings](screenshots/Screenshot_20250615_223904.png)

---

## ⚙️ Tech Stack

- 🖥 **Android Studio (Kotlin + Jetpack Compose)**
- 🧭 **Navigation Compose (for screens navigation)**
- 🔀 **MVVM architecture**
- 🗃 **SharedPreferences (local data storage)**
- 🔔 **WorkManager (background notifications)**
- 📦 **Gson (data serialization)**
- 🔄 **StateFlow + Compose Lifecycle management**

---

## 📂 Project Structure

- `view/` — UI components and screens
- `viewmodel/` — ViewModel business logic layer
- `model/` — Data classes (User, Exercises, Generator, etc.)
- `navigation/` — Screens navigation management
- `worker/` — Background worker for notifications
- `sharedpreferences/` — Serialization for persistent local data

---

## 🔥 Main Features

- Smart workout plan generator based on personal data.
- Dynamic difficulty adjustment for each training.
- Full user data saving and loading via SharedPreferences.
- Daily workout reminders (notifications even after device restarts).
- Prevents double workouts in the same day.
- Automatically rotates between selected muscle groups.
- English\Ukrainian language supported.
- Clean, simple and minimalistic user interface.

---

## 🌟 Future Features (Planned)

- 📊 **Training History:** activity tracking and calendar.
- 🏆 **Achievements:** motivational rewards system.
- 🤖 **AI-powered adaptation:** auto-adjust plan based on progress.
- 🔔 **More flexible reminders:** multiple daily notifications.
- ☁️ **Cloud sync:** Firebase/Supabase integration.
- 📈 **Analytics:** progress charts, statistics and insights.
- 👥 **Multi-user profiles:** separate profiles for family or friends.

---

## 🙏 Special Thanks

**Author:** _Artem (who built the full project from scratch 💪🔥)_

---

⭐ If you like the project — feel free to ⭐ the repository!
