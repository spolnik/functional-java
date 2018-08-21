package io.functional;

import java.util.function.Function;

public class PatternMatching<TInput> {

    private final TInput input;
    private final MatchingRule<TInput, ?> matchingRule;

    private PatternMatching(TInput input, MatchingRule<TInput, ?> matchingRule) {
        this.input = input;
        this.matchingRule = matchingRule;
    }

    public static <TInput> PatternMatching<TInput> when(TInput input) {
        return new PatternMatching<>(input, null);
    }

    public TypeChecking<TInput> is(Class<? super TInput> type) {
        return type.isAssignableFrom(input.getClass())
                ? new MatchFound(type)
                : new NoMatch();
    }

    private Object execute() {
        if (matchingRule != null && matchingRule.operation != null) {
            return matchingRule.operation.apply(input);
        }

        throw new UnsupportedOperationException("You have to define at least one correct rule");
    }

    public interface TypeChecking<T> {
        <TResult> ExecuteOperation<T, TResult> thenReturn(Function<T, TResult> operation);
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

    private final class MatchFound implements TypeChecking<TInput> {

        private final Class<? super TInput> type;

        MatchFound(Class<? super TInput> type) {
            this.type = type;
        }

        @Override
        public <R> ExecuteOperation<TInput, R> thenReturn(Function<TInput, R> operation) {

            return shouldAssignNewMatchingRule()
                    ? new DelegateExecuteOperation<>(patternMatchingWithNewMatchingRule(operation))
                    : new DelegateExecuteOperation<>(PatternMatching.this);

        }

        private <R> PatternMatching<TInput> patternMatchingWithNewMatchingRule(Function<TInput, R> operation) {
            return new PatternMatching<>(
                    input, new MatchingRule<>(type, operation)
            );
        }

        private boolean shouldAssignNewMatchingRule() {
            return matchingRule == null ||
                    matchingRule.type != type && isParentOf(matchingRule.type);
        }

        private boolean isParentOf(Class<? super TInput> currentType) {
            return type.isAssignableFrom(currentType);
        }
    }

    private final class NoMatch implements TypeChecking<TInput> {

        @Override
        public <R> ExecuteOperation<TInput, R> thenReturn(Function<TInput, R> operation) {
            return new DelegateExecuteOperation<>(PatternMatching.this);
        }
    }

    private final static class MatchingRule<T, R> {
        private final Class<? super T> type;
        private final Function<? super T, R> operation;

        MatchingRule(Class<? super T> type, Function<? super T, R> operation) {
            this.type = type;
            this.operation = operation;
        }
    }
}
