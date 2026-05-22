# Task Hari Ini — 2026-05-22

Status singkat work-in-progress untuk SabiTani. Dokumen ini ephemeral — fokus apa yang dikerjakan hari ini, bukan roadmap panjang (lihat `task.md` untuk roadmap).

---

## ✅ Selesai hari ini

### PR0 — Fix CI pipeline (DONE)
Branch `feature/phase-1-foundation`, 5 commit fix untuk unblock PR #1:

| Commit | Isi | Alasan |
|---|---|---|
| `4d20c17` | `git update-index --chmod=+x gradlew` | Linux CI runner gagal eksekusi gradlew karena bit executable tidak ke-set (mode 100644 → 100755) |
| `9966ad6` | Add `.editorconfig` + `.gitattributes` | Exempt @Composable dari ktlint function-naming, disable filename rule untuk Route extension pattern, normalize LF di git |
| `1ecb602` | Manual format `SabiTaniApp.kt` + `ArchitectureTest.kt` | Hapus blank line setelah class opening, format chained Konsist calls |
| `ddf860a` | Trim trailing blank line di `app/build.gradle.kts` | spotlessKotlinGradleCheck violation |
| `600822d` | `./gradlew spotlessApply` menyeluruh — 86 file | Auto-fix sisa format violations di semua module |

**Hasil CI run #26284038977: ALL GREEN** ✅
- Spotless ✓ · Detekt ✓ · Lint ✓ · Unit tests ✓ · assembleDebug ✓ · Dependency Guard ✓

**Action item untuk Khalif:** approve & merge PR #1 di https://github.com/khlif-and/SabiTani_Apps/pull/1

---

## 🚧 Berikutnya (begitu PR #1 merged)

### PR1 — Activate `:feature:home` + bottom nav shell + dashboard ringkas

