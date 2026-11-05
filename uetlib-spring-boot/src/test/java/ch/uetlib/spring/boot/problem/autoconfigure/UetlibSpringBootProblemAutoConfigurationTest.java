package ch.uetlib.spring.boot.problem.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import ch.uetlib.spring.boot.problem.handler.TestUetlibExceptionHandler;
import ch.uetlib.spring.boot.problem.handler.UetlibSpringBootProblemProperties;
import ch.uetlib.spring.boot.problem.handler.UnhandledExceptionLoggingResolver;

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
                        "ch.uetlib.spring.boot.problem.include-constraint-violations=true")
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

    @Test
    void doesNotRegisterResolverWithoutAdvice() {
        new ApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(UetlibSpringBootProblemAutoConfiguration.class))
                .run(
                        context ->
                                assertThat(context)
                                        .doesNotHaveBean(UnhandledExceptionLoggingResolver.class));
    }

    @Test
    void registersOneResolverWhenMultipleAdviceBeansExist() {
        contextRunner
                .withBean("otherExceptionHandler", TestUetlibExceptionHandler.class)
                .run(
                        context -> {
                            assertThat(context).hasNotFailed();
                            assertThat(context)
                                    .hasSingleBean(UnhandledExceptionLoggingResolver.class);
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
