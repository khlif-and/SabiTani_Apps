# SabiTani — Task & Roadmap

Dokumen ini melacak progres pengembangan aplikasi **SabiTani** beserta hal-hal keamanan yang harus diperhatikan sebelum rilis production.

> Update terakhir: 2026-05-22
> PR Phase 1: https://github.com/khlif-and/SabiTani_Apps/pull/1

---

## 1. Sudah selesai

### Fondasi & infrastruktur
- [x] Modular project structure (Now-in-Android style: `:core:*` + `:feature:*` + convention plugins)
- [x] Hilt DI + KSP + Compose + Material 3 + Room 2.6 + DataStore + kotlinx-datetime + Orbit MVI
- [x] Konsist architecture tests (domain murni, no cross-feature deps)
- [x] Detekt + Spotless + Dependency Guard + Konsist
- [x] Splash, Onboarding, Login screens (login masih *FakeAuthRepository* — lihat bagian Keamanan)
- [x] Git repository + GitHub remote + branch workflow

### Phase 1 — Fondasi data pertanian (PR #1)
- [x] Domain models di `:core:model`: `Farm`, `Plot`, `CropCycle`, `FarmActivity`, `Transaction`, `CycleCostSummary` + enums
- [x] Room database v1 di `:core:database` dengan 5 entity + foreign keys + indices + DAO + Hilt module + type converters + schema export
- [x] `:feature:plot` (baru) — FarmList, PlotList, PlotDetail screens (Orbit MVI)
- [x] `:feature:cycle` (baru) — CycleForm, CycleDetail screens dengan tabbed activity/transaction timeline + cost summary (laba-rugi) real-time per siklus tanam
- [x] Validasi input via `require()` di use case (nama wajib, luas > 0, dst.)
- [x] Lint + detekt clean, arch test passing, `assembleDebug` sukses

---

## 2. Belum dikerjakan

### Integrasi UX (harus segera)
- [ ] Wire `FarmListRoute` ke `SabiTaniAppNavGraph` setelah login (saat ini TODO masih di `app/.../SabiTaniAppNavGraph.kt:44`)
- [ ] Wire navigasi antar feature module di `:app`: PlotList → PlotDetail → CycleForm → CycleDetail
- [ ] Bottom navigation atau drawer untuk akses cepat ke feature utama

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
- [ ] Unit test untuk semua use case (`:feature:plot`, `:feature:cycle`)
- [ ] Unit test untuk repository impl dengan in-memory DAO
- [ ] Integration test untuk Room DAO (`androidTest/`)
- [ ] Compose UI test untuk form validation
- [ ] Konsist test tambahan: package layering, naming conventions
- [ ] Coverage minimum target (mis. 70% line coverage di domain & data)

### Technical debt / housekeeping
- [ ] `feature/auth` menggunakan `FakeAuthRepository` — perlu real auth (lihat Keamanan)
- [ ] `feature/auth/.../AuthRoute.kt` punya file-name vs declaration mismatch (detekt warning di kode lama)
- [ ] `MagicNumber` di `FakeAuthRepository` (`password.length >= 6`)
- [ ] `:core:data`, `:core:domain`, `:core:ui`, `:core:testing` folder ada tapi belum di-register di `settings.gradle.kts` — putuskan: dihapus atau diisi
- [ ] Migrasi Room saat schema berubah (sudah ada schema export, tinggal tambah `Migration` saat v2)
- [ ] CI/CD: GitHub Actions untuk run lint + test setiap PR (folder `.github/workflows/ci.yml` sudah ada — verifikasi isinya)

---

## 3. Yang perlu diperbaiki untuk **KEAMANAN**

Urutan dari paling kritis → kurang kritis. **Wajib selesai sebelum production release.**

### 🔴 Kritis (blocker production)

#### S-1. Authentication masih FAKE
- **Lokasi:** `feature/auth/.../data/repository/FakeAuthRepository.kt:14`
- **Risiko:** Siapa pun bisa login dengan password ≥ 6 karakter apa saja. Tidak ada verifikasi server, tidak ada session token, tidak ada logout.
- **Fix:**
  - Pilih provider: Firebase Auth (paling cepat untuk MVP), atau backend custom dengan JWT.
  - Simpan session token di `EncryptedSharedPreferences` atau `Android Keystore`, BUKAN di plain DataStore.
  - Implementasi refresh token + auto-logout saat token expired.

