import io.functional.PatternMatching;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PatternMatchingTest {

    @Test
    public void check_type_and_map_correctly() {
        int length = PatternMatching.when("abc")
                .is(String.class).thenReturn(String::length)
                .execute();

        assertThat(length).isEqualTo(3);
    }
}
