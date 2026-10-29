# CHANGE360 — Android Architecture & Implementation Plan

- **Application Name:** CHANGE360
- **Package Name:** `org.change360.app`
- **Tech Stack:** Kotlin 2.0+, Jetpack Compose (Material 3), Room (KSP), Lifecycle & ViewModel (UDF/MVVM), Navigation Compose (Type-Safe), Coroutines & Flow
- **Minimum SDK:** API 26 (Android 8.0) | **Target / Compile SDK:** API 35 (Android 15)

---

## 1. Complete Directory Structure Tree

```text
change360/
├── build.gradle.kts                          # Root build configuration (plugins declaration)
├── settings.gradle.kts                       # Repositories & module inclusions (:app)
├── gradle.properties                         # JVM args, AndroidX, non-transitive R class flags
├── PLAN.md                                   # Architecture & specification document
├── gradle/
│   ├── libs.versions.toml                    # Centralized Gradle Version Catalog
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── app/
    ├── build.gradle.kts                      # App module config, Compose, Room KSP, dependencies
    ├── proguard-rules.pro                    # R8/ProGuard shrinking & serialization rules
    ├── schemas/                              # Auto-exported Room database schemas (JSON)
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml           # App permissions, FileProvider (Poster export), MainActivity
        │   ├── java/org/change360/app/
        │   │   ├── Change360Application.kt   # Application class initializing AppContainer
        │   │   ├── MainActivity.kt           # Single-Activity host with enableEdgeToEdge()
        │   │   │
        │   │   ├── di/
        │   │   │   ├── AppContainer.kt       # Manual DI container / factory provider
        │   │   │   └── ViewModelFactory.kt   # ViewModelProvider.Factory helpers
        │   │   │
        │   │   ├── data/
        │   │   │   ├── local/
        │   │   │   │   ├── Change360Database.kt
        │   │   │   │   ├── Converters.kt     # Room TypeConverters (Instant, List, Enums)
        │   │   │   │   ├── SeedDatabaseCallback.kt # Pre-populates initial Challenges & Templates
        │   │   │   │   ├── datastore/
        │   │   │   │   │   └── UserPreferencesDataSource.kt
        │   │   │   │   ├── dao/
        │   │   │   │   │   ├── UserDao.kt
        │   │   │   │   │   ├── ChallengeDao.kt
        │   │   │   │   │   ├── ImpactLogDao.kt
        │   │   │   │   │   ├── ChatMessageDao.kt
        │   │   │   │   │   └── PosterDao.kt
        │   │   │   │   └── entity/
        │   │   │   │       ├── UserEntity.kt
        │   │   │   │       ├── ChallengeEntity.kt
        │   │   │   │       ├── ImpactLogEntity.kt
        │   │   │   │       ├── ChatMessageEntity.kt
        │   │   │   │       └── PosterEntity.kt
        │   │   │   ├── mapper/
        │   │   │   │   ├── UserMapper.kt
        │   │   │   │   ├── ChallengeMapper.kt
        │   │   │   │   ├── ImpactMapper.kt
        │   │   │   │   ├── ChatMapper.kt
        │   │   │   │   └── PosterMapper.kt
        │   │   │   └── repository/
        │   │   │       ├── AuthRepositoryImpl.kt
        │   │   │       ├── ChallengeRepositoryImpl.kt
        │   │   │       ├── ImpactRepositoryImpl.kt
        │   │   │       ├── AiAssistantRepositoryImpl.kt
        │   │   │       └── PosterRepositoryImpl.kt
        │   │   │
        │   │   ├── domain/
        │   │   │   ├── model/
        │   │   │   │   ├── User.kt
        │   │   │   │   ├── Challenge.kt
        │   │   │   │   ├── ChallengeCategory.kt
        │   │   │   │   ├── ImpactSummary.kt
        │   │   │   │   ├── ImpactLog.kt
        │   │   │   │   ├── ChatMessage.kt
        │   │   │   │   └── PosterTemplate.kt
        │   │   │   ├── repository/
        │   │   │   │   ├── AuthRepository.kt
        │   │   │   │   ├── ChallengeRepository.kt
        │   │   │   │   ├── ImpactRepository.kt
        │   │   │   │   ├── AiAssistantRepository.kt
        │   │   │   │   └── PosterRepository.kt
        │   │   │   └── usecase/
        │   │   │       ├── ObserveAuthStateUseCase.kt
        │   │   │       ├── AuthenticateUserUseCase.kt
        │   │   │       ├── GetDashboardOverviewUseCase.kt
        │   │   │       ├── CompleteChallengeUseCase.kt
        │   │   │       ├── CalculateImpactStatsUseCase.kt
        │   │   │       ├── SendAiMessageUseCase.kt
        │   │   │       └── SaveGeneratedPosterUseCase.kt
        │   │   │
        │   │   └── ui/
        │   │       ├── theme/
        │   │       │   ├── Color.kt          # Bio-luminescent Aura & Material 3 color tokens
        │   │       │   ├── Type.kt           # Material 3 Typography scale
        │   │       │   ├── Shape.kt          # Rounded corner tokens
        │   │       │   └── Theme.kt          # Change360Theme composable
        │   │       ├── components/
        │   │       │   ├── AuraPulseCanvas.kt
        │   │       │   ├── Change360TopBar.kt
        │   │       │   ├── Change360BottomBar.kt
        │   │       │   ├── GlassmorphicCard.kt
        │   │       │   ├── ImpactMetricRing.kt
        │   │       │   └── ChallengeCard.kt
        │   │       ├── navigation/
        │   │       │   ├── AppDestination.kt # Type-safe route definitions (@Serializable)
        │   │       │   ├── RootNavHost.kt    # Splash -> Auth -> Main transition graph
        │   │       │   └── MainScaffold.kt   # BottomBar + Nested NavHost for 5 core tabs
        │   │       ├── screens/
        │   │       │   ├── splash/
        │   │       │   │   ├── SplashScreen.kt
        │   │       │   │   └── SplashViewModel.kt
        │   │       │   ├── auth/
        │   │       │   │   ├── AuthScreen.kt
        │   │       │   │   ├── AuthUiState.kt
        │   │       │   │   └── AuthViewModel.kt
        │   │       │   ├── home/
        │   │       │   │   ├── HomeScreen.kt
        │   │       │   │   ├── HomeUiState.kt
        │   │       │   │   └── HomeViewModel.kt
        │   │       │   ├── ai/
        │   │       │   │   ├── AiAssistantScreen.kt
        │   │       │   │   ├── AiAssistantUiState.kt
        │   │       │   │   └── AiAssistantViewModel.kt
        │   │       │   ├── challenges/
        │   │       │   │   ├── ChallengesScreen.kt
        │   │       │   │   ├── ChallengesUiState.kt
        │   │       │   │   └── ChallengesViewModel.kt
        │   │       │   ├── impact/
        │   │       │   │   ├── ImpactScreen.kt
        │   │       │   │   ├── ImpactUiState.kt
        │   │       │   │   └── ImpactViewModel.kt
        │   │       │   └── poster/
        │   │       │       ├── PosterGeneratorScreen.kt
        │   │       │       ├── PosterGeneratorUiState.kt
        │   │       │       └── PosterGeneratorViewModel.kt
        │   │       └── util/
        │   │           ├── PosterBitmapExporter.kt
        │   │           └── UiText.kt
        │   └── res/
        │       ├── drawable/                 # Vector icons, emblem assets
        │       ├── values/
        │       │   ├── strings.xml
        │       │   └── themes.xml
        │       └── xml/
        │           └── file_paths.xml        # FileProvider paths for sharing generated posters
        ├── test/java/org/change360/app/      # Unit tests (ViewModels, UseCases, Calculators)
        └── androidTest/java/org/change360/app/ # Room DAO & Compose UI instrumentation tests
```

