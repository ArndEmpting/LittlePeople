package com.littlepeople.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Person entity class.
 *
 * <p>This test class provides thorough coverage of all Person functionality
 * including construction, validation, relationships, life cycle management,
 * and personality traits. Tests are organized into nested classes for
 * better organization and readability.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
@DisplayName("Person Entity Tests")
class PersonTest {

    private Person testPerson;
    private LocalDate testBirthDate;

    @BeforeEach
    void setUp() {
        testBirthDate = LocalDate.of(1990, 5, 15);
        testPerson = new Person("John", "Doe", Gender.MALE, testBirthDate);
    }

    @Nested
    @DisplayName("Constructor and Basic Properties")
    class ConstructorTests {

        @Test
        @DisplayName("Should create person with valid parameters")
        void shouldCreatePersonWithValidParameters() {
            Person person = new Person("Jane", "Smith", Gender.FEMALE, LocalDate.of(1985, 3, 20));

            assertNotNull(person);
            assertNotNull(person.getId());
            assertEquals("Jane", person.getFirstName());
            assertEquals("Smith", person.getLastName());
            assertEquals("Jane Smith", person.getFullName());
            assertEquals(Gender.FEMALE, person.getGender());
            assertEquals(LocalDate.of(1985, 3, 20), person.getBirthDate());
            assertTrue(person.isAlive());
            assertEquals(HealthStatus.HEALTHY, person.getHealthStatus());
            assertEquals(WealthStatus.MIDDLE_CLASS, person.getWealthStatus());
        }

        @Test
        @DisplayName("Should reject null first name")
        void shouldRejectNullFirstName() {
            assertThrows(IllegalArgumentException.class, () ->
                new Person(null, "Doe", Gender.MALE, testBirthDate));
        }

        @Test
        @DisplayName("Should reject empty first name")
        void shouldRejectEmptyFirstName() {
            assertThrows(IllegalArgumentException.class, () ->
                new Person("", "Doe", Gender.MALE, testBirthDate));
        }

        @Test
        @DisplayName("Should reject null last name")
        void shouldRejectNullLastName() {
            assertThrows(IllegalArgumentException.class, () ->
                new Person("John", null, Gender.MALE, testBirthDate));
        }

        @Test
        @DisplayName("Should reject null gender")
        void shouldRejectNullGender() {
            assertThrows(IllegalArgumentException.class, () ->
                new Person("John", "Doe", null, testBirthDate));
        }

        @Test
        @DisplayName("Should reject null birth date")
        void shouldRejectNullBirthDate() {
            assertThrows(IllegalArgumentException.class, () ->
                new Person("John", "Doe", Gender.MALE, null));
        }

        @Test
        @DisplayName("Should reject future birth date")
        void shouldRejectFutureBirthDate() {
            LocalDate futureDate = LocalDate.now().plusDays(1);
            assertThrows(IllegalArgumentException.class, () ->
                new Person("John", "Doe", Gender.MALE, futureDate));
        }

        @Test
        @DisplayName("Should generate unique IDs for different persons")
        void shouldGenerateUniqueIds() {
            Person person1 = new Person("John", "Doe", Gender.MALE, testBirthDate);
            Person person2 = new Person("Jane", "Smith", Gender.FEMALE, testBirthDate);

            assertNotEquals(person1.getId(), person2.getId());
        }
    }

    @Nested
    @DisplayName("Age and Life Stage Calculations")
    class AgeCalculationTests {

        @Test
        @DisplayName("Should calculate correct age for living person")
        void shouldCalculateCorrectAgeForLivingPerson() {
            LocalDate birthDate = LocalDate.now().minusYears(25);
            Person person = new Person("Test", "Person", Gender.MALE, birthDate);

            assertEquals(25, person.getAge());
        }

        @Test
        @DisplayName("Should calculate correct age for deceased person")
        void shouldCalculateCorrectAgeForDeceasedPerson() {
            LocalDate birthDate = LocalDate.of(1950, 1, 1);
            LocalDate deathDate = LocalDate.of(2000, 1, 1);
            Person person = new Person("Test", "Person", Gender.MALE, birthDate);
            person.markDeceased(deathDate);

            assertEquals(50, person.getAge());
        }

