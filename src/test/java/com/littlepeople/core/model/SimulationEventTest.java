package com.littlepeople.core.model;

import com.littlepeople.core.interfaces.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for SimulationEvent.
 */
@DisplayName("SimulationEvent")
class SimulationEventTest {

    private LocalDateTime testTime;
    private Map<String, Object> testData;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", 42);
    }

    @Test
    @DisplayName("Should create event with all parameters")
    void shouldCreateEventWithAllParameters() {
        SimulationEvent event = new SimulationEvent("TEST_EVENT", testTime, "entity123", 5, testData);

        assertThat(event.getId()).isNotNull().isNotEmpty();
        assertThat(event.getType()).isEqualTo("TEST_EVENT");
        assertThat(event.getScheduledTime()).isEqualTo(testTime);
        assertThat(event.getTargetEntityId()).isEqualTo("entity123");
        assertThat(event.getPriority()).isEqualTo(5);
        assertThat(event.getData()).isEqualTo(testData);
        assertThat(event.isProcessed()).isFalse();
        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    @DisplayName("Should create event with minimal parameters")
    void shouldCreateEventWithMinimalParameters() {
        SimulationEvent event = new SimulationEvent("TEST_EVENT", testTime);

        assertThat(event.getId()).isNotNull().isNotEmpty();
        assertThat(event.getType()).isEqualTo("TEST_EVENT");
        assertThat(event.getScheduledTime()).isEqualTo(testTime);
        assertThat(event.getTargetEntityId()).isNull();
        assertThat(event.getPriority()).isEqualTo(0);
        assertThat(event.getData()).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception for null type")
    void shouldThrowExceptionForNullType() {
        assertThatThrownBy(() -> new SimulationEvent(null, testTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event type cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for empty type")
    void shouldThrowExceptionForEmptyType() {
        assertThatThrownBy(() -> new SimulationEvent("", testTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event type cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for null scheduled time")
    void shouldThrowExceptionForNullScheduledTime() {
        assertThatThrownBy(() -> new SimulationEvent("TEST_EVENT", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Scheduled time cannot be null");
    }

    @Test
    @DisplayName("Should mark event as processed")
    void shouldMarkEventAsProcessed() {
        SimulationEvent event = new SimulationEvent("TEST_EVENT", testTime);

        assertThat(event.isProcessed()).isFalse();
        event.markProcessed();
        assertThat(event.isProcessed()).isTrue();
    }

    @Test
    @DisplayName("Should cancel event")
    void shouldCancelEvent() {
        SimulationEvent event = new SimulationEvent("TEST_EVENT", testTime);

        assertThat(event.isCancelled()).isFalse();
        event.cancel();
        assertThat(event.isCancelled()).isTrue();
    }

    @Test
    @DisplayName("Should get specific data value")
    void shouldGetSpecificDataValue() {
        SimulationEvent event = new SimulationEvent("TEST_EVENT", testTime, null, 0, testData);

        assertThat(event.getData("key1")).isEqualTo("value1");
        assertThat(event.getData("key2")).isEqualTo(42);
        assertThat(event.getData("nonexistent")).isNull();
    }

    @Test
    @DisplayName("Should return defensive copy of data")
    void shouldReturnDefensiveCopyOfData() {
        SimulationEvent event = new SimulationEvent("TEST_EVENT", testTime, null, 0, testData);

        Map<String, Object> retrievedData = event.getData();
        retrievedData.put("newKey", "newValue");

        // Original event data should not be modified
        assertThat(event.getData()).doesNotContainKey("newKey");
    }

    @Test
    @DisplayName("Should generate unique IDs for different events")
    void shouldGenerateUniqueIdsForDifferentEvents() {
        SimulationEvent event1 = new SimulationEvent("TEST_EVENT", testTime);
        SimulationEvent event2 = new SimulationEvent("TEST_EVENT", testTime);

        assertThat(event1.getId()).isNotEqualTo(event2.getId());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        SimulationEvent event1 = new SimulationEvent("TEST_EVENT", testTime);
        SimulationEvent event2 = new SimulationEvent("TEST_EVENT", testTime);

        // Different events should not be equal
        assertThat(event1).isNotEqualTo(event2);
        assertThat(event1.hashCode()).isNotEqualTo(event2.hashCode());

        // Same event should be equal to itself
        assertThat(event1).isEqualTo(event1);
        assertThat(event1.hashCode()).isEqualTo(event1.hashCode());
    }
}
