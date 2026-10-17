# Benchmark Stream API

Proyek ini membandingkan tiga cara melakukan operasi yang sama pada daftar bilangan
`0..n-1`: filter nilai genap, kalikan dengan 2, lalu jumlahkan sebagai `long`.
Benchmark dibuat dengan JMH 1.37 (rilis terbaru di Maven Central saat konfigurasi
ini dibuat).

## Menjalankan

```sh
mvn test
mvn -DskipTests package
java -jar target/benchmarks.jar 'id.ac.trinita.stream.StreamBenchmark.*' \
  -rf json -rff target/benchmark-results.json
```

JMH mengukur `forLoop`, `stream`, dan `parallelStream` untuk 1.000, 100.000, dan
5.000.000 elemen. Tiap benchmark menggunakan 3 iterasi pemanasan, 5 iterasi
pengukuran, dan 2 fork. Data dibuat di `@Setup`, di luar waktu yang diukur.

## Hasil

Diukur pada 17 Oktober 2026 dengan JMH 1.37 dan JDK 25.0.4.1 (Java HotSpot 64-Bit
Server VM), Apple M5 Pro, arm64, 15 CPU core yang terlihat oleh macOS, dan RAM
24 GiB. Waktu adalah rata-rata mikrodetik per operasi; deviasi adalah simpangan
baku sampel dari 10 hasil iterasi pengukuran (5 iterasi pada masing-masing dari
2 fork).

| Elemen | `for` (µs/op ± deviasi) | `stream()` (µs/op ± deviasi) | `parallelStream()` (µs/op ± deviasi) |
|---:|---:|---:|---:|
| 1.000 | 1.155 ± 0.019 | 0.586 ± 0.008 | 37.968 ± 0.679 |
| 100.000 | 56.683 ± 2.163 | 57.446 ± 2.972 | 64.368 ± 3.633 |
| 5.000.000 | 3,343.120 ± 199.970 | 3,289.764 ± 243.550 | 1,551.573 ± 224.352 |

Pada mesin ini `parallelStream()` mulai menang pada **5.000.000 elemen**,
dibandingkan kedua implementasi sekuensial. Di 1.000 elemen, pembagian tugas
fork/join, penjadwalan, dan penggabungan hasil memerlukan jauh lebih banyak waktu
daripada operasi sederhana per elemen. Pada 100.000 elemen, biaya tersebut masih
belum terbayar. Ambang ini bergantung pada perangkat keras, beban sistem, dan
biaya kerja per elemen; hasil ini bukan angka universal.

## Race saat memutasi `ArrayList` bersama

Jangan menambahkan elemen ke satu `ArrayList` bersama di dalam operasi paralel:
`ArrayList` tidak thread-safe. Penulisan bersamaan dapat kehilangan pembaruan,
menghasilkan ukuran yang salah, atau merusak struktur daftar. Tes demonstrasi
berikut diisolasi dengan profil agar tidak membuat tes reguler nondeterministik:

```sh
mvn -P race-demo test
```

Tes menjalankan operasi paralel 10 kali dan mengharapkan semua 1.000.000 elemen
masuk. Di mesin pengukuran, tes gagal 10/10 kali; ukuran aktual bervariasi antara
66.626 dan 91.762, bukan 1.000.000. Kegagalan dapat berubah antar-run dan mesin.
Gunakan operasi reduksi yang aman untuk paralel seperti `collect(Collectors.toList())`
alih-alih memutasi daftar bersama.

## Ringkasan transaksi

Laporan statistik di `StreamApp` menggunakan `Collectors.teeing` untuk
menghasilkan `Summary(count, total, average)`. Nilai uang dihitung dengan
`BigDecimal`; rata-rata kosong menghasilkan nol.