#### S-2. Data Room tidak ter-enkripsi
- **Lokasi:** `core/database/.../di/DatabaseModule.kt`
- **Risiko:** File `sabitani.db` di internal storage bisa dibaca oleh attacker di device yang di-root. Data finansial (laba/rugi, pengeluaran) + lokasi kebun adalah PII sensitif.
- **Fix:**
  - Integrasikan **SQLCipher for Android** (`net.zetetic:android-database-sqlcipher`) atau **Room with Cipher** (`androidx.sqlite:sqlite-bundled` + passphrase).
  - Passphrase di-derive dari Android Keystore-backed key.

#### S-3. Release build tidak di-minify / obfuscate
- **Lokasi:** `app/build.gradle.kts:19`
- **Detail:** `isMinifyEnabled = false` di build type `release`
- **Risiko:** APK release bisa di-reverse engineer dengan mudah. API key (saat Tania ditambah) akan terlihat.
- **Fix:**
  - Set `isMinifyEnabled = true` dan `isShrinkResources = true` untuk `release`.
  - Tambahkan ProGuard rules untuk: Room, Hilt, Orbit, kotlinx.serialization, kotlinx.datetime, Retrofit DTOs.
  - Test release build full flow sebelum publish.

#### S-4. `allowBackup="true"` + backup rules kosong
- **Lokasi:** `app/src/main/AndroidManifest.xml:7` + `app/src/main/res/xml/backup_rules.xml`
- **Risiko:** Auto-backup ke Google Drive bisa membawa `sabitani.db` (data finansial petani). Saat restore di device lain, data ikut.
- **Fix:** Pilih salah satu:
  - Set `android:allowBackup="false"` (paling aman).
  - Atau pertahankan tapi exclude database & sensitive prefs di `backup_rules.xml` dan `data_extraction_rules.xml`.

#### S-5. API key Gemini (Phase 2) — strategi penyimpanan
- **Risiko:** Embed API key langsung di code/git = key di-publik exposed (repo public). Auto-extracted dengan tools.
- **Fix (Phase 2 sebelum implementasi Tania):**
  - **Terbaik:** backend proxy. Aplikasi panggil backend, backend yang punya key.
  - **MVP shortcut:** key di `local.properties` (sudah gitignored) → di-inject via `buildConfigField` → minimal tidak di git, tapi *masih* visible di APK release. Gabungkan dengan minify + obfuscation. Tetap rotasi key secara berkala.
  - **JANGAN PERNAH** taruh di repo, dalam string resource, atau di SharedPreferences plain.

### 🟠 Penting (sebelum buka public beta)

#### S-6. Network Security Config belum ada
- **Risiko:** Saat Tania API/cloud sync online, default Android masih allow cleartext HTTP di beberapa kondisi.
- **Fix:** Buat `res/xml/network_security_config.xml`:
  - `cleartextTrafficPermitted="false"`
  - Sertifikat pinning untuk endpoint produksi (opsional tapi recommended).
  - Referensi di `AndroidManifest.xml` lewat `android:networkSecurityConfig`.

#### S-7. EXIF metadata di foto sebelum upload ke Gemini
- **Risiko:** Saat scan tanaman, foto biasanya mengandung GPS coordinates + timestamp + device info di EXIF. Upload ke pihak ketiga (Google) tanpa di-strip = bocor lokasi kebun + identitas device.
- **Fix:**
  - Strip EXIF sebelum upload pakai `ExifInterface` (remove GPS tags + timestamp tags).
  - Compress foto sebelum upload (kurangi ukuran payload).
  - Informasikan user via privacy notice sebelum scan pertama.

#### S-8. Logging Timber di production
- **Lokasi:** `app/.../SabiTaniApp.kt` (perlu di-verify)
- **Risiko:** Kalau Timber `DebugTree()` di-plant di release build, semua `Timber.d()` akan ke logcat — bisa expose data sensitif kalau ada developer yang `Timber.d(user.email)`.
- **Fix:**
  - Plant `DebugTree()` hanya di `BuildConfig.DEBUG`.
  - Untuk release, plant custom `Tree` yang kirim ke Crashlytics tapi **filter PII** (email, nominal, lokasi).

