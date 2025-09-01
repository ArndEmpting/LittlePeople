# RFC-007: Configuration System

**Version:** 1.0  
**Date:** August 27, 2025  
**Status:** Draft  
**Authors:** LittlePeople Development Team  
**Complexity:** Medium

## Summary

This RFC defines the configuration system for the LittlePeople simulation engine. It establishes how simulation parameters are defined, validated, loaded, and persisted. The configuration system is a critical component that enables users to customize simulation behavior without code changes and ensures all parameters are properly validated for consistency and realism. This system provides the foundation for scenario creation and parameter tuning that enhances the storytelling value of the simulation.

## Features Addressed

- **F018:** Configuration Management
- **F028:** Configuration Framework
- **F025:** Simulation Scenarios (foundational aspects)
- **F001:** Initial Population Configuration (parameter management)
- **F002:** Immigration/Emigration Controls (parameter management)

## Technical Approach

### Configuration System Architecture

The configuration system will provide comprehensive management of all simulation parameters through several key components:

1. **Configuration Model:** Define parameter data classes with validation rules
2. **Configuration Loaders:** Load configuration from various sources (files, defaults, UI)
3. **Configuration Validation:** Ensure parameters meet constraints and are consistent
4. **Configuration Persistence:** Save and load configuration sets
5. **Configuration Access:** Provide access to configuration throughout the application

### Core Components

#### Configuration Components

1. **ConfigurationManager:** Central component coordinating all configuration operations
2. **ConfigurationLoader:** Loads configuration from different sources
3. **ConfigurationValidator:** Validates parameter values and relationships
4. **ConfigurationRepository:** Stores and retrieves configuration sets
5. **ConfigurationBuilder:** Fluent API for programmatic configuration creation

#### Configuration Model Components

1. **SimulationConfiguration:** Top-level configuration container
2. **PopulationConfiguration:** Population-related parameters
3. **DemographicConfiguration:** Age, gender, and health distribution parameters
4. **LifeCycleConfiguration:** Aging, mortality, partnership, and fertility parameters
5. **SimulationControlConfiguration:** Time step, speed, and execution parameters

### Core Events

The system will define several configuration-related events:

1. **ConfigurationLoadedEvent:** Triggered when configuration is loaded
2. **ConfigurationChangedEvent:** Triggered when configuration parameters change
3. **ConfigurationValidationEvent:** Triggered during configuration validation
4. **ConfigurationSavedEvent:** Triggered when configuration is saved

## Technical Specifications

### ConfigurationManager Interface
All configuration related classes are in teh `com.littlepeople.simulation.config` package.
```java
/**
 * Central manager for configuration-related operations.
 */
public interface ConfigurationManager {
    
    /**
     * Gets the current active configuration.
     * 
     * @return the current configuration
     */
    SimulationConfiguration getCurrentConfiguration();
    
    /**
     * Loads configuration from the specified source.
     * 
     * @param source the configuration source
     * @return the loaded configuration
     * @throws ConfigurationException if loading fails
     */
    SimulationConfiguration loadConfiguration(ConfigurationSource source) 
            throws ConfigurationException;
    
    /**
     * Loads configuration from a file.
     * 
     * @param filePath the path to the configuration file
     * @return the loaded configuration
     * @throws ConfigurationException if loading fails
     */
    SimulationConfiguration loadConfigurationFromFile(Path filePath) 
            throws ConfigurationException;
    
    /**
     * Saves the current configuration to a file.
     * 
     * @param filePath the path to save the configuration to
     * @throws ConfigurationException if saving fails
     */
    void saveConfiguration(Path filePath) throws ConfigurationException;
    
    /**
     * Saves the specified configuration to a file.
     * 
     * @param configuration the configuration to save
     * @param filePath the path to save the configuration to
     * @throws ConfigurationException if saving fails
     */
    void saveConfiguration(SimulationConfiguration configuration, Path filePath) 
            throws ConfigurationException;
    
    /**
     * Sets the current active configuration.
     * 
     * @param configuration the configuration to set
     * @throws ConfigurationException if validation fails
     */
    void setCurrentConfiguration(SimulationConfiguration configuration) 
            throws ConfigurationException;
    
    /**
     * Validates the specified configuration.
     * 
     * @param configuration the configuration to validate
     * @return validation results with any issues found
     */
    ConfigurationValidationResult validateConfiguration(SimulationConfiguration configuration);
    
    /**
     * Creates a default configuration.
     * 
     * @return a default configuration
     */
    SimulationConfiguration createDefaultConfiguration();
    
    /**
     * Gets all available configuration templates.
     * 
     * @return list of configuration templates
     */
    List<ConfigurationTemplate> getAvailableTemplates();
    
    /**
     * Loads a configuration from a template.
     * 
     * @param templateId the template identifier
     * @return the loaded configuration
     * @throws ConfigurationException if loading fails
     */
    SimulationConfiguration loadFromTemplate(String templateId) throws ConfigurationException;
}
```

### SimulationConfiguration Class

```java
/**
 * Top-level configuration container for all simulation parameters.
 */
public class SimulationConfiguration {
    
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;
    private final PopulationConfiguration populationConfig;
    private final DemographicConfiguration demographicConfig;
    private final LifeCycleConfiguration lifeCycleConfig;
    private final SimulationControlConfiguration controlConfig;
    private final Map<String, Object> customParameters;
    
    /**
     * Creates a new simulation configuration.
     * 
     * @param name the configuration name
     * @param description the configuration description
     * @param populationConfig the population configuration
     * @param demographicConfig the demographic configuration
     * @param lifeCycleConfig the life cycle configuration
     * @param controlConfig the simulation control configuration
     * @param customParameters custom extension parameters
     */
    @Builder
    public SimulationConfiguration(
            String name,
            String description,
            PopulationConfiguration populationConfig,
            DemographicConfiguration demographicConfig,
            LifeCycleConfiguration lifeCycleConfig,
            SimulationControlConfiguration controlConfig,
            Map<String, Object> customParameters) {
        
        this.name = Objects.requireNonNull(name, "Configuration name cannot be null");
        this.description = description != null ? description : "";
        this.createdAt = LocalDateTime.now();
        this.populationConfig = Objects.requireNonNull(populationConfig, 
                "Population configuration cannot be null");
        this.demographicConfig = Objects.requireNonNull(demographicConfig, 
                "Demographic configuration cannot be null");
        this.lifeCycleConfig = Objects.requireNonNull(lifeCycleConfig, 
                "Life cycle configuration cannot be null");
        this.controlConfig = Objects.requireNonNull(controlConfig, 
                "Control configuration cannot be null");
        this.customParameters = customParameters != null ? 
                new HashMap<>(customParameters) : new HashMap<>();
    }
    
    // Getters and validation methods...
    
    /**
     * Creates a builder for SimulationConfiguration.
     * 
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for SimulationConfiguration.
     */
    public static class Builder {
        // Builder implementation...
    }
}
```

