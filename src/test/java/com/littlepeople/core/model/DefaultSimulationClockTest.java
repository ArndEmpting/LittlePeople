package com.littlepeople.core.model;

import com.littlepeople.core.interfaces.SimulationClock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for DefaultSimulationClock.
 */
@DisplayName("DefaultSimulationClock")
class DefaultSimulationClockTest {

    private LocalDateTime startTime;
    private DefaultSimulationClock clock;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        clock = new DefaultSimulationClock(startTime);
    }

    @Test
    @DisplayName("Should initialize with start time")
    void shouldInitializeWithStartTime() {
        assertThat(clock.getCurrentTime()).isEqualTo(startTime);
        assertThat(clock.getStartTime()).isEqualTo(startTime);
        assertThat(clock.getTimeScale()).isEqualTo(1.0);
        assertThat(clock.isRunning()).isFalse();
        assertThat(clock.getElapsedTime()).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("Should throw exception for null start time")
    void shouldThrowExceptionForNullStartTime() {
        assertThatThrownBy(() -> new DefaultSimulationClock(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start time cannot be null");
    }

    @Test
    @DisplayName("Should start and stop clock")
    void shouldStartAndStopClock() {
        assertThat(clock.isRunning()).isFalse();

        clock.start();
        assertThat(clock.isRunning()).isTrue();

        clock.stop();
        assertThat(clock.isRunning()).isFalse();
    }

    @Test
    @DisplayName("Should pause and resume clock")
    void shouldPauseAndResumeClock() {
        clock.start();
        assertThat(clock.isRunning()).isTrue();

        clock.pause();
        assertThat(clock.isRunning()).isFalse();
        assertThat(clock.isPaused()).isTrue();

        clock.resume();
        assertThat(clock.isRunning()).isTrue();
        assertThat(clock.isPaused()).isFalse();
    }

    @Test
    @DisplayName("Should advance time when running")
    void shouldAdvanceTimeWhenRunning() {
        clock.start();
        LocalDateTime newTime = clock.advance(Duration.ofHours(5));

        assertThat(newTime).isEqualTo(startTime.plusHours(5));
        assertThat(clock.getCurrentTime()).isEqualTo(startTime.plusHours(5));
    }

    @Test
    @DisplayName("Should not advance time when paused")
    void shouldNotAdvanceTimeWhenPaused() {
        clock.start();
        clock.pause();
        LocalDateTime timeBeforeAdvance = clock.getCurrentTime();

        clock.advance(Duration.ofHours(5));

        assertThat(clock.getCurrentTime()).isEqualTo(timeBeforeAdvance);
    }

    @Test
    @DisplayName("Should advance days")
    void shouldAdvanceDays() {
        clock.start();
        LocalDateTime newTime = clock.advanceDays(10);

        assertThat(newTime).isEqualTo(startTime.plusDays(10));
        assertThat(clock.getCurrentTime()).isEqualTo(startTime.plusDays(10));
    }

    @Test
    @DisplayName("Should apply time scale")
    void shouldApplyTimeScale() {
        clock.setTimeScale(2.0);
        clock.start();

        LocalDateTime newTime = clock.advance(Duration.ofHours(1));

        assertThat(newTime).isEqualTo(startTime.plusHours(2));
        assertThat(clock.getTimeScale()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Should set current time")
    void shouldSetCurrentTime() {
        LocalDateTime newTime = startTime.plusDays(5);
        clock.setCurrentTime(newTime);

        assertThat(clock.getCurrentTime()).isEqualTo(newTime);
    }

    @Test
    @DisplayName("Should reset clock")
    void shouldResetClock() {
        clock.start();
        clock.advance(Duration.ofDays(5));

        assertThat(clock.getCurrentTime()).isNotEqualTo(startTime);

        clock.reset();

        assertThat(clock.getCurrentTime()).isEqualTo(startTime);
        assertThat(clock.isRunning()).isFalse();
        assertThat(clock.getElapsedTime()).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("Should calculate elapsed time")
    void shouldCalculateElapsedTime() {
        clock.setCurrentTime(startTime.plusDays(5).plusHours(3));

        Duration elapsed = clock.getElapsedTime();
        assertThat(elapsed).isEqualTo(Duration.ofDays(5).plusHours(3));
    }

    @Test
    @DisplayName("Should throw exception for invalid time scale")
    void shouldThrowExceptionForInvalidTimeScale() {
        assertThatThrownBy(() -> clock.setTimeScale(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Time scale must be positive");

        assertThatThrownBy(() -> clock.setTimeScale(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Time scale must be positive");
    }

    @Test
    @DisplayName("Should throw exception for null duration")
    void shouldThrowExceptionForNullDuration() {
        assertThatThrownBy(() -> clock.advance(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duration cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for negative duration")
    void shouldThrowExceptionForNegativeDuration() {
        assertThatThrownBy(() -> clock.advance(Duration.ofHours(-1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duration cannot be negative");
    }

    @Test
    @DisplayName("Should throw exception for negative days")
    void shouldThrowExceptionForNegativeDays() {
        assertThatThrownBy(() -> clock.advanceDays(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Days cannot be negative");
    }
}
