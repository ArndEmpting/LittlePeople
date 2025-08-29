package com.littlepeople.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PersonValidator class.
 *
 * <p>This test class validates the business rule validation
 * logic for Person entities and relationships.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
@DisplayName("PersonValidator Tests")
class PersonValidatorTest {

    private Person validPerson;
    private Person partner;
    private Person child;

    @BeforeEach
    void setUp() {
        validPerson = new Person("John", "Doe", Gender.MALE, LocalDate.of(1990, 5, 15));
        partner = new Person("Jane", "Smith", Gender.FEMALE, LocalDate.of(1988, 8, 10));
        child = new Person("Child", "Doe", Gender.FEMALE, LocalDate.now().minusYears(5));
    }

    @Test
    @DisplayName("Should validate valid person without errors")
    void shouldValidateValidPersonWithoutErrors() {
        List<String> errors = PersonValidator.validatePerson(validPerson);
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Should detect null person")
    void shouldDetectNullPerson() {
        List<String> errors = PersonValidator.validatePerson(null);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Person cannot be null"));
    }

    @Test
    @DisplayName("Should validate partnership compatibility")
    void shouldValidatePartnershipCompatibility() {
        List<String> errors = PersonValidator.validatePartnershipCompatibility(validPerson, partner);
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Should reject partnership with same person")
    void shouldRejectPartnershipWithSamePerson() {
        List<String> errors = PersonValidator.validatePartnershipCompatibility(validPerson, validPerson);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("partner with themselves")));
    }

    @Test
    @DisplayName("Should reject partnership with child")
    void shouldRejectPartnershipWithChild() {
        List<String> errors = PersonValidator.validatePartnershipCompatibility(validPerson, child);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("must be adults")));
    }

    @Test
    @DisplayName("Should validate child adoption")
    void shouldValidateChildAdoption() {
        List<String> errors = PersonValidator.validateChildAdoption(validPerson, child);
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Should reject child adoption when child is older")
    void shouldRejectChildAdoptionWhenChildIsOlder() {
        Person olderChild = new Person("Older", "Child", Gender.MALE, LocalDate.of(1980, 1, 1));
        List<String> errors = PersonValidator.validateChildAdoption(validPerson, olderChild);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("older than parent")));
    }

    @Test
    @DisplayName("Should identify valid person for simulation")
    void shouldIdentifyValidPersonForSimulation() {
        assertTrue(PersonValidator.isValidForSimulation(validPerson));
    }


}
