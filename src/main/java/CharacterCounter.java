import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

class CharacterCounter {
    Map<Character, Long> countIn(String input) {
        return input == null || input.isEmpty()
                ? Collections.emptyMap()
                : parseAndCountString(input);
    }

    private Map<Character, Long> parseAndCountString(String input) {
        return stream(input.split(""))
                .map(item -> item.charAt(0))
                .collect(groupingBy(c -> c, counting()));
    }
}