### PopulationConfiguration Class

```java
/**
 * Configuration for population-related parameters.
 */
public class PopulationConfiguration {
    
    private final int initialPopulationSize;
    private final double annualImmigrationRate;
    private final double annualEmigrationRate;
    private final int minPopulationSize;
    private final int maxPopulationSize;
    
    /**
     * Creates a new population configuration.
     * 
     * @param initialPopulationSize the starting population size
     * @param annualImmigrationRate the annual immigration rate
     * @param annualEmigrationRate the annual emigration rate (0.0-1.0)
     * @param minPopulationSize the minimum allowed population size
     * @param maxPopulationSize the maximum allowed population size
     */
    @Builder
    public PopulationConfiguration(
            int initialPopulationSize,
            double annualImmigrationRate,
            double annualEmigrationRate,
            int minPopulationSize,
            int maxPopulationSize) {
        
        validateParameters(initialPopulationSize, annualImmigrationRate, annualEmigrationRate,
                minPopulationSize, maxPopulationSize);
        
        this.initialPopulationSize = initialPopulationSize;
        this.annualImmigrationRate = annualImmigrationRate;
        this.annualEmigrationRate = annualEmigrationRate;
        this.minPopulationSize = minPopulationSize;
        this.maxPopulationSize = maxPopulationSize;
    }
    
    // Getters and validation methods...
}
```

### DemographicConfiguration Class

```java
/**
 * Configuration for demographic distribution parameters.
 */
public class DemographicConfiguration {
    
    private final double maleRatio;
    private final AgeDistribution ageDistribution;
    private final HealthDistribution healthDistribution;
    
    /**
     * Creates a new demographic configuration.
     * 
     * @param maleRatio the ratio of males in the population (0.0-1.0)
     * @param ageDistribution the age distribution to use
     * @param healthDistribution the health status distribution to use
     */
    @Builder
    public DemographicConfiguration(
            double maleRatio,
            AgeDistribution ageDistribution,
            HealthDistribution healthDistribution) {
        
        validateParameters(maleRatio, ageDistribution, healthDistribution);
        
        this.maleRatio = maleRatio;
        this.ageDistribution = ageDistribution;
        this.healthDistribution = healthDistribution;
    }
    
    // Getters and validation methods...
}
```

### LifeCycleConfiguration Class

```java
/**
 * Configuration for life cycle parameters.
 */
public class LifeCycleConfiguration {
    
    private final int adultAge;
    private final int elderlyAge;
    private final int maxAge;
    private final MortalityModel mortalityModel;
    private final FertilityModel fertilityModel;
    private final PartnershipModel partnershipModel;
    
    /**
     * Creates a new life cycle configuration.
     * 
     * @param adultAge the age at which inhabitants become adults
     * @param elderlyAge the age at which inhabitants become elderly
     * @param maxAge the maximum age any inhabitant can reach
     * @param mortalityModel the mortality model to use
     * @param fertilityModel the fertility model to use
     * @param partnershipModel the partnership model to use
     */
    @Builder
    public LifeCycleConfiguration(
            int adultAge,
            int elderlyAge,
            int maxAge,
            MortalityModel mortalityModel,
            FertilityModel fertilityModel,
            PartnershipModel partnershipModel) {
        
        validateParameters(adultAge, elderlyAge, maxAge, 
                mortalityModel, fertilityModel, partnershipModel);
        
        this.adultAge = adultAge;
        this.elderlyAge = elderlyAge;
        this.maxAge = maxAge;
        this.mortalityModel = mortalityModel;
        this.fertilityModel = fertilityModel;
        this.partnershipModel = partnershipModel;
    }
    
    // Getters and validation methods...
}
```

### SimulationControlConfiguration Class

```java
/**
 * Configuration for simulation control parameters.
 */
public class SimulationControlConfiguration {
    
    private final TimeUnit timeUnit;
    private final int timeStepSize;
    private final boolean autoSaveEnabled;
    private final int autoSaveInterval;
    private final long randomSeed;
    private final boolean deterministic;
    
    /**
     * Creates a new simulation control configuration.
     * 
     * @param timeUnit the time unit for simulation steps
     * @param timeStepSize the size of each time step
     * @param autoSaveEnabled whether auto-save is enabled
     * @param autoSaveInterval the interval between auto-saves
     * @param randomSeed the random seed for reproducible simulations
     * @param deterministic whether the simulation should be deterministic
     */
    @Builder
    public SimulationControlConfiguration(
            TimeUnit timeUnit,
            int timeStepSize,
            boolean autoSaveEnabled,
            int autoSaveInterval,
            long randomSeed,
            boolean deterministic) {
        
        validateParameters(timeUnit, timeStepSize, autoSaveInterval);
        
        this.timeUnit = timeUnit;
        this.timeStepSize = timeStepSize;
        this.autoSaveEnabled = autoSaveEnabled;
        this.autoSaveInterval = autoSaveInterval;
        this.randomSeed = randomSeed;
        this.deterministic = deterministic;
    }
    
    // Getters and validation methods...
}
```

### ConfigurationSource Interface

```java
/**
 * Source for loading configuration data.
 */
public interface ConfigurationSource {
    
    /**
     * Gets the configuration data as a stream.
     * 
     * @return input stream with configuration data
     * @throws IOException if reading fails
     */
    InputStream getInputStream() throws IOException;
    
    /**
     * Gets the source description.
     * 
     * @return source description for error reporting
     */
    String getDescription();
    
    /**
     * Gets the format of the configuration data.
     * 
     * @return configuration format
     */
    ConfigurationFormat getFormat();
}
```

