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
        return new PatternMatching<>(input, new MatchingRule<>(null, null));
    }

    public TypeChecking<TInput> is(Class<? super TInput> type) {
        return type.isAssignableFrom(input.getClass())
                ? new MatchFound(type)
                : new NoMatch();
    }

    private Object execute() {
        if (matchingRule.isEmpty()) {
            throw new UnsupportedOperationException("You have to define at least one correct rule");
        }

        return matchingRule.operation.apply(input);
    }

    public interface TypeChecking<T> {
        <R> Operation<T, R> thenReturn(Function<T, R> operation);
    }

    public interface Operation<T, R> {
        TypeChecking<T> is(Class<? super T> type);
        R execute();
    }

    private final class DelegateOperation<T, R> implements Operation<T, R> {
        private final PatternMatching<T> patternMatching;

        private DelegateOperation(PatternMatching<T> patternMatching) {
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
        private final Class<? super TInput> newType;

        MatchFound(Class<? super TInput> newType) {
            this.newType = newType;
        }

        @Override
        public <R> Operation<TInput, R> thenReturn(Function<TInput, R> operation) {
            return shouldChooseNewMatchingRule()
                    ? new DelegateOperation<>(new PatternMatching<>(input, new MatchingRule<>(newType, operation)))
                    : new DelegateOperation<>(PatternMatching.this);
        }

        private boolean shouldChooseNewMatchingRule() {
            if (matchingRule.isEmpty()) {
                return true;
            }

            return matchingRule.currentType != newType &&
                    newType.isAssignableFrom(matchingRule.currentType);
        }
    }

    private final class NoMatch implements TypeChecking<TInput> {
        @Override
        public <R> Operation<TInput, R> thenReturn(Function<TInput, R> operation) {
            return new DelegateOperation<>(PatternMatching.this);
        }
    }

    private final static class MatchingRule<T, R> {
        private final Class<? super T> currentType;
        private final Function<T, R> operation;

        MatchingRule(Class<? super T> currentType, Function<T, R> operation) {
            this.currentType = currentType;
            this.operation = operation;
        }

        boolean isEmpty() {
            return currentType == null || operation == null;
        }
    }
}
