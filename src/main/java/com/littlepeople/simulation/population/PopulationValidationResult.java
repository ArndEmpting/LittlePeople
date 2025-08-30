package com.littlepeople.simulation.population;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Result of population validation containing any issues found during demographic consistency checks.
 *
 * <p>This class encapsulates the results of population validation including validation
 * status, error messages, warnings, and recommendations for maintaining demographic
 * consistency. It provides both summary information and detailed issue descriptions.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class PopulationValidationResult {

    private final boolean valid;
    private final List<ValidationIssue> errors;
    private final List<ValidationIssue> warnings;
    private final List<String> recommendations;
    private final long validationTimestamp;

    private PopulationValidationResult(Builder builder) {
        this.valid = builder.errors.isEmpty();
        this.errors = List.copyOf(builder.errors);
        this.warnings = List.copyOf(builder.warnings);
        this.recommendations = List.copyOf(builder.recommendations);
        this.validationTimestamp = System.currentTimeMillis();
    }

    /**
     * Checks if the population passed validation without errors.
     *
     * @return true if validation passed, false if errors were found
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the list of validation errors found.
     *
     * @return unmodifiable list of validation errors
     */
    public List<ValidationIssue> getErrors() {
        return errors;
    }

    /**
     * Gets the list of validation warnings found.
     *
     * @return unmodifiable list of validation warnings
     */
    public List<ValidationIssue> getWarnings() {
        return warnings;
    }

    /**
     * Gets the list of recommendations for improving population balance.
     *
     * @return unmodifiable list of recommendations
     */
    public List<String> getRecommendations() {
        return recommendations;
    }

    /**
     * Gets the timestamp when validation was performed.
     *
     * @return validation timestamp in milliseconds
     */
    public long getValidationTimestamp() {
        return validationTimestamp;
    }

    /**
     * Checks if there are any validation errors.
     *
     * @return true if errors exist, false otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Checks if there are any validation warnings.
     *
     * @return true if warnings exist, false otherwise
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Checks if there are any recommendations.
     *
     * @return true if recommendations exist, false otherwise
     */
    public boolean hasRecommendations() {
        return !recommendations.isEmpty();
    }

    /**
     * Gets the total number of issues (errors + warnings).
     *
     * @return the total issue count
     */
    public int getTotalIssueCount() {
        return errors.size() + warnings.size();
    }

    /**
     * Creates a successful validation result with no issues.
     *
     * @return a successful validation result
     */
    public static PopulationValidationResult success() {
        return new Builder().build();
    }

    /**
     * Creates a validation result with a single error.
     *
     * @param error the validation error
     * @return a validation result with the error
     */
    public static PopulationValidationResult withError(ValidationIssue error) {
        return new Builder().addError(error).build();
    }

    /**
     * Creates a validation result with a single warning.
     *
     * @param warning the validation warning
     * @return a validation result with the warning
     */
    public static PopulationValidationResult withWarning(ValidationIssue warning) {
        return new Builder().addWarning(warning).build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PopulationValidationResult that = (PopulationValidationResult) obj;
        return valid == that.valid &&
               Objects.equals(errors, that.errors) &&
               Objects.equals(warnings, that.warnings) &&
               Objects.equals(recommendations, that.recommendations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, errors, warnings, recommendations);
    }

    @Override
    public String toString() {
        return "PopulationValidationResult{" +
               "valid=" + valid +
               ", errors=" + errors.size() +
               ", warnings=" + warnings.size() +
               ", recommendations=" + recommendations.size() +
               ", timestamp=" + validationTimestamp +
               '}';
    }

    /**
     * Creates a new builder for PopulationValidationResult.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating PopulationValidationResult instances.
     */
    public static class Builder {
        private final List<ValidationIssue> errors = new ArrayList<>();
        private final List<ValidationIssue> warnings = new ArrayList<>();
        private final List<String> recommendations = new ArrayList<>();

        private Builder() {}

        /**
         * Adds a validation error.
         *
         * @param error the validation error to add
         * @return this builder
         */
        public Builder addError(ValidationIssue error) {
            this.errors.add(error);
            return this;
        }

        /**
         * Adds a validation error with message and severity.
         *
         * @param message the error message
         * @param severity the error severity
         * @return this builder
         */
        public Builder addError(String message, ValidationSeverity severity) {
            return addError(new ValidationIssue(message, severity, ValidationIssue.IssueType.ERROR));
        }

        /**
         * Adds a validation warning.
         *
         * @param warning the validation warning to add
         * @return this builder
         */
        public Builder addWarning(ValidationIssue warning) {
            this.warnings.add(warning);
            return this;
        }

        /**
         * Adds a validation warning with message and severity.
         *
         * @param message the warning message
         * @param severity the warning severity
         * @return this builder
         */
        public Builder addWarning(String message, ValidationSeverity severity) {
            return addWarning(new ValidationIssue(message, severity, ValidationIssue.IssueType.WARNING));
        }

        /**
         * Adds a recommendation.
         *
         * @param recommendation the recommendation text
         * @return this builder
         */
        public Builder addRecommendation(String recommendation) {
            this.recommendations.add(recommendation);
            return this;
        }

        /**
         * Builds the PopulationValidationResult instance.
         *
         * @return a new PopulationValidationResult
         */
        public PopulationValidationResult build() {
            return new PopulationValidationResult(this);
        }
    }

    /**
     * Represents a validation issue with message, severity, and type.
     */
    public static class ValidationIssue {
        private final String message;
        private final ValidationSeverity severity;
        private final IssueType type;

        public ValidationIssue(String message, ValidationSeverity severity, IssueType type) {
            this.message = Objects.requireNonNull(message, "Message cannot be null");
            this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
            this.type = Objects.requireNonNull(type, "Type cannot be null");
        }

        public String getMessage() { return message; }
        public ValidationSeverity getSeverity() { return severity; }
        public IssueType getType() { return type; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            ValidationIssue that = (ValidationIssue) obj;
            return Objects.equals(message, that.message) &&
                   severity == that.severity &&
                   type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(message, severity, type);
        }

        @Override
        public String toString() {
            return type + " [" + severity + "]: " + message;
        }

        /**
         * Enumeration of issue types.
         */
        public enum IssueType {
            ERROR, WARNING, INFO
        }
    }

    /**
     * Enumeration of validation severity levels.
     */
    public enum ValidationSeverity {
        CRITICAL, HIGH, MEDIUM, LOW
    }
}