---

## 2. `gradle/libs.versions.toml` Dependency Block

Below is the complete, production-ready version catalog covering Kotlin, KSP, Jetpack Compose (Material 3), Room, Navigation, Lifecycle, DataStore, and Serialization:

```toml
[versions]
agp = "8.7.2"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.27"
coreKtx = "1.15.0"
lifecycle = "2.8.7"
activityCompose = "1.9.3"
composeBom = "2024.11.00"
navigationCompose = "2.8.4"
room = "2.6.1"
datastore = "1.1.1"
coroutines = "1.9.0"
kotlinxSerializationJson = "1.7.3"
coil = "2.7.0"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"

[libraries]
# Core AndroidX
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

# Jetpack Compose & Material 3
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }

# Lifecycle & ViewModel (MVVM)
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }

# Navigation Compose (Type-Safe Routes)
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }

# Room Database (KSP)
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }

# DataStore & Coroutines
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }

# Image Loading
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
androidx-room = { id = "androidx.room", version.ref = "room" }
```

### `app/build.gradle.kts` Wiring Reference

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM & Material 3
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Lifecycle & MVVM
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navigation & Serialization
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Room + KSP
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore, Coroutines & Coil
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil.compose)
}
```

---

## 3. Core Architecture Modules Breakdown

CHANGE360 follows **Clean Architecture with MVVM and Unidirectional Data Flow (UDF)**. State flows strictly upward from the Data layer to the UI via Kotlin `Flow` / `StateFlow`, while user events flow downward as intentions to ViewModels and UseCases.

```
[ Compose UI Screens ]
   │  ▲
   │  │ StateFlow<UiState> & SharedFlow<UiEffect>
   ▼  │ (User Events / Intents)
