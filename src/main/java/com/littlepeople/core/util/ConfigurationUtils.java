package com.littlepeople.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.littlepeople.core.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class for configuration loading and management.
 *
 * <p>This class provides cross-platform configuration loading capabilities
 * supporting various formats including YAML, JSON, and Properties files.
 * It handles file path resolution in an OS-independent manner.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public final class ConfigurationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Private constructor to prevent instantiation.
     */
    private ConfigurationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Loads a YAML configuration file from the classpath or file system.
     *
     * @param configPath the path to the configuration file
     * @param configClass the class to deserialize the configuration into
     * @param <T> the type of the configuration object
     * @return the loaded configuration object
     * @throws ConfigurationException if the configuration cannot be loaded
     */
    public static <T> T loadYamlConfig(String configPath, Class<T> configClass)
            throws ConfigurationException {
        ValidationUtils.requireNonEmpty(configPath, "configPath");
        ValidationUtils.requireNonNull(configClass, "configClass");

        try {
            // Try loading from classpath first
            InputStream inputStream = ConfigurationUtils.class.getClassLoader()
                .getResourceAsStream(configPath);

            if (inputStream != null) {
                logger.debug("Loading YAML configuration from classpath: {}", configPath);
                return yamlMapper.readValue(inputStream, configClass);
            }

            // Try loading from file system
            Path filePath = Paths.get(configPath);
            if (Files.exists(filePath)) {
                logger.debug("Loading YAML configuration from file system: {}", configPath);
                return yamlMapper.readValue(filePath.toFile(), configClass);
            }

            throw new ConfigurationException("Configuration file not found: " + configPath);

        } catch (IOException e) {
            throw new ConfigurationException("Failed to load YAML configuration: " + configPath, e);
        }
    }

    /**
     * Loads a JSON configuration file from the classpath or file system.
     *
     * @param configPath the path to the configuration file
     * @param configClass the class to deserialize the configuration into
     * @param <T> the type of the configuration object
     * @return the loaded configuration object
     * @throws ConfigurationException if the configuration cannot be loaded
     */
    public static <T> T loadJsonConfig(String configPath, Class<T> configClass)
            throws ConfigurationException {
        ValidationUtils.requireNonEmpty(configPath, "configPath");
        ValidationUtils.requireNonNull(configClass, "configClass");

        try {
            // Try loading from classpath first
            InputStream inputStream = ConfigurationUtils.class.getClassLoader()
                .getResourceAsStream(configPath);

            if (inputStream != null) {
                logger.debug("Loading JSON configuration from classpath: {}", configPath);
                return jsonMapper.readValue(inputStream, configClass);
            }

            // Try loading from file system
            Path filePath = Paths.get(configPath);
            if (Files.exists(filePath)) {
                logger.debug("Loading JSON configuration from file system: {}", configPath);
                return jsonMapper.readValue(filePath.toFile(), configClass);
            }

            throw new ConfigurationException("Configuration file not found: " + configPath);

        } catch (IOException e) {
            throw new ConfigurationException("Failed to load JSON configuration: " + configPath, e);
        }
    }

    /**
     * Loads a Properties configuration file from the classpath or file system.
     *
     * @param configPath the path to the configuration file
     * @return the loaded properties
     * @throws ConfigurationException if the configuration cannot be loaded
     */
    public static Properties loadProperties(String configPath) throws ConfigurationException {
        ValidationUtils.requireNonEmpty(configPath, "configPath");

        Properties properties = new Properties();

        try {
            // Try loading from classpath first
            InputStream inputStream = ConfigurationUtils.class.getClassLoader()
                .getResourceAsStream(configPath);

            if (inputStream != null) {
                logger.debug("Loading properties from classpath: {}", configPath);
                properties.load(inputStream);
                return properties;
            }

            // Try loading from file system
            Path filePath = Paths.get(configPath);
            if (Files.exists(filePath)) {
                logger.debug("Loading properties from file system: {}", configPath);
                try (InputStream fileStream = Files.newInputStream(filePath)) {
                    properties.load(fileStream);
                    return properties;
                }
            }

            throw new ConfigurationException("Configuration file not found: " + configPath);

        } catch (IOException e) {
            throw new ConfigurationException("Failed to load properties: " + configPath, e);
        }
    }

    /**
     * Resolves a file path in an OS-independent manner.
     *
     * @param path the path to resolve
     * @return the resolved path
     */
    public static Path resolvePath(String path) {
        ValidationUtils.requireNonEmpty(path, "path");

        // Handle home directory expansion
        if (path.startsWith("~/")) {
            String userHome = System.getProperty("user.home");
            path = userHome + path.substring(1);
        }

        return Paths.get(path).normalize();
    }

    /**
     * Gets the system-appropriate configuration directory.
     *
     * @param applicationName the name of the application
     * @return the configuration directory path
     */
    public static Path getConfigDirectory(String applicationName) {
        ValidationUtils.requireNonEmpty(applicationName, "applicationName");

        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            // Windows: %APPDATA%\ApplicationName
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                return Paths.get(appData, applicationName);
            }
            return Paths.get(userHome, "AppData", "Roaming", applicationName);
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/ApplicationName
            return Paths.get(userHome, "Library", "Application Support", applicationName);
        } else {
            // Linux/Unix: ~/.config/ApplicationName
            String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
            if (xdgConfigHome != null) {
                return Paths.get(xdgConfigHome, applicationName);
            }
            return Paths.get(userHome, ".config", applicationName);
        }
    }

    /**
     * Gets the system-appropriate data directory.
     *
     * @param applicationName the name of the application
     * @return the data directory path
     */
    public static Path getDataDirectory(String applicationName) {
        ValidationUtils.requireNonEmpty(applicationName, "applicationName");

        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            // Windows: %LOCALAPPDATA%\ApplicationName
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null) {
                return Paths.get(localAppData, applicationName);
            }
            return Paths.get(userHome, "AppData", "Local", applicationName);
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/ApplicationName
            return Paths.get(userHome, "Library", "Application Support", applicationName);
        } else {
            // Linux/Unix: ~/.local/share/ApplicationName
            String xdgDataHome = System.getenv("XDG_DATA_HOME");
            if (xdgDataHome != null) {
                return Paths.get(xdgDataHome, applicationName);
            }
            return Paths.get(userHome, ".local", "share", applicationName);
        }
    }
}
