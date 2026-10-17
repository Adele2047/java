package id.ac.trinita.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class StreamBenchmark {
    @Param({"1000", "100000", "5000000"})
    public int size;

    private List<Integer> values;

    @Setup(Level.Trial)
    public void setUp() {
        values = new ArrayList<>(size);
        IntStream.range(0, size).forEach(values::add);
    }

    @Benchmark
    public long forLoop() {
        long sum = 0;
        for (int value : values) {
            if ((value & 1) == 0) {
                sum += value * 2L;
            }
        }
        return sum;
    }

    @Benchmark
    public long stream() {
        return values.stream()
            .filter(value -> (value & 1) == 0)
            .mapToLong(value -> value * 2L)
            .sum();
    }

    @Benchmark
    public long parallelStream() {
        return values.parallelStream()
            .filter(value -> (value & 1) == 0)
            .mapToLong(value -> value * 2L)
            .sum();
    }
}
