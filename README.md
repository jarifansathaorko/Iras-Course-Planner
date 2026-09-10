<div align="center">
  
  <!-- Add your Einstein logo or a custom banner image here -->
  <img src="app/src/main/res/drawable/ic_iras_logo.png" alt="Iras Course Planner Logo" width="120" />

  # 🎓 Iras Course Planner

  **A blazingly fast, beautifully designed university schedule builder for Android.** <br>
  *Say goodbye to overlapping classes and manual timetable drawing.*

  <!-- Badges -->
  <p>
    <img src="https://img.shields.io/badge/Kotlin-1.9+-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=android&logoColor=white" alt="Jetpack Compose" />
    <img src="https://img.shields.io/badge/Architecture-MVVM-00C4B6" alt="Architecture" />
    <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Android" />
  </p>

</div>

---

## ⚡ Why Iras Course Planner?

Building a university schedule shouldn't be a puzzle. Iras Course Planner uses a highly optimized **bitmask conflict detection engine** to instantly prevent schedule clashes as you add courses. Build, compare, and export visually clean, clash-free weekly timetables directly to your camera roll.

<div align="center">
  <table>
    <tr>
      <td align="center"><b>🛡️ Bulletproof Scheduling</b><br>Instant conflict detection prevents you from adding overlapping classes.</td>
      <td align="center"><b>🔄 Seamless Swapping</b><br>Switch between lab, tutorial, and lecture sections with a single tap.</td>
      <td align="center"><b>🖼️ High-Res Export</b><br>Generate beautiful PNG timetables to set as your lock screen or share with friends.</td>
    </tr>
  </table>
</div>

---

## 📸 Sneak Peek

*Replace these placeholder image paths with your actual screenshots in the `.github/assets` or `public` folder.*

<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src="https://via.placeholder.com/250x500.png?text=Course+Catalog" width="220"/>
        <br /><b>Smart Catalog</b>
      </td>
      <td align="center">
        <img src="https://via.placeholder.com/250x500.png?text=Weekly+Grid" width="220"/>
        <br /><b>Weekly Grid</b>
      </td>
      <td align="center">
        <img src="https://via.placeholder.com/250x500.png?text=Exported+PNG" width="220"/>
        <br /><b>Exported Schedule</b>
      </td>
    </tr>
  </table>
</div>

---

## ✨ Features at a Glance

* **Real-time Conflict Engine:** Uses binary bitmasking against 15-minute time blocks for zero-latency clash detection.
* **Draft & Compare:** Create multiple schedule drafts, save them locally, and compare which semester plan works best.
* **Material Design 3:** Fluid animations, responsive layouts, and dynamic theming.
* **Offline First:** All data and saved plans are stored locally on your device. No internet required to plan your semester.

---

## 🛠️ Tech Stack & Architecture

Built with modern Android development practices to ensure performance and scalability:

* **[Kotlin](https://kotlinlang.org/):** 100% Kotlin codebase.
* **[Jetpack Compose](https://developer.android.com/jetpack/compose):** Declarative UI for smooth, responsive interfaces.
* **[Room Database](https://developer.android.com/training/data-storage/room):** Robust SQLite abstraction for local data persistence.
* **[Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html):** Asynchronous programming and reactive data streams.
* **Architecture:** Strict adherence to **Clean Architecture** and **MVVM** principles.

---