**Branch:** `feature/home-and-shell` (dari `main` setelah PR #1 merge)

**Tujuan:** Wire entry point post-login → home shell dengan bottom navigation, sehingga TODO di `app/.../SabiTaniAppNavGraph.kt:44` hilang dan user punya cara akses fitur Plot/Cycle.

#### Scope (sesuai keputusan kemarin)
- **4 tab bottom navigation**: Beranda · Kebun · Tania · Profil
- **Shell composable di `:app`** (bukan di `:feature:home`) — supaya tidak melanggar rule cross-feature import (`:feature:home` tidak boleh tahu `:feature:plot`)
- **Tab Beranda** = Dashboard ringkas dengan 4 summary card:
  - Total kebun
  - Plot aktif
  - Cycle berjalan
  - Laba/rugi bulan ini (aggregate dari semua cycle aktif)
- **Tab Kebun** = `FarmListRoute` dari `:feature:plot`
- **Tab Tania** = Placeholder "Segera hadir" (Phase 2)
- **Tab Profil** = Placeholder "Segera hadir" + tombol logout (Phase 4)

#### Deliverables
1. **Register `:feature:home`** di `settings.gradle.kts`
2. **`feature/home/build.gradle.kts`** pakai `sabitani.android.feature` convention plugin
3. **Domain layer** di `:feature:home`:
   - `DashboardSummary` data class (kebun count, plot count, cycle count, monthly P&L)
   - `ObserveDashboardSummaryUseCase` — aggregate dari `:feature:plot` & `:feature:cycle` use cases
   - **PROBLEM**: cross-feature import dilarang. Solusi: gerakkan domain interface ke `:core:domain` atau buat `DashboardRepository` di `:core:data` yang query Room langsung
4. **Data layer**: `DashboardRepositoryImpl` query Room DAO langsung untuk aggregate
5. **Presentation**: `HomeViewModel` (Orbit MVI), `HomeScreen`, `HomeRoute`, `homeScreen()` NavGraphBuilder extension
6. **`SabiTaniMainShell.kt`** di `:app/navigation/` — Scaffold + NavigationBar dengan 4 destinasi
7. **Update `SabiTaniAppNavGraph.kt`** — hapus TODO di line 44, navigate ke `MainShell` after login success
8. **`:core:ui`** widget reusable: `LoadingState`, `EmptyState`, `ErrorState` untuk dipakai di Home dashboard cards

#### Acceptance criteria
- [ ] `./gradlew spotlessCheck detekt lintDebug testDebugUnitTest assembleDebug` semua hijau
- [ ] Manual smoke test: launch app → splash → onboarding → login → MainShell terbuka di tab Beranda dengan summary card terisi
- [ ] Bottom nav switch antar tab smooth, state masing-masing tab preserved
- [ ] Tab Kebun memanggil `FarmListRoute` existing dari `:feature:plot` tanpa modifikasi
- [ ] Tap card "Plot aktif" di Beranda → navigate ke tab Kebun (cross-tab nav)
- [ ] Konsist test masih passing (no cross-feature import detected)

#### Estimated effort
~1 hari coding + 0.5 hari testing & polish. Target submit PR1 tomorrow.

---

## 🔭 Setelah PR1 (urutan PR berikutnya — lihat `task.md` untuk detail)

| PR | Scope | Target |
|---|---|---|
| **PR2** | S-3 (minify) + S-4 (allowBackup=false) + S-15 (ProGuard rules) | Quick-win config tweak, low risk |
| **PR3** | S-2 SQLCipher integrasi (passphrase dari Android Keystore) | Database encryption — touch `:core:database` |
| **PR4** | S-1 Local PIN + BiometricPrompt, replace `FakeAuthRepository` | Replacement auth — touch `:feature:auth` + onboarding flow |

Backend stack discussion: **tertunda sampai Phase 4** (1-2 tahun lagi). Saat itu pakai Kotlin + Ktor + PostgreSQL. IoT components (Go MQTT + TimescaleDB) baru di Phase 5+ saat ada hardware budget.

---

## 📋 Pertanyaan yang sudah dijawab

1. ✅ `DashboardRepository` → `:core:data` (sub-package `dashboard`, interface + impl + DI di sini). Notasi Khalif: "bukan mutlak", bisa direvisit kalau ternyata feature-local lebih cocok untuk concern serupa nanti.
2. ✅ `:core:ui` dibuat sekarang dengan `LoadingState`, `EmptyState`, `ErrorState`.
3. ✅ Tab Tania & Profil = inline placeholder di `:app/navigation/PlaceholderRoutes.kt` (bukan module terpisah).

## ✅ PR1 implementasi (siap push)

Struktur akhir yang dibangun:

```
:core:data (NEW)
└── dashboard/
    ├── DashboardRepository.kt        # interface (public)
    ├── DashboardRepositoryImpl.kt    # impl (internal), inject 4 DAO + Clock
    └── di/DashboardDataModule.kt     # @Binds Hilt

:core:ui (NEW)
└── state/
    ├── LoadingState.kt
    ├── EmptyState.kt
    └── ErrorState.kt

:core:model (UPDATED)
└── DashboardSummary.kt               # data class aggregate

:core:database (UPDATED — 4 DAO method baru, 1 projection baru)
├── dao/FarmDao.kt                    # + observeCount()
├── dao/PlotDao.kt                    # + observeTotalCount()
├── dao/CropCycleDao.kt               # + observeCountByStatus()
├── dao/TransactionDao.kt             # + observeTotalsForRange()
└── projection/TransactionRangeTotals.kt

:feature:home (NEW)
├── domain/usecase/ObserveDashboardSummaryUseCase.kt
└── presentation/
    ├── screen/HomeRoute.kt           # @Serializable data object
    ├── screen/HomeScreen.kt          # composable + homeScreen() extension
    ├── component/DashboardCard.kt
    ├── state/HomeContract.kt         # State, Intent, Effect
    └── viewmodel/HomeViewModel.kt    # Orbit MVI

:app/navigation (UPDATED)
├── SabiTaniAppNavGraph.kt            # login → MainShellRoute (TODO dihapus)
├── MainShellRoute.kt                 # NEW @Serializable
├── SabiTaniMainShell.kt              # NEW Scaffold + NavigationBar 4 tab
├── TopLevelDestination.kt            # NEW enum + matches() helper
└── PlaceholderRoutes.kt              # NEW Tania + Profil placeholder
```

**Local verification PASS:**
- ✅ `./gradlew spotlessApply :app:assembleDebug` — BUILD SUCCESSFUL 8m13s
- ✅ `./gradlew detekt lintDebug testDebugUnitTest` — BUILD SUCCESSFUL 4m12s
- ✅ `./gradlew :app:dependencyGuardBaseline` — baseline refreshed
