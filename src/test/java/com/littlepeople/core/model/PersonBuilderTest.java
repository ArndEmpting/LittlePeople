package com.littlepeople.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the PersonBuilder class.
 *
 * <p>This test class validates all functionality of the PersonBuilder including:</p>
 * <ul>
 *   <li>Basic builder pattern functionality</li>
 *   <li>Required field validation</li>
 *   <li>Personality trait handling with 0-100 range</li>
 *   <li>Random seeding person generation</li>
 *   <li>Edge cases and error conditions</li>
 * </ul>
 */
class PersonBuilderTest {

    private PersonBuilder builder;
    private LocalDate testBirthDate;

    @BeforeEach
    void setUp() {
        builder = new PersonBuilder();
        testBirthDate = LocalDate.of(1990, 5, 15);
    }

    @Nested
    @DisplayName("Basic Builder Functionality")
    class BasicBuilderFunctionality {

        @Test
        @DisplayName("Should build person with all required fields")
        void shouldBuildPersonWithRequiredFields() {
            Person person = builder
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .birthDate(testBirthDate)
                .build();

            assertNotNull(person);
            assertEquals("John", person.getFirstName());
            assertEquals("Doe", person.getLastName());
            assertEquals(Gender.MALE, person.getGender());
            assertEquals(testBirthDate, person.getBirthDate());
        }

        @Test
        @DisplayName("Should build person with optional fields")
        void shouldBuildPersonWithOptionalFields() {
            Person person = builder
                .firstName("Jane")
                .lastName("Smith")
                .gender(Gender.FEMALE)
                .birthDate(testBirthDate)
                .healthStatus(HealthStatus.EXCELLENT)
                .wealthStatus(WealthStatus.RICH)
                .build();

            assertNotNull(person);
            assertEquals("Jane", person.getFirstName());
            assertEquals("Smith", person.getLastName());
            assertEquals(Gender.FEMALE, person.getGender());
            assertEquals(testBirthDate, person.getBirthDate());
            assertEquals(HealthStatus.EXCELLENT, person.getHealthStatus());
            assertEquals(WealthStatus.RICH, person.getWealthStatus());
        }

        @Test
        @DisplayName("Should support method chaining")
        void shouldSupportMethodChaining() {
            assertDoesNotThrow(() -> {
                PersonBuilder result = builder
                    .firstName("Test")
                    .lastName("Person")
                    .gender(Gender.MALE)
                    .birthDate(testBirthDate)
                    .healthStatus(HealthStatus.HEALTHY)
                    .wealthStatus(WealthStatus.MIDDLE_CLASS);

                assertSame(builder, result.firstName("Test"));
                assertSame(builder, result.lastName("Person"));
                assertSame(builder, result.gender(Gender.MALE));
                assertSame(builder, result.birthDate(testBirthDate));
                assertSame(builder, result.healthStatus(HealthStatus.HEALTHY));
                assertSame(builder, result.wealthStatus(WealthStatus.MIDDLE_CLASS));
            });
        }
    }

    @Nested
    @DisplayName("Required Field Validation")
    class RequiredFieldValidation {