### ConfigurationFormat Enumeration

```java
/**
 * Supported configuration file formats.
 */
public enum ConfigurationFormat {
    /**
     * JSON format
     */
    JSON,
    
    /**
     * YAML format
     */
    YAML,
    
    /**
     * Properties format
     */
    PROPERTIES
}
```

### ConfigurationValidationResult Class

```java
/**
 * Result of configuration validation.
 */
public class ConfigurationValidationResult {
    
    private final List<ValidationError> errors = new ArrayList<>();
    private final List<ValidationWarning> warnings = new ArrayList<>();
    
    /**
     * Adds an error to the validation result.
     * 
     * @param parameterPath the path to the parameter
     * @param message the error message
     */
    public void addError(String parameterPath, String message) {
        errors.add(new ValidationError(parameterPath, message));
    }
    
    /**
     * Adds a warning to the validation result.
     * 
     * @param parameterPath the path to the parameter
     * @param message the warning message
     */
    public void addWarning(String parameterPath, String message) {
        warnings.add(new ValidationWarning(parameterPath, message));
    }
    
    /**
     * Checks if validation passed without errors.
     * 
     * @return true if no errors were found
     */
    public boolean isValid() {
        return errors.isEmpty();
    }
    
    /**
     * Gets all validation errors.
     * 
     * @return list of validation errors
     */
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }
    
    /**
     * Gets all validation warnings.
     * 
     * @return list of validation warnings
     */
    public List<ValidationWarning> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
    
    /**
     * Represents a validation error.
     */
    public static class ValidationError {
        private final String parameterPath;
        private final String message;
        
        // Constructor and getters...
    }
    
    /**
     * Represents a validation warning.
     */
    public static class ValidationWarning {
        private final String parameterPath;
        private final String message;
        
        // Constructor and getters...
    }
}
```

### ConfigurationTemplate Interface

```java
/**
 * Template for predefined configurations.
 */
public interface ConfigurationTemplate {
    
    /**
     * Gets the template identifier.
     * 
     * @return the template ID
     */
    String getId();
    
    /**
     * Gets the template name.
     * 
     * @return the display name
     */
    String getName();
    
    /**
     * Gets the template description.
     * 
     * @return the description
     */
    String getDescription();
    
    /**
     * Creates a configuration from this template.
     * 
     * @return the created configuration
     */
    SimulationConfiguration createConfiguration();
    
    /**
     * Gets the category for this template.
     * 
     * @return the template category
     */
    String getCategory();
}
```

## Implementation Details

### DefaultConfigurationManager

The main implementation of the configuration management system:

```java
/**
 * Default implementation of the ConfigurationManager interface.
 */
@Component
public class DefaultConfigurationManager implements ConfigurationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultConfigurationManager.class);
    
    private final List<ConfigurationTemplate> templates;
    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;
    private final ConfigurationValidator validator;
    private final EventBus eventBus;
    
    private SimulationConfiguration currentConfiguration;
    
    /**
     * Creates a new DefaultConfigurationManager.
     * 
     * @param validator the configuration validator
     * @param eventBus the event bus for publishing events
     */
    @Inject
    public DefaultConfigurationManager(
            ConfigurationValidator validator,
            EventBus eventBus) {
        
        this.validator = Objects.requireNonNull(validator, "Validator cannot be null");
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus cannot be null");
        
        // Initialize object mappers
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.jsonMapper = new ObjectMapper();
        configureMappers(yamlMapper);
        configureMappers(jsonMapper);
        
        // Load templates
        this.templates = loadTemplates();
        
        // Set default configuration
        this.currentConfiguration = createDefaultConfiguration();
        
        logger.info("Configuration manager initialized with {} templates", templates.size());
    }
    
    @Override
    public SimulationConfiguration getCurrentConfiguration() {
        return currentConfiguration;
    }
    
    @Override
    public SimulationConfiguration loadConfiguration(ConfigurationSource source) 
            throws ConfigurationException {
        
        Objects.requireNonNull(source, "Configuration source cannot be null");
        
        try (InputStream inputStream = source.getInputStream()) {
            logger.debug("Loading configuration from source: {}", source.getDescription());
            
            SimulationConfiguration configuration = parseConfiguration(
                    inputStream, source.getFormat());
            
            // Validate configuration
            ConfigurationValidationResult validationResult = validateConfiguration(configuration);
            if (!validationResult.isValid()) {
                throw new ConfigurationException(
                        "Invalid configuration: " + formatValidationErrors(validationResult));
            }
            
            // Publish event
            publishConfigurationLoadedEvent(configuration, source.getDescription());
            
            logger.info("Successfully loaded configuration: {}", configuration.getName());
            
            return configuration;
            
        } catch (IOException e) {
            logger.error("Failed to load configuration: {}", source.getDescription(), e);
            throw new ConfigurationException("Failed to load configuration", e);
        }
    }
    
    @Override
    public SimulationConfiguration loadConfigurationFromFile(Path filePath) 
            throws ConfigurationException {
        
        Objects.requireNonNull(filePath, "File path cannot be null");
        
        if (!Files.exists(filePath)) {
            throw new ConfigurationException("Configuration file does not exist: " + filePath);
        }
        
        try {
            // Determine format from file extension
            ConfigurationFormat format = determineFormatFromExtension(filePath);
            
            // Create file source
            ConfigurationSource source = new FileConfigurationSource(filePath, format);
            
            return loadConfiguration(source);
            
        } catch (Exception e) {
            logger.error("Failed to load configuration from file: {}", filePath, e);
            throw new ConfigurationException("Failed to load configuration from file", e);
        }
    }
    
    @Override
    public void saveConfiguration(Path filePath) throws ConfigurationException {
        saveConfiguration(currentConfiguration, filePath);
    }
    
    @Override
    public void saveConfiguration(SimulationConfiguration configuration, Path filePath) 
            throws ConfigurationException {
        
        Objects.requireNonNull(configuration, "Configuration cannot be null");
        Objects.requireNonNull(filePath, "File path cannot be null");
        
        try {
            // Determine format from file extension
            ConfigurationFormat format = determineFormatFromExtension(filePath);
            
            // Ensure parent directory exists
            Files.createDirectories(filePath.getParent());
            
            // Write configuration to file
            switch (format) {
                case JSON:
                    jsonMapper.writeValue(filePath.toFile(), configuration);
                    break;
                case YAML:
                    yamlMapper.writeValue(filePath.toFile(), configuration);
                    break;
                case PROPERTIES:
                    saveAsProperties(configuration, filePath);
                    break;
                default:
                    throw new ConfigurationException("Unsupported configuration format: " + format);
            }
            
            // Publish event
            publishConfigurationSavedEvent(configuration, filePath.toString());
            
            logger.info("Successfully saved configuration to: {}", filePath);
            
        } catch (Exception e) {
            logger.error("Failed to save configuration to file: {}", filePath, e);
            throw new ConfigurationException("Failed to save configuration to file", e);
        }
    }
    
    @Override
    public void setCurrentConfiguration(SimulationConfiguration configuration) 
            throws ConfigurationException {
        
        Objects.requireNonNull(configuration, "Configuration cannot be null");
        
        // Validate configuration
        ConfigurationValidationResult validationResult = validateConfiguration(configuration);
        if (!validationResult.isValid()) {
            throw new ConfigurationException(
                    "Invalid configuration: " + formatValidationErrors(validationResult));
        }
        
        // Update current configuration
        SimulationConfiguration previousConfig = this.currentConfiguration;
        this.currentConfiguration = configuration;
        
        // Publish event
        publishConfigurationChangedEvent(previousConfig, configuration);
        
        logger.info("Set current configuration: {}", configuration.getName());
    }
    
    @Override
    public ConfigurationValidationResult validateConfiguration(SimulationConfiguration configuration) {
        return validator.validate(configuration);
    }
    
    @Override
    public SimulationConfiguration createDefaultConfiguration() {
        logger.debug("Creating default configuration");
        
        // Create component configurations with default values
        PopulationConfiguration populationConfig = PopulationConfiguration.builder()
                .initialPopulationSize(100)
                .annualImmigrationRate(5)
                .annualEmigrationRate(0.02)
                .minPopulationSize(10)
                .maxPopulationSize(100000)
                .build();
        
        DemographicConfiguration demographicConfig = DemographicConfiguration.builder()
                .maleRatio(0.5)
                .ageDistribution(AgeDistribution.BALANCED)
                .healthDistribution(HealthDistribution.NORMAL)
                .build();
        
        LifeCycleConfiguration lifeCycleConfig = LifeCycleConfiguration.builder()
                .adultAge(18)
                .elderlyAge(65)
                .maxAge(100)
                .mortalityModel(MortalityModel.REALISTIC)
                .fertilityModel(FertilityModel.STANDARD)
                .partnershipModel(PartnershipModel.STANDARD)
                .build();
        
        SimulationControlConfiguration controlConfig = SimulationControlConfiguration.builder()
                .timeUnit(TimeUnit.YEAR)
                .timeStepSize(1)
                .autoSaveEnabled(true)
                .autoSaveInterval(10)
                .randomSeed(System.currentTimeMillis())
                .deterministic(false)
                .build();
        
        // Create top-level configuration
        return SimulationConfiguration.builder()
                .name("Default Configuration")
                .description("Default simulation configuration with balanced demographics")
                .populationConfig(populationConfig)
                .demographicConfig(demographicConfig)
                .lifeCycleConfig(lifeCycleConfig)
                .controlConfig(controlConfig)
                .build();
    }
    
    @Override
    public List<ConfigurationTemplate> getAvailableTemplates() {
        return Collections.unmodifiableList(templates);
    }
    
    @Override
    public SimulationConfiguration loadFromTemplate(String templateId) throws ConfigurationException {
        Optional<ConfigurationTemplate> template = templates.stream()
                .filter(t -> t.getId().equals(templateId))
                .findFirst();
        
        if (template.isPresent()) {
            logger.debug("Loading configuration from template: {}", templateId);
            return template.get().createConfiguration();
        } else {
            throw new ConfigurationException("Template not found: " + templateId);
        }
    }
    
    /**
     * Configures Jackson object mappers with common settings.
     * 
     * @param mapper the mapper to configure
     */
    private void configureMappers(ObjectMapper mapper) {
        // Configure common mapper settings
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    
    /**
     * Loads available configuration templates.
     * 
     * @return list of available templates
     */
    private List<ConfigurationTemplate> loadTemplates() {
        List<ConfigurationTemplate> result = new ArrayList<>();
        
        // Add built-in templates
        result.add(new MedievalVillageTemplate());
        result.add(new ModernTownTemplate());
        result.add(new FuturisticColonyTemplate());
        
        // Load custom templates from classpath
        // (Implementation would depend on how templates are stored)
        
        return result;
    }
    
    /**
     * Parses configuration from input stream based on format.
     * 
     * @param inputStream the input stream to parse
     * @param format the configuration format
     * @return the parsed configuration
     * @throws IOException if parsing fails
     * @throws ConfigurationException if format is not supported
     */
    private SimulationConfiguration parseConfiguration(
            InputStream inputStream, ConfigurationFormat format) 
            throws IOException, ConfigurationException {
        
        switch (format) {
            case JSON:
                return jsonMapper.readValue(inputStream, SimulationConfiguration.class);
            case YAML:
                return yamlMapper.readValue(inputStream, SimulationConfiguration.class);
            case PROPERTIES:
                return parseFromProperties(inputStream);
            default:
                throw new ConfigurationException("Unsupported configuration format: " + format);
        }
    }
    
    /**
     * Determines configuration format from file extension.
     * 
     * @param filePath the file path
     * @return the configuration format
     * @throws ConfigurationException if the extension is not recognized
     */
    private ConfigurationFormat determineFormatFromExtension(Path filePath) 
            throws ConfigurationException {
        
        String fileName = filePath.getFileName().toString().toLowerCase();
        
        if (fileName.endsWith(".json")) {
            return ConfigurationFormat.JSON;
        } else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            return ConfigurationFormat.YAML;
        } else if (fileName.endsWith(".properties")) {
            return ConfigurationFormat.PROPERTIES;
        } else {
            throw new ConfigurationException("Unrecognized configuration file extension: " + fileName);
        }
    }
    
    /**
     * Formats validation errors for error message.
     * 
     * @param result the validation result
     * @return formatted error message
     */
    private String formatValidationErrors(ConfigurationValidationResult result) {
        return result.getErrors().stream()
                .map(e -> e.getParameterPath() + ": " + e.getMessage())
                .collect(Collectors.joining(", "));
    }
    
    /**
     * Publishes configuration loaded event.
     * 
     * @param configuration the loaded configuration
     * @param source the source description
     */
    private void publishConfigurationLoadedEvent(
            SimulationConfiguration configuration, String source) {
        
        try {
            ConfigurationLoadedEvent event = new ConfigurationLoadedEvent(
                    configuration, source, LocalDateTime.now());
            eventBus.publishEvent(event);
        } catch (Exception e) {
            logger.warn("Failed to publish configuration loaded event", e);
        }
    }
    
    /**
     * Publishes configuration saved event.
     * 
     * @param configuration the saved configuration
     * @param destination the destination description
     */
    private void publishConfigurationSavedEvent(
            SimulationConfiguration configuration, String destination) {
        
        try {
            ConfigurationSavedEvent event = new ConfigurationSavedEvent(
                    configuration, destination, LocalDateTime.now());
            eventBus.publishEvent(event);
        } catch (Exception e) {
            logger.warn("Failed to publish configuration saved event", e);
        }
    }
    
    /**
     * Publishes configuration changed event.
     * 
     * @param oldConfiguration the previous configuration
     * @param newConfiguration the new configuration
     */
    private void publishConfigurationChangedEvent(
            SimulationConfiguration oldConfiguration, 
            SimulationConfiguration newConfiguration) {
        
        try {
            ConfigurationChangedEvent event = new ConfigurationChangedEvent(
                    oldConfiguration, newConfiguration, LocalDateTime.now());
            eventBus.publishEvent(event);
        } catch (Exception e) {
            logger.warn("Failed to publish configuration changed event", e);
        }
    }
    
    /**
     * Parses configuration from properties format.
     * 
     * @param inputStream the input stream to parse
     * @return the parsed configuration
     * @throws IOException if parsing fails
     */
    private SimulationConfiguration parseFromProperties(InputStream inputStream) throws IOException {
        // Implementation would convert properties to configuration
        // This is a placeholder for the actual implementation
        Properties properties = new Properties();
        properties.load(inputStream);
        
        // Convert properties to configuration objects
        // (Actual implementation would be more complex)
        
        throw new UnsupportedOperationException("Properties format not yet implemented");
    }
    
    /**
     * Saves configuration in properties format.
     * 
     * @param configuration the configuration to save
     * @param filePath the file path to save to
     * @throws IOException if saving fails
     */
    private void saveAsProperties(SimulationConfiguration configuration, Path filePath) 
            throws IOException {
        
        // Implementation would convert configuration to properties
        // This is a placeholder for the actual implementation
        Properties properties = new Properties();
        
        // Convert configuration objects to properties
        // (Actual implementation would be more complex)
        
        try (OutputStream os = Files.newOutputStream(filePath)) {
            properties.store(os, "LittlePeople Simulation Configuration");
        }
        
        throw new UnsupportedOperationException("Properties format not yet implemented");
    }
}
```

