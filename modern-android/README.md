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
- Flow/StateFlow-first architecture planned

## Run

```bash
cd modern-android
./gradlew :app:assembleDebug
```

## Next Slice

The next step is to add the API contract layer from the local Swagger document:

- `https://localhost:5078/swagger/v1/swagger.json`
- `/api/home-banners`
- `/api/articles`
- `/api/directory-items`
- `/api/inheritors`
