import io.functional.PatternMatching;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PatternMatchingTest {

    @Test
    public void check_type_and_map_correctly() {
        int length = PatternMatching.when("abc")
                .is(String.class).thenReturn(String::length)
                .execute();

        assertThat(length).isEqualTo(3);
    }

    @Test
    public void returns_first_registered_type_rule() {
        String result = PatternMatching.when("abc")
                .is(String.class).thenReturn(s -> s + "1")
                .is(String.class).thenReturn(s -> s + "2")
                .execute();

        assertThat(result).isEqualTo("abc1");
    }

    @Test
    public void executes_operations_in_lazy_mode() {
        PatternMatching.MatchingChain<String, Object> operation = PatternMatching.when("abc")
                .is(String.class).thenReturn(s -> {
                    throw new RuntimeException("lazy one!");
                });

        assertThatThrownBy(operation::execute).hasMessage("lazy one!");
    }

    class A {}
    private class B extends A {}

    @Test
    public void match_parent_class_firstly_as_more_generic() {
        String result = PatternMatching.when(new B())
                .is(A.class).thenReturn(c -> "A")
                .is(B.class).thenReturn(c -> "B")
                .execute();

        assertThat(result).isEqualTo("A");
    }

    @Test
    public void match_parent_class_firstly_as_more_generic_even_if_it_was_defined_after_other_matches() {
        String result = PatternMatching.when(new B())
                .is(B.class).thenReturn(c -> "B")
                .is(A.class).thenReturn(c -> "A")
                .execute();

        assertThat(result).isEqualTo("A");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void throws_exception_if_execute_run_without_any_rule_specified() {
        PatternMatching.when("abc")
                .is(Object.class).thenReturn(null)
                .execute();
    }
}
