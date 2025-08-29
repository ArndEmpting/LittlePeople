package com.littlepeople.core.model;

import com.littlepeople.core.model.DeathCause;
import com.littlepeople.core.model.Gender;
import com.littlepeople.core.model.HealthStatus;
import com.littlepeople.core.model.LifeStage;
import com.littlepeople.core.model.PersonalityTrait;
import com.littlepeople.core.model.WealthStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the enumeration classes.
 *
 * @since 1.0.0
 * @version 1.0.0
 */
@DisplayName("Enumeration Tests")
class EnumerationTest {

    @Test
    @DisplayName("HealthStatus should have correct mortality multipliers")
    void healthStatusShouldHaveCorrectMortalityMultipliers() {
        assertEquals(1.0, HealthStatus.HEALTHY.getMortalityMultiplier());
        assertEquals(1.5, HealthStatus.SICK.getMortalityMultiplier());
        assertEquals(3.0, HealthStatus.CRITICALLY_ILL.getMortalityMultiplier());
    }

    @Test
    @DisplayName("WealthStatus should determine status from income correctly")
    void wealthStatusShouldDetermineStatusFromIncomeCorrectly() {
        assertEquals(WealthStatus.POOR, WealthStatus.fromIncome(15000));
        assertEquals(WealthStatus.LOWER_MIDDLE_CLASS, WealthStatus.fromIncome(35000));
        assertEquals(WealthStatus.MIDDLE_CLASS, WealthStatus.fromIncome(75000));
        assertEquals(WealthStatus.UPPER_MIDDLE_CLASS, WealthStatus.fromIncome(150000));
        assertEquals(WealthStatus.RICH, WealthStatus.fromIncome(500000));
    }

    @Test
    @DisplayName("LifeStage should determine stage from age correctly")
    void lifeStageShouldDetermineStageFromAgeCorrectly() {
        assertEquals(LifeStage.INFANT, LifeStage.fromAge(1));
        assertEquals(LifeStage.CHILD, LifeStage.fromAge(8));
        assertEquals(LifeStage.ADOLESCENT, LifeStage.fromAge(15));
        assertEquals(LifeStage.YOUNG_ADULT, LifeStage.fromAge(22));
        assertEquals(LifeStage.ADULT, LifeStage.fromAge(40));
        assertEquals(LifeStage.ELDER, LifeStage.fromAge(70));
    }

    @Test
    @DisplayName("LifeStage should correctly identify adults")
    void lifeStageShouldCorrectlyIdentifyAdults() {
        assertFalse(LifeStage.INFANT.isAdult());
        assertFalse(LifeStage.CHILD.isAdult());
        assertFalse(LifeStage.ADOLESCENT.isAdult());
        assertTrue(LifeStage.YOUNG_ADULT.isAdult());
        assertTrue(LifeStage.ADULT.isAdult());
        assertTrue(LifeStage.ELDER.isAdult());
    }

    @Test
    @DisplayName("LifeStage should reject negative age")
    void lifeStageShouldRejectNegativeAge() {
        assertThrows(IllegalArgumentException.class, () -> LifeStage.fromAge(-1));
    }

    @Test
    @DisplayName("PersonalityTrait should have display names")
    void personalityTraitShouldHaveDisplayNames() {
        assertEquals("Openness", PersonalityTrait.OPENNESS.getDisplayName());
        assertEquals("Conscientiousness", PersonalityTrait.CONSCIENTIOUSNESS.getDisplayName());
        assertEquals("Extraversion", PersonalityTrait.EXTRAVERSION.getDisplayName());
    }

    @Test
    @DisplayName("DeathCause should have descriptions")
    void deathCauseShouldHaveDescriptions() {
        assertEquals("Disease", DeathCause.DISEASE.getDescription());
        assertEquals("Natural Old Age", DeathCause.NATURAL_OLD_AGE.getDescription());
        assertEquals("Accident", DeathCause.ACCIDENT.getDescription());
    }

    @Test
    @DisplayName("Gender enumeration should have correct values")
    void genderEnumerationShouldHaveCorrectValues() {
        assertEquals(2, Gender.values().length);
        assertNotNull(Gender.MALE);
        assertNotNull(Gender.FEMALE);
    }
}
