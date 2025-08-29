package com.littlepeople.core.model;

/**
 * Enumeration representing personality traits of a person in the simulation.
 *
 * <p>This enumeration defines various personality characteristics
 * that influence a person's behavior, decisions, and interactions
 * within the simulation. Each trait can have varying intensity
 * levels (typically 1-10) to represent individual differences.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public enum PersonalityTrait {

    /**
     * Openness to experience.
     *
     * <p>Represents creativity, intellectual curiosity, and
     * willingness to try new experiences. High openness
     * correlates with artistic interests and innovation.</p>
     */
    OPENNESS("Openness"),

    /**
     * Conscientiousness and self-discipline.
     *
     * <p>Represents organization, responsibility, and
     * goal-oriented behavior. High conscientiousness
     * correlates with academic and career success.</p>
     */
    CONSCIENTIOUSNESS("Conscientiousness"),

    /**
     * Extraversion and social energy.
     *
     * <p>Represents sociability, assertiveness, and
     * comfort in social situations. High extraversion
     * correlates with leadership and social influence.</p>
     */
    EXTRAVERSION("Extraversion"),

    /**
     * Agreeableness and cooperation.
     *
     * <p>Represents kindness, trust, and willingness
     * to help others. High agreeableness correlates
     * with successful relationships and teamwork.</p>
     */
    AGREEABLENESS("Agreeableness"),

    /**
     * Neuroticism and emotional stability.
     *
     * <p>Represents anxiety, mood swings, and stress
     * sensitivity. High neuroticism correlates with
     * mental health challenges and relationship difficulties.</p>
     */
    NEUROTICISM("Neuroticism"),

    /**
     * Intelligence and cognitive ability.
     *
     * <p>Represents problem-solving skills, learning
     * capacity, and analytical thinking. High intelligence
     * correlates with educational and professional achievement.</p>
     */
    INTELLIGENCE("Intelligence"),

    /**
     * Ambition and drive for success.
     *
     * <p>Represents goal-setting, competitive spirit,
     * and desire for achievement. High ambition
     * correlates with career advancement and wealth accumulation.</p>
     */
    AMBITION("Ambition"),

    /**
     * Empathy and emotional understanding.
     *
     * <p>Represents ability to understand and share
     * others' emotions. High empathy correlates with
     * strong relationships and helping behaviors.</p>
     */
    EMPATHY("Empathy"),

    /**
     * Humor and ability to find things funny.
     *
     * <p>Represents wit, playfulness, and ability to
     * lighten social situations. High humor correlates
     * with social popularity and stress resilience.</p>
     */
    HUMOR("Humor"),

    /**
     * Patience and tolerance for delays.
     *
     * <p>Represents calmness, persistence, and ability
     * to wait for long-term rewards. High patience
     * correlates with better decision-making and relationships.</p>
     */
    PATIENCE("Patience"),

    /**
     * Creativity and innovative thinking.
     *
     * <p>Represents artistic ability, original ideas,
     * and unconventional problem-solving. High creativity
     * correlates with artistic achievement and innovation.</p>
     */
    CREATIVITY("Creativity"),

    /**
     * Resilience and ability to bounce back.
     *
     * <p>Represents recovery from setbacks, stress
     * tolerance, and mental toughness. High resilience
     * correlates with better life outcomes after adversity.</p>
     */
    RESILIENCE("Resilience"),

    /**
     * Curiosity and desire to learn.
     *
     * <p>Represents inquisitiveness, exploration drive,
     * and interest in new information. High curiosity
     * correlates with lifelong learning and adaptability.</p>
     */
    CURIOSITY("Curiosity"),

    /**
     * Altruism and concern for others.
     *
     * <p>Represents selflessness, charity, and
     * willingness to sacrifice for others. High altruism
     * correlates with volunteer work and social contribution.</p>
     */
    ALTRUISM("Altruism"),

    /**
     * Confidence and self-assurance.
     *
     * <p>Represents self-belief, assertiveness, and
     * comfort with leadership. High confidence
     * correlates with career success and social influence.</p>
     */
    CONFIDENCE("Confidence"),

    /**
     * Optimism and positive outlook.
     *
     * <p>Represents hopefulness, positive expectations,
     * and resilience to negative events. High optimism
     * correlates with better mental health and relationships.</p>
     */
    OPTIMISM("Optimism"),

    /**
     * Cautiousness and risk aversion.
     *
     * <p>Represents careful decision-making, risk
     * assessment, and preference for safety. High
     * cautiousness correlates with financial security and longevity.</p>
     */
    CAUTIOUSNESS("Cautiousness");

    private final String displayName;

    /**
     * Creates a personality trait with the specified display name.
     *
     * @param displayName human-readable name for this trait
     */
    PersonalityTrait(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this trait.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