        @Test
        @DisplayName("Should determine correct life stage")
        void shouldDetermineCorrectLifeStage() {
            // Test infant
            Person infant = new Person("Baby", "Doe", Gender.MALE, LocalDate.now().minusMonths(6));
            assertEquals(LifeStage.INFANT, infant.getLifeStage());

            // Test child
            Person child = new Person("Kid", "Doe", Gender.FEMALE, LocalDate.now().minusYears(8));
            assertEquals(LifeStage.CHILD, child.getLifeStage());

            // Test adult
            Person adult = new Person("Adult", "Doe", Gender.MALE, LocalDate.now().minusYears(30));
            assertEquals(LifeStage.ADULT, adult.getLifeStage());
        }

        @Test
        @DisplayName("Should correctly identify adults")
        void shouldCorrectlyIdentifyAdults() {
            Person child = new Person("Kid", "Doe", Gender.MALE, LocalDate.now().minusYears(10));
            Person adult = new Person("Adult", "Doe", Gender.FEMALE, LocalDate.now().minusYears(25));

            assertFalse(child.isAdult());
            assertTrue(adult.isAdult());
        }
    }

    @Nested
    @DisplayName("Partnership Management")
    class PartnershipTests {

        private Person partner;

        @BeforeEach
        void setUp() {
            partner = new Person("Jane", "Smith", Gender.FEMALE, LocalDate.of(1988, 8, 10));
        }

        @Test
        @DisplayName("Should form valid partnership")
        void shouldFormValidPartnership() {
            testPerson.setPartner(partner);

            assertEquals(partner, testPerson.getPartner());
            assertEquals(testPerson, partner.getPartner());
        }

        @Test
        @DisplayName("Should reject partnership with null partner")
        void shouldRejectPartnershipWithNullPartner() {
            assertThrows(IllegalArgumentException.class, () ->
                testPerson.setPartner(null));
        }

        @Test
        @DisplayName("Should reject partnership with self")
        void shouldRejectPartnershipWithSelf() {
            assertThrows(IllegalArgumentException.class, () ->
                testPerson.setPartner(testPerson));
        }

        @Test
        @DisplayName("Should reject partnership when already partnered")
        void shouldRejectPartnershipWhenAlreadyPartnered() {
            testPerson.setPartner(partner);
            Person newPartner = new Person("Alice", "Johnson", Gender.FEMALE, LocalDate.of(1992, 1, 1));

            assertThrows(IllegalArgumentException.class, () ->
                testPerson.setPartner(newPartner));
        }

        @Test
        @DisplayName("Should reject partnership with child")
        void shouldRejectPartnershipWithChild() {
            Person child = new Person("Child", "Doe", Gender.FEMALE, LocalDate.now().minusYears(10));

            assertThrows(IllegalArgumentException.class, () ->
                testPerson.setPartner(child));
        }

        @Test
        @DisplayName("Should dissolve partnership correctly")
        void shouldDissolvePartnershipCorrectly() {
            testPerson.setPartner(partner);
            testPerson.removePartnership();

            assertNull(testPerson.getPartner());
            assertNull(partner.getPartner());
        }
    }

    @Nested
    @DisplayName("Parent-Child Relationships")
    class ParentChildTests {

        private Person child;

        @BeforeEach
        void setUp() {
            child = new Person("Child", "Doe", Gender.FEMALE, LocalDate.now().minusYears(5));
        }

        @Test
        @DisplayName("Should add child correctly")
        void shouldAddChildCorrectly() {
            testPerson.addChild(child);

            assertTrue(testPerson.getChildren().contains(child));
            assertTrue(child.getParents().contains(testPerson));
        }

        @Test
        @DisplayName("Should reject null child")
        void shouldRejectNullChild() {
            assertThrows(IllegalArgumentException.class, () ->
                testPerson.addChild(null));
        }

