import io.functional.Function;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionTest {

    @Test
    public void composes_functions() {
        Function<Integer, Integer> triple = x -> x * 3;
        Function<Integer, Integer> square = x -> x * x;

        Integer result = Function.<Integer, Integer, Integer>compose().apply(square).apply(triple).apply(2);

        assertThat(result).isEqualTo(36);
    }

    @Test
    public void chain_functions() {
        Function<Integer, Integer> triple = x -> x * 3;
        Function<Integer, Integer> square = x -> x * x;

        Integer result = Function.<Integer, Integer, Integer>andThen().apply(square).apply(triple).apply(2);

        assertThat(result).isEqualTo(12);
    }

    @Test
    public void composes_functions_of_different_types() {
        Function<Double, Integer> f = a -> (int) (a * 3);
        Function<Long, Double> g = a -> a + 2.0;

        Integer result = Function.<Long, Double, Integer>compose().apply(f).apply(g).apply(1L);

        assertThat(result).isEqualTo(9);
    }

    @Test
    public void chain_functions_of_different_types() {
        Function<Double, Integer> f = a -> (int) (a * 3);
        Function<Long, Double> g = a -> a + 2.0;

        Integer result = Function.<Long, Double, Integer>andThen().apply(g).apply(f).apply(1L);

        assertThat(result).isEqualTo(9);
    }
}
