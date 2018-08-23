package io.functional;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static java.util.Collections.*;

public class CollectionUtilities {

    public static <T> List<T> list() {
        return emptyList();
    }

    public static <T> List<T> list(T t) {
        return singletonList(t);
    }

    public static <T> List<T> list(List<T> ts) {
        return unmodifiableList(ts);
    }

    @SafeVarargs
    public static <T> List<T> list(T... t) {
        return unmodifiableList(asList(copyOf(t, t.length)));
    }
}
