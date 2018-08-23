import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CharacterCountTest {

    private final CharacterCounter counter = new CharacterCounter();

    @Test
    public void counts_single_character() {
        Map<Character, Long> result = counter.countIn("a");

        assertThat(result.get('a')).isEqualTo(1);
    }

    @Test
    public void count_repeated_character() {
        Map<Character, Long> result = counter.countIn("aa");

        assertThat(result.get('a')).isEqualTo(2);
    }

    @Test
    public void count_different_characters() {
        Map<Character, Long> result = counter.countIn("ab");

        assertThat(result.get('a')).isEqualTo(1);
        assertThat(result.get('b')).isEqualTo(1);
    }

    @Test
    public void count_characters_in_complex_string() {
        Map<Character, Long> result = counter.countIn("cabaac");

        HashMap<Character, Long> expected = new HashMap<>();
        expected.put('a', 3L);
        expected.put('b', 1L);
        expected.put('c', 2L);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void return_empty_map_for_null() {
        Map<Character, Long> result = counter.countIn(null);

        assertThat(result).isEmpty();
    }

    @Test
    public void return_empty_map_for_empty_string() {
        Map<Character, Long> result = counter.countIn("");

        assertThat(result).isEmpty();
    }
}