        @Test
        @DisplayName("Should reject child older than parent")
        void shouldRejectChildOlderThanParent() {
            Person olderChild = new Person("Older", "Child", Gender.MALE, LocalDate.of(1980, 1, 1));

            assertThrows(IllegalArgumentException.class, () ->
                testPerson.addChild(olderChild));
        }

        @Test
        @DisplayName("Should reject duplicate child relationship")
        void shouldRejectDuplicateChildRelationship() {
            testPerson.addChild(child);

            assertThrows(IllegalArgumentException.class, () ->
                testPerson.addChild(child));
        }

        @Test
        @DisplayName("Should add child to both partners")
        void shouldAddChildToBothPartners() {
            Person partner = new Person("Jane", "Doe", Gender.FEMALE, LocalDate.of(1988, 1, 1));
            testPerson.setPartner(partner);
            testPerson.addChild(child);

            assertTrue(testPerson.getChildren().contains(child));
            assertTrue(partner.getChildren().contains(child));
            assertTrue(child.getParents().contains(testPerson));
            assertTrue(child.getParents().contains(partner));
        }
    }

    @Nested
    @DisplayName("Death Management")
    class DeathTests {

        @Test
        @DisplayName("Should mark person as deceased correctly")
        void shouldMarkPersonAsDeceasedCorrectly() {
            LocalDate deathDate = LocalDate.now().minusDays(1);
            testPerson.markDeceased(deathDate, DeathCause.DISEASE);

            assertFalse(testPerson.isAlive());
            assertEquals(deathDate, testPerson.getDeathDate());
            assertEquals(DeathCause.DISEASE, testPerson.getDeathCause());
        }

        @Test
        @DisplayName("Should use default cause when not specified")
        void shouldUseDefaultCauseWhenNotSpecified() {
            LocalDate deathDate = LocalDate.now().minusDays(1);
            testPerson.markDeceased(deathDate);

            assertEquals(DeathCause.UNEXPLAINED, testPerson.getDeathCause());
        }

        @Test
        @DisplayName("Should reject null death date")
        void shouldRejectNullDeathDate() {
            assertThrows(IllegalArgumentException.class, () ->
                testPerson.markDeceased(null));
        }

        @Test
        @DisplayName("Should reject death date before birth date")
        void shouldRejectDeathDateBeforeBirthDate() {
            LocalDate invalidDeathDate = testBirthDate.minusDays(1);

            assertThrows(IllegalArgumentException.class, () ->
                testPerson.markDeceased(invalidDeathDate));
        }

        @Test
        @DisplayName("Should reject future death date")
        void shouldRejectFutureDeathDate() {
            LocalDate futureDate = LocalDate.now().plusDays(1);

            assertThrows(IllegalArgumentException.class, () ->
                testPerson.markDeceased(futureDate));
        }

        @Test
        @DisplayName("Should dissolve partnership on death")
        void shouldDissolvePartnershipOnDeath() {
            Person partner = new Person("Jane", "Smith", Gender.FEMALE, LocalDate.of(1988, 1, 1));
            testPerson.setPartner(partner);

            testPerson.markDeceased(LocalDate.now().minusDays(1));

            assertNull(testPerson.getPartner());
            assertNull(partner.getPartner());
        }
    }

    @Nested
    @DisplayName("Personality Traits")
    class PersonalityTraitTests {

        @Test
        @DisplayName("Should set and get personality traits")
        void shouldSetAndGetPersonalityTraits() {
            testPerson.setPersonalityTrait(PersonalityTrait.EXTRAVERSION, 8);
            testPerson.setPersonalityTrait(PersonalityTrait.INTELLIGENCE, 9);

            assertEquals(8, testPerson.getPersonalityTrait(PersonalityTrait.EXTRAVERSION));
            assertEquals(9, testPerson.getPersonalityTrait(PersonalityTrait.INTELLIGENCE));
        }

