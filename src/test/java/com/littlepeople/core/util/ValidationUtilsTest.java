package com.littlepeople.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ValidationUtils.
 */
@DisplayName("ValidationUtils")
class ValidationUtilsTest {



    @Nested
    @DisplayName("requireNonNull Tests")
    class RequireNonNullTests {

        @Test
        @DisplayName("Should not throw when object is not null")
        void shouldNotThrowWhenObjectIsNotNull() {
            assertThatCode(() -> ValidationUtils.requireNonNull("test", "param"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when object is null")
        void shouldThrowWhenObjectIsNull() {
            assertThatThrownBy(() -> ValidationUtils.requireNonNull(null, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam cannot be null");
        }
    }

    @Nested
    @DisplayName("requireNonEmpty String Tests")
    class RequireNonEmptyStringTests {

        @ParameterizedTest
        @ValueSource(strings = {"test", "  valid  ", "a"})
        @DisplayName("Should not throw when string is not empty")
        void shouldNotThrowWhenStringIsNotEmpty(String value) {
            assertThatCode(() -> ValidationUtils.requireNonEmpty(value, "param"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when string is null")
        void shouldThrowWhenStringIsNull() {
            assertThatThrownBy(() -> ValidationUtils.requireNonEmpty((String) null, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should throw IllegalArgumentException when string is empty or whitespace")
        void shouldThrowWhenStringIsEmptyOrWhitespace(String value) {
            assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(value, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam cannot be empty");
        }
    }

    @Nested
    @DisplayName("requireNonEmpty Collection Tests")
    class RequireNonEmptyCollectionTests {

        @Test
        @DisplayName("Should not throw when collection is not empty")
        void shouldNotThrowWhenCollectionIsNotEmpty() {
            List<String> list = Arrays.asList("item1", "item2");
            assertThatCode(() -> ValidationUtils.requireNonEmpty(list, "param"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when collection is null")
        void shouldThrowWhenCollectionIsNull() {
            assertThatThrownBy(() -> ValidationUtils.requireNonEmpty((Collection<String>) null, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam cannot be null");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when collection is empty")
        void shouldThrowWhenCollectionIsEmpty() {
            List<String> emptyList = new ArrayList<>();
            assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(emptyList, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam cannot be empty");
        }
    }

    @Nested
    @DisplayName("requirePositive int Tests")
    class RequirePositiveIntTests {

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 100, Integer.MAX_VALUE})
        @DisplayName("Should not throw when int is positive")
        void shouldNotThrowWhenIntIsPositive(int value) {
            assertThatCode(() -> ValidationUtils.requirePositive(value, "param"))
                .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100, Integer.MIN_VALUE})
        @DisplayName("Should throw IllegalArgumentException when int is not positive")
        void shouldThrowWhenIntIsNotPositive(int value) {
            assertThatThrownBy(() -> ValidationUtils.requirePositive(value, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam must be positive, but was: " + value);
        }
    }

    @Nested
    @DisplayName("requirePositive long Tests")
    class RequirePositiveLongTests {

        @ParameterizedTest
        @ValueSource(longs = {1L, 5L, 100L, Long.MAX_VALUE})
        @DisplayName("Should not throw when long is positive")
        void shouldNotThrowWhenLongIsPositive(long value) {
            assertThatCode(() -> ValidationUtils.requirePositive(value, "param"))
                .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -100L, Long.MIN_VALUE})
        @DisplayName("Should throw IllegalArgumentException when long is not positive")
        void shouldThrowWhenLongIsNotPositive(long value) {
            assertThatThrownBy(() -> ValidationUtils.requirePositive(value, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam must be positive, but was: " + value);
        }
    }

    @Nested
    @DisplayName("requireNonNegative Tests")
    class RequireNonNegativeTests {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 5, 100, Integer.MAX_VALUE})
        @DisplayName("Should not throw when int is non-negative")
        void shouldNotThrowWhenIntIsNonNegative(int value) {
            assertThatCode(() -> ValidationUtils.requireNonNegative(value, "param"))
                .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
        @DisplayName("Should throw IllegalArgumentException when int is negative")
        void shouldThrowWhenIntIsNegative(int value) {
            assertThatThrownBy(() -> ValidationUtils.requireNonNegative(value, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam cannot be negative, but was: " + value);
        }
    }

    @Nested
    @DisplayName("requireInRange Tests")
    class RequireInRangeTests {

        @Test
        @DisplayName("Should not throw when value is within range")
        void shouldNotThrowWhenValueIsWithinRange() {
            assertThatCode(() -> ValidationUtils.requireInRange(5, 1, 10, "param"))
                .doesNotThrowAnyException();
            assertThatCode(() -> ValidationUtils.requireInRange(1, 1, 10, "param"))
                .doesNotThrowAnyException();
            assertThatCode(() -> ValidationUtils.requireInRange(10, 1, 10, "param"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when value is below range")
        void shouldThrowWhenValueIsBelowRange() {
            assertThatThrownBy(() -> ValidationUtils.requireInRange(0, 1, 10, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam must be between 1 and 10 (inclusive), but was: 0");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when value is above range")
        void shouldThrowWhenValueIsAboveRange() {
            assertThatThrownBy(() -> ValidationUtils.requireInRange(11, 1, 10, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam must be between 1 and 10 (inclusive), but was: 11");
        }
    }

    @Nested
    @DisplayName("requireValidUUID Tests")
    class RequireValidUUIDTests {

        @Test
        @DisplayName("Should not throw when UUID is valid")
        void shouldNotThrowWhenUUIDIsValid() {
            UUID validUUID = UUID.randomUUID();
            assertThatCode(() -> ValidationUtils.requireValidUUID(validUUID, "param"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when UUID is null")
        void shouldThrowWhenUUIDIsNull() {
            assertThatThrownBy(() -> ValidationUtils.requireValidUUID(null, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam cannot be null");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when UUID is nil")
        void shouldThrowWhenUUIDIsNil() {
            UUID nilUUID = new UUID(0L, 0L);
            assertThatThrownBy(() -> ValidationUtils.requireValidUUID(nilUUID, "testParam"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testParam cannot be the nil UUID");
        }
    }
}
