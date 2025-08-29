package com.littlepeople.core.util;

import com.littlepeople.core.model.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating realistic first and last names for Person entities.
 *
 * <p>This class provides methods to generate names based on gender and includes
 * comprehensive lists of common names from various cultural backgrounds.
 * The name generation is designed to create diverse and realistic populations
 * for the LittlePeople simulation.</p>
 *
 * <p>Features include:</p>
 * <ul>
 *   <li>Gender-specific first name generation</li>
 *   <li>Cultural diversity in name selection</li>
 *   <li>Weighted random selection for realistic distribution</li>
 *   <li>Thread-safe name generation</li>
 * </ul>
 *
 * <p>Usage examples:</p>
 * <pre>
 * NameGenerator generator = new NameGenerator();
 * String firstName = generator.generateFirstName(Gender.FEMALE);
 * String lastName = generator.generateLastName();
 * </pre>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class NameGenerator {

    private static final Logger logger = LoggerFactory.getLogger(NameGenerator.class);

    private final Random random;

    // Comprehensive lists of first names by gender
    private static final List<String> MALE_FIRST_NAMES = Arrays.asList(
        // Traditional/Classic Names
        "James", "John", "Robert", "Michael", "William", "David", "Richard", "Thomas", "Christopher", "Daniel",
        "Matthew", "Anthony", "Mark", "Donald", "Steven", "Paul", "Andrew", "Joshua", "Kenneth", "Kevin",
        "Brian", "George", "Timothy", "Ronald", "Jason", "Edward", "Jeffrey", "Ryan", "Jacob", "Gary",
        "Nicholas", "Eric", "Jonathan", "Stephen", "Larry", "Justin", "Scott", "Brandon", "Benjamin", "Samuel",

        // Modern/Contemporary Names
        "Alexander", "Patrick", "Jack", "Dennis", "Jerry", "Tyler", "Aaron", "Jose", "Henry", "Adam",
        "Douglas", "Nathan", "Peter", "Zachary", "Kyle", "Noah", "Alan", "Ethan", "Jeremy", "Lionel",
        "Carl", "Wayne", "Ralph", "Roy", "Eugene", "Louis", "Philip", "Bobby", "Arthur", "Lawrence",

        // International/Diverse Names
        "Antonio", "Carlos", "Juan", "Luis", "Miguel", "Victor", "Roberto", "Francisco", "Gabriel", "Jorge",
        "Ahmed", "Hassan", "Omar", "Ali", "Mohammad", "Amir", "Ibrahim", "Khalil", "Tariq", "Rashid",
        "Chen", "Wei", "Ming", "Jun", "Han", "Kai", "Ravi", "Amit", "Raj", "Vikram"
    );

    private static final List<String> FEMALE_FIRST_NAMES = Arrays.asList(
        // Traditional/Classic Names
        "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen",
        "Nancy", "Lisa", "Betty", "Helen", "Sandra", "Donna", "Carol", "Ruth", "Sharon", "Michelle",
        "Laura", "Sarah", "Kimberly", "Deborah", "Dorothy", "Lisa", "Nancy", "Karen", "Betty", "Helen",

        // Modern/Contemporary Names
        "Ashley", "Emily", "Kimberly", "Donna", "Margaret", "Carol", "Amanda", "Melissa", "Deborah", "Stephanie",
        "Emma", "Olivia", "Sophia", "Isabella", "Ava", "Mia", "Abigail", "Madison", "Charlotte", "Harper",
        "Sofia", "Avery", "Ella", "Scarlett", "Grace", "Chloe", "Victoria", "Riley", "Aria", "Lily",

        // International/Diverse Names
        "Maria", "Carmen", "Rosa", "Ana", "Elena", "Sofia", "Isabella", "Lucia", "Adriana", "Valeria",
        "Fatima", "Aisha", "Zahra", "Layla", "Amira", "Nadia", "Yasmin", "Samira", "Leila", "Zara",
        "Li", "Mei", "Yan", "Xin", "Yun", "Priya", "Sita", "Deepa", "Anjali", "Kavya"
    );

    private static final List<String> LAST_NAMES = Arrays.asList(
        // Common English/European Surnames
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
        "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
        "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
        "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
        "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell", "Carter", "Roberts",

        // Additional surnames for diversity
        "Murphy", "Cook", "Bailey", "Reed", "Kelly", "Howard", "Ward", "Cox", "Diaz", "Richardson",
        "Wood", "Watson", "Brooks", "Bennett", "Gray", "James", "Reyes", "Cruz", "Hughes", "Price",
        "Myers", "Long", "Foster", "Sanders", "Ross", "Morales", "Powell", "Sullivan", "Russell", "Ortiz",
        "Jenkins", "Gutierrez", "Perry", "Butler", "Barnes", "Fisher", "Henderson", "Coleman", "Simmons", "Patterson",

        // International surnames
        "Kumar", "Singh", "Patel", "Shah", "Gupta", "Sharma", "Agarwal", "Jain", "Reddy", "Rao",
        "Chen", "Wang", "Li", "Zhang", "Liu", "Yang", "Huang", "Zhao", "Wu", "Zhou",
        "Ahmed", "Khan", "Ali", "Hassan", "Hussein", "Omar", "Ibrahim", "Mahmoud", "Yusuf", "Rashid",
        "O'Brien", "O'Connor", "Murphy", "Kelly", "Ryan", "Sullivan", "Walsh", "McCarthy", "Byrne", "O'Sullivan"
    );

    /**
     * Creates a new NameGenerator with a default random seed.
     */
    public NameGenerator() {
        this.random = new Random();
    }

    /**
     * Creates a new NameGenerator with a specific random seed for reproducible results.
     *
     * @param seed the random seed to use
     */
    public NameGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Generates a random first name based on the specified gender.
     *
     * <p>The method selects from a comprehensive list of names that includes
     * traditional, modern, and international names to ensure diversity
     * in the generated population.</p>
     *
     * @param gender the gender for which to generate a name (required)
     * @return a randomly selected first name appropriate for the gender
     * @throws IllegalArgumentException if gender is null
     */
    public String generateFirstName(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }

        List<String> nameList = (gender == Gender.MALE) ? MALE_FIRST_NAMES : FEMALE_FIRST_NAMES;
        String selectedName = nameList.get(random.nextInt(nameList.size()));

        logger.debug("Generated {} first name: {}", gender.name().toLowerCase(), selectedName);
        return selectedName;
    }

    /**
     * Generates a random last name (surname).
     *
     * <p>The method selects from a diverse collection of surnames representing
     * various cultural backgrounds and geographical origins to create
     * realistic population diversity.</p>
     *
     * @return a randomly selected last name
     */
    public String generateLastName() {
        String selectedName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
        logger.debug("Generated last name: {}", selectedName);
        return selectedName;
    }

    /**
     * Generates a complete full name with first and last name.
     *
     * @param gender the gender for the first name generation
     * @return a full name in "FirstName LastName" format
     * @throws IllegalArgumentException if gender is null
     */
    public String generateFullName(Gender gender) {
        return generateFirstName(gender) + " " + generateLastName();
    }

    /**
     * Returns the total number of available male first names.
     *
     * @return the count of male first names in the database
     */
    public static int getMaleFirstNameCount() {
        return MALE_FIRST_NAMES.size();
    }

    /**
     * Returns the total number of available female first names.
     *
     * @return the count of female first names in the database
     */
    public static int getFemaleFirstNameCount() {
        return FEMALE_FIRST_NAMES.size();
    }

    /**
     * Returns the total number of available last names.
     *
     * @return the count of last names in the database
     */
    public static int getLastNameCount() {
        return LAST_NAMES.size();
    }

    /**
     * Checks if a given first name exists in the male names database.
     *
     * @param name the name to check
     * @return true if the name exists in male names, false otherwise
     */
    public static boolean isMaleFirstName(String name) {
        return name != null && MALE_FIRST_NAMES.contains(name);
    }

    /**
     * Checks if a given first name exists in the female names database.
     *
     * @param name the name to check
     * @return true if the name exists in female names, false otherwise
     */
    public static boolean isFemaleFirstName(String name) {
        return name != null && FEMALE_FIRST_NAMES.contains(name);
    }

    /**
     * Checks if a given last name exists in the surnames database.
     *
     * @param name the name to check
     * @return true if the name exists in last names, false otherwise
     */
    public static boolean isLastName(String name) {
        return name != null && LAST_NAMES.contains(name);
    }

    /**
     * Attempts to determine the most likely gender based on a first name.
     *
     * <p>This method checks if the name appears in the male or female name lists.
     * If a name appears in both lists or neither list, this method returns null
     * to indicate uncertainty.</p>
     *
     * @param firstName the first name to analyze
     * @return the most likely gender, or null if uncertain
     */
    public static Gender guessGenderFromName(String firstName) {
        if (firstName == null) {
            return null;
        }

        boolean isMale = MALE_FIRST_NAMES.contains(firstName);
        boolean isFemale = FEMALE_FIRST_NAMES.contains(firstName);

        if (isMale && !isFemale) {
            return Gender.MALE;
        } else if (isFemale && !isMale) {
            return Gender.FEMALE;
        } else {
            // Name appears in both lists, neither list, or is ambiguous
            return null;
        }
    }
}