### DefaultConfigurationValidator

The main implementation of the configuration validation system:

```java
/**
 * Default implementation of the ConfigurationValidator interface.
 */
@Component
public class DefaultConfigurationValidator implements ConfigurationValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultConfigurationValidator.class);
    
    // Constants for validation rules
    private static final int MIN_INITIAL_POPULATION_SIZE = 10;
    private static final int MAX_INITIAL_POPULATION_SIZE = 100000;
    private static final double MIN_IMMIGRATION_RATE = 0.0;
    private static final double MAX_IMMIGRATION_RATE = 10000.0;
    private static final double MIN_EMIGRATION_RATE = 0.0;
    private static final double MAX_EMIGRATION_RATE = 1.0;
    private static final int MIN_POPULATION_SIZE = 1;
    private static final int MAX_POPULATION_SIZE = 100000;
    private static final double MIN_MALE_RATIO = 0.0;
    private static final double MAX_MALE_RATIO = 1.0;
    private static final int MIN_ADULT_AGE = 10;
    private static final int MAX_ADULT_AGE = 30;
    private static final int MIN_ELDERLY_AGE = 40;
    private static final int MAX_ELDERLY_AGE = 90;
    private static final int MIN_MAX_AGE = 50;
    private static final int MAX_MAX_AGE = 150;
    
    /**
     * Creates a new DefaultConfigurationValidator.
     */
    public DefaultConfigurationValidator() {
        logger.info("Configuration validator initialized");
    }
    
    @Override
    public ConfigurationValidationResult validate(SimulationConfiguration configuration) {
        Objects.requireNonNull(configuration, "Configuration cannot be null");
        
        logger.debug("Validating configuration: {}", configuration.getName());
        
        ConfigurationValidationResult result = new ConfigurationValidationResult();
        
        // Validate each component configuration
        validatePopulationConfig(configuration.getPopulationConfig(), result);
        validateDemographicConfig(configuration.getDemographicConfig(), result);
        validateLifeCycleConfig(configuration.getLifeCycleConfig(), result);
        validateControlConfig(configuration.getControlConfig(), result);
        
        // Validate cross-component relationships
        validateCrossComponentRelationships(configuration, result);
        
        // Validate custom parameters
        validateCustomParameters(configuration.getCustomParameters(), result);
        
        // Log validation results
        int errorCount = result.getErrors().size();
        int warningCount = result.getWarnings().size();
        
        if (errorCount > 0) {
            logger.warn("Configuration validation failed with {} errors and {} warnings",
                    errorCount, warningCount);
        } else if (warningCount > 0) {
            logger.info("Configuration validation passed with {} warnings", warningCount);
        } else {
            logger.debug("Configuration validation passed successfully");
        }
        
        return result;
    }
    
    /**
     * Validates population configuration.
     * 
     * @param config the population configuration to validate
     * @param result the validation result to update
     */
    private void validatePopulationConfig(
            PopulationConfiguration config, ConfigurationValidationResult result) {
        
        if (config == null) {
            result.addError("populationConfig", "Population configuration cannot be null");
            return;
        }
        
        // Validate initial population size
        int initialSize = config.getInitialPopulationSize();
        if (initialSize < MIN_INITIAL_POPULATION_SIZE) {
            result.addError("populationConfig.initialPopulationSize",
                    "Initial population size must be at least " + MIN_INITIAL_POPULATION_SIZE);
        } else if (initialSize > MAX_INITIAL_POPULATION_SIZE) {
            result.addWarning("populationConfig.initialPopulationSize",
                    "Large initial population may impact performance");
        }
        
        // Validate immigration rate
        double immigrationRate = config.getAnnualImmigrationRate();
        if (immigrationRate < MIN_IMMIGRATION_RATE) {
            result.addError("populationConfig.annualImmigrationRate",
                    "Annual immigration rate cannot be negative");
        } else if (immigrationRate > MAX_IMMIGRATION_RATE) {
            result.addWarning("populationConfig.annualImmigrationRate",
                    "Very high immigration rate may lead to rapid population growth");
        }
        
        // Validate emigration rate
        double emigrationRate = config.getAnnualEmigrationRate();
        if (emigrationRate < MIN_EMIGRATION_RATE) {
            result.addError("populationConfig.annualEmigrationRate",
                    "Annual emigration rate cannot be negative");
        } else if (emigrationRate > MAX_EMIGRATION_RATE) {
            result.addError("populationConfig.annualEmigrationRate",
                    "Annual emigration rate cannot exceed 100%");
        }
        
        // Validate population size limits
        int minSize = config.getMinPopulationSize();
        int maxSize = config.getMaxPopulationSize();
        
        if (minSize < MIN_POPULATION_SIZE) {
            result.addError("populationConfig.minPopulationSize",
                    "Minimum population size must be at least " + MIN_POPULATION_SIZE);
        }
        
        if (maxSize > MAX_POPULATION_SIZE) {
            result.addWarning("populationConfig.maxPopulationSize",
                    "Very large maximum population may impact performance");
        }
        
        if (minSize > maxSize) {
            result.addError("populationConfig",
                    "Minimum population size cannot be greater than maximum");
        }
        
        if (initialSize < minSize) {
            result.addError("populationConfig",
                    "Initial population size cannot be less than minimum");
        }
        
        if (initialSize > maxSize) {
            result.addError("populationConfig",
                    "Initial population size cannot be greater than maximum");
        }
    }
    
    /**
     * Validates demographic configuration.
     * 
     * @param config the demographic configuration to validate
     * @param result the validation result to update
     */
    private void validateDemographicConfig(
            DemographicConfiguration config, ConfigurationValidationResult result) {
        
        if (config == null) {
            result.addError("demographicConfig", "Demographic configuration cannot be null");
            return;
        }
        
        // Validate male ratio
        double maleRatio = config.getMaleRatio();
        if (maleRatio < MIN_MALE_RATIO) {
            result.addError("demographicConfig.maleRatio",
                    "Male ratio cannot be negative");
        } else if (maleRatio > MAX_MALE_RATIO) {
            result.addError("demographicConfig.maleRatio",
                    "Male ratio cannot exceed 100%");
        }
        
        // Validate age distribution
        if (config.getAgeDistribution() == null) {
            result.addError("demographicConfig.ageDistribution",
                    "Age distribution cannot be null");
        }
        
        // Validate health distribution
        if (config.getHealthDistribution() == null) {
            result.addError("demographicConfig.healthDistribution",
                    "Health distribution cannot be null");
        }
    }
    
    /**
     * Validates life cycle configuration.
     * 
     * @param config the life cycle configuration to validate
     * @param result the validation result to update
     */
    private void validateLifeCycleConfig(
            LifeCycleConfiguration config, ConfigurationValidationResult result) {
        
        if (config == null) {
            result.addError("lifeCycleConfig", "Life cycle configuration cannot be null");
            return;
        }
        
        // Validate adult age
        int adultAge = config.getAdultAge();
        if (adultAge < MIN_ADULT_AGE) {
            result.addError("lifeCycleConfig.adultAge",
                    "Adult age must be at least " + MIN_ADULT_AGE);
        } else if (adultAge > MAX_ADULT_AGE) {
            result.addWarning("lifeCycleConfig.adultAge",
                    "Adult age is unusually high");
        }
        
        // Validate elderly age
        int elderlyAge = config.getElderlyAge();
        if (elderlyAge < MIN_ELDERLY_AGE) {
            result.addError("lifeCycleConfig.elderlyAge",
                    "Elderly age must be at least " + MIN_ELDERLY_AGE);
        } else if (elderlyAge > MAX_ELDERLY_AGE) {
            result.addWarning("lifeCycleConfig.elderlyAge",
                    "Elderly age is unusually high");
        }
        
        // Validate max age
        int maxAge = config.getMaxAge();
        if (maxAge < MIN_MAX_AGE) {
            result.addError("lifeCycleConfig.maxAge",
                    "Maximum age must be at least " + MIN_MAX_AGE);
        } else if (maxAge > MAX_MAX_AGE) {
            result.addWarning("lifeCycleConfig.maxAge",
                    "Maximum age is unusually high");
        }
        
        // Validate age relationships
        if (adultAge >= elderlyAge) {
            result.addError("lifeCycleConfig",
                    "Adult age must be less than elderly age");
        }
        
        if (elderlyAge >= maxAge) {
            result.addError("lifeCycleConfig",
                    "Elderly age must be less than maximum age");
        }
        
        // Validate models
        if (config.getMortalityModel() == null) {
            result.addError("lifeCycleConfig.mortalityModel",
                    "Mortality model cannot be null");
        }
        
        if (config.getFertilityModel() == null) {
            result.addError("lifeCycleConfig.fertilityModel",
                    "Fertility model cannot be null");
        }
        
        if (config.getPartnershipModel() == null) {
            result.addError("lifeCycleConfig.partnershipModel",
                    "Partnership model cannot be null");
        }
    }
    
    /**
     * Validates simulation control configuration.
     * 
     * @param config the control configuration to validate
     * @param result the validation result to update
     */
    private void validateControlConfig(
            SimulationControlConfiguration config, ConfigurationValidationResult result) {
        
        if (config == null) {
            result.addError("controlConfig", "Control configuration cannot be null");
            return;
        }
        
        // Validate time unit
        if (config.getTimeUnit() == null) {
            result.addError("controlConfig.timeUnit",
                    "Time unit cannot be null");
        }
        
        // Validate time step size
        int timeStepSize = config.getTimeStepSize();
        if (timeStepSize <= 0) {
            result.addError("controlConfig.timeStepSize",
                    "Time step size must be positive");
        } else if (timeStepSize > 10) {
            result.addWarning("controlConfig.timeStepSize",
                    "Large time steps may reduce simulation accuracy");
        }
        
        // Validate auto-save interval
        if (config.isAutoSaveEnabled()) {
            int autoSaveInterval = config.getAutoSaveInterval();
            if (autoSaveInterval <= 0) {
                result.addError("controlConfig.autoSaveInterval",
                        "Auto-save interval must be positive");
            } else if (autoSaveInterval > 100) {
                result.addWarning("controlConfig.autoSaveInterval",
                        "Long auto-save interval may risk data loss");
            }
        }
    }
    
    /**
     * Validates relationships between different configuration components.
     * 
     * @param config the complete configuration to validate
     * @param result the validation result to update
     */
    private void validateCrossComponentRelationships(
            SimulationConfiguration config, ConfigurationValidationResult result) {
        
        // Check if adult age is compatible with child-bearing logic
        LifeCycleConfiguration lifeCycleConfig = config.getLifeCycleConfig();
        if (lifeCycleConfig != null) {
            int adultAge = lifeCycleConfig.getAdultAge();
            
            // Some specific cross-component validations would go here
            // For example, checking if adult age matches fertility model assumptions
            
            if (lifeCycleConfig.getFertilityModel() == FertilityModel.STANDARD && adultAge > 18) {
                result.addWarning("lifeCycleConfig",
                        "Standard fertility model assumes adult age of 18, but configured age is " + adultAge);
            }
        }
        
        // Additional cross-component validations...
    }
    
    /**
     * Validates custom parameters.
     * 
     * @param customParameters the custom parameters to validate
     * @param result the validation result to update
     */
    private void validateCustomParameters(
            Map<String, Object> customParameters, ConfigurationValidationResult result) {
        
        // In a real implementation, this would validate any extension-specific
        // parameters based on registered validators from plugins
        
        // For now, just log the presence of custom parameters
        if (customParameters != null && !customParameters.isEmpty()) {
            logger.debug("Found {} custom parameters", customParameters.size());
        }
    }
}
```

