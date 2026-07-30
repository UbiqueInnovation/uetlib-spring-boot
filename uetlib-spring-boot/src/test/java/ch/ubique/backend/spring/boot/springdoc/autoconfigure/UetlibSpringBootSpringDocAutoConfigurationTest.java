package ch.ubique.backend.spring.boot.springdoc.autoconfigure;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import ch.ubique.backend.spring.boot.springdoc.LocalizedValueModelConverter;

import org.junit.jupiter.api.Test;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class UetlibSpringBootSpringDocAutoConfigurationTest {

    private final ObjectMapperProvider objectMapperProvider = mock(ObjectMapperProvider.class);

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(
                            AutoConfigurations.of(UetlibSpringBootSpringDocAutoConfiguration.class))
                    .withBean(ObjectMapperProvider.class, () -> objectMapperProvider);

    @Test
    void registersLocalizedValueModelConverter() {
        contextRunner.run(
                context -> assertThat(context).hasSingleBean(LocalizedValueModelConverter.class));
    }

    @Test
    void backsOffWhenCustomConverterIsProvided() {
        final var customConverter = new LocalizedValueModelConverter(objectMapperProvider);

        contextRunner
                .withBean(LocalizedValueModelConverter.class, () -> customConverter)
                .run(
                        context -> {
                            assertThat(context).hasSingleBean(LocalizedValueModelConverter.class);

                            assertThat(context.getBean(LocalizedValueModelConverter.class))
                                    .isSameAs(customConverter);
                        });
    }

    @Test
    void doesNotFailWhenObjectMapperProviderIsMissing() {
        final var contextRunnerWithoutWebContext =
                new ApplicationContextRunner()
                        .withConfiguration(
                                AutoConfigurations.of(
                                        UetlibSpringBootSpringDocAutoConfiguration.class));

        contextRunnerWithoutWebContext.run(
                context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(LocalizedValueModelConverter.class);
                });
    }
}
