package io.functional;

import java.util.function.Function;

public final class PatternMatching<TInput> {

    private final TInput input;
    private final MatchingRule<TInput, ?> matchingRule;

    private PatternMatching(TInput input, MatchingRule<TInput, ?> matchingRule) {
        this.input = input;
        this.matchingRule = matchingRule;
    }

    public static <TInput> PatternMatching<TInput> when(TInput input) {
        return new PatternMatching<>(input, new MatchingRule<>());
    }

    public TypeChecking<TInput> is(Class<? super TInput> type) {
        return type.isAssignableFrom(input.getClass())
                ? new MatchFound<>(this, type, input)
                : new NoMatch<>(this);
    }

    private Object execute() {
        if (matchingRule.isEmpty()) {
            throw new UnsupportedOperationException("You have to define at least one correct rule");
        }

        return matchingRule.operation.apply(input);
    }

    public interface TypeChecking<T> {
        <R> ExecuteOperation<T, R> thenReturn(Function<T, R> operation);
    }

    public interface ExecuteOperation<T, R> {
        TypeChecking<T> is(Class<? super T> type);
        R execute();
    }

    private final static class DelegateExecuteOperation<T, R> implements ExecuteOperation<T, R> {

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

    private final static class MatchFound<T> implements TypeChecking<T> {

        private final PatternMatching<T> patternMatching;
        private final Class<? super T> newType;
        private final T input;

        MatchFound(PatternMatching<T> patternMatching, Class<? super T> newType, T input) {
            this.patternMatching = patternMatching;
            this.newType = newType;
            this.input = input;
        }

        @Override
        public <R> ExecuteOperation<T, R> thenReturn(Function<T, R> operation) {

            return shouldChooseNewMatchingRule()
                    ? new DelegateExecuteOperation<>(patternMatchingWithNewMatchingRule(operation))
                    : new DelegateExecuteOperation<>(patternMatching);

        }

        private <R> PatternMatching<T> patternMatchingWithNewMatchingRule(Function<T, R> operation) {
            return new PatternMatching<>(input, new MatchingRule<>(newType, operation));
        }

        private boolean shouldChooseNewMatchingRule() {
            if (patternMatching.matchingRule.isEmpty()) {
                return true;
            }

            return patternMatching.matchingRule.currentType != newType &&
                    isNewTypeParentOfCurrentType(patternMatching.matchingRule.currentType);
        }

        private boolean isNewTypeParentOfCurrentType(Class<? super T> currentType) {
            return newType.isAssignableFrom(currentType);
        }
    }

    private final static class NoMatch<T> implements TypeChecking<T> {

        private final PatternMatching<T> patternMatching;

        NoMatch(PatternMatching<T> patternMatching) {
            this.patternMatching = patternMatching;
        }

        @Override
        public <R> ExecuteOperation<T, R> thenReturn(Function<T, R> operation) {
            return new DelegateExecuteOperation<>(patternMatching);
        }
    }

    private final static class MatchingRule<T, R> {
        private final Class<? super T> currentType;
        private final Function<T, R> operation;

        MatchingRule() {
            this(null, null);
        }

        MatchingRule(Class<? super T> currentType, Function<T, R> operation) {
            this.currentType = currentType;
            this.operation = operation;
        }

        boolean isEmpty() {
            return currentType == null || operation == null;
        }
    }
}