[ ViewModels ]
   │  ▲
   │  │ Domain Models (Flow<T> / Result<T>)
   ▼  │
[ Domain Layer: UseCases & Repository Interfaces ]
   │  ▲
   │  │ Entity <-> Domain Mappers
   ▼  │
[ Data Layer: Repositories -> Room DAOs + DataStore ]
```

### 3.1 Data Layer (`org.change360.app.data`)

Serves as the **Offline-First Single Source of Truth (SSOT)**.

#### Room Database (`Change360Database`)
- **Version:** `1`, configured with `SeedDatabaseCallback` on first creation to populate curated eco-challenges, initial AI welcome prompts, and poster templates.
- **Entities:**
  1. `UserEntity` (`users` table):
     - `id: String (PK)`, `displayName: String`, `email: String`, `ecoPledge: String`, `auraLevel: Int`, `xpPoints: Int`, `currentStreakDays: Int`, `createdAtEpochMillis: Long`
  2. `ChallengeEntity` (`challenges` table):
     - `id: String (PK)`, `title: String`, `description: String`, `category: String` (`ENERGY`, `WATER`, `ZERO_WASTE`, `MOBILITY`, `COMMUNITY`), `difficulty: String`, `xpReward: Int`, `co2SavedKg: Float`, `waterSavedLiters: Float`, `wasteSavedKg: Float`, `totalSteps: Int`, `completedSteps: Int`, `isCompleted: Boolean`, `completedAtEpochMillis: Long?`
  3. `ImpactLogEntity` (`impact_logs` table):
     - `id: Long (PK, autoGenerate)`, `userId: String`, `sourceChallengeId: String?`, `actionTitle: String`, `co2SavedKg: Float`, `waterSavedLiters: Float`, `wasteSavedKg: Float`, `loggedAtEpochMillis: Long`
  4. `ChatMessageEntity` (`chat_messages` table):
     - `id: Long (PK, autoGenerate)`, `sender: String` (`USER` | `AI`), `content: String`, `suggestedActionCategory: String?`, `timestampEpochMillis: Long`
  5. `PosterEntity` (`posters` table):
     - `id: Long (PK, autoGenerate)`, `title: String`, `pledgeQuote: String`, `templateStyle: String`, `accentColorHex: String`, `exportedUriString: String?`, `createdAtEpochMillis: Long`
- **DAOs:**
  - `UserDao`: `observeActiveUser(): Flow<UserEntity?>`, `upsertUser(user: UserEntity)`, `incrementXpAndStreak(...)`
  - `ChallengeDao`: `observeAllChallenges(): Flow<List<ChallengeEntity>>`, `observeChallengesByCategory(cat: String): Flow<List<ChallengeEntity>>`, `updateProgress(...)`
  - `ImpactLogDao`: `observeAllLogs(): Flow<List<ImpactLogEntity>>`, `observeTotalCo2Saved(): Flow<Float>`, `insertImpactLog(...)`
  - `ChatMessageDao`: `observeConversation(): Flow<List<ChatMessageEntity>>`, `insertMessage(...)`, `clearHistory()`
  - `PosterDao`: `observeSavedPosters(): Flow<List<PosterEntity>>`, `insertPoster(...)`

#### Preferences DataSource (`UserPreferencesDataSource`)
- Backed by Jetpack `DataStore<Preferences>`.
- Stores lightweight session state: `active_user_id`, `is_authenticated`, `theme_aura_variant`, and `last_active_epoch_day`.

#### Repositories (`*RepositoryImpl`)
- `AuthRepositoryImpl`: Coordinates `UserDao` and `UserPreferencesDataSource` for login, registration, guest eco-pledge onboarding, and session observation.
- `ChallengeRepositoryImpl`: Exposes challenge streams and executes transactional challenge step updates alongside inserting an `ImpactLogEntity`.
- `ImpactRepositoryImpl`: Aggregates time-series metrics (7-day, 30-day, all-time) from `ImpactLogDao`.
- `AiAssistantRepositoryImpl`: Persists user prompts and generates contextual sustainability guidance (supporting local rule/knowledge engine + pluggable LLM client endpoint) into `ChatMessageDao`.
- `PosterRepositoryImpl`: Manages poster templates, user customizations, and saved poster metadata.

---

### 3.2 Domain Layer (`org.change360.app.domain`)

Pure Kotlin module layer with zero Android UI dependencies, ensuring 100% unit testability.

- **Domain Models (`domain/model`):**
  - `User`, `Challenge`, `ChallengeCategory`, `ImpactSummary` (includes calculated equivalents like *trees planted equivalent* and *car kilometers avoided*), `ImpactLog`, `ChatMessage`, `PosterTemplate`.
- **Repository Contracts (`domain/repository`):**
  - Clean interfaces returning `Flow<T>` for reactive queries and `suspend fun` returning `Result<T>` for mutations.
- **Core UseCases (`domain/usecase`):**
  - `ObserveAuthStateUseCase`: Combines `DataStore` auth flag and active `User` entity to emit `AuthState` (`Loading`, `Unauthenticated`, `Authenticated(user)`).
  - `CompleteChallengeUseCase`: Validates step completion, awards XP, updates `User` aura level (`XP / 500 + 1`), and writes an atomic `ImpactLog` entry.
  - `CalculateImpactStatsUseCase`: Transforms raw `ImpactLog` lists into `ImpactSummary` containing category breakdowns, weekly trend percentages, and real-world equivalents (e.g., `1 tree ≈ 21.77 kg CO2/year`).
  - `SendAiMessageUseCase`: Appends the user's message, analyzes active challenges/impact context, and streams or commits the AI Assistant's tailored response.

---

### 3.3 ViewModels (`org.change360.app.ui.screens.*`)

Each screen is backed by a dedicated `ViewModel` managing a single immutable `UiState` data class:

| ViewModel | State Exposed (`StateFlow`) | Key Responsibilities |
| :--- | :--- | :--- |
| `SplashViewModel` | `SplashUiState(isAuraReady, nextDestination)` | Runs a minimum 3,000ms Aura animation timer in parallel with `ObserveAuthStateUseCase` using `combine`, emitting target route (`Auth` or `Main`) once both complete. |
| `AuthViewModel` | `AuthUiState(mode, name, email, pledge, isLoading, error)` | Handles Sign In, Create Account, and Quick Eco-Pledge onboarding; validates inputs and persists session. |
| `HomeViewModel` | `HomeUiState(user, auraScore, activeChallenges, todayImpact, dailyTip)` | Combines active `User`, top 3 active challenges, and today's impact summary into a unified dashboard state. |
| `AiAssistantViewModel` | `AiAssistantUiState(messages, inputPrompt, isTyping, quickChips)` | Manages chat message stream, prompt chip selection, typing indicator state, and action shortcuts. |
| `ChallengesViewModel` | `ChallengesUiState(selectedCategory, challenges, completedCount)` | Filters challenges by category (`ALL`, `ENERGY`, `WATER`, `ZERO_WASTE`, `MOBILITY`), handles step increments & completion celebration events. |
| `ImpactViewModel` | `ImpactUiState(timeRange, summary, logs, milestones)` | Switches between `WEEK`, `MONTH`, and `ALL_TIME` views; computes chart data points and unlocked eco-badges. |
| `PosterGeneratorViewModel` | `PosterGeneratorUiState(selectedTemplate, headline, pledge, includeStatsBadge, savedPosters, isExporting)` | Controls live poster customization state, injects the user's verified impact stats into the poster badge, and triggers bitmap export/share intents. |

---

### 3.4 UI Layer (`org.change360.app.ui`)

- **Material 3 Design System (`ui/theme`):**
  - **Palette:** Deep Forest Obsidian background (`#081410`), Bio-Luminescent Emerald primary (`#00E676`), Solar Cyan secondary (`#00E5FF`), Warm Amber tertiary (`#FFD740`), with translucent surface containers (`SurfaceContainerHigh` with alpha gradients).
  - **Edge-to-Edge:** Enabled globally in `MainActivity` with proper `WindowInsets` consumption across top and bottom bars.
