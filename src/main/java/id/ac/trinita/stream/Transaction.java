package id.ac.trinita.stream;

import java.time.LocalDateTime;

public record Transaction(
    String transactionId,
    String accountId,
    double amount,
    TransactionType type,
    String locationCity,
    LocalDateTime timestamp,
    boolean isFlaggedSuspicious
) {}