### Example Configuration Templates

```java
/**
 * Template for a medieval village configuration.
 */
public class MedievalVillageTemplate implements ConfigurationTemplate {
    
    @Override
    public String getId() {
        return "medieval-village";
    }
    
    @Override
    public String getName() {
        return "Medieval Village";
    }
    
    @Override
    public String getDescription() {
        return "Simulates a medieval village with historically accurate demographic patterns";
    }
    
    @Override
    public SimulationConfiguration createConfiguration() {
        // Create a configuration tuned for medieval demographics
        
        PopulationConfiguration populationConfig = PopulationConfiguration.builder()
                .initialPopulationSize(250)
                .annualImmigrationRate(3)
                .annualEmigrationRate(0.01)
                .minPopulationSize(50)
                .maxPopulationSize(100000)
                .build();
        
        DemographicConfiguration demographicConfig = DemographicConfiguration.builder()
                .maleRatio(0.51)
                .ageDistribution(AgeDistribution.YOUNG)
                .healthDistribution(HealthDistribution.HISTORICAL)
                .build();
        
        LifeCycleConfiguration lifeCycleConfig = LifeCycleConfiguration.builder()
                .adultAge(14)
                .elderlyAge(50)
                .maxAge(70)
                .mortalityModel(MortalityModel.HISTORICAL)
                .fertilityModel(FertilityModel.HISTORICAL)
                .partnershipModel(PartnershipModel.TRADITIONAL)
                .build();
        
        SimulationControlConfiguration controlConfig = SimulationControlConfiguration.builder()
                .timeUnit(TimeUnit.YEAR)
                .timeStepSize(1)
                .autoSaveEnabled(true)
                .autoSaveInterval(10)
                .randomSeed(System.currentTimeMillis())
                .deterministic(false)
                .build();
        
        return SimulationConfiguration.builder()
                .name("Medieval Village Simulation")
                .description("Historical simulation of a medieval village (circa 1300 CE)")
                .populationConfig(populationConfig)
                .demographicConfig(demographicConfig)
                .lifeCycleConfig(lifeCycleConfig)
                .controlConfig(controlConfig)
                .build();
    }
    
    @Override
    public String getCategory() {
        return "Historical";
    }
}

/**
 * Template for a modern town configuration.
 */
public class ModernTownTemplate implements ConfigurationTemplate {
    
    @Override
    public String getId() {
        return "modern-town";
    }
    
    @Override
    public String getName() {
        return "Modern Town";
    }
    
    @Override
    public String getDescription() {
        return "Simulates a contemporary small town with modern demographic patterns";
    }
    
    @Override
    public SimulationConfiguration createConfiguration() {
        // Create a configuration for modern demographics
        
        PopulationConfiguration populationConfig = PopulationConfiguration.builder()
                .initialPopulationSize(500)
                .annualImmigrationRate(15)
                .annualEmigrationRate(0.03)
                .minPopulationSize(100)
                .maxPopulationSize(2000)
                .build();
        
        DemographicConfiguration demographicConfig = DemographicConfiguration.builder()
                .maleRatio(0.49)
                .ageDistribution(AgeDistribution.BALANCED)
                .healthDistribution(HealthDistribution.NORMAL)
                .build();
        
        LifeCycleConfiguration lifeCycleConfig = LifeCycleConfiguration.builder()
                .adultAge(18)
                .elderlyAge(65)
                .maxAge(100)
                .mortalityModel(MortalityModel.REALISTIC)
                .fertilityModel(FertilityModel.MODERN)
                .partnershipModel(PartnershipModel.MODERN)
                .build();
        
        SimulationControlConfiguration controlConfig = SimulationControlConfiguration.builder()
                .timeUnit(TimeUnit.YEAR)
                .timeStepSize(1)
                .autoSaveEnabled(true)
                .autoSaveInterval(5)
                .randomSeed(System.currentTimeMillis())
                .deterministic(false)
                .build();
        
        return SimulationConfiguration.builder()
                .name("Modern Town Simulation")
                .description("Contemporary small town simulation (present day)")
                .populationConfig(populationConfig)
                .demographicConfig(demographicConfig)
                .lifeCycleConfig(lifeCycleConfig)
                .controlConfig(controlConfig)
                .build();
    }
    
    @Override
    public String getCategory() {
        return "Contemporary";
    }
}
```