- **State Hoisting Pattern:**
  - Every destination defines a stateful `*Route(viewModel, ...)` composable that collects `viewModel.uiState.collectAsStateWithLifecycle()` and delegates rendering to a stateless `*Screen(uiState, onAction)` composable for effortless `@Preview` and UI testing.

---

## 4. App Flow Specifications

### High-Level Navigation Architecture

```
RootNavHost (startDestination = Route.Splash)
 ├── Route.Splash (3s Aura Animation + Session Check)
 │     ├─[Unauthenticated]──► Route.Auth (popUpTo Splash inclusive)
 │     └─[Authenticated]────► Route.Main (popUpTo Splash inclusive)
 │
 ├── Route.Auth (Login / Sign-Up / Eco-Pledge)
 │     └─[Success]──────────► Route.Main (popUpTo Auth inclusive)
 │
 └── Route.Main (Hosts MainScaffold + Material 3 NavigationBar)
       ├── Tab 1: MainRoute.Home
       ├── Tab 2: MainRoute.AiAssistant
       ├── Tab 3: MainRoute.Challenges
       ├── Tab 4: MainRoute.Impact
       └── Tab 5: MainRoute.PosterGenerator
```

---

### 4.1 Splash Screen (`SplashScreen.kt`) — 3-Second Aura Experience

