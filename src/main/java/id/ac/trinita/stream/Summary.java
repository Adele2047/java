package id.ac.trinita.stream;

import java.math.BigDecimal;

public record Summary(long count, BigDecimal total, BigDecimal average) {}
