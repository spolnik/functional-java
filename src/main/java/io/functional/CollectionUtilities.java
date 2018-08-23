package io.functional;

import java.util.ArrayList;
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

    public static <T> T head(List<T> list) {
        if (list.size() == 0) {
            throw new IllegalStateException("head of empty list");
        }

        return list.get(0);
    }

    public static <T> List<T> tail(List<T> list) {
        if (list.size() == 0) {
            throw new IllegalStateException("head of empty list");
        }

        List<T> tail = copy(list);
        tail.remove(0);
        return list(tail);
    }

    public static <T> List<T> append(List<T> list, T item) {
        List<T> copy = copy(list);
        copy.add(item);
        return list(copy);
    }

    public static <T, R> R foldLeft(List<T> list, R identity, Function<R, Function<T, R>> f) {
        R result = identity;

        for (T item : list) {
            result = f.apply(result).apply(item);
        }

        return result;
    }

    public static <T, R> R foldRight(List<T> list, R identity, Function<T, Function<R, R>> f) {
        R result = identity;

        for (int i = list.size(); i > 0; i--) {
            result = f.apply(list.get(i - 1)).apply(result);
        }

        return result;
    }

    private static <T> List<T> copy(List<T> list) {
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }
}
