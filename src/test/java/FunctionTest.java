import io.functional.Function;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionTest {

    @Test
    public void composes_functions() {

        Integer result = Function.<Integer, Integer, Integer>compose()
                .apply(x -> x * x)
                .apply(x -> x * 3)
                .apply(2);

        assertThat(result).isEqualTo(36);
    }

    @Test
    public void chain_functions() {

        Integer result = Function.<Integer, Integer, Integer>andThen()
                .apply(x -> x * x)
                .apply(x -> x * 3)
                .apply(2);

        assertThat(result).isEqualTo(12);
    }

    @Test
    public void composes_functions_of_different_types() {

        Integer result = Function.<Long, Double, Integer>compose()
                .apply(a -> (int) (a * 3))
                .apply(a -> a + 2.0)
                .apply(1L);

        assertThat(result).isEqualTo(9);
    }

    @Test
    public void chain_functions_of_different_types() {

        Integer result = Function.<Long, Double, Integer>andThen()
                .apply(a -> a + 2.0)
                .apply(a -> (int) (a * 3))
                .apply(1L);

        assertThat(result).isEqualTo(9);
    }
}
