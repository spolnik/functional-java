package io.functional;

public interface Function<T, U> {
    U apply(T arg);

    static <T, U, V> Function<Function<U, V>,
                            Function<Function<T, U>,
                                     Function<T, V>>> compose() {
        return f -> g -> x -> f.apply(g.apply(x));
    }

    static <T, U, V> Function<Function<T, U>,
                     Function<Function<U, V>,
                              Function<T, V>>> andThen() {
        return f -> g -> x -> g.apply(f.apply(x));
    }
}
