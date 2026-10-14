package ch.ubique.backend.spring.boot.problem.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import ch.ubique.backend.spring.boot.problem.handler.TestUetlibExceptionHandler;
import ch.ubique.backend.spring.boot.problem.handler.UetlibSpringBootProblemProperties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class UetlibSpringBootProblemAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(
                            AutoConfigurations.of(UetlibSpringBootProblemAutoConfiguration.class))
                    .withUserConfiguration(AdviceConfiguration.class);

    @Test
    void registersPropertiesForAdviceCreatedByBeanMethod() {
        contextRunner.run(
                context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(UetlibSpringBootProblemProperties.class);
                    final var properties = context.getBean(UetlibSpringBootProblemProperties.class);
                    assertThat(properties.isIncludeConstraintViolations()).isFalse();
                    assertThat(
                                    context.getBean(TestUetlibExceptionHandler.class)
                                            .getUetlibSpringBootProblemProperties())
                            .isSameAs(properties);
                });
    }

    @Test
    void bindsPropertiesForAdviceCreatedByBeanMethod() {
        contextRunner
                .withPropertyValues(
                        "ch.ubique.backend.spring.boot.problem.include-constraint-violations=true")
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            assertThat(
                                            context.getBean(TestUetlibExceptionHandler.class)
                                                    .getUetlibSpringBootProblemProperties()
                                                    .isIncludeConstraintViolations())
                                    .isTrue();
                        });
    }

    @Configuration(proxyBeanMethods = false)
    static class AdviceConfiguration {

        @Bean
        TestUetlibExceptionHandler exceptionHandler() {
            return new TestUetlibExceptionHandler();
        }
    }
}
