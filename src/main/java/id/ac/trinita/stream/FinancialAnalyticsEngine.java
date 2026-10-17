package id.ac.trinita.stream;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinancialAnalyticsEngine {
    public List<Transaction> getTop3LargestTransfers(List<Transaction> transactions, String accountId) {
        return transactions.stream()
            .filter(t -> t.accountId().equals(accountId))
            .filter(t -> t.type() == TransactionType.TRANSFER)
            .sorted(Comparator.comparingDouble(Transaction::amount).reversed())
            .limit(3)
            .toList();
    }

    public Map<String, Double> calculateTotalVolumePerCity(List<Transaction> transactions) {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::locationCity,
                Collectors.summingDouble(Transaction::amount)
            ));
    }

    public Map<Boolean, List<Transaction>> partitionBySuspiciousRisk(List<Transaction> transactions) {
        return transactions.stream()
            .collect(Collectors.partitioningBy(Transaction::isFlaggedSuspicious));
    }

    public Summary getTransactionSummary(List<Transaction> transactions) {
        return transactions.stream().collect(Collectors.teeing(
            Collectors.counting(),
            Collectors.reducing(
                BigDecimal.ZERO,
                transaction -> BigDecimal.valueOf(transaction.amount()),
                BigDecimal::add
            ),
            (count, total) -> new Summary(
                count,
                total,
                count == 0
                    ? BigDecimal.ZERO
                    : total.divide(BigDecimal.valueOf(count), MathContext.DECIMAL128)
            )
        ));
    }
}
