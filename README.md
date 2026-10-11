# 🐱 PinkCat Manager

Aplikasi Android utilitas bertema kucing pink untuk melihat informasi perangkat dan informasi aplikasi/permission yang dapat diakses secara resmi oleh Android.

<p align="center">
  <img src="docs/hero.jpg" alt="PinkCat Manager Mascot" width="320" style="border-radius: 24px; box-shadow: 0 10px 25px rgba(233, 30, 99, 0.2);" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-pink?logo=android" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin" alt="Language" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-pink?logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-lightgrey" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-blue" alt="Target SDK" />
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License" />
</p>

---

## ✨ Fitur Unggulan

- 📱 **Device information**: Spesifikasi hardware & sistem operasi nyata (Brand, Model, CPU ABI & Cores, RAM aktual, Penyimpanan internal, Baterai, Jaringan Wi-Fi/Seluler, Bluetooth, dan Resolusi/Densitas Layar).
- 🔐 **Permission information**: Audit status izin sensitif secara legal melalui Android `PackageManager` (Kamera, Mikrofon, Lokasi, Notifikasi).
- 📦 **Installed apps**: Menampilkan seluruh aplikasi terpasang lengkap dengan icon asli, nama, package, versi, target SDK, dan tanggal instalasi.
- 🔎 **App search**: Pencarian instan berdasarkan nama aplikasi atau nama package.
- 🗂️ **Filter**: Filter cepat berdasarkan Semua Aplikasi, Punya Izin, Aplikasi Pengguna, dan Aplikasi Sistem.
- 🛡️ **Security check**: Pemeriksaan status Kunci Layar (Screen Lock), Opsi Pengembang (Developer Options), USB Debugging, dan panduan resmi Google Play Protect.
- ⚙️ **Settings**: Pembersihan cache lokal aplikasi, pembukaan info pengaturan sistem, dan auto-refresh data sensor.
- 🌙 **Dark mode**: Dukungan mode Terang, Gelap, dan Mengikuti Sistem.
- 🐱 **Pink cat UI**: Desain modern Material 3 dengan warna pastel pink, putih, sentuhan violet, dan maskot kucing pink yang imut.
- 🔄 **Refresh device information**: Tombol penyegaran data yang langsung mengambil ulang status sistem Android terkini.

---

## 📥 Download File APK

### 1. Unduh via GitHub Releases (Rekomendasi Langsung)

File APK yang siap di-install di perangkat Android dapat diunduh langsung melalui tautan rilis berikut:

👉 **[Download APK Terbaru (GitHub Releases)](https://github.com/mjalalaopang201265/PinkCat-Manager/releases/latest)**

Nama file APK pada setiap rilis:
- `PinkCat-Manager-v1.0.0-debug.apk`

> *Catatan: Jika rilis belum diterbitkan, workflow akan membangun file APK saat tag versi seperti `v1.0.0` di-push.*

### 2. Unduh via GitHub Actions Artifact

Setiap kali ada commit atau push ke branch `main`, GitHub Actions akan otomatis mem-build APK debug dan mengunggahnya sebagai Artifact:
- Nama Artifact: **`PinkCat-Manager-Debug-APK`**
- Dapat diunduh di tab **Actions** > Pilih workflow run terbaru > Bagian **Artifacts**.

---

## 🛠️ Build APK dari Source Code

### Prasyarat:
- JDK 17 atau yang lebih baru
- Android SDK (API 34/35/36)

### Perintah Build:

**Linux & macOS:**
```bash
git clone https://github.com/mjalalaopang201265/PinkCat-Manager.git
cd PinkCat-Manager
chmod +x gradlew
./gradlew assembleDebug
```

**Windows (Command Prompt / PowerShell):**
```cmd
git clone https://github.com/mjalalaopang201265/PinkCat-Manager.git
cd PinkCat-Manager
gradlew.bat assembleDebug
```

### Lokasi File APK Hasil Build:
File APK yang dihasilkan Gradle berada di:
```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🚀 GitHub Actions CI/CD & Automated Releases

Project ini memiliki dua workflow GitHub Actions resmi di direktori `.github/workflows/`:

### 1. `build-apk.yml`
- **Trigger**: Setiap `push` ke branch `main`, `pull_request`, atau dijalankan secara manual (*workflow_dispatch*).
- **Proses**: Menjalankan `./gradlew assembleDebug`, memvalidasi bahwa `app/build/outputs/apk/debug/app-debug.apk` benar-benar ada, dan mengunggah artifact dengan nama **`PinkCat-Manager-Debug-APK`**.

### 2. `release.yml`
- **Trigger**: Setiap kali membuat tag rilis seperti `v1.0.0`, `v1.1.0`.
- **Proses**:
  1. Menjalankan `./gradlew assembleDebug`.
  2. Memverifikasi integritas file APK.
  3. Menamai file menjadi `PinkCat-Manager-${TAG}-debug.apk` (contoh: `PinkCat-Manager-v1.0.0-debug.apk`).
  4. Menerbitkan GitHub Release dan melampirkan file APK tersebut sebagai file Asset publik yang bisa langsung di-download siapa saja.

---

## 📲 Cara Publikasi & Menerbitkan Release Pertama Kali

Jalankan perintah berikut di komputer Anda:

```bash
# 1. Inisialisasi dan Push kode ke GitHub
git init
git add .
git commit -m "feat: inisialisasi PinkCat Manager v1.0.0"
git branch -M main
git remote add origin https://github.com/mjalalaopang201265/PinkCat-Manager.git
git push -u origin main

# 2. Buat Tag Release v1.0.0
git tag v1.0.0
git push origin v1.0.0
```

Setelah `git push origin v1.0.0` selesai:
1. Buka tab **Actions** di repositori GitHub Anda untuk melihat proses build otomatis berjalan.
2. Dalam waktu 1–2 menit, buka halaman **Releases** di `https://github.com/mjalalaopang201265/PinkCat-Manager/releases`.
3. File **`PinkCat-Manager-v1.0.0-debug.apk`** telah tersedia di bagian Assets dan siap diunduh!

---

## 🔒 Privasi & Keamanan

- **100% Offline**: PinkCat Manager tidak mengirimkan data ke internet. Seluruh analisis spesifikasi dan perizinan diproses secara lokal di perangkat.
- **Bebas Secret / Keystore di Git**: Repositori telah dilengkapi `.gitignore` yang ketat agar tidak ada credential atau keystore pribadi yang ter-commit.

---

## 📄 Lisensi

Didistribusikan di bawah lisensi **Apache License 2.0**. Lihat file [`LICENSE`](LICENSE) untuk informasi lebih lanjut.