1. **Visual Specification:**
   - Full-screen edge-to-edge dark canvas (`#06100C`).
   - **3s Aura Effect (`AuraPulseCanvas`):**
     - Uses Compose `Canvas` driven by `Animatable` and `rememberInfiniteTransition`.
     - Renders 3 concentric, breathing radial gradients (Emerald `#00E676`, Teal `#00BFA5`, Cyan `#00E5FF`) with `BlendMode.Screen` and dynamic radius scaling from `0.4f` to `1.15f` over 3,000ms.
     - Central CHANGE360 globe/orbit emblem animates in with `scaleIn(tween(1200, easing = FastOutSlowInEasing))` + `fadeIn(tween(1000))`.
     - Subtitle *"360° Action for a Regenerative Planet"* fades in at `t = 1,200ms`.
2. **Synchronization & Routing Logic (`SplashViewModel`):**
   - Launches a coroutine `delay(3000L)` concurrently with `observeAuthStateUseCase().first()`.
   - When the 3,000ms aura sequence completes:
     - If `isAuthenticated == true` -> navigates to `Route.Main` with `popUpTo<Route.Splash> { inclusive = true }`.
     - If `isAuthenticated == false` -> navigates to `Route.Auth` with `popUpTo<Route.Splash> { inclusive = true }`.

---

### 4.2 Auth Screen (`AuthScreen.kt`)

1. **User Modes:**
   - **Mode Toggle:** Segmented button switching between **Join Movement (Sign Up)** and **Sign In**, plus a one-tap **Quick Eco-Pledge (Guest Mode)** button for zero-friction onboarding.
2. **Fields & Validation:**
   - `DisplayName` (required for Sign Up / Pledge, min 2 chars).
   - `Email` (validated via email regex in Sign In / Sign Up modes).
   - `Personal Eco-Pledge` selector chip group (e.g., *"Cut single-use plastics"*, *"Plant-forward meals"*, *"Green daily commute"*, *"Zero-waste home"*).
3. **Persistence & Transition:**
   - Submitting creates or loads the `UserEntity` in Room, sets `is_authenticated = true` in `DataStore`, seeds starter impact logs if new, and navigates to `Route.Main` clearing `Route.Auth` from the back stack.

---

### 4.3 Main Navigation Graph (`MainScaffold.kt`)

Uses a single Material 3 `Scaffold` with a persistent `NavigationBar` containing 5 top-level destinations. Tab switching uses standard Compose Navigation state restoration:

```kotlin
navController.navigate(destination) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
```

