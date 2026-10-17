package id.ac.trinita.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class FinancialAnalyticsEngineTest {
    private final FinancialAnalyticsEngine engine = new FinancialAnalyticsEngine();

    @Test
    void summaryUsesExactDecimalTotalsAndAverage() {
        List<Transaction> transactions = List.of(
            transaction(0.1),
            transaction(0.2),
            transaction(0.3)
        );

        Summary summary = engine.getTransactionSummary(transactions);

        assertEquals(3, summary.count());
        assertEquals(new BigDecimal("0.6"), summary.total());
        assertEquals(new BigDecimal("0.2"), summary.average());
    }

    @Test
    void summaryForEmptyTransactionsIsZero() {
        Summary summary = engine.getTransactionSummary(List.of());

        assertEquals(0, summary.count());
        assertEquals(BigDecimal.ZERO, summary.total());
        assertEquals(BigDecimal.ZERO, summary.average());
    }

    private Transaction transaction(double amount) {
        return new Transaction(
            "TX", "ACC", amount, TransactionType.PURCHASE, "Manado", LocalDateTime.MIN, false
        );
    }
}
