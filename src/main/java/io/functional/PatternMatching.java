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
        return new PatternMatching<>(input, new NoMatchingRule<>());
    }

    public MatchingType<TInput> is(Class<? super TInput> nextMatchingRuleType) {
        return nextMatchingRuleType.isAssignableFrom(input.getClass()) &&
                bestMatchingRule.isNotBetterMatchThan(nextMatchingRuleType)
                ? new BetterMatchFound(nextMatchingRuleType)
                : new NoBetterMatch();
    }

    private Object execute() {
        return bestMatchingRule.execute();
    }

    public interface MatchingType<T> {
        <R> MatchingChain<T, R> thenReturn(Function<T, R> operation);
    }

    public interface MatchingChain<T, R> {
        MatchingType<T> is(Class<? super T> type);
        R execute();
    }

    private final class DelegateChainTo<T, R> implements MatchingChain<T, R> {
        private final PatternMatching<T> patternMatching;

        private DelegateChainTo(PatternMatching<T> patternMatching) {
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

    private final class BetterMatchFound implements MatchingType<TInput> {
        private final Class<? super TInput> nextMatchingRuleType;

        BetterMatchFound(Class<? super TInput> nextMatchingRuleType) {
            this.nextMatchingRuleType = nextMatchingRuleType;
        }

        @Override
        public <R> MatchingChain<TInput, R> thenReturn(Function<TInput, R> operation) {
            PatternMatching<TInput> newPatternMatching =
                    new PatternMatching<>(input, new FoundMatchingRule<>(nextMatchingRuleType, operation));

            return operation != null
                    ? new DelegateChainTo<>(newPatternMatching)
                    : new DelegateChainTo<>(PatternMatching.this);
        }
    }

    private final class NoBetterMatch implements MatchingType<TInput> {
        @Override
        public <R> MatchingChain<TInput, R> thenReturn(Function<TInput, R> operation) {
            return new DelegateChainTo<>(PatternMatching.this);
        }
    }

    private interface MatchingRule<T, R> {
        R execute();
        boolean isNotBetterMatchThan(Class<? super T> nextMatchingRuleType);
    }

    private final static class NoMatchingRule<T, R> implements MatchingRule<T, R> {
        @Override
        public R execute() {
            throw new UnsupportedOperationException("You have to define at least one correct rule");
        }

        @Override
        public boolean isNotBetterMatchThan(Class<? super T> nextMatchingRuleType) {
            return true;
        }
    }

    private final class FoundMatchingRule<R> implements MatchingRule<TInput, R> {
        private final Class<? super TInput> currentType;
        private final Function<TInput, R> operation;

        FoundMatchingRule(Class<? super TInput> currentType, Function<TInput, R> operation) {
            this.currentType = currentType;
            this.operation = operation;
        }

        @Override
        public R execute() {
            return this.operation.apply(input);
        }

        @Override
        public boolean isNotBetterMatchThan(Class<? super TInput> nextMatchingRuleType) {
            return currentType != nextMatchingRuleType &&
                    nextMatchingRuleType.isAssignableFrom(currentType);
        }
    }
}
