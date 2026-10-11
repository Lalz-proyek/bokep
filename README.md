# 🐱 PinkCat Manager

Aplikasi Android utilitas bertema kucing pink untuk melihat informasi perangkat dan informasi aplikasi/permission yang dapat diakses secara resmi oleh Android.

<p align="center">
  <img src="docs/hero.jpg" alt="PinkCat Manager Mascot" width="340" style="border-radius: 24px; box-shadow: 0 10px 25px rgba(233, 30, 99, 0.2);" />
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

## ✨ Features

- 📱 **Device information**: Spesifikasi hardware & sistem operasi nyata (Brand, Model, CPU ABI & Cores, RAM aktual, Penyimpanan internal, Baterai, Jaringan Wi-Fi/Seluler, Bluetooth, dan Resolusi/Densitas Layar).
- 🔐 **Permission information**: Audit status izin sensitif secara legal melalui Android `PackageManager` (Kamera, Mikrofon, Lokasi, Notifikasi).
- 📦 **Installed apps**: Menampilkan seluruh aplikasi terpasang lengkap dengan icon, nama, package, versi, target SDK, dan tanggal instalasi.
- 🔎 **App search**: Pencarian instan berdasarkan nama aplikasi atau nama package.
- 🗂️ **Filter**: Filter cepat berdasarkan Semua Aplikasi, Punya Izin, Aplikasi Pengguna, dan Aplikasi Sistem.
- 🛡️ **Security check**: Pemeriksaan status Kunci Layar (Screen Lock), Opsi Pengembang (Developer Options), USB Debugging, dan panduan Google Play Protect.
- ⚙️ **Settings**: Pembersihan cache lokal aplikasi, pembukaan info pengaturan sistem, dan auto-refresh data sensor.
- 🌙 **Dark mode**: Dukungan mode Terang, Gelap, dan Mengikuti Sistem.
- 🐱 **Pink cat UI**: Desain modern Material 3 dengan warna pastel pink, putih, sentuhan violet, dan maskot kucing pink yang imut.
- 🔄 **Refresh device information**: Tombol penyegaran data yang langsung mengambil ulang status sistem Android terkini.

---

## 📥 Download APK

### Latest Release

Untuk mengunduh file APK siap install terbaru di perangkat Android Anda:

**Download APK:**  
👉 **[https://github.com/USERNAME/REPOSITORY/releases/latest](https://github.com/USERNAME/REPOSITORY/releases/latest)**

Nama file APK yang dihasilkan:
- `PinkCat-Manager-v1.0.0.apk`

> *Catatan: Ganti `USERNAME` dan `REPOSITORY` dengan nama akun dan repositori GitHub Anda.*

### Cara Install di HP Android:
1. Buka link rilis di atas dan download file APK.
2. Buka file APK yang telah diunduh di perangkat Android Anda.
3. Jika muncul dialog keamanan *"Install unknown apps"* atau *"Sumber tidak dikenal"*, pilih **Izinkan untuk browser/file manager ini**.
4. Tekan **Install** dan buka PinkCat Manager!

---

## 🛠️ Build dari Source Code

### Prasyarat:
- JDK 17 atau yang lebih baru
- Android SDK (API 34/35/36)

### Clone & Build

**Linux & macOS:**
```bash
git clone https://github.com/USERNAME/REPOSITORY.git
cd PinkCat-Manager
chmod +x gradlew
./gradlew assembleRelease
```

**Windows (Command Prompt / PowerShell):**
```cmd
git clone https://github.com/USERNAME/REPOSITORY.git
cd PinkCat-Manager
gradlew.bat assembleRelease
```

### Lokasi File APK Hasil Build:
```text
app/build/outputs/apk/release/
```

File APK siap install akan berada di direktori tersebut.

---

## 🚀 GitHub Actions CI/CD & Automated Releases

Repository ini telah dilengkapi dengan workflow GitHub Actions otomatis di folder `.github/workflows/`:

1. **`build-apk.yml`**  
   Otomatis mem-build APK setiap kali ada `push` atau `pull request` ke branch `main`, atau dapat dijalankan secara manual via `workflow_dispatch`. Hasil build disimpan sebagai **Artifact**.

2. **`release.yml`**  
   Otomatis membuat **GitHub Release** dan melampirkan file **`PinkCat-Manager-vX.Y.Z.apk`** setiap kali Anda membuat tag Git baru:

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions akan secara otomatis:
- Mem-build release APK.
- Menamai file APK menjadi `PinkCat-Manager-v1.0.0.apk`.
- Membuat halaman GitHub Release baru lengkap dengan release notes dan asset APK yang bisa langsung diunduh publik.

---

## 🔒 Privasi & Keamanan

- **100% Offline**: PinkCat Manager tidak memiliki modul pelacak, analitik pihak ketiga, atau pengiriman data ke server eksternal.
- **Tanpa Izin Berbahaya**: Aplikasi ini tidak meminta izin kontak, SMS, atau kamera untuk dirinya sendiri. Seluruh data perizinan diperoleh dari pembacaan legal sistem manajemen paket Android (`PackageManager`).
- **Bebas Secret / Keystore di Git**: Repositori telah dikonfigurasi dengan `.gitignore` ketat untuk menjaga keamanan signing key dan credential.

---

## 📄 Lisensi

Didistribusikan di bawah lisensi **Apache License 2.0**. Lihat file [`LICENSE`](LICENSE) untuk informasi lebih lanjut.
