package ch.uetlib.spring.boot.problem.handler.trait.violation;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.compare.ComparableUtils;
import org.junit.jupiter.api.Test;

class ViolationTest {

    @Test
    void twoArgConstructor_setsNullRejectedValue() {
        final var violation = new Violation("field", "must not be null");
        assertThat(violation.rejectedValue()).isNull();
    }

    @Test
    void threeArgConstructor_setsRejectedValue() {
        final var violation = new Violation("field", "must not be null", "badValue");
        assertThat(violation.rejectedValue()).isEqualTo("badValue");
    }

    @Test
    void compareTo_equalViolations_returnsZero() {
        final var v1 = new Violation("field", "message", "val");
        final var v2 = new Violation("field", "message", "val");
        assertThat(ComparableUtils.is(v1).equalTo(v2)).isTrue();
    }

    @Test
    void compareTo_differentFieldName_sortsByFieldName() {
        final var a = new Violation("alpha", "msg");
        final var b = new Violation("beta", "msg");
        assertThat(ComparableUtils.is(a).lessThan(b)).isTrue();
        assertThat(ComparableUtils.is(b).greaterThan(a)).isTrue();
    }

    @Test
    void compareTo_sameFieldName_sortsByMessage() {
        final var earlier = new Violation("field", "a message");
        final var later = new Violation("field", "z message");
        assertThat(ComparableUtils.is(earlier).lessThan(later)).isTrue();
        assertThat(ComparableUtils.is(later).greaterThan(earlier)).isTrue();
    }

    @Test
    void compareTo_sameFieldAndMessage_nullRejectedValueSortsFirst() {
        // null maps to Integer.MIN_VALUE, non-null to Object.hashCode
        final var withNull = new Violation("field", "msg", null);
        // Use an object whose hashCode is guaranteed to be > Integer.MIN_VALUE
        final var withValue = new Violation("field", "msg", "nonNull");
        assertThat(ComparableUtils.is(withNull).lessThan(withValue)).isTrue();
        assertThat(ComparableUtils.is(withValue).greaterThan(withNull)).isTrue();
    }

    @Test
    void compareTo_sameEverything_returnsZero() {
        final var v1 = new Violation("a", "b", null);
        final var v2 = new Violation("a", "b", null);
        assertThat(ComparableUtils.is(v1).equalTo(v2)).isTrue();
    }

    @Test
    void compareTo_fieldNameTakesPrecedenceOverMessage() {
        // "b" field with "a" message should sort after "a" field with "z" message
        final var aField = new Violation("a", "z message");
        final var bField = new Violation("b", "a message");
        assertThat(ComparableUtils.is(aField).lessThan(bField)).isTrue();
    }
}
