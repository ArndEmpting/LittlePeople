package com.littlepeople.core.util;

import com.littlepeople.core.exceptions.ConfigurationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ConfigurationUtils.
 */
@DisplayName("ConfigurationUtils")
class ConfigurationUtilsTest {

    @TempDir
    Path tempDir;



    @Nested
    @DisplayName("loadProperties Tests")
    class LoadPropertiesTests {

        @Test
        @DisplayName("Should load properties from file system")
        void shouldLoadPropertiesFromFileSystem() throws IOException, ConfigurationException {
            // Create a temporary properties file
            Path propsFile = tempDir.resolve("test.properties");
            Files.write(propsFile, "key1=value1\nkey2=value2".getBytes());

            Properties props = ConfigurationUtils.loadProperties(propsFile.toString());

            assertThat(props).isNotNull();
            assertThat(props.getProperty("key1")).isEqualTo("value1");
            assertThat(props.getProperty("key2")).isEqualTo("value2");
        }

        @Test
        @DisplayName("Should throw ConfigurationException when file not found")
        void shouldThrowWhenFileNotFound() {
            assertThatThrownBy(() -> ConfigurationUtils.loadProperties("nonexistent.properties"))
                .isInstanceOf(ConfigurationException.class)
                .hasMessage("Configuration file not found: nonexistent.properties");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when path is null")
        void shouldThrowWhenPathIsNull() {
            assertThatThrownBy(() -> ConfigurationUtils.loadProperties(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("configPath cannot be null");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when path is empty")
        void shouldThrowWhenPathIsEmpty() {
            assertThatThrownBy(() -> ConfigurationUtils.loadProperties(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("configPath cannot be empty");
        }
    }

    @Nested
    @DisplayName("resolvePath Tests")
    class ResolvePathTests {

        @Test
        @DisplayName("Should resolve home directory path")
        void shouldResolveHomeDirectoryPath() {
            String userHome = System.getProperty("user.home");
            Path resolved = ConfigurationUtils.resolvePath("~/test");

            assertThat(resolved).isEqualTo(Paths.get(userHome, "test"));
        }

        @Test
        @DisplayName("Should resolve absolute path")
        void shouldResolveAbsolutePath() {
            Path resolved = ConfigurationUtils.resolvePath("/test/path");

            assertThat(resolved).isEqualTo(Paths.get("/test/path"));
        }

        @Test
        @DisplayName("Should resolve relative path")
        void shouldResolveRelativePath() {
            Path resolved = ConfigurationUtils.resolvePath("test/path");

            assertThat(resolved).isEqualTo(Paths.get("test/path"));
        }

        @Test
        @DisplayName("Should normalize path with dots")
        void shouldNormalizePathWithDots() {
            Path resolved = ConfigurationUtils.resolvePath("test/../other/./path");

            assertThat(resolved).isEqualTo(Paths.get("other/path"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when path is null")
        void shouldThrowWhenPathIsNull() {
            assertThatThrownBy(() -> ConfigurationUtils.resolvePath(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("path cannot be null");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when path is empty")
        void shouldThrowWhenPathIsEmpty() {
            assertThatThrownBy(() -> ConfigurationUtils.resolvePath(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("path cannot be empty");
        }
    }

    @Nested
    @DisplayName("getConfigDirectory Tests")
    class GetConfigDirectoryTests {

        @Test
        @DisplayName("Should return valid config directory path")
        void shouldReturnValidConfigDirectoryPath() {
            Path configDir = ConfigurationUtils.getConfigDirectory("TestApp");

            assertThat(configDir).isNotNull();
            assertThat(configDir.toString()).contains("TestApp");
        }

        @Test
        @DisplayName("Should handle Windows-style paths on Windows")
        void shouldHandleWindowsStylePaths() {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Path configDir = ConfigurationUtils.getConfigDirectory("TestApp");
                assertThat(configDir.toString()).containsAnyOf("AppData", "TestApp");
            }
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when application name is null")
        void shouldThrowWhenApplicationNameIsNull() {
            assertThatThrownBy(() -> ConfigurationUtils.getConfigDirectory(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("applicationName cannot be null");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when application name is empty")
        void shouldThrowWhenApplicationNameIsEmpty() {
            assertThatThrownBy(() -> ConfigurationUtils.getConfigDirectory(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("applicationName cannot be empty");
        }
    }

    @Nested
    @DisplayName("getDataDirectory Tests")
    class GetDataDirectoryTests {

        @Test
        @DisplayName("Should return valid data directory path")
        void shouldReturnValidDataDirectoryPath() {
            Path dataDir = ConfigurationUtils.getDataDirectory("TestApp");

            assertThat(dataDir).isNotNull();
            assertThat(dataDir.toString()).contains("TestApp");
        }

        @Test
        @DisplayName("Should handle Windows-style paths on Windows")
        void shouldHandleWindowsStylePathsForData() {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Path dataDir = ConfigurationUtils.getDataDirectory("TestApp");
                assertThat(dataDir.toString()).containsAnyOf("AppData", "TestApp");
            }
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when application name is null")
        void shouldThrowWhenApplicationNameIsNullForData() {
            assertThatThrownBy(() -> ConfigurationUtils.getDataDirectory(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("applicationName cannot be null");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when application name is empty")
        void shouldThrowWhenApplicationNameIsEmptyForData() {
            assertThatThrownBy(() -> ConfigurationUtils.getDataDirectory(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("applicationName cannot be empty");
        }
    }
}
