# SabiTani

Aplikasi Android native ditulis dengan Kotlin + Jetpack Compose, mengikuti pendekatan **Clean Architecture** + **MVI** dengan struktur **multi-module by feature**.

Dokumen ini adalah *single source of truth* untuk arsitektur, struktur folder, dan tech stack yang dipakai/akan dipakai di project ini. Baca dulu sebelum menambah module/feature baru supaya konsistensi terjaga.

---

## Setup Lokal

1. Salin `local.properties.example` → `local.properties` (sudah gitignored).
2. Isi minimal:
   - `sdk.dir` — auto-fill oleh Android Studio saat sync pertama.
   - `GEMINI_API_KEY` — ambil dari [Google AI Studio](https://aistudio.google.com/apikey). Diperlukan saat fitur Tania (Phase 2) aktif. Build tetap sukses dengan key kosong, tapi call ke Gemini akan gagal.
   - `GEMINI_MODEL` *(opsional)* — override default `gemini-2.0-flash`.
3. Build: `./gradlew assembleDebug`.

### Secrets di CI

Tambahkan ke **GitHub repo settings → Secrets and variables → Actions**:

| Secret | Wajib | Sumber |
|---|---|---|
| `GEMINI_API_KEY` | Saat Phase 2 aktif | Google AI Studio |
| `GEMINI_MODEL` | Tidak | Kosongkan untuk pakai default |
| `GOOGLE_SERVICES_JSON` | Saat PR1b (Firebase Auth) | Firebase console → Project settings |

Workflow `.github/workflows/ci.yml` membaca secrets via env var → `Project.readSecret(name)` di `build-logic` resolve ke env saat di CI atau ke `local.properties` saat di lokal.

---

## Daftar Isi

1. [Filosofi Arsitektur](#1-filosofi-arsitektur)
2. [Diagram Modul](#2-diagram-modul)
3. [Struktur Folder Top-Level](#3-struktur-folder-top-level)
4. [Penjelasan Setiap Layer (Clean Architecture)](#4-penjelasan-setiap-layer-clean-architecture)
5. [Pattern MVI di Presentation Layer](#5-pattern-mvi-di-presentation-layer)
6. [Modul `:core:*` — Penjelasan per Modul](#6-modul-core--penjelasan-per-modul)
7. [Modul `:feature:*` — Anatomi Internal](#7-modul-feature--anatomi-internal)
8. [Modul `:build-logic` — Convention Plugins](#8-modul-build-logic--convention-plugins)
9. [Aturan Ketergantungan Antar Modul](#9-aturan-ketergantungan-antar-modul)
10. [Tech Stack Final](#10-tech-stack-final)
11. [Konvensi Penamaan](#11-konvensi-penamaan)
12. [Cara Menambah Feature Baru](#12-cara-menambah-feature-baru)
13. [Testing Strategy](#13-testing-strategy)
14. [Build, Lint, dan Quality Gate](#14-build-lint-dan-quality-gate)
15. [Roadmap Implementasi Bertahap](#15-roadmap-implementasi-bertahap)

---

## 1. Filosofi Arsitektur

Tujuan utama struktur ini ada tiga:

1. **Skalabilitas tim** — banyak orang bisa kerja di feature berbeda tanpa saling injak. Setiap feature adalah module independen dengan boundary jelas.
2. **Build time pendek** — perubahan di satu feature tidak memicu rebuild semua module. Gradle hanya recompile yang berubah + downstream-nya.
3. **Testability** — business logic (domain) tidak bergantung framework Android, jadi bisa di-test pakai JVM unit test biasa (cepat, tidak perlu emulator).

Tiga pilar yang kita pegang:

- **Clean Architecture (Uncle Bob)** — pemisahan ketat antara `domain`, `data`, dan `presentation`. Dependency rule: layer dalam tidak boleh tahu soal layer luar.
- **MVI (Model-View-Intent)** — satu arah data flow di UI. State immutable, perubahan lewat Intent, side effect terisolasi.
- **Modularization by feature** — setiap fitur produk = satu module. Shared concerns dipecah ke `:core:*`.

Referensi blueprint: [Now in Android](https://github.com/android/nowinandroid) oleh tim Android, [Tivi](https://github.com/chrisbanes/tivi) oleh Chris Banes.

---

## 2. Diagram Modul

```
                       ┌─────────────────┐
                       │      :app       │  ← entry point, DI graph, navigation host
                       └────────┬────────┘
                                │ depends on
                ┌───────────────┼───────────────┐
                ▼               ▼               ▼
        ┌───────────────┐  ┌──────────┐  ┌──────────────┐
        │  :feature:*   │  │ :core:*  │  │ :build-logic │
        └───────┬───────┘  └────┬─────┘  └──────────────┘
                │ depends on    │
                └──────────┬────┘
                           ▼
                      ┌──────────┐
                      │ :core:*  │  (designsystem, ui, domain, data, dll)
                      └──────────┘
```

**Rule of thumb:**
- `:app` tahu semua `:feature:*` dan `:core:*` (hanya untuk wiring & navigation).
- `:feature:*` tidak boleh tahu `:feature:*` lain (komunikasi antar feature lewat `:core:navigation` atau abstraksi di `:core:domain`).
- `:core:*` tidak boleh tahu `:feature:*` (terbalik = code smell).
- `:build-logic` tidak depend ke siapapun, dia yang men-define konvensi build untuk yang lain.

---

## 3. Struktur Folder Top-Level

```
SabiTani/
├── app/                        # Module utama (com.android.application)
├── build-logic/                # Convention plugins Gradle
│   └── convention/
├── core/                       # Module-module shared
│   ├── analytics/
│   ├── common/
│   ├── data/
│   ├── database/
│   ├── datastore/
│   ├── designsystem/
│   ├── domain/
│   ├── model/
│   ├── navigation/
│   ├── network/
│   ├── notifications/
│   ├── testing/
│   └── ui/
├── feature/                    # Module per fitur produk
│   ├── splash/
│   ├── onboarding/
│   ├── auth/
│   ├── home/
│   ├── catalog/
│   ├── detail/
│   ├── profile/
│   └── settings/
├── gradle/
│   └── libs.versions.toml      # Version catalog terpusat
├── marketing/                  # Asset non-runtime (icon Play Store 1024×1024, screenshot, dll)
├── build.gradle.kts            # Root build script
├── settings.gradle.kts         # Daftar semua module
└── README.md                   # File ini
```

> Setiap folder modul sekarang berisi `.gitkeep` agar struktur ter-commit. File-file Kotlin, `build.gradle.kts` per-module, dan resource akan ditambahkan saat implementasi tiap fitur.

### 3.1. Folder `marketing/`

Berisi asset yang **bukan** untuk di-bundle ke APK — biasanya untuk listing Play Store, presentasi, atau dokumentasi. Contoh: `ic_sabitani_logo-playstore.png` (1024×1024 hi-res icon untuk Play Console upload). Folder ini di-exclude dari build (tidak ada `build.gradle.kts`), jadi tidak menambah ukuran APK.

### 3.2. Status Bersih-Bersih Module `:app`

Saat scaffolding awal, beberapa file template default Android Studio yang sudah tidak ke-reference sudah dihapus dari `:app`:
- `res/drawable/ic_launcher_background.xml`, `ic_launcher_foreground.xml`, `ic_logo_app.png` — sisa template, sudah digantikan adaptive icon `ic_sabitani_logo*`.
- `src/test/.../ExampleUnitTest.kt`, `src/androidTest/.../ExampleInstrumentedTest.kt` — test skeleton kosong.

Update Phase 2: theme (`Color.kt`, `Theme.kt`, `Type.kt`) sudah dipindah ke `:core:designsystem`. Sekarang `:app/src/main/java/tech/sabitani/app/` hanya berisi `MainActivity.kt` yang ringan — Compose theme di-import dari `tech.sabitani.core.designsystem.theme.SabiTaniTheme`.

---

## 4. Penjelasan Setiap Layer (Clean Architecture)

Clean Architecture membagi kode ke dalam tiga lingkaran konsentris. Aturan dependency: panah selalu menunjuk ke dalam.

### 4.1. Domain Layer (paling dalam, paling stabil)

Berisi *business rules* aplikasi. **Tidak boleh import** apapun dari Android SDK, Retrofit, Room, Compose, dll. Murni Kotlin.

Isinya:
- **Entity / Model** — POJO bisnis (`User`, `Product`, `Order`).
- **Repository Interface** — kontrak yang akan di-implement oleh `data` layer (contoh: `interface UserRepository { suspend fun getUser(id: String): User }`).
- **Use Case / Interactor** — satu kelas = satu aksi bisnis (`GetUserUseCase`, `SubmitOrderUseCase`). Single responsibility, biasanya hanya punya `operator fun invoke(...)`.

Kenapa dipisah? Karena business rules adalah yang paling jarang berubah. Framework bisa ganti (Retrofit → Ktor), tapi rule "user harus punya email" tidak.

### 4.2. Data Layer (lingkaran tengah)

Implementasi konkret dari kontrak yang didefinisikan di `domain`. Tahu detail teknologi penyimpanan (Room, Retrofit, DataStore).

Sub-struktur dalam satu feature:
- **remote/** — `ApiService` (Retrofit interface), `DTO` (data transfer object dari JSON).
- **local/** — `Dao` (Room), `Entity` (Room @Entity).
- **mapper/** — fungsi konversi `Dto → Domain`, `Entity → Domain`, dan sebaliknya. Mapper memastikan layer luar tidak bocor ke domain.
- **repository/** — implementasi `RepositoryImpl` yang menggabungkan remote + local (offline-first, caching, dll).

**Aturan emas:** repository selalu return tipe dari `domain.model`, bukan `DTO` atau `Entity`.

### 4.3. Presentation Layer (lingkaran luar)

Yang user lihat. Compose UI + ViewModel (MVI). Tahu domain (panggil UseCase), tidak tahu data (tidak boleh import Retrofit/Room langsung).

Sub-struktur:
- **screen/** — Composable level screen (`HomeScreen`, `LoginScreen`).
- **component/** — Composable kecil reusable di feature ini (`ProductCard`, `OrderSummaryRow`). Kalau dipakai lintas feature, naikkan ke `:core:ui`.
- **state/** — `XxxState` (data class), `XxxIntent` (sealed interface), `XxxEffect` (sealed interface), opsional `XxxReducer`.
- **viewmodel/** — `XxxViewModel` extends `ViewModel` (Hilt) + Orbit container.

---

## 5. Pattern MVI di Presentation Layer

MVI = unidirectional data flow. State adalah single source of truth untuk UI.

```
        ┌────────┐  emit  ┌────────┐  render  ┌─────────┐
User -->│ Intent │ -----> │ State  │ -------> │   View  │
        └────────┘        └────────┘          └────┬────┘
             ▲                                     │
             └────────── user action ──────────────┘
```

Definisi pakai pattern berikut:

```kotlin
// State — immutable snapshot UI
data class HomeState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val errorMessage: String? = null,
)

// Intent — apa yang user ingin terjadi
sealed interface HomeIntent {
    data object LoadProducts : HomeIntent
    data class SelectProduct(val id: String) : HomeIntent
}

// Effect — one-shot side effect (navigasi, snackbar, toast)
sealed interface HomeEffect {
    data class NavigateToDetail(val productId: String) : HomeEffect
    data class ShowError(val message: String) : HomeEffect
}
```

ViewModel meng-handle `Intent` → update `State` → kadang emit `Effect`. Pilihan library:

- **Orbit MVI** (rekomendasi default) — Kotlin-first, API ringkas, well-maintained.
- **MVIKotlin** — kalau ke depan mau Multiplatform.
- **Roll-your-own** — `StateFlow<State>` + `Channel<Effect>` + `fun onIntent(intent)`. Cukup untuk feature sederhana.

Pilih satu dan **konsisten** di semua feature.

---

## 6. Modul `:core:*` — Penjelasan per Modul

| Modul | Tanggung jawab |
|---|---|
| **:core:analytics** | Abstraksi event tracking (`AnalyticsHelper`) + adapter ke Firebase Analytics / Amplitude. Feature memanggil interface, tidak tahu vendor-nya. |
| **:core:common** | Coroutine dispatchers (`IoDispatcher`, `MainDispatcher`), `Result<T>` wrapper, extension function umum. Tidak boleh punya logic bisnis. |
| **:core:data** | Cross-cutting repository (mis. `UserRepository` yang dipakai banyak feature) + qualifier Hilt yang sifatnya global. |
| **:core:database** | Room database, type converter global. DAO spesifik feature boleh tinggal di feature module masing-masing dan di-expose lewat module ini. |
| **:core:datastore** | Wrapper di atas Jetpack DataStore (Preferences & Proto) untuk simpan setting, token auth, cache kecil. Menggantikan SharedPreferences. |
| **:core:designsystem** | Theme Material 3 (Color, Typography, Shape), token (spacing, radius), icon set. Dasar visual aplikasi. |
| **:core:domain** | Use case lintas-feature (mis. `GetCurrentUserUseCase` yang dipakai banyak screen). Use case spesifik tetap di feature. |
| **:core:model** | Domain entity yang shared antar feature (mis. `User`, `Product`). Pure Kotlin. |
| **:core:navigation** | Definisi route, NavGraph builder, type-safe argument. Tempat feature register entry point-nya. |
| **:core:network** | Konfigurasi OkHttp/Retrofit (atau Ktor), interceptor (auth, logging, Chucker), serializer. |
| **:core:notifications** | Push notification handling, channel setup. |
| **:core:testing** | Test fixture, fake repository, helper Compose test, JUnit rule. Hanya dipakai di test source set. |
| **:core:ui** | Composable reusable lintas-feature (`LoadingState`, `EmptyState`, `ErrorState`, `AppBar`). Beda dari `:core:designsystem` yang isinya token; di sini isinya widget jadi. |

---

## 7. Modul `:feature:*` — Anatomi Internal

Setiap feature module berisi tiga layer Clean Architecture sebagai package internal:

```
feature/<name>/src/main/java/tech/sabitani/feature/<name>/
├── data/
│   ├── remote/
│   │   ├── api/         # Retrofit/Ktor service interface
│   │   └── dto/         # Data classes hasil JSON parse
│   ├── local/
│   │   ├── dao/         # Room @Dao
│   │   └── entity/      # Room @Entity
│   ├── mapper/          # Konversi antar layer
│   └── repository/      # RepositoryImpl
├── domain/
│   ├── model/           # Domain model spesifik feature
│   ├── repository/      # Repository interface (kontrak)
│   └── usecase/         # Use case / interactor
├── presentation/
│   ├── component/       # Composable kecil khusus feature
│   ├── screen/          # Composable level screen
│   ├── state/           # State / Intent / Effect
│   └── viewmodel/       # ViewModel
└── di/                  # Modul Hilt feature ini (binds RepositoryImpl ke interface)
```

Setiap feature **harus** punya `di/` Hilt module yang melakukan binding `RepositoryImpl` → `Repository` interface. Ini yang membuat domain layer tidak perlu tahu implementasi.

---

## 8. Modul `:build-logic` — Convention Plugins

`:build-logic` adalah module Gradle terpisah yang menampung **convention plugins** — script Gradle reusable untuk konfigurasi yang sering diulang.

Tanpa convention plugin: setiap `build.gradle.kts` feature module akan punya 50+ baris setup yang sama (compose, hilt, kotlin options, lint config, test config).

Dengan convention plugin: tiap feature cukup tulis:

```kotlin
plugins {
    id("sabitani.android.feature")
    id("sabitani.android.library.compose")
    id("sabitani.android.hilt")
}
```

Plugin yang tersedia:

| Plugin ID | Status | Isi |
|---|---|---|
| `sabitani.android.application` | ✅ | `com.android.application`, `kotlin-android`, compileSdk 36, minSdk 26, targetSdk 36, JVM 11, default test runner |
| `sabitani.android.application.compose` | ✅ | Apply `kotlin-compose` plugin, `buildFeatures.compose = true`, auto-platform Compose BOM |
| `sabitani.android.library` | ✅ | `com.android.library`, `kotlin-android`, SDK & JVM sama dengan application, consumer ProGuard |
| `sabitani.android.library.compose` | ✅ | Compose untuk library module (sama dengan application.compose tapi untuk library) |
| `sabitani.android.hilt` | ✅ | Apply `dagger.hilt.android` + `ksp`, auto-add `hilt-android` & `hilt-compiler` dependency |
| `sabitani.android.feature` | ✅ | Aggregator: apply library + library.compose + hilt + auto-add dependency ke `:core:designsystem/common/model/navigation`, `hilt-navigation-compose`, `lifecycle-runtime-compose`, dan bundle Orbit MVI |
| `sabitani.kotlin.jvm` | ✅ | Pure Kotlin module (`:core:common`, `:core:model`). JVM 11, Kotlin compiler `-opt-in=kotlin.RequiresOptIn` |
| `sabitani.android.room` | ⏳ | Apply Room + KSP processor + room-runtime/ktx dependency. Akan dibuat saat entity pertama hadir |
| `sabitani.android.test` | ⏳ | Test config (JUnit5 + MockK + Turbine + Robolectric). Saat ini test deps di-declare per-module di `dependencies {}` |

Lokasi: `build-logic/convention/src/main/java/tech/sabitani/convention/`.

### 8.1. Cara Pakai (Contoh Real dari `:app`)

Modul `:app` sudah pakai convention plugin. Lihat `app/build.gradle.kts` — boilerplate Android (SDK version, JVM target, Compose buildFeatures, BOM platform) sudah pindah ke plugin:

```kotlin
plugins {
    id("sabitani.android.application")
    id("sabitani.android.application.compose")
}

android {
    namespace = "tech.sabitani.app"
    defaultConfig {
        applicationId = "tech.sabitani.app"
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.bundles.compose)        // 9 library Compose sekaligus
    debugImplementation(libs.bundles.compose.debug)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
```

Yang **tinggal** di build script tiap module hanya yang **unik** untuk module itu: namespace, applicationId/versionCode (kalau application), buildTypes custom, dan dependency-nya. Sisanya tidak perlu diulang.

### 8.2. Cara Tambah Convention Plugin Baru

1. Buat class di `build-logic/convention/src/main/java/tech/sabitani/convention/XxxConventionPlugin.kt` yang implement `Plugin<Project>`.
2. Daftarkan ke `build-logic/convention/build.gradle.kts` dalam blok `gradlePlugin { plugins { register(...) {} } }`.
3. Plugin id sebaiknya pakai prefix `sabitani.` agar konsisten dan tidak conflict.

---

## 9. Aturan Ketergantungan Antar Modul

Aturan ini di-enforce dengan **Konsist** (architectural tests) supaya tidak bisa dilanggar tanpa CI fail.

```
:app                    → boleh depend ke :feature:* dan :core:*
:feature:*              → boleh depend ke :core:*, TIDAK BOLEH ke :feature:* lain
:core:ui                → boleh depend ke :core:designsystem, :core:common
:core:data              → boleh depend ke :core:network, :core:database, :core:datastore, :core:domain, :core:model
:core:domain            → hanya boleh depend ke :core:model dan :core:common
:core:model             → TIDAK BOLEH depend siapapun (pure Kotlin)
:core:common            → TIDAK BOLEH depend siapapun
:build-logic            → independen, hanya pakai Gradle API + plugin classpath
```

Jika dua feature perlu share sesuatu, dorong ke `:core:*`.

---

## 10. Tech Stack Final

### UI
- **Jetpack Compose** + **Material 3** (`androidx.compose.material3`)
- **Compose Navigation** (type-safe) untuk navigasi standar
- **Accompanist** untuk system UI controller, permission helper

### State Management
- **Orbit MVI** sebagai default MVI library

### Dependency Injection
- **Hilt** (project ini Android-only, jadi Hilt lebih ergonomis dari Koin/Kotlin-Inject)

### Async & Reactive
- **Kotlin Coroutines** + **Flow** (StateFlow, SharedFlow)
- **Turbine** untuk testing Flow

### Networking
- **Retrofit** + **OkHttp** + **kotlinx.serialization**
- **Chucker** untuk inspect network di debug build only

### Database & Storage
- **Room** sebagai SQLite ORM
- **DataStore** (Preferences untuk simple, Proto untuk struktural) menggantikan SharedPreferences
- **EncryptedSharedPreferences** untuk data sensitif (token, refresh token)

### Image Loading
- **Coil** (Kotlin-first, Compose-friendly)

### Background Work
- **WorkManager** untuk deferrable task (sync, upload)

### Testing
- **JUnit 5** sebagai test framework
- **MockK** untuk mocking
- **Turbine** untuk Flow assertion
- **Robolectric** untuk JVM-side Android test
- **Compose UI Test** untuk UI test
- **Maestro** untuk E2E test (lebih maintainable dari Espresso untuk happy path)

### Code Quality
- **Detekt** — static analysis
- **Spotless** — formatter (wraps ktlint)
- **Android Lint** — built-in
- **Dependency Guard** — lock dependency tree, deteksi perubahan transitive
- **Konsist** — enforce architectural rule sebagai test

### Build Tools
- **Gradle Version Catalogs** (`gradle/libs.versions.toml`)
- **KSP** (Kotlin Symbol Processing) menggantikan KAPT untuk Hilt, Room
- **Convention plugins** di `build-logic`
- **Gradle Build Cache** (local + remote kalau sudah ada CI)

### Logging & Monitoring
- **Timber** untuk logging (debug only)
- **Firebase Crashlytics** untuk crash reporting
- **Firebase Analytics** untuk product analytics (di-wrap `:core:analytics`)
- **LeakCanary** untuk memory leak detection (debug only)

### Date/Time & Utility
- **kotlinx-datetime** untuk waktu (gantinya `java.util.Date`)
- **Arrow-kt** (opsional, kalau perlu `Either`, `Option`) — jangan force adopt kalau tim belum familiar functional programming

### CI/CD
- **GitHub Actions** — pipeline build, test, lint, distribute
- **Fastlane** — release automation ke Play Store
- **Firebase App Distribution** — internal testing build

> **Catatan multiplatform:** project ini Android-only untuk sekarang. Kalau nanti perlu KMP, modul `:core:model`, `:core:common`, `:core:domain` yang paling siap dipindah ke `commonMain` karena tidak ada dependency Android di sana.

---

## 11. Konvensi Penamaan

| Item | Konvensi | Contoh |
|---|---|---|
| Module Gradle | kebab-case dengan colon | `:feature:home`, `:core:designsystem` |
| Package | lowercase, tanpa underscore, prefix `tech.sabitani` | `tech.sabitani.feature.home.presentation` |
| Class | PascalCase | `HomeViewModel`, `GetProductsUseCase` |
| Composable | PascalCase, noun-based | `HomeScreen`, `ProductCard` |
| Function | camelCase, verb-based | `loadProducts()`, `onProductClick()` |
| Constant | UPPER_SNAKE_CASE di `companion object` | `const val MAX_RETRY = 3` |
| State class | `<Feature>State` | `HomeState`, `LoginState` |
| Intent class | `<Feature>Intent` | `HomeIntent` |
| Effect class | `<Feature>Effect` | `HomeEffect` |
| Use case | `<Verb><Noun>UseCase` | `GetUserUseCase`, `SubmitOrderUseCase` |
| Repository interface | `<Noun>Repository` | `UserRepository` |
| Repository impl | `<Noun>RepositoryImpl` | `UserRepositoryImpl` |
| Hilt module | `<Feature><Type>Module` | `HomeDataModule`, `NetworkModule` |
| DTO | `<Noun>Dto` | `UserDto`, `ProductDto` |
| Entity (Room) | `<Noun>Entity` | `UserEntity` |

---

## 12. Cara Menambah Feature Baru

Misal kita mau tambah feature `cart`:

1. **Buat folder modul** mengikuti struktur `:feature:home` (sudah ada sebagai template).
2. **Daftarkan ke `settings.gradle.kts`**:
   ```kotlin
   include(":feature:cart")
   ```
3. **Buat `feature/cart/build.gradle.kts`** apply convention plugin:
   ```kotlin
   plugins {
       id("sabitani.android.feature")
       id("sabitani.android.library.compose")
       id("sabitani.android.hilt")
   }
   android {
       namespace = "tech.sabitani.feature.cart"
   }
   ```
   > Catatan: pakai `src/main/java/tech/sabitani/feature/cart/` (folder `java/`, bukan `kotlin/`) supaya konsisten dengan modul `:app` yang sudah ada. File `.kt` tetap bisa ditaruh di folder `java/` — itu konvensi default Android Studio.
4. **Implementasi tiga layer** (domain → data → presentation, urutan ini paling sehat).
5. **Tambahkan route** di `:core:navigation` dan wiring di `:app` NavHost.
6. **Tulis test** minimal untuk use case + ViewModel.
7. **Update Konsist test** kalau perlu rule baru.

---

## 13. Testing Strategy

| Layer | Tipe test | Tools |
|---|---|---|
| `domain` | Pure JVM unit test | JUnit5, MockK, Turbine |
| `data` (repository) | JVM unit test dengan fake DAO/API | JUnit5, MockK, Turbine |
| `data` (DAO) | Instrumented test (Room in-memory) | Robolectric atau androidTest |
| `presentation` (ViewModel) | JVM unit test | JUnit5, MockK, Turbine |
| `presentation` (Compose) | Compose UI test | `createComposeRule()` |
| End-to-end | Black box di device | Maestro flows |

**Coverage target:** domain 90%+, data 80%+, presentation 60%+ (UI test mahal, fokus ke happy path + error path).

---

## 14. Build, Lint, dan Quality Gate

Setiap PR harus lolos:

1. `./gradlew assembleDebug` — compile sukses
2. `./gradlew testDebugUnitTest` — semua unit test pass
3. `./gradlew lintDebug` — Android Lint 0 error
4. `./gradlew detekt` — Detekt 0 issue di severity error
5. `./gradlew spotlessCheck` — format konsisten
6. `./gradlew dependencyGuard` — dependency tree tidak berubah tanpa update lock
7. `./gradlew konsistTest` — architectural rule terjaga

Pre-push hook (opsional via Git hooks) bisa jalanin subset (1, 2, 4, 5) supaya feedback loop cepat.

---

## 15. Roadmap Implementasi Bertahap

Struktur folder sudah berdiri, tapi belum ada kode. Urutan implementasi yang disarankan:

- [x] **Phase 0** — Setup `gradle/libs.versions.toml` (version catalog) untuk semua dependency yang akan dipakai. ✅ Sudah berisi semua versi, library, plugin, dan bundle dari tech stack Section 10. Aman dipakai dari module manapun via `libs.<alias>` atau `libs.bundles.<name>`.
- [x] **Phase 1** — Implementasi `:build-logic` convention plugins. ✅ 5 plugin sudah ready: `sabitani.android.application`, `sabitani.android.application.compose`, `sabitani.android.library`, `sabitani.android.library.compose`, `sabitani.android.hilt`. Modul `:app` sudah di-refactor pakai plugin baru (60 → 32 baris). Build hijau (`./gradlew :app:assembleDebug` BUILD SUCCESSFUL). Plugin `sabitani.android.feature`, `sabitani.android.room`, `sabitani.android.test`, `sabitani.kotlin.jvm` ditunda sampai dibutuhkan.
- [x] **Phase 2** — Setup `:core:designsystem`, `:core:common`, `:core:model`. ✅ Tambahan: convention plugin `sabitani.kotlin.jvm` untuk modul pure Kotlin. `:core:designsystem` sudah berisi theme (Color, Typography, Theme) hasil migrasi dari `:app`. `:core:common` berisi `Result<T>` sealed wrapper + `Flow.asResult()` extension. `:core:model` masih kosong (entity ditambah saat feature pertama butuh). Type-safe project accessors aktif — pakai `projects.core.designsystem` dari module lain.
- [x] **Phase 3** — Setup `:core:network`, `:core:datastore`, dan `:core:database` (scaffold). ✅ Tambahan: Hilt di-bootstrap di `:app` (`@HiltAndroidApp class SabiTaniApp`, `@AndroidEntryPoint` di `MainActivity`, manifest update). `:core:network` siap pakai — Hilt module menyediakan `Json`, `OkHttpClient` (dengan `HttpLoggingInterceptor` + `ChuckerInterceptor`), dan `Retrofit`. Chucker di-noop saat release via `library-no-op` artifact (tanpa branching kode). `:core:datastore` menyediakan `DataStore<Preferences>`. `:core:database` masih scaffold — `AppDatabase` ditunda sampai entity pertama hadir (Phase 5). Base URL masih hardcoded di `NetworkConfig` (TODO: pindah ke BuildConfig saat backend siap).
- [x] **Phase 4** — Setup `:core:navigation`. ✅ Wrapper `SabiTaniNavHost` di atas Compose Navigation type-safe. Pattern: tiap feature expose `NavGraphBuilder.<feature>Screen(onXxx: () -> Unit)` extension yang dipanggil dari `:app/navigation/SabiTaniAppNavGraph.kt`. Hilt graph sudah terhubung otomatis via `@AndroidEntryPoint MainActivity` (di-bootstrap Phase 3).
- [x] **Phase 5** — Implementasi feature pertama end-to-end. ✅ 3 feature module siap, plus convention plugin `sabitani.android.feature` (aggregator: library + compose + hilt + auto-wire `:core:designsystem/common/model/navigation` + orbit + hilt-navigation-compose). Pattern yang ditunjukkan:
  - **`:feature:splash`** — UI murni, navigasi setelah delay (no ViewModel, no data layer).
  - **`:feature:onboarding`** — Full Clean Arch: `OnboardingPreferencesRepository` interface (domain) + impl di data layer baca/tulis ke `DataStore<Preferences>` (`:core:datastore`), `CompleteOnboardingUseCase`, `OnboardingViewModel` (Hilt), `OnboardingDataModule` (@Binds). Demo wiring full stack.
  - **`:feature:auth`** — Full Clean Arch + **Orbit MVI**: `AuthRepository` interface, `FakeAuthRepository` (delay 800ms, validasi password ≥6 char), `LoginUseCase`, `LoginViewModel` extends `ContainerHost<LoginState, LoginEffect>`. State/Intent/Effect terpisah di `LoginContract.kt`. Side effect (`LoginSucceeded`, `ShowError`) di-emit dari `intent { ... postSideEffect(...) }` dan di-collect via `viewModel.collectSideEffect { ... }`.
- [x] **Phase 6** — `:core:analytics` + `:core:notifications` + Timber. ✅ `AnalyticsHelper` interface + `LogAnalyticsHelper` (logs ke Timber). `NotificationChannels.ensureChannels(context)` setup channel `general`. `SabiTaniApp.onCreate()` plant Timber DebugTree (debug build only) + ensure notification channels.
- [x] **Phase 7** — Quality gate + CI/CD. ✅ Detekt + Spotless plugin di-apply ke semua subprojects via root `build.gradle.kts`. Detekt config minimal di `config/detekt/detekt.yml` (`maxIssues: 100` — tighten lagi seiring waktu). Dependency Guard plugin di `:app` (config `releaseRuntimeClasspath`). Konsist test (`app/src/test/.../ArchitectureTest.kt`) menge-encode 3 rule: (1) domain layer ≠ Android, (2) domain layer ≠ Retrofit/Room/OkHttp, (3) feature module ≠ feature module lain. GitHub Actions workflow `.github/workflows/ci.yml` chain: spotlessCheck → detekt → lintDebug → testDebugUnitTest → assembleDebug → dependencyGuard.

> **Catatan post-setup yang masih perlu ditangani:**
> - Run `./gradlew :app:dependencyGuardBaseline` sekali untuk generate baseline lockfile sebelum CI bisa lolos task `dependencyGuard`.
> - Tighten `maxIssues` di `detekt.yml` setelah baseline issue di-cleanup (atau `./gradlew detektBaseline` untuk grandfather existing issues).
> - `BASE_URL` di `:core:network/NetworkConfig.kt` masih hardcoded — pindah ke `BuildConfig` saat backend siap (`buildFeatures.buildConfig = true` di `:core:network` + `buildConfigField`).
> - `:core:database` `AppDatabase` class belum ada — tambah saat entity pertama hadir (mis. saat `:feature:home` butuh persistence offline).
> - `:feature:auth` `FakeAuthRepository` perlu diganti `RemoteAuthRepository` yang panggil API real lewat `Retrofit` (dari `:core:network`) saat endpoint backend siap.
> - CI workflow butuh git init + remote setup untuk aktif. Pertimbangkan tambah job `release` untuk Play Store distribution via Fastlane.

> Setiap phase idealnya jadi satu PR terpisah supaya review-able.

---

## Lisensi & Kontribusi

(Akan diisi saat repo sudah di-init dan dipublish.)
