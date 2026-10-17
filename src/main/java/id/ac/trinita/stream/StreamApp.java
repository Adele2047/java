package id.ac.trinita.stream;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class StreamApp {
    public static void main(String[] args) {
        System.out.println("=== SISTEM ANALITIK TRANSAKSI FINANSIAL (STREAM API) ===");

        List<Transaction> dataset = List.of(
            new Transaction("TX-001", "ACC-7788", 500_000.0, TransactionType.PURCHASE, "Manado", LocalDateTime.now(), false),
            new Transaction("TX-002", "ACC-7788", 12_500_000.0, TransactionType.TRANSFER, "Jakarta", LocalDateTime.now(), false),
            new Transaction("TX-003", "ACC-7788", 25_000_000.0, TransactionType.TRANSFER, "Jakarta", LocalDateTime.now(), true),
            new Transaction("TX-004", "ACC-9900", 2_000_000.0, TransactionType.WITHDRAWAL, "Tomohon", LocalDateTime.now(), false),
            new Transaction("TX-005", "ACC-7788", 8_000_000.0, TransactionType.TRANSFER, "Surabaya", LocalDateTime.now(), false),
            new Transaction("TX-006", "ACC-7788", 15_000_000.0, TransactionType.TRANSFER, "Manado", LocalDateTime.now(), false),
            new Transaction("TX-007", "ACC-9900", 750_000.0, TransactionType.BILL_PAYMENT, "Manado", LocalDateTime.now(), false)
        );

        FinancialAnalyticsEngine engine = new FinancialAnalyticsEngine();

        // 1. Eksekusi Top 3 Transfers untuk Rekening ACC-7788
        System.out.println("\n--- Top 3 Transaksi Transfer Terbesar (ACC-7788) ---");
        engine.getTop3LargestTransfers(dataset, "ACC-7788")
            .forEach(t -> System.out.printf("TxID: %s | Rp%,.2f | Kota: %s%n", 
                t.transactionId(), t.amount(), t.locationCity()));

        // 2. Eksekusi Volume Transaksi Per Kota
        System.out.println("\n--- Total Volume Finansial Per Kota ---");
        Map<String, Double> volumePerKota = engine.calculateTotalVolumePerCity(dataset);
        volumePerKota.forEach((kota, total) -> 
            System.out.printf("Kota: %-10s -> Total: Rp%,.2f%n", kota, total));

        // 3. Eksekusi Partisi Fraud Risk
        System.out.println("\n--- Partisi Status Kecurigaan (Fraud Flagged) ---");
        Map<Boolean, List<Transaction>> partisi = engine.partitionBySuspiciousRisk(dataset);
        System.out.println("Jumlah Transaksi Valid & Aman  : " + partisi.get(false).size());
        System.out.println("Jumlah Transaksi Berisiko Fraud: " + partisi.get(true).size());

        // 4. Eksekusi Ringkasan Statistik
        System.out.println("\n--- Statistik Deskriptif Nominal Finansial ---");
        Summary summary = engine.getTransactionSummary(dataset);
        double highest = dataset.stream().mapToDouble(Transaction::amount).max().orElse(0.0);
        System.out.printf("Total Volume: Rp%,.2f | Rata-rata: Rp%,.2f | Tertinggi: Rp%,.2f | Jumlah Transaksi: %d%n",
            summary.total(), summary.average(), highest, summary.count());
    }
}