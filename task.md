# SabiTani — Task & Roadmap

Dokumen ini melacak progres pengembangan aplikasi **SabiTani** beserta hal-hal keamanan yang harus diperhatikan sebelum rilis production.

> Update terakhir: 2026-05-23
> PR sudah merge: #1 (Phase 1), #2 (home shell + bottom nav), #3 (R8 + ProGuard), #4 (SQLCipher + EncryptedSharedPreferences), #6 (PIN + Biometric opt-in)
> PR pending review: pre-beta hardening (S-6 Network Security Config + S-9 Crashlytics opt-in)

---

## 1. Sudah selesai (changelog ringkas)

### Fondasi & Phase 1 (#1, #2)
- Modular project structure, Hilt + KSP + Compose Material 3 + Room 2.6 + DataStore + Orbit MVI
- Konsist + Detekt + Spotless + Dependency Guard
- Splash, Onboarding, Login (FakeAuth), Phase 1 data foundation (Farm/Plot/CropCycle/Activity/Transaction)
- Home shell + 4-tab bottom nav (Beranda · Kebun · Tania · Profil) + dashboard ringkas
- Wiring `:feature:plot` + `:feature:cycle` di MainShell, navigasi PlotList → PlotDetail → CycleForm → CycleDetail

### Security wave 1 (#3, #4, #6)
- **S-2** Room ter-encrypt via SQLCipher 4.6.1 + `core:security` (DEK 32-byte random di EncryptedSharedPreferences) — PR #4
- **S-3** `isMinifyEnabled=true` + `isShrinkResources=true` di release build + ProGuard rules untuk kotlinx.serialization (type-safe routes) + datetime + `-dontwarn` transitive — PR #3
- **S-15** ProGuard `-keep` untuk `@Serializable` + Tink errorprone `-dontwarn` (consumer-rules.pro per modul) — PR #3, #6
- **S-8** Timber `DebugTree()` sudah gated `BuildConfig.DEBUG` di `SabiTaniApp.kt:12` (sudah ada dari awal, di-verify)
- **PIN + Biometric opt-in (parsial S-1)** — `core/security` (PinManager PBKDF2-200k, BiometricKeyStore hardware-bound dengan `setUserAuthenticationRequired(true)` + `setInvalidatedByBiometricEnrollment(true)`) + `feature/lock` (SetupPin, Unlock, SecuritySettings) + MainActivity lock gate — PR #6
- **CI/CD**: GitHub Actions `.github/workflows/ci.yml` aktif (verified hijau pada PR #3/#4/#6)

### Pre-beta hardening
- **S-6** (PR #7 merged) `network_security_config.xml` (cleartextTrafficPermitted=false, system trust anchors) + `android:usesCleartextTraffic="false"` + reference di `AndroidManifest.xml`
- **S-9** (PR #7 merged) Firebase Crashlytics conditional wiring (apply google-services + crashlytics plugins hanya kalau `app/google-services.json` ada) + `AnalyticsConsent` DataStore-backed (default OFF) + `CrashlyticsBridge` (Firebase availability guard) + `AnalyticsInitializer` (subscribe consent → `setCrashlyticsCollectionEnabled`) + toggle UI di SecuritySettings ("Analitik Anonim")
- **S-4** (PR1a) `android:allowBackup="false"` + hapus reference backup_rules.xml + data_extraction_rules.xml dari manifest + delete file XML. Data portability di-handle via export feature atau cloud sync (Phase 4).
- **S-5** (PR1a) Infrastructure key Gemini: `Project.readSecret(name)` di build-logic (fallback local.properties → env), `:core:network` buildConfigField `GEMINI_API_KEY` + `GEMINI_MODEL`, `GeminiConfig` + `GeminiConfigModule` Hilt provider, `local.properties.example`, CI workflow inject secrets via env, README setup section.

---

## 2. Belum dikerjakan

### Phase 2 — Asisten AI Tania
- [ ] Setup Gemini API client di `:core:network` (atau `:feature:tania` baru)
- [ ] Image scan screen — kamera/galeri → upload foto tanaman → identifikasi tanaman & deteksi penyakit
- [ ] Chat Q&A screen (text-only first)
- [ ] Seed JSON knowledge base: penyakit umum Indonesia, pupuk (Urea/NPK Phonska/KCl), pestisida, varietas komoditas → bundle sebagai asset, di-load saat onboarding
- [ ] Sederhana RAG: query keyword + sertakan konteks ke prompt LLM
- [ ] Riwayat scan tersimpan di Room (`DiseaseScanEntity`)
- [ ] Smart logging: NL ("tadi pagi nyemprot Urea 200ml di plot A") → otomatis jadi `FarmActivity`

### Phase 3 — Dashboard & jadwal
- [ ] Dashboard chart: laba-rugi per cycle, trend pengeluaran per kategori, status semua plot
- [ ] Kalender aktivitas mendatang
- [ ] Reminder pupuk/pestisida via WorkManager + `:core:notifications`
- [ ] Cuaca 3-7 hari ke depan (BMKG public API atau OpenWeather) — render di Home
- [ ] Notifikasi cuaca buruk (peringatan hujan deras sebelum panen)

### Phase 4 — Lanjutan
- [ ] Multi-device sync (Firebase Firestore atau backend custom)
- [ ] Kolaborasi: petani bisa share kebun ke pekerja/penyuluh
- [ ] Harga pasar komoditas (sumber: Panel Harga Pangan Bapanas, dll.)
- [ ] Ekspor laporan PDF/Excel per cycle
- [ ] Mode offline-first (sync ulang saat online)

### Testing
- [ ] Unit test untuk semua use case (`:feature:plot`, `:feature:cycle`, `:feature:home`, `:feature:lock`)
- [ ] Unit test untuk repository impl dengan in-memory DAO
- [ ] Integration test untuk Room DAO (`androidTest/`) — verifikasi SQLCipher encryption end-to-end
- [ ] Compose UI test untuk form validation
- [ ] Test PinManager (verifyPin dengan PIN salah, setupPin idempotent, disablePin restore plain DEK)
- [ ] Konsist test tambahan: package layering, naming conventions
- [ ] Coverage minimum target (mis. 70% line coverage di domain & data)

### Auto-lock & PIN UX lanjutan (follow-up PR #6)
- [ ] Auto-lock saat resume dari background > N detik (hook `ProcessLifecycleOwner`, configurable di Settings)
- [ ] Ganti PIN in-place (sekarang harus disable + setup lagi)
- [ ] Indikator strength PIN saat setup
- [ ] Rate limit PIN attempt (lockout setelah 5 percobaan salah)

### Technical debt / housekeeping
- [ ] **S-1 sisa**: `FakeAuthRepository` masih dipakai di flow Splash→Onboarding→Login (saat PIN belum aktif). Putuskan: hapus login screen + jadikan PIN/biometric mandatory, atau replace dengan real auth (Firebase/backend custom)
- [ ] `feature/auth/.../AuthRoute.kt` punya file-name vs declaration mismatch (file `AuthRoute.kt` isinya `LoginRoute`)
- [ ] `MagicNumber` di `FakeAuthRepository` (`password.length >= 6`)
- [ ] `:core:domain` dan `:core:testing` folder ada tapi belum di-register di `settings.gradle.kts` — putuskan: dihapus atau diisi
- [ ] Migrasi Room saat schema berubah (sudah ada schema export, tinggal tambah `Migration` saat v2 — termasuk migrasi data lama yang belum ter-encrypt jika ada user lama)

---

## 3. Yang perlu diperbaiki untuk **KEAMANAN**

Urutan dari paling kritis → kurang kritis. **Wajib selesai sebelum production release.**

### 🔴 Kritis (blocker production)

#### S-1. (parsial) Login flow masih FAKE saat PIN belum aktif
- **Status:** PIN + Biometric sudah ada (PR #6) — saat user aktifkan PIN, cold start langsung ke UnlockRoute → MainShell, FakeAuth dilewati.
- **Sisa risiko:** Saat PIN belum aktif (default), flow tetap Splash → Onboarding → Login (FakeAuth) → MainShell. Siapa pun bisa login dengan password ≥ 6 karakter apa saja.
- **Lokasi:** `feature/auth/.../data/repository/FakeAuthRepository.kt:14`
- **Fix (pilih salah satu):**
  - **A**: Hapus Login screen entirely, jadikan PIN setup wajib di onboarding step terakhir. Onboarding → SetupPin → MainShell.
  - **B**: Implement real auth (Firebase Auth / backend JWT) untuk multi-device. Simpan session token di `EncryptedSharedPreferences`. PIN tetap retained sebagai second factor lokal.

#### S-4. `allowBackup` ✅ DONE (PR1a)
- ✅ `android:allowBackup="false"` di AndroidManifest.xml
- ✅ Hapus `android:dataExtractionRules` + `android:fullBackupContent` reference + delete file XML (sudah dead reference)
- ⏳ Data portability untuk legit user → export feature (PR future) atau cloud sync (Phase 4)

#### S-5. API key Gemini ✅ DONE (PR1a infrastructure, akan dikonsumsi PR2)
- ✅ MVP strategi: `local.properties` (gitignored) → `buildConfigField` di `:core:network` → `BuildConfig.GEMINI_API_KEY`
- ✅ Helper `Project.readSecret(name)` di build-logic (local.properties → env fallback)
- ✅ `GeminiConfig` data class + Hilt provider, ready di-inject saat Tania module dibangun
- ✅ CI workflow inject `GEMINI_API_KEY` + `GEMINI_MODEL` dari GitHub Secrets via env var
- ✅ `local.properties.example` + README setup section
- ⏳ Backend proxy migration → Phase 4 saat backend SabiTani dibangun. Key rotation playbook akan ditulis saat key pertama dipakai produksi.

### 🟠 Penting (sebelum buka public beta)

#### S-6. Network Security Config ✅ DONE (PR pre-beta hardening)
- ✅ `app/src/main/res/xml/network_security_config.xml`: `cleartextTrafficPermitted="false"` + system trust anchors
- ✅ Manifest referensi `android:networkSecurityConfig="@xml/network_security_config"` + `android:usesCleartextTraffic="false"`
- ⏳ Certificate pinning ditunda sampai endpoint produksi resmi ada (Phase 2/4)

#### S-7. EXIF metadata di foto sebelum upload ke Gemini ⏸ DEFER ke Phase 2
- **Risiko:** Saat scan tanaman, foto biasanya mengandung GPS coordinates + timestamp + device info di EXIF. Upload ke pihak ketiga (Google) tanpa di-strip = bocor lokasi kebun + identitas device.
- **Fix (dikerjakan saat fitur Tania scan dibangun):**
  - Strip EXIF sebelum upload pakai `ExifInterface` (remove GPS tags + timestamp tags).
  - Compress foto sebelum upload (kurangi ukuran payload).
  - Informasikan user via privacy notice sebelum scan pertama.
- **Alasan defer:** Belum ada UI scan/upload — utility prematur tanpa konsumen.

#### S-9. Crashlytics & analytics privacy ✅ DONE (PR pre-beta hardening)
- **Lokasi:** `:core:analytics`, Firebase Crashlytics di `libs.versions.toml`
- ✅ `AnalyticsConsent` (DataStore-backed, default OFF/opt-in)
- ✅ `CrashlyticsBridge` membungkus Firebase SDK — no-op kalau Firebase tidak terinisialisasi (no `google-services.json`)
- ✅ Plugin `google-services` + `firebase-crashlytics` apply conditional di `app/build.gradle.kts` — repo tetap buildable tanpa `google-services.json` (placeholder di local & CI). Saat user/CI provide file, plugin auto-apply.
- ✅ `AnalyticsInitializer` subscribe `consent.isEnabled` → `setCrashlyticsCollectionEnabled(enabled)`
- ✅ Toggle "Analitik Anonim" di SecuritySettingsScreen (Profil → Keamanan)
- ✅ `CrashlyticsAnalyticsHelper` menggantikan `LogAnalyticsHelper` (Timber log + Crashlytics bridge log)
- ⏳ Audit `FirebaseCrashlytics.log()` / `setCustomKey()` — N/A sekarang, belum ada call site. Saat Phase 2 ada call site PII, harus audit.
- ⏳ Privacy policy URL — S-16

#### S-10. Permission camera & storage (Phase 2) ⏸ DEFER ke Phase 2
- **Risiko:** Saat Tania scan ditambah, butuh permission CAMERA. Salah handle = security/UX issue.
- **Fix (dikerjakan saat fitur Tania scan dibangun):**
  - Minta permission **just-in-time** (saat user tap "Scan"), bukan di app start.
  - Pakai `accompanist-permissions` yang sudah ada di catalog.
  - Untuk file picker, pakai Photo Picker (Android 13+) atau Storage Access Framework — bukan `READ_EXTERNAL_STORAGE`.
- **Alasan defer:** Belum ada UI camera/scan.

#### S-11. Deep link validation (saat dibuka nanti) ⏸ DEFER sampai deep link ditambah
- **Risiko:** Deep link tanpa validasi bisa di-exploit untuk bypass auth flow (atau lock gate) dan buka screen sensitif tanpa unlock.
- **Fix (dikerjakan saat deep link pertama ditambah):**
  - Validasi parameter di setiap composable destination.
  - Lock guard di nav graph: kalau PIN aktif & user belum unlock, deep link → redirect ke UnlockRoute dulu.
  - Verifikasi intent source via `intent.scheme` whitelisting.
- **Alasan defer:** Saat ini AndroidManifest hanya ada satu activity launcher tanpa intent-filter deep link. Tidak ada attack surface.

### 🟡 Disarankan (production hardening)

#### S-12. FLAG_SECURE untuk screen sensitif
- **Risiko:** Screenshot/screen recording di screen dengan data finansial bocor lewat recent apps screen.
- **Fix:** Tambahkan `window.setFlags(FLAG_SECURE, FLAG_SECURE)` di `MainActivity` (atau via `DisposableEffect` per screen) saat berada di:
  - `CycleDetailScreen` (cost summary)
  - `UnlockScreen` / `SetupPinScreen` (PIN entry)
  - `SecuritySettingsScreen`

#### S-13. Root / tamper detection
- **Risiko:** Device yang di-root bisa baca/modifikasi Room DB (meski sudah SQLCipher, kunci di EncryptedSharedPreferences bisa di-extract dari KeyStore di device bermasalah).
- **Fix:**
  - Integrasikan **Play Integrity API** untuk verifikasi device integrity sebelum sync ke cloud (saat Phase 4).
  - Tampilkan warning (bukan block) kalau device di-root.

#### S-14. Dependency vulnerability scanning
- **Risiko:** Library outdated atau punya CVE yang belum di-patch.
- **Fix:**
  - Enable **Dependabot** di GitHub repo settings.
  - Periodic `./gradlew dependencyCheckAnalyze` (OWASP plugin) atau Snyk integration.

#### S-16. Privacy Policy & Terms of Service
- **Risiko:** Wajib untuk publish ke Play Store + GDPR/UU PDP Indonesia compliance.
- **Fix:**
  - Tulis privacy policy yang jelas (data apa yang dikumpulkan, ke mana dikirim, retention).
  - Tampilkan saat onboarding + accessible dari Settings.
  - Sediakan opsi delete account + export data.

---

## 4. Konvensi yang sudah disepakati

- **Push setiap perubahan via PR** ke `main` branch — manual merge approval oleh Khalif (kecuali user authorize otomatisasi per-sesi)
- **Lint/detekt/format wajib clean** sebelum push
- **CI hijau wajib** sebelum merge (sekarang sudah blocking via branch protection)
- **Tidak ada scope creep** tanpa diskusi terlebih dahulu
- **Domain layer murni Kotlin** (enforced via Konsist — `androidx.*` dilarang di package `.domain`)
- **No cross-feature module imports** (enforced via Konsist)
- **File > ~150 lines** dipertimbangkan untuk di-split (SRP)
- **Tania AI provider:** Gemini API (free tier sufficient untuk MVP)
- **Knowledge base:** seed JSON statis (curated) + LLM sebagai reasoner, BUKAN LLM only
- **PIN opt-in, biometric opsional**: default off. User aktifkan via Profil → Keamanan. PIN setup wajib sebelum biometric.
