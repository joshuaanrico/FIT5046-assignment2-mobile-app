# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**SmartFit Fuel** — a student fitness/wellness Android app (FIT5046 Assignment 2). Package: `com.example.ass2`. The UI reference (`UI-reference.png` in the repo root) shows the target screens: a Register screen and a Login screen with email/password fields, password strength indicator, and Google Sign-in.

## Build & Run Commands

All commands run from the repo root (single-module project):

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.ass2.ExampleUnitTest"

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean assembleDebug
```

## Configuration

- **compileSdk / targetSdk**: 36 (Android 15, minor API level 1)
- **minSdk**: 24 (Android 7.0)
- **Java compatibility**: VERSION_11
- **Kotlin**: 2.2.10, **AGP**: 9.1.1
- **Compose BOM**: `2024.09.00`
- Dependencies managed via `gradle/libs.versions.toml`

## Architecture

Single-activity Compose app. `MainActivity` calls `setContent { Ass2Theme { Scaffold { ... } } }` with `enableEdgeToEdge()`. All UI lives under `app/src/main/java/com/example/ass2/`. Theme files (Color, Theme, Type) are in `ui/theme/`.

The project is in its initial scaffold state — screens, navigation, ViewModels, and data layers are yet to be built out. When adding features follow standard MVVM: screen composables → ViewModel (with `StateFlow`) → Repository → data source.
