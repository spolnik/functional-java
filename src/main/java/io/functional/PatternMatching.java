package io.functional;

import java.util.function.Function;

public class PatternMatching<TInput> {

    private final TInput input;
    private MatchingRule<?> matchingRule;

    private PatternMatching(TInput input) {
        this.input = input;
    }

    public static <TInput> PatternMatching<TInput> when(TInput input) {
        return new PatternMatching<>(input);
    }

    public TypeChecking<TInput> is(Class<? super TInput> type) {
        return type.isAssignableFrom(input.getClass())
                ? new MatchFound(this, type)
                : new NoMatch(this);
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

    private final class DelegateExecuteOperation<T, R> implements ExecuteOperation<T, R> {

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

        private final PatternMatching<TInput> patternMatching;
        private final Class<? super TInput> type;

        MatchFound(PatternMatching<TInput> patternMatching, Class<? super TInput> type) {
            this.patternMatching = patternMatching;
            this.type = type;
        }

        @Override
        public <R> ExecuteOperation<TInput, R> thenReturn(Function<TInput, R> operation) {
            if (matchingRule == null) {
                matchingRule = new MatchingRule<>(type, operation);
            } else if (matchingRule.type != type && noParentPresent(matchingRule)) {
                matchingRule = new MatchingRule<>(type, operation);
            }

            return new DelegateExecuteOperation<>(patternMatching);
        }

        private boolean noParentPresent(MatchingRule<?> matchingRule) {
            return type.isAssignableFrom(matchingRule.type);
        }
    }

    private final class NoMatch implements TypeChecking<TInput> {

        private final PatternMatching<TInput> patternMatching;

        NoMatch(PatternMatching<TInput> patternMatching) {
            this.patternMatching = patternMatching;
        }

        @Override
        public <R> ExecuteOperation<TInput, R> thenReturn(Function<TInput, R> operation) {
            return new DelegateExecuteOperation<>(patternMatching);
        }
    }

    private final class MatchingRule<R> {
        private final Class<? super TInput> type;
        private final Function<? super TInput, R> operation;

        MatchingRule(Class<? super TInput> type, Function<? super TInput, R> operation) {
            this.type = type;
            this.operation = operation;
        }
    }
}
