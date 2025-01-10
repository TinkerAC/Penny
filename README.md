# Penny

---

![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-blueviolet)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7.0-orange)
![Android Gradle Plugin](https://img.shields.io/badge/AGP-8.2.2-brightgreen)
![SQLDelight](https://img.shields.io/badge/SQLDelight-2.0.2-yellow)
![Ktor](https://img.shields.io/badge/Ktor-3.0.0-blue)
![Koin](https://img.shields.io/badge/Koin-3.5.0-brightgreen)

A modern, AI-powered financial management application built for the next generation of users. Penny
helps you track expenses, analyze spending patterns, and make smarter financial decisions with ease.

[//]: # (![App Screenshot]&#40;screenshot/) //todo: add screenshot

## Features

### 🌩️ Cloud Sync & Storage

- Seamlessly synchronize your financial data across all your devices
- Secure cloud storage ensures your data is always backed up and accessible
- Real-time synchronization keeps your records up-to-date everywhere

### ✨ AI-Powered Automation

- Automatic transaction categorization using advanced AI technology
- Smart chatbot assistant for financial advice and queries
- Automated monthly report generation with insights and recommendations
- Natural language processing for quick and easy expense entry

### 🎨 Customizable Themes

- Modern and youth-oriented design philosophy
- Extensive theme customization options to match your style
- Personalized dashboard layouts and widgets
- Dark/Light mode support with custom color schemes

### 📊 Data Visualization

- Interactive spending trend analysis
- Category-wise expense distribution through intuitive pie charts
- Custom report generation with exportable charts

---

## Getting Started

### Prerequisites

- **Operating Systems**:
    - Windows 11
    - macOS Sonoma (Sequoia)
- **IDE**:
    - Android Studio 2024.2.1 (Ladybug)
    - Xcode 16.1
        - Package Manager: CocoaPods 1.16.2

> Note: The application should work in similar development environments. This information is
> provided as a reference to ensure optimal compatibility.

## Installation

1. Run Git clone

```
git clone https://www.github.com/TinkerAC/penny.git
```

2. Open the project in Android Studio and wait for the Gradle sync to complete

### 🏗️ Building the Application And Run

<details>
<summary> 🖥️ Desktop Application</summary>

```bash
# Using Gradle
./gradlew :ComposeApp:run
# Or using Android Studio
Run the `Penny Desktop` configuration at "Run Configurations"
```

</details>

<details>
<summary>📱 Android Application</summary>

1. Select the `Penny Android` configuration
2. Choose your target device/emulator
3. Click the "Run" button or press `Shift + F10`

</details>

<details>
<summary>🍎iOS Application </summary>

### Android Studio

1. edit the configuration `Penny IOS` to match your environment(Simulator or Device)
2. Click the "Run" button or press `Shift + F10`

### Xcode

1. open `iosApp/iosApp.xcworkspace` in Xcode
2. set your team in the `Signing & Capabilities` tab
3. Select your target device/simulator
4. Click the "Run" button or press `⌘ + R`

> Note: To run the iOS app on a physical device, you need to have a valid Apple Developer account
> and a provisioning profile set up in Xcode,
> and you need to trust the developer certificate on the device.

</details>

## Tech Stack

---

### Core

- **[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)**: Cross-platform
  development
- **[Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)**: Asynchronous
  programming

### Data Management

- **[SQLDelight](https://sqldelight.github.io/sqldelight)**: SQL database for storing financial data
    - Local persistence
    - Offline-first capability
- **[Ktor](https://ktor.io/)**: HTTP client for network requests
- **[Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)**: JSON serialization

### Dependency Injection

- **[Koin](https://insert-koin.io/)**: Lightweight DI framework

### Resources Management

- **[Moko Resources](https://github.com/icerockdev/moko-resources)**: Shared resources across
  platforms

### Presentation & UI

- **[Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)**: UI framework for
  all platforms
- **[Voyager](https://voyager.adriel.cafe/)**: Navigation library

## Modules & Directory Structure

---

### :shared

Contains the shared codebase for the application, including the data model, business logic, and UI
build with Compose Multiplatform. 

### :composeApp

depended on :shared module,

Contains the entry point for supported platform ,include Desktop and Android.

### :server

depended on :shared module,

Contains the server-side codebase for the application, including the API routes ,database setup and
AI feature implementation.

### Dir `iosApp`

Contains the Xcode project for the iOS application.

### Dir `shared/server_shared`

Contains the shared codebase for the server-side application, including the data model and
request/response encapsulation.

## License

---

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Organiks1_KMP_Server](https://github.com/samAricha/Organiks1_KMP_Server):
  One of the winner project
  Of [Kotlin Multiplatform Contest 2024](https://kotlinconf.com/2024/contest/)
  by [Aricha Samson](https://github.com/samAricha).


- [AAY-chart](https://github.com/TheChance101/AAY-chart)  
  A library contains several chart composables for usage in Kotlin Multiplatform projects and
  Android Native, developed by [TheChance101](https://github.com/TheChance101).  
  Copyright (c) 2023 The Chance  
  The code under `/shared/src/commonMain/kotlin/app/penny/presentation/ui/components/aayChart`
  contains modified versions of AAY-chart code, used under the MIT License.

  