### Example Configuration Files

#### JSON Configuration Example

```json
{
  "name": "Small Village",
  "description": "A small village with balanced demographics",
  "populationConfig": {
    "initialPopulationSize": 150,
    "annualImmigrationRate": 5,
    "annualEmigrationRate": 0.02,
    "minPopulationSize": 50,
    "maxPopulationSize": 500
  },
  "demographicConfig": {
    "maleRatio": 0.51,
    "ageDistribution": "BALANCED",
    "healthDistribution": "NORMAL"
  },
  "lifeCycleConfig": {
    "adultAge": 18,
    "elderlyAge": 65,
    "maxAge": 90,
    "mortalityModel": "REALISTIC",
    "fertilityModel": "STANDARD",
    "partnershipModel": "STANDARD"
  },
  "controlConfig": {
    "timeUnit": "YEAR",
    "timeStepSize": 1,
    "autoSaveEnabled": true,
    "autoSaveInterval": 10,
    "randomSeed": 123456789,
    "deterministic": false
  },
  "customParameters": {
    "customValue1": 42,
    "customFlag": true
  }
}
```

#### YAML Configuration Example

```yaml
name: Small Village
description: A small village with balanced demographics

populationConfig:
  initialPopulationSize: 150
  annualImmigrationRate: 5
  annualEmigrationRate: 0.02
  minPopulationSize: 50
  maxPopulationSize: 500

demographicConfig:
  maleRatio: 0.51
  ageDistribution: BALANCED
  healthDistribution: NORMAL

lifeCycleConfig:
  adultAge: 18
  elderlyAge: 65
  maxAge: 90
  mortalityModel: REALISTIC
  fertilityModel: STANDARD
  partnershipModel: STANDARD

controlConfig:
  timeUnit: YEAR
  timeStepSize: 1
  autoSaveEnabled: true
  autoSaveInterval: 10
  randomSeed: 123456789
  deterministic: false

customParameters:
  customValue1: 42
  customFlag: true
```