#### Tab 1: Home (`HomeScreen.kt`)
- **Aura Status Header:** Displays user greeting, current streak badge (`🔥 X Days`), and an interactive **Aura Energy Ring** showing current Level & XP progress.
- **Today's Snapshot Row:** 3 compact glassmorphic metric pills (`kg CO₂ Saved`, `Liters Water`, `Kg Waste Diverted`).
- **Active Challenges Carousel:** Horizontal/vertical list of in-progress challenges with one-tap `+1 Step` quick-log buttons.
- **Quick Launch Cards:**
  - *"Ask Eco-AI Coach"* card (navigates to **AI Assistant** tab).
  - *"Create Advocacy Poster"* card (navigates to **Poster Generator** tab).

#### Tab 2: AI Assistant (`AiAssistantScreen.kt`)
- **Conversational UI:** `LazyColumn` with reverse-chronological/auto-scrolling chat bubbles styled distinctly for `USER` (Emerald surface) and `AI` (Translucent glass card with Aura icon).
- **Contextual Prompt Chips:** Horizontally scrollable row above the input bar:
  - *"Calculate my daily commute footprint"*
  - *"5-minute zero-waste swap"*
  - *"Explain carbon offsetting"*
  - *"Suggest a challenge for me"*
- **Smart Eco Engine:** Generates structured advice tailored to the user's active challenges and impact stats, persisted automatically in `ChatMessageDao` so chat history survives app restarts.

#### Tab 3: Challenges (`ChallengesScreen.kt`)
- **Category Filter Bar:** `ScrollableTabRow` / `FilterChip` bar for `All`, `Energy ⚡`, `Water 💧`, `Zero-Waste ♻️`, `Mobility 🚲`, and `Community 🌱`.
- **Challenge Cards:**
  - Displays title, description, difficulty tag, environmental reward (`-2.5 kg CO₂`, `+150 XP`), and linear step progress bar (`completedSteps / totalSteps`).
  - **"Log Step" / "Complete" Action:** Triggers `CompleteChallengeUseCase`, animating progress and emitting a celebratory Snackbar + updating Room `ImpactLogEntity` in real time.

#### Tab 4: Impact (`ImpactScreen.kt`)
- **Time-Range Selector:** `SegmentedButtonRow` for `This Week`, `This Month`, and `All Time`.
- **Custom Compose Charts:**
  - **CO₂ & Resource Savings Bar/Area Chart** drawn with Compose `Canvas`.
  - **Category Breakdown Donut Chart** showing proportion of impact across Energy, Water, Waste, and Mobility.
- **Real-World Equivalencies Grid:**
  - 🌳 **Trees Equivalent:** `totalCo2Kg / 21.77f`
  - 🚗 **Car Km Avoided:** `totalCo2Kg * 4.1f`
  - 🍼 **Plastic Bottles Saved:** `(totalWasteKg * 40).toInt()`
- **Recent Activity Log:** Chronological feed of completed challenges and logged eco-actions from `ImpactLogDao`.

#### Tab 5: Poster Generator (`PosterGeneratorScreen.kt`)
- **Live Poster Canvas Preview:**
  - Uses Compose `Modifier.drawWithContent` + `rememberGraphicsLayer()` (Compose 1.7+ offscreen capture API) to render a high-resolution `4:5` social poster card in real time.
  - Poster includes:
    1. Dynamic Aura gradient background (selectable from 4 curated palettes: *Bio-Emerald*, *Oceanic Pulse*, *Solar Flare*, *Midnight Gaia*).
    2. Custom user headline & personal eco-pledge quote.
    3. Optional **Verified CHANGE360 Impact Badge** automatically populated with the user's real `CO₂ Saved`, `Streak`, and `Aura Level`.
    4. CHANGE360 watermark footer.
- **Customization Controls:**
  - Template style selector, aura color picker, headline/quote `OutlinedTextField` inputs, and toggle switch for *"Show My Live Impact Stats"*.
- **Export & Share Pipeline (`PosterBitmapExporter.kt`):**
  - Captures `graphicsLayer.toImageBitmap().asAndroidBitmap()`, saves as a PNG to `context.cacheDir/posters/` (and logs metadata in `PosterDao`), wraps the file in a `FileProvider` URI, and launches the native Android Share Sheet (`Intent.ACTION_SEND` with `image/png`).