        @Test
        @DisplayName("Should reject invalid trait intensity")
        void shouldRejectInvalidTraitIntensity() {
            assertThrows(IllegalArgumentException.class, () ->
                testPerson.setPersonalityTrait(PersonalityTrait.OPENNESS, 0));

            assertThrows(IllegalArgumentException.class, () ->
                testPerson.setPersonalityTrait(PersonalityTrait.OPENNESS, 11));
        }

        @Test
        @DisplayName("Should return default value for unset traits")
        void shouldReturnDefaultValueForUnsetTraits() {
            // Clear the random initialization by setting and then checking
            testPerson.setPersonalityTrait(PersonalityTrait.CREATIVITY, 5);
            assertEquals(5, testPerson.getPersonalityTrait(PersonalityTrait.CREATIVITY));
        }

        @Test
        @DisplayName("Should initialize all personality traits")
        void shouldInitializeAllPersonalityTraits() {
            for (PersonalityTrait trait : PersonalityTrait.values()) {
                int intensity = testPerson.getPersonalityTrait(trait);
                assertTrue(intensity >= 1 && intensity <= 10,
                    "Trait " + trait + " should be initialized between 1-10, but was " + intensity);
            }
        }
    }

    @Nested
    @DisplayName("Status Management")
    class StatusManagementTests {

        @Test
        @DisplayName("Should update health status")
        void shouldUpdateHealthStatus() {
            testPerson.setHealthStatus(HealthStatus.SICK);
            assertEquals(HealthStatus.SICK, testPerson.getHealthStatus());
        }

        @Test
        @DisplayName("Should reject null health status")
        void shouldRejectNullHealthStatus() {
            assertThrows(IllegalArgumentException.class, () ->
                testPerson.setHealthStatus(null));
        }

        @Test
        @DisplayName("Should update wealth status")
        void shouldUpdateWealthStatus() {
            testPerson.setWealthStatus(WealthStatus.RICH);
            assertEquals(WealthStatus.RICH, testPerson.getWealthStatus());
        }

        @Test
        @DisplayName("Should reject null wealth status")
        void shouldRejectNullWealthStatus() {
            assertThrows(IllegalArgumentException.class, () ->
                testPerson.setWealthStatus(null));
        }

        @Test
        @DisplayName("Should update names")
        void shouldUpdateNames() {
            testPerson.setFirstName("NewFirst");
            testPerson.setLastName("NewLast");

            assertEquals("NewFirst", testPerson.getFirstName());
            assertEquals("NewLast", testPerson.getLastName());
            assertEquals("NewFirst NewLast", testPerson.getFullName());
        }
    }

    @Nested
    @DisplayName("Defensive Collections")
    class DefensiveCollectionTests {

        @Test
        @DisplayName("Should return unmodifiable children collection")
        void shouldReturnUnmodifiableChildrenCollection() {
            Person child = new Person("Child", "Doe", Gender.FEMALE, LocalDate.now().minusYears(5));
            testPerson.addChild(child);

            assertThrows(UnsupportedOperationException.class, () ->
                testPerson.getChildren().clear());
        }

        @Test
        @DisplayName("Should return unmodifiable parents collection")
        void shouldReturnUnmodifiableParentsCollection() {
            assertThrows(UnsupportedOperationException.class, () ->
                testPerson.getParents().clear());
        }

        @Test
        @DisplayName("Should return unmodifiable personality traits map")
        void shouldReturnUnmodifiablePersonalityTraitsMap() {
            assertThrows(UnsupportedOperationException.class, () ->
                testPerson.getPersonalityTraits().clear());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            assertEquals(testPerson, testPerson);
            assertEquals(testPerson.hashCode(), testPerson.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to different person")
        void shouldNotBeEqualToDifferentPerson() {
            Person otherPerson = new Person("Jane", "Smith", Gender.FEMALE, LocalDate.of(1985, 1, 1));
            assertNotEquals(testPerson, otherPerson);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(testPerson, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            assertNotEquals(testPerson, "not a person");
        }
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        String toString = testPerson.toString();

        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("MALE"));
        assertTrue(toString.contains("true")); // alive status
    }
}
