# Heritage Online Modern Android

This is the clean, modern Android shell for the next version of E迹.

The legacy app in the repository root remains untouched as a functional reference. This project is intentionally independent so the new client can move to current Android tooling without dragging the old Gradle setup along.

## Current Baseline

- Gradle 9.5.1
- Android Gradle Plugin 9.2.1
- Kotlin 2.3.21
- Compose BOM 2026.05.00
- Material 3
- Navigation 3 dependencies prepared for the next slice
- Ktor Client 3.4.3
- kotlinx.serialization 1.11.0
- Flow/StateFlow-first architecture planned

## Run

```bash
cd modern-android
./gradlew :app:assembleDebug
```

## API Contract Layer

The first network contract layer is in place:

- DTOs: `app/src/main/java/com/duckylife/heritage/modern/core/network/dto`
- API client: `app/src/main/java/com/duckylife/heritage/modern/core/network/HeritageApiClient.kt`
- Repository boundary: `app/src/main/java/com/duckylife/heritage/modern/core/data/HeritageRepository.kt`
- Local development base URL: `https://10.0.2.2:5078`

The contract maps the local Swagger document:

- `/api/home-banners`
- `/api/articles`
- `/api/directory-items`
- `/api/inheritors`

## Verify

```bash
cd modern-android
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

## Next Slice

The first visible product slice has started:

1. Home banner data loading
2. Article list ViewModel with loading/error/empty states
3. Compose article list screen

Next up:

1. Article detail screen
2. Navigation 3 back stack wiring
3. Basic pagination for article list
