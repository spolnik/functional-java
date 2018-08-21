import io.functional.PatternMatching
import spock.lang.Specification

class PatternMatchingSpec extends Specification {

    @SuppressWarnings("all")
    def "incompatible types  do not impact correctly defined rules"() {
        when: "having one correct rule and one incorrect"
        def result = PatternMatching.when("abc")
                .is(String.class).thenReturn({s -> "success"})
                .is(Integer.class).thenReturn({i -> "fail"})
                .execute()

        then: "resolved only correct one"
        result == "success"
    }

    @SuppressWarnings("all")
    def "using only incompatible rules causes throwing exception"() {
        when: "having incorrect rule"
        def result = PatternMatching.when("abc")
                .is(Integer.class).thenReturn({i -> "fail"})
                .execute()

        then: "causes throwing exception"
        thrown UnsupportedOperationException
    }
}