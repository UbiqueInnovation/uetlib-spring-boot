package ch.ubique.backend.spring.boot.env;

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * We want to have the values from the {@link BuildProperties} in the environment, especially in the
 * application.properties. Usually, this is not possible, as the {@link BuildProperties} is created
 * after the {@code application.properties} file is read. This {@link EnvironmentPostProcessor}
 * loads the {@link BuildProperties} with code copied from {@link ProjectInfoAutoConfiguration} into
 * the environment.
 *
 * <p>This class is overridden in tests by a noop class, as the build-info does not exist during
 * tests.
 */
public class BuildPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(
            final ConfigurableEnvironment environment, final SpringApplication application) {
        final var buildProperties = loadBuildProperties();
        if (buildProperties == null) {
            return;
        }

        final var systemProperties = environment.getSystemProperties();
        systemProperties.put(
                "build-properties.version", requireNonNull(buildProperties.getVersion()));
        systemProperties.put(
                "build-properties.artifact", requireNonNull(buildProperties.getArtifact()));
        systemProperties.put("build-properties.group", requireNonNull(buildProperties.getGroup()));
        systemProperties.put("build-properties.name", requireNonNull(buildProperties.getName()));
        systemProperties.put("build-properties.time", requireNonNull(buildProperties.getTime()));
    }

    @Nullable
    protected BuildProperties loadBuildProperties() {
        final var buildInfoPropertiesResource =
                new ClassPathResource("META-INF/build-info.properties");
        if (!buildInfoPropertiesResource.exists()) {
            return null;
        }

        final Properties source;
        try {
            source =
                    PropertiesLoaderUtils.loadProperties(
                            new EncodedResource(
                                    buildInfoPropertiesResource, StandardCharsets.UTF_8));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }

        final var prefix = "build.";
        final var target = new Properties();
        for (final var key : source.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                target.put(key.substring(prefix.length()), source.get(key));
            }
        }
        return new BuildProperties(target);
    }
}
