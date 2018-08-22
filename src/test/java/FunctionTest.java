import io.functional.Function;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionTest {

    private final Function<Integer, Integer> square = x -> x * x;
    private final Function<Integer, Integer> times2 = x -> x * 2;

    @Test
    public void construct_and_run_simple_function() {
        Function<Integer, Double> times3AndConvertToDouble = x -> (double) (x * 3);
        Double result = times3AndConvertToDouble.apply(2);

        assertThat(result).isEqualTo(6.0);
    }

    @Test
    public void combine_function_with_another_ones() {
        Function<Integer, Integer> times2AndThenSquare = square.compose(times2);

        Integer result = times2AndThenSquare.apply(2);

        assertThat(result).isEqualTo(16);
    }

    @Test
    public void apply_another_func_after_defined_one() {
        Function<Integer, Integer> squareAndThenTimes2 = square.andThen(times2);

        Integer result = squareAndThenTimes2.apply(2);

        assertThat(result).isEqualTo(8);
    }

    @Test
    public void high_order_compose() {

        Integer result = Function.<Integer, Integer, Integer>higherCompose()
                .apply(x -> x * x)
                .apply(x -> x * 3)
                .apply(2);

        assertThat(result).isEqualTo(36);
    }

    @Test
    public void higher_and_then() {

        Integer result = Function.<Integer, Integer, Integer>higherAndThen()
                .apply(x -> x * x)
                .apply(x -> x * 3)
                .apply(2);

        assertThat(result).isEqualTo(12);
    }

    @Test
    public void composes_functions_of_different_types() {

        Integer result = Function.<Long, Double, Integer>higherCompose()
                .apply(a -> (int) (a * 3))
                .apply(a -> a + 2.0)
                .apply(1L);

        assertThat(result).isEqualTo(9);
    }

    @Test
    public void chain_functions_of_different_types() {

        Integer result = Function.<Long, Double, Integer>higherAndThen()
                .apply(a -> a + 2.0)
                .apply(a -> (int) (a * 3))
                .apply(1L);

        assertThat(result).isEqualTo(9);
    }
}