## Acceptance Criteria

- [x] Configuration can be loaded from JSON, YAML, and Properties formats
- [x] Configuration parameters are validated for constraints and consistency
- [x] Configuration can be saved to files in multiple formats
- [x] Default configuration provides reasonable starting values
- [x] Predefined configuration templates for different scenarios are available
- [x] Custom parameters support for extensions
- [x] Configuration validation provides clear error messages
- [x] Configuration changes generate appropriate events
- [x] Comprehensive documentation of all configuration parameters
- [x] Configuration values maintain correct types and ranges
- [x] Builder pattern for programmatic configuration creation
- [x] Thread-safe implementation for concurrent access
- [x] Complete unit tests for all configuration components
- [x] All components have comprehensive JavaDoc documentation

## Dependencies

**Builds Upon:**
- RFC-001: Project Setup and Architecture
- RFC-002: Core Entity Models
- RFC-003: Simulation Clock and Event System
- RFC-005: Partnerships and Family Formation
- RFC-006: Population Management

**Required For:**
- RFC-008: Simulation Control Interface
- RFC-009: Persistence Layer
- RFC-011: Statistics and Reporting

## Testing Strategy

### Unit Tests

#### ConfigurationManager Tests
- Test loading from various sources (file, stream, template)
- Test saving to different formats (JSON, YAML, Properties)
- Test configuration validation
- Test default configuration creation
- Test template application

#### Configuration Models Tests
- Test builder pattern for all configuration classes
- Test validation logic for parameters
- Test constraints and cross-parameter validation
- Test immutability and defensive copying

#### ConfigurationValidator Tests
- Test validation of valid configurations
- Test validation of invalid configurations
- Test validation error messages
- Test validation warnings
- Test cross-component validation

### Integration Tests

- Test end-to-end configuration loading, validation, and application
- Test configuration template application to simulation
- Test configuration persistence across simulation runs
- Test configuration event handling
- Test custom parameter handling for extensions

## Security Considerations

- Validate all external configuration input to prevent injection attacks
- Use defensive copying for mutable objects to prevent modification
- Implement proper error handling for malformed configuration files
- Apply secure JSON/YAML parsing to prevent deserialization attacks
- Validate file paths to prevent directory traversal
- Sanitize configuration values before using in database or file operations
- Handle configuration files with appropriate permissions

## Performance Considerations

- Optimize Jackson configuration for fast parsing and serialization
- Implement lazy loading for large configuration files
- Cache validation results for repeated validation operations
- Use efficient data structures for configuration parameter access
- Minimize defensive copying where safe to do so
- Consider incremental validation for performance-critical scenarios
- Implement efficient error collection to avoid excessive object creation

## Open Questions

1. Should we support environment variable overrides for configuration parameters?
2. How should configuration versioning be handled for backward compatibility?
3. Should we implement a configuration diff tool for comparing configurations?
4. How detailed should validation error messages be for end users?
5. Should we support partial configuration updates or require complete configurations?

## References

- [Jackson Documentation](https://github.com/FasterXML/jackson-docs)
- [YAML Specification](https://yaml.org/spec/1.2.2/)
- [Java Properties](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Properties.html)
- [Builder Pattern](https://refactoring.guru/design-patterns/builder)
- [Validation Best Practices](https://docs.oracle.com/javaee/7/tutorial/bean-validation001.htm)
