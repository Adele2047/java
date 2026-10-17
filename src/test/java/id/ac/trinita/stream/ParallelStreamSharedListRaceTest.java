package id.ac.trinita.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.stream.IntStream;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;

@Tag("race-demo")
class ParallelStreamSharedListRaceTest {
    @RepeatedTest(10)
    void mutatingOneArrayListFromParallelStreamCanLoseElements() {
        int elementCount = 1_000_000;
        ArrayList<Integer> shared = new ArrayList<>(elementCount);

        IntStream.range(0, elementCount).parallel().forEach(value -> shared.add(value));

        assertEquals(elementCount, shared.size());
    }
}
