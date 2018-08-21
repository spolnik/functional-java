package io.functional;

import java.util.function.Function;

public class PatternMatching<TInput> {
    private final TInput input;

    private PatternMatching(TInput input) {
        this.input = input;
    }

    public static <TInput> PatternMatching<TInput> when(TInput input) {
        return new PatternMatching<>(input);
    }

    public TypeChecking<TInput> is(Class<? super TInput> type) {
        return type.isAssignableFrom(input.getClass()) ?
                new MatchFound(this, type)
                : new NoMatch(this);
    }

    private Object execute() {
        return null;
    }

    public interface TypeChecking<T> {
        <R> ExecuteOperation<T, R> thenReturn(Function<T, R> operation);
    }

    public interface ExecuteOperation<T, R> {
        TypeChecking<T> is(Class<? super T> type);
        R execute();
    }

    private class DelegateExecuteOperation<T, R> implements ExecuteOperation<T, R> {

        private final PatternMatching<T> patternMatching;

        private DelegateExecuteOperation(PatternMatching<T> patternMatching) {
            this.patternMatching = patternMatching;
        }

        @Override
        public TypeChecking<T> is(Class<? super T> type) {
            return patternMatching.is(type);
        }

        @SuppressWarnings("unchecked")
        @Override
        public R execute() {
            return (R) patternMatching.execute();
        }
    }

    private class MatchFound implements TypeChecking<TInput> {

        private final PatternMatching<TInput> patternMatching;
        private final Class<? super TInput> type;

        MatchFound(PatternMatching<TInput> patternMatching, Class<? super TInput> type) {
            this.patternMatching = patternMatching;
            this.type = type;
        }

        @Override
        public <R> ExecuteOperation<TInput, R> thenReturn(Function<TInput, R> operation) {
            return null;
        }
    }

    private class NoMatch implements TypeChecking<TInput> {

        private final PatternMatching<TInput> patternMatching;

        NoMatch(PatternMatching<TInput> patternMatching) {
            this.patternMatching = patternMatching;
        }

        @Override
        public <R> ExecuteOperation<TInput, R> thenReturn(Function<TInput, R> operation) {
            return null;
        }
    }

    private class MatchingRule<T, R> {

    }
}