#### S-9. Crashlytics & analytics privacy
- **Lokasi:** `:core:analytics`, Firebase Crashlytics di `libs.versions.toml`
- **Risiko:** Custom keys/logs di Crashlytics bisa berisi data sensitif (email, lokasi kebun). Crash dengan stack trace bisa expose query parameters.
- **Fix:**
  - Audit semua `FirebaseCrashlytics.log()` / `setCustomKey()` — JANGAN kirim PII.
  - Implementasikan opt-in analytics (Material 3 toggle di Settings).
  - Privacy policy URL yang jelas.

#### S-10. Permission camera & storage (Phase 2)
- **Risiko:** Saat Tania scan ditambah, butuh permission CAMERA. Salah handle = security/UX issue.
- **Fix:**
  - Minta permission **just-in-time** (saat user tap "Scan"), bukan di app start.
  - Pakai `accompanist-permissions` yang sudah ada di catalog.
  - Untuk file picker, pakai Photo Picker (Android 13+) atau Storage Access Framework — bukan `READ_EXTERNAL_STORAGE`.

#### S-11. Deep link validation (saat dibuka nanti)
- **Risiko:** Deep link tanpa validasi bisa di-exploit untuk bypass auth flow atau buka screen sensitif tanpa login.
- **Fix:**
  - Validasi parameter di setiap composable destination.
  - Auth guard di nav graph: kalau user belum login, deep link → redirect ke login.
  - Verifikasi intent source via `intent.scheme` whitelisting.

### 🟡 Disarankan (production hardening)

#### S-12. FLAG_SECURE untuk screen sensitif
- **Risiko:** Screenshot/screen recording di screen dengan data finansial bocor lewat recent apps screen.
- **Fix:** Tambahkan `WindowCompat.setDecorFitsSystemWindows(window, false)` lalu `window.setFlags(FLAG_SECURE, FLAG_SECURE)` di `MainActivity` saat berada di screen `CycleDetailScreen` (cost summary).

#### S-13. Root / tamper detection
- **Risiko:** Device yang di-root bisa baca/modifikasi Room DB. Untuk data finansial moderately important.
- **Fix:**
  - Integrasikan **Play Integrity API** untuk verifikasi device integrity sebelum sync ke cloud (saat Phase 4).
  - Tampilkan warning (bukan block) kalau device di-root.

#### S-14. Dependency vulnerability scanning
- **Risiko:** Library outdated atau punya CVE yang belum di-patch.
- **Fix:**
  - Enable **Dependabot** di GitHub repo settings.
  - Periodic `./gradlew dependencyCheckAnalyze` (OWASP plugin) atau Snyk integration.

#### S-15. ProGuard rules untuk obfuscation Domain models
- **Risiko:** Setelah minify enabled, `kotlinx-serialization` annotated classes (route objects) bisa rusak.
- **Fix:** Tambahkan rules `-keep` untuk:
  - `@Serializable` classes di `:feature:*/presentation/screen`
  - Room entities & DAOs di `:core:database`
  - Hilt-generated code

#### S-16. Privacy Policy & Terms of Service
- **Risiko:** Wajib untuk publish ke Play Store + GDPR/UU PDP Indonesia compliance.
- **Fix:**
  - Tulis privacy policy yang jelas (data apa yang dikumpulkan, ke mana dikirim, retention).
  - Tampilkan saat onboarding + accessible dari Settings.
  - Sediakan opsi delete account + export data.

---

## 4. Konvensi yang sudah disepakati

- **Push setiap perubahan via PR** ke `main` branch — manual merge approval oleh Khalif
- **Lint/detekt/format wajib clean** sebelum push
- **Tidak ada scope creep** tanpa diskusi terlebih dahulu
- **Domain layer murni Kotlin** (enforced via Konsist)
- **No cross-feature module imports** (enforced via Konsist)
- **File > ~150 lines** dipertimbangkan untuk di-split (SRP)
- **Tania AI provider:** Gemini API (free tier sufficient untuk MVP)
- **Knowledge base:** seed JSON statis (curated) + LLM sebagai reasoner, BUKAN LLM only
