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
}
