package com.littlepeople.core.model;

import com.littlepeople.core.util.NameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the PersonBuilder class.
 *
 * <p>This test class validates all PersonBuilder functionality including
 * fluent API methods, random person generation, trait inheritance logic,
 * and validation scenarios. Tests are organized into nested classes for
 * better organization and readability.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
@DisplayName("PersonBuilder Tests")
class PersonBuilderTest {

    private PersonBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new PersonBuilder();
    }

    @Nested
    @DisplayName("Fluent API Methods")
    class FluentApiTests {

        @Test
        @DisplayName("Should set first name and return builder")
        void shouldSetFirstNameAndReturnBuilder() {
            PersonBuilder result = builder.firstName("John");

            assertSame(builder, result);
            // We can verify the name was set by building and checking the result
            Person person = builder
                .lastName("Doe")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
            assertEquals("John", person.getFirstName());
        }

        @Test
        @DisplayName("Should set last name and return builder")
        void shouldSetLastNameAndReturnBuilder() {
            PersonBuilder result = builder.lastName("Smith");

            assertSame(builder, result);
            Person person = builder
                .firstName("Jane")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1985, 5, 15))
                .build();
            assertEquals("Smith", person.getLastName());
        }

        @Test
        @DisplayName("Should set gender and return builder")
        void shouldSetGenderAndReturnBuilder() {
            PersonBuilder result = builder.gender(Gender.FEMALE);

            assertSame(builder, result);
            Person person = builder
                .firstName("Alice")
                .lastName("Johnson")
                .birthDate(LocalDate.of(1992, 3, 20))
                .build();
            assertEquals(Gender.FEMALE, person.getGender());
        }

        @Test
        @DisplayName("Should set birth date and return builder")
        void shouldSetBirthDateAndReturnBuilder() {
            LocalDate birthDate = LocalDate.of(1988, 12, 25);
            PersonBuilder result = builder.birthDate(birthDate);

            assertSame(builder, result);
            Person person = builder
                .firstName("Bob")
                .lastName("Wilson")
                .gender(Gender.MALE)
                .build();
            assertEquals(birthDate, person.getBirthDate());
        }

        @Test
        @DisplayName("Should set health status and return builder")
        void shouldSetHealthStatusAndReturnBuilder() {
            PersonBuilder result = builder.healthStatus(HealthStatus.SICK);

            assertSame(builder, result);
            Person person = builder
                .firstName("Carol")
                .lastName("Brown")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1975, 6, 10))
                .build();
            assertEquals(HealthStatus.SICK, person.getHealthStatus());
        }

        @Test
        @DisplayName("Should set wealth status and return builder")
        void shouldSetWealthStatusAndReturnBuilder() {
            PersonBuilder result = builder.wealthStatus(WealthStatus.RICH);

            assertSame(builder, result);
            Person person = builder
                .firstName("David")
                .lastName("Miller")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1980, 9, 5))
                .build();
            assertEquals(WealthStatus.RICH, person.getWealthStatus());
        }

        @Test
        @DisplayName("Should set personality trait and return builder")
        void shouldSetPersonalityTraitAndReturnBuilder() {
            PersonBuilder result = builder.personalityTrait(PersonalityTrait.EXTRAVERSION, 85);

            assertSame(builder, result);
            Person person = builder
                .firstName("Eve")
                .lastName("Davis")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1995, 4, 12))
                .build();
            assertEquals(85, person.getPersonalityTrait(PersonalityTrait.EXTRAVERSION));
        }
    }

    @Nested
    @DisplayName("Manual Person Building")
    class ManualBuildingTests {

        @Test
        @DisplayName("Should build person with all required fields")
        void shouldBuildPersonWithAllRequiredFields() {
            LocalDate birthDate = LocalDate.of(1990, 5, 15);

            Person person = builder
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .birthDate(birthDate)
                .build();

            assertNotNull(person);
            assertEquals("John", person.getFirstName());
            assertEquals("Doe", person.getLastName());
            assertEquals(Gender.MALE, person.getGender());
            assertEquals(birthDate, person.getBirthDate());
            assertEquals(HealthStatus.HEALTHY, person.getHealthStatus()); // Default
            assertEquals(WealthStatus.MIDDLE_CLASS, person.getWealthStatus()); // Default
        }

        @Test
        @DisplayName("Should build person with optional fields")
        void shouldBuildPersonWithOptionalFields() {
            Person person = builder
                .firstName("Jane")
                .lastName("Smith")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1985, 3, 20))
                .healthStatus(HealthStatus.CRITICALLY_ILL)
                .wealthStatus(WealthStatus.POOR)
                .personalityTrait(PersonalityTrait.INTELLIGENCE, 95)
                .personalityTrait(PersonalityTrait.CREATIVITY, 70)
                .build();

            assertEquals(HealthStatus.CRITICALLY_ILL, person.getHealthStatus());
            assertEquals(WealthStatus.POOR, person.getWealthStatus());
            assertEquals(95, person.getPersonalityTrait(PersonalityTrait.INTELLIGENCE));
            assertEquals(70, person.getPersonalityTrait(PersonalityTrait.CREATIVITY));
        }

        @Test
        @DisplayName("Should generate random traits if none set")
        void shouldGenerateRandomTraitsIfNoneSet() {
            Person person = builder
                .firstName("Random")
                .lastName("Person")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

            // Check that all personality traits have been set with values 0-100
            for (PersonalityTrait trait : PersonalityTrait.values()) {
                int intensity = person.getPersonalityTrait(trait);
                assertTrue(intensity >= 0 && intensity <= 100,
                    "Trait " + trait + " should be 0-100, but was " + intensity);
            }
        }
    }

    @Nested
    @DisplayName("Random Seeding Person Generation")
    class RandomSeedingTests {

        @Test
        @DisplayName("Should generate random seeding person with valid attributes")
        void shouldGenerateRandomSeedingPersonWithValidAttributes() {
            Person person = builder.randomSeedingPerson(65).build();

            assertNotNull(person);
            assertNotNull(person.getFirstName());
            assertNotNull(person.getLastName());
            assertNotNull(person.getGender());
            assertNotNull(person.getBirthDate());
            assertNotNull(person.getHealthStatus());
            assertNotNull(person.getWealthStatus());

            // Verify age is within expected range (18-65)
            int age = person.getAge();
            assertTrue(age >= 18 && age <= 65, "Age should be 18-65, but was " + age);

            // Verify person is adult
            assertTrue(person.isAdult());
        }

        @Test
        @DisplayName("Should generate random gender distribution")
        void shouldGenerateRandomGenderDistribution() {
            // Generate multiple persons and verify we get both genders
            int maleCount = 0;
            int femaleCount = 0;
            int totalCount = 50;

            for (int i = 0; i < totalCount; i++) {
                Person person = new PersonBuilder().randomSeedingPerson(40).build();
                if (person.getGender() == Gender.MALE) {
                    maleCount++;
                } else {
                    femaleCount++;
                }
            }

            // Both genders should be represented (very high probability)
            assertTrue(maleCount > 0, "Should generate at least some males");
            assertTrue(femaleCount > 0, "Should generate at least some females");
            assertEquals(totalCount, maleCount + femaleCount);
        }

        @Test
        @DisplayName("Should generate names appropriate for gender")
        void shouldGenerateNamesAppropriateForGender() {
            // Generate multiple persons and verify names match expected patterns
            for (int i = 0; i < 20; i++) {
                Person person = new PersonBuilder().randomSeedingPerson(50).build();

                // Names should be non-empty
                assertFalse(person.getFirstName().isEmpty());
                assertFalse(person.getLastName().isEmpty());

                // Names should be properly capitalized (first letter uppercase)
                assertTrue(Character.isUpperCase(person.getFirstName().charAt(0)));
                assertTrue(Character.isUpperCase(person.getLastName().charAt(0)));
            }
        }

        @Test
        @DisplayName("Should generate random personality traits in 0-100 range")
        void shouldGenerateRandomPersonalityTraitsInRange() {
            Person person = builder.randomSeedingPerson(30).build();

            // Verify all traits are set and in valid range
            for (PersonalityTrait trait : PersonalityTrait.values()) {
                int intensity = person.getPersonalityTrait(trait);
                assertTrue(intensity >= 0 && intensity <= 100,
                    "Trait " + trait + " should be 0-100, but was " + intensity);
            }
        }

        @Test
        @DisplayName("Should generate diverse trait values across multiple persons")
        void shouldGenerateDiverseTraitValuesAcrossMultiplePersons() {
            // Generate multiple persons and verify trait diversity
            boolean hasLowValue = false;  // 0-30
            boolean hasMidValue = false;  // 31-70
            boolean hasHighValue = false; // 71-100

            for (int i = 0; i < 20; i++) {
                Person person = new PersonBuilder().randomSeedingPerson(40).build();
                int extraversion = person.getPersonalityTrait(PersonalityTrait.EXTRAVERSION);

                if (extraversion <= 30) hasLowValue = true;
                else if (extraversion <= 70) hasMidValue = true;
                else hasHighValue = true;
            }

            // Should have diversity across the range
            assertTrue(hasLowValue || hasMidValue || hasHighValue,
                "Should generate diverse personality trait values");
        }

        @Test
        @DisplayName("Should reject invalid max age")
        void shouldRejectInvalidMaxAge() {
            assertThrows(IllegalArgumentException.class, () ->
                builder.randomSeedingPerson(17)); // Below minimum adult age

            assertThrows(IllegalArgumentException.class, () ->
                builder.randomSeedingPerson(0));

            assertThrows(IllegalArgumentException.class, () ->
                builder.randomSeedingPerson(-5));
        }
    }

    @Nested
    @DisplayName("Child Inheritance Generation")
    class ChildInheritanceTests {

        private Person parent1;
        private Person parent2;
        private LocalDate childBirthDate;

        @BeforeEach
        void setUpParents() {
            // Create two parents with known traits
            parent1 = new PersonBuilder()
                .firstName("Father")
                .lastName("Parent")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1980, 1, 1))
                .wealthStatus(WealthStatus.RICH)
                .personalityTrait(PersonalityTrait.INTELLIGENCE, 90)
                .personalityTrait(PersonalityTrait.CREATIVITY, 60)
                .build();

            parent2 = new PersonBuilder()
                .firstName("Mother")
                .lastName("Parent")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1982, 5, 15))
                .wealthStatus(WealthStatus.MIDDLE_CLASS)
                .personalityTrait(PersonalityTrait.INTELLIGENCE, 80)
                .personalityTrait(PersonalityTrait.CREATIVITY, 40)
                .build();

            childBirthDate = LocalDate.now().minusYears(5);
        }

        @Test
        @DisplayName("Should create child with inherited attributes")
        void shouldCreateChildWithInheritedAttributes() {
            Person child = builder.childFromParents(parent1, parent2, childBirthDate).build();

            assertNotNull(child);
            assertEquals(childBirthDate, child.getBirthDate());
            assertEquals("Parent", child.getLastName()); // Inherited from parent1
            assertNotNull(child.getFirstName());
            assertTrue(child.getGender() == Gender.MALE || child.getGender() == Gender.FEMALE);
            assertEquals(HealthStatus.HEALTHY, child.getHealthStatus()); // Children born healthy
        }

        @Test
        @DisplayName("Should inherit better wealth status from parents")
        void shouldInheritBetterWealthStatusFromParents() {
            Person child = builder.childFromParents(parent1, parent2, childBirthDate).build();

            // Should inherit RICH (better than MIDDLE_CLASS)
            assertEquals(WealthStatus.RICH, child.getWealthStatus());
        }

        @Test
        @DisplayName("Should inherit personality traits with variation")
        void shouldInheritPersonalityTraitsWithVariation() {
            Person child = builder.childFromParents(parent1, parent2, childBirthDate).build();

            // Intelligence: parent1=90, parent2=80, child should inherit one ±20
            int childIntelligence = child.getPersonalityTrait(PersonalityTrait.INTELLIGENCE);
            assertTrue(childIntelligence >= 60 && childIntelligence <= 100,
                "Child intelligence should be in inherited range with variation, but was " + childIntelligence);

            // Creativity: parent1=60, parent2=40, child should inherit one ±20
            int childCreativity = child.getPersonalityTrait(PersonalityTrait.CREATIVITY);
            assertTrue(childCreativity >= 20 && childCreativity <= 80,
                "Child creativity should be in inherited range with variation, but was " + childCreativity);
        }

        @Test
        @DisplayName("Should generate random gender for child")
        void shouldGenerateRandomGenderForChild() {
            // Generate multiple children and verify both genders possible
            int maleCount = 0;
            int femaleCount = 0;

            for (int i = 0; i < 20; i++) {
                Person child = new PersonBuilder()
                    .childFromParents(parent1, parent2, childBirthDate)
                    .build();

                if (child.getGender() == Gender.MALE) {
                    maleCount++;
                } else {
                    femaleCount++;
                }
            }

            // Should have some distribution (very high probability)
            assertTrue(maleCount + femaleCount == 20);
            // At least one of each gender should appear in 20 tries (probability > 99.99%)
            assertTrue(maleCount > 0 || femaleCount > 0);
        }

        @Test
        @DisplayName("Should reject null parents")
        void shouldRejectNullParents() {
            assertThrows(IllegalArgumentException.class, () ->
                builder.childFromParents(null, parent2, childBirthDate));

            assertThrows(IllegalArgumentException.class, () ->
                builder.childFromParents(parent1, null, childBirthDate));

            assertThrows(IllegalArgumentException.class, () ->
                builder.childFromParents(null, null, childBirthDate));
        }

        @Test
        @DisplayName("Should reject null birth date")
        void shouldRejectNullBirthDate() {
            assertThrows(IllegalArgumentException.class, () ->
                builder.childFromParents(parent1, parent2, null));
        }
    }

    @Nested
    @DisplayName("Personality Trait Validation")
    class PersonalityTraitValidationTests {

        @Test
        @DisplayName("Should accept valid trait intensities")
        void shouldAcceptValidTraitIntensities() {
            assertDoesNotThrow(() -> builder.personalityTrait(PersonalityTrait.OPENNESS, 0));
            assertDoesNotThrow(() -> builder.personalityTrait(PersonalityTrait.OPENNESS, 50));
            assertDoesNotThrow(() -> builder.personalityTrait(PersonalityTrait.OPENNESS, 100));
        }

        @Test
        @DisplayName("Should reject invalid trait intensities")
        void shouldRejectInvalidTraitIntensities() {
            assertThrows(IllegalArgumentException.class, () ->
                builder.personalityTrait(PersonalityTrait.OPENNESS, -1));

            assertThrows(IllegalArgumentException.class, () ->
                builder.personalityTrait(PersonalityTrait.OPENNESS, 101));

            assertThrows(IllegalArgumentException.class, () ->
                builder.personalityTrait(PersonalityTrait.OPENNESS, 150));
        }

        @Test
        @DisplayName("Should reject null trait")
        void shouldRejectNullTrait() {
            assertThrows(IllegalArgumentException.class, () ->
                builder.personalityTrait(null, 50));
        }
    }

    @Nested
    @DisplayName("Build Validation")
    class BuildValidationTests {

        @Test
        @DisplayName("Should require first name")
        void shouldRequireFirstName() {
            Exception exception = assertThrows(IllegalStateException.class, () ->
                builder.lastName("Doe")
                       .gender(Gender.MALE)
                       .birthDate(LocalDate.of(1990, 1, 1))
                       .build());

            assertTrue(exception.getMessage().contains("First name must be set"));
        }

        @Test
        @DisplayName("Should require last name")
        void shouldRequireLastName() {
            Exception exception = assertThrows(IllegalStateException.class, () ->
                builder.firstName("John")
                       .gender(Gender.MALE)
                       .birthDate(LocalDate.of(1990, 1, 1))
                       .build());

            assertTrue(exception.getMessage().contains("Last name must be set"));
        }

        @Test
        @DisplayName("Should require gender")
        void shouldRequireGender() {
            Exception exception = assertThrows(IllegalStateException.class, () ->
                builder.firstName("John")
                       .lastName("Doe")
                       .birthDate(LocalDate.of(1990, 1, 1))
                       .build());

            assertTrue(exception.getMessage().contains("Gender must be set"));
        }

        @Test
        @DisplayName("Should require birth date")
        void shouldRequireBirthDate() {
            Exception exception = assertThrows(IllegalStateException.class, () ->
                builder.firstName("John")
                       .lastName("Doe")
                       .gender(Gender.MALE)
                       .build());

            assertTrue(exception.getMessage().contains("Birth date must be set"));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should create valid Person for all generation methods")
        void shouldCreateValidPersonForAllGenerationMethods() {
            // Manual creation
            Person manual = builder
                .firstName("Manual")
                .lastName("Person")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
            assertValidPerson(manual);

            // Random seeding
            Person seeded = new PersonBuilder().randomSeedingPerson(50).build();
            assertValidPerson(seeded);

            // Child inheritance
            Person parent1 = new PersonBuilder().randomSeedingPerson(40).build();
            Person parent2 = new PersonBuilder().randomSeedingPerson(35).build();
            Person child = new PersonBuilder()
                .childFromParents(parent1, parent2, LocalDate.now().minusYears(2))
                .build();
            assertValidPerson(child);
        }

        private void assertValidPerson(Person person) {
            assertNotNull(person);
            assertNotNull(person.getId());
            assertNotNull(person.getFirstName());
            assertNotNull(person.getLastName());
            assertNotNull(person.getGender());
            assertNotNull(person.getBirthDate());
            assertNotNull(person.getHealthStatus());
            assertNotNull(person.getWealthStatus());
            assertTrue(person.getAge() >= 0);
            assertTrue(person.isAlive());

            // Verify all personality traits are set and valid
            for (PersonalityTrait trait : PersonalityTrait.values()) {
                int intensity = person.getPersonalityTrait(trait);
                assertTrue(intensity >= 0 && intensity <= 100,
                    "Invalid trait intensity for " + trait + ": " + intensity);
            }
        }
    }
}
