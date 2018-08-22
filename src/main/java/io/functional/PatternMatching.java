package io.functional;

import java.util.function.Function;

public final class PatternMatching<TInput> {
    private final TInput input;
    private final MatchingRule<TInput, ?> bestMatchingRule;

    private PatternMatching(TInput input, MatchingRule<TInput, ?> matchingRule) {
        this.input = input;
        this.bestMatchingRule = matchingRule;
    }

    public static <TInput> PatternMatching<TInput> when(TInput input) {
        return new PatternMatching<>(input, new MatchingRule<>(null, null));
    }

    public MatchingType<TInput> is(Class<? super TInput> matchingRuleType) {
        return matchingRuleType.isAssignableFrom(input.getClass())
                ? new MatchFound(matchingRuleType)
                : new NoMatch();
    }

    private Object execute() {
        if (bestMatchingRule.isEmpty()) {
            throw new UnsupportedOperationException("You have to define at least one correct rule");
        }

        return bestMatchingRule.operation.apply(input);
    }

    public interface MatchingType<T> {
        <R> Transform<T, R> thenReturn(Function<T, R> operation);
    }

    public interface Transform<T, R> {
        MatchingType<T> is(Class<? super T> type);
        R execute();
    }

    private final class DelegateOperation<T, R> implements Transform<T, R> {
        private final PatternMatching<T> patternMatching;

        private DelegateOperation(PatternMatching<T> patternMatching) {
            this.patternMatching = patternMatching;
        }

        @Override
        public MatchingType<T> is(Class<? super T> type) {
            return patternMatching.is(type);
        }

        @SuppressWarnings("unchecked")
        @Override
        public R execute() {
            return (R) patternMatching.execute();
        }
    }

    private final class MatchFound implements MatchingType<TInput> {
        private final Class<? super TInput> nextMatchingRuleType;

        MatchFound(Class<? super TInput> nextMatchingRuleType) {
            this.nextMatchingRuleType = nextMatchingRuleType;
        }

        @Override
        public <R> Transform<TInput, R> thenReturn(Function<TInput, R> operation) {
            return shouldChooseNewMatchingRule()
                    ? new DelegateOperation<>(new PatternMatching<>(input, new MatchingRule<>(nextMatchingRuleType, operation)))
                    : new DelegateOperation<>(PatternMatching.this);
        }

        private boolean shouldChooseNewMatchingRule() {
            if (bestMatchingRule.isEmpty()) {
                return true;
            }

            return bestMatchingRule.currentType != nextMatchingRuleType &&
                    nextMatchingRuleType.isAssignableFrom(bestMatchingRule.currentType);
        }
    }

    private final class NoMatch implements MatchingType<TInput> {
        @Override
        public <R> Transform<TInput, R> thenReturn(Function<TInput, R> operation) {
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