        @Test
        @DisplayName("Should throw exception when first name is missing")
        void shouldThrowWhenFirstNameMissing() {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                builder
                    .lastName("Doe")
                    .gender(Gender.MALE)
                    .birthDate(testBirthDate)
                    .build()
            );
            assertEquals("First name must be set", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when last name is missing")
        void shouldThrowWhenLastNameMissing() {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                builder
                    .firstName("John")
                    .gender(Gender.MALE)
                    .birthDate(testBirthDate)
                    .build()
            );
            assertEquals("Last name must be set", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when gender is missing")
        void shouldThrowWhenGenderMissing() {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                builder
                    .firstName("John")
                    .lastName("Doe")
                    .birthDate(testBirthDate)
                    .build()
            );
            assertEquals("Gender must be set", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when birth date is missing")
        void shouldThrowWhenBirthDateMissing() {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                builder
                    .firstName("John")
                    .lastName("Doe")
                    .gender(Gender.MALE)
                    .build()
            );
            assertEquals("Birth date must be set", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Personality Traits Functionality")
    class PersonalityTraitsFunctionality {

        @Test
        @DisplayName("Should set individual personality trait with valid range")
        void shouldSetIndividualPersonalityTrait() {
            Person person = builder
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .birthDate(testBirthDate)
                .personalityTrait(PersonalityTrait.EXTROVERSION, 75)
                .personalityTrait(PersonalityTrait.AGREEABLENESS, 60)
                .build();

            Map<PersonalityTrait, Integer> traits = person.getPersonalityTraits();
            assertEquals(75, traits.get(PersonalityTrait.EXTROVERSION));
            assertEquals(60, traits.get(PersonalityTrait.AGREEABLENESS));
        }

        @Test
        @DisplayName("Should set multiple personality traits via map")
        void shouldSetMultiplePersonalityTraitsViaMap() {
            Map<PersonalityTrait, Integer> traitMap = new HashMap<>();
            traitMap.put(PersonalityTrait.EXTROVERSION, 80);
            traitMap.put(PersonalityTrait.CONSCIENTIOUSNESS, 90);
            traitMap.put(PersonalityTrait.NEUROTICISM, 30);

            Person person = builder
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .birthDate(testBirthDate)
                .personalityTraits(traitMap)
                .build();

            Map<PersonalityTrait, Integer> traits = person.getPersonalityTraits();
            assertEquals(80, traits.get(PersonalityTrait.EXTROVERSION));
            assertEquals(90, traits.get(PersonalityTrait.CONSCIENTIOUSNESS));
            assertEquals(30, traits.get(PersonalityTrait.NEUROTICISM));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 25, 50, 75, 100})
        @DisplayName("Should accept valid trait values (0-100)")
        void shouldAcceptValidTraitValues(int intensity) {
            assertDoesNotThrow(() ->
                builder
                    .firstName("John")
                    .lastName("Doe")
                    .gender(Gender.MALE)
                    .birthDate(testBirthDate)
                    .personalityTrait(PersonalityTrait.EXTROVERSION, intensity)
                    .build()
            );
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -10, 101, 150})
        @DisplayName("Should reject invalid trait values (outside 0-100)")
        void shouldRejectInvalidTraitValues(int intensity) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                builder.personalityTrait(PersonalityTrait.EXTROVERSION, intensity)
            );
            assertEquals("Personality trait intensity must be between 0 and 100: " + intensity,
                        exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for null personality trait")
        void shouldThrowForNullPersonalityTrait() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                builder.personalityTrait(null, 50)
            );
            assertEquals("Personality trait cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should generate random traits when none are specified")
        void shouldGenerateRandomTraitsWhenNoneSpecified() {
            Person person = builder
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .birthDate(testBirthDate)
                .build();

            Map<PersonalityTrait, Integer> traits = person.getPersonalityTraits();
            assertFalse(traits.isEmpty());

            // All personality traits should be present
            for (PersonalityTrait trait : PersonalityTrait.values()) {
                assertTrue(traits.containsKey(trait));
                int value = traits.get(trait);
                assertTrue(value >= 0 && value <= 100,
                    "Trait " + trait + " should be 0-100, was: " + value);
            }
        }
    }

    @Nested
    @DisplayName("Random Seeding Person Generation")
    class RandomSeedingPersonGeneration {

        @Test
        @DisplayName("Should generate random seeding person with valid attributes")
        void shouldGenerateRandomSeedingPerson() {
            Person person = builder
                .randomSeedingPerson(65)
                .build();

            assertNotNull(person);
            assertNotNull(person.getFirstName());
            assertNotNull(person.getLastName());
            assertNotNull(person.getGender());
            assertNotNull(person.getBirthDate());
            assertNotNull(person.getHealthStatus());
            assertNotNull(person.getWealthStatus());

            // Age should be between 18 and maxAge (65)
            int age = person.getAge();
            assertTrue(age >= 18 && age <= 65,
                "Age should be between 18 and 65, was: " + age);
        }

        @Test
        @DisplayName("Should generate person with age within specified range")
        void shouldGeneratePersonWithAgeInRange() {
            int maxAge = 50;
            Person person = builder
                .randomSeedingPerson(maxAge)
                .build();

            int age = person.getAge();
            assertTrue(age >= 18 && age <= maxAge,
                "Age should be between 18 and " + maxAge + ", was: " + age);
        }

        @Test
        @DisplayName("Should generate all personality traits in valid range")
        void shouldGenerateAllPersonalityTraitsInValidRange() {
            Person person = builder
                .randomSeedingPerson(65)
                .build();

            Map<PersonalityTrait, Integer> traits = person.getPersonalityTraits();

            // All personality traits should be present
            assertEquals(PersonalityTrait.values().length, traits.size());

            for (PersonalityTrait trait : PersonalityTrait.values()) {
                assertTrue(traits.containsKey(trait));
                int value = traits.get(trait);
                assertTrue(value >= 0 && value <= 100,
                    "Trait " + trait + " should be 0-100, was: " + value);
            }
        }

        @Test
        @DisplayName("Should generate different random persons")
        void shouldGenerateDifferentRandomPersons() {
            Person person1 = new PersonBuilder().randomSeedingPerson(65).build();
            Person person2 = new PersonBuilder().randomSeedingPerson(65).build();

            // While it's possible they could be the same by chance, it's extremely unlikely
            // At least one of these should be different
            boolean hasDifference =
                !person1.getFirstName().equals(person2.getFirstName()) ||
                !person1.getLastName().equals(person2.getLastName()) ||
                !person1.getGender().equals(person2.getGender()) ||
                !person1.getBirthDate().equals(person2.getBirthDate()) ||
                !person1.getHealthStatus().equals(person2.getHealthStatus()) ||
                !person1.getWealthStatus().equals(person2.getWealthStatus());

            assertTrue(hasDifference, "Two random persons should have at least one difference");
        }

        @ParameterizedTest
        @EnumSource(Gender.class)
        @DisplayName("Should be capable of generating both genders")
        void shouldGenerateBothGenders(Gender expectedGender) {
            // Generate multiple persons to increase chance of getting the expected gender
            boolean foundExpectedGender = false;
            for (int i = 0; i < 50; i++) {
                Person person = new PersonBuilder().randomSeedingPerson(65).build();
                if (person.getGender() == expectedGender) {
                    foundExpectedGender = true;
                    break;
                }
            }
            assertTrue(foundExpectedGender,
                "Should be able to generate " + expectedGender + " gender");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle minimum age for random seeding")
        void shouldHandleMinimumAgeForRandomSeeding() {
            Person person = builder
                .randomSeedingPerson(18) // Minimum possible age
                .build();

            assertEquals(18, person.getAge());
        }

        @Test
        @DisplayName("Should handle very old age for random seeding")
        void shouldHandleVeryOldAgeForRandomSeeding() {
            int maxAge = 120;
            Person person = builder
                .randomSeedingPerson(maxAge)
                .build();

            int age = person.getAge();
            assertTrue(age >= 18 && age <= maxAge,
                "Age should be between 18 and " + maxAge + ", was: " + age);
        }

        @Test
        @DisplayName("Should override existing traits when setting new ones")
        void shouldOverrideExistingTraitsWhenSettingNew() {
            Map<PersonalityTrait, Integer> initialTraits = new HashMap<>();
            initialTraits.put(PersonalityTrait.EXTROVERSION, 50);

            Map<PersonalityTrait, Integer> newTraits = new HashMap<>();
            newTraits.put(PersonalityTrait.EXTROVERSION, 80);
            newTraits.put(PersonalityTrait.AGREEABLENESS, 70);

            Person person = builder
                .firstName("John")
                .lastName("Doe")
                .gender(Gender.MALE)
                .birthDate(testBirthDate)
                .personalityTraits(initialTraits)
                .personalityTraits(newTraits)
                .build();

            Map<PersonalityTrait, Integer> traits = person.getPersonalityTraits();
            assertEquals(80, traits.get(PersonalityTrait.EXTROVERSION));
            assertEquals(70, traits.get(PersonalityTrait.AGREEABLENESS));
        }

        @Test
        @DisplayName("Should handle future birth dates through Person constructor validation")
        void shouldHandleFutureBirthDates() {
            LocalDate futureDate = LocalDate.now().plusYears(1);

            // The PersonBuilder should pass the validation to the Person constructor
            assertThrows(IllegalArgumentException.class, () ->
                builder
                    .firstName("John")
                    .lastName("Doe")
                    .gender(Gender.MALE)
                    .birthDate(futureDate)
                    .build()
            );
        }

        @Test
        @DisplayName("Should create new builder instance for each build")
        void shouldCreateNewBuilderInstanceForEachBuild() {
            PersonBuilder builder1 = new PersonBuilder();
            PersonBuilder builder2 = new PersonBuilder();

            assertNotSame(builder1, builder2);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should create complete person with all features")
        void shouldCreateCompletePersonWithAllFeatures() {
            Map<PersonalityTrait, Integer> customTraits = new HashMap<>();
            customTraits.put(PersonalityTrait.EXTROVERSION, 85);
            customTraits.put(PersonalityTrait.AGREEABLENESS, 70);
            customTraits.put(PersonalityTrait.CONSCIENTIOUSNESS, 90);
            customTraits.put(PersonalityTrait.NEUROTICISM, 25);
            customTraits.put(PersonalityTrait.OPENNESS, 80);

            Person person = builder
                .firstName("Alice")
                .lastName("Johnson")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1985, 3, 20))
                .healthStatus(HealthStatus.EXCELLENT)
                .wealthStatus(WealthStatus.RICH)
                .personalityTraits(customTraits)
                .build();

            // Verify all attributes
            assertEquals("Alice", person.getFirstName());
            assertEquals("Johnson", person.getLastName());
            assertEquals(Gender.FEMALE, person.getGender());
            assertEquals(LocalDate.of(1985, 3, 20), person.getBirthDate());
            assertEquals(HealthStatus.EXCELLENT, person.getHealthStatus());
            assertEquals(WealthStatus.RICH, person.getWealthStatus());

            Map<PersonalityTrait, Integer> traits = person.getPersonalityTraits();
            assertEquals(85, traits.get(PersonalityTrait.EXTROVERSION));
            assertEquals(70, traits.get(PersonalityTrait.AGREEABLENESS));
            assertEquals(90, traits.get(PersonalityTrait.CONSCIENTIOUSNESS));
            assertEquals(25, traits.get(PersonalityTrait.NEUROTICISM));
            assertEquals(80, traits.get(PersonalityTrait.OPENNESS));
        }

        @Test
        @DisplayName("Should maintain builder state across multiple method calls")
        void shouldMaintainBuilderStateAcrossMultipleCalls() {
            // Build up the person step by step
            builder.firstName("Robert");
            builder.lastName("Brown");
            builder.gender(Gender.MALE);
            builder.birthDate(testBirthDate);
            builder.healthStatus(HealthStatus.HEALTHY);
            builder.personalityTrait(PersonalityTrait.EXTROVERSION, 60);

            Person person = builder.build();

            assertEquals("Robert", person.getFirstName());
            assertEquals("Brown", person.getLastName());
            assertEquals(Gender.MALE, person.getGender());
            assertEquals(testBirthDate, person.getBirthDate());
            assertEquals(HealthStatus.HEALTHY, person.getHealthStatus());
            assertEquals(60, person.getPersonalityTraits().get(PersonalityTrait.EXTROVERSION));
        }
    }
}
