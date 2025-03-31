package ch.ubique.backend.core.utils.retry;

import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;
import java.util.Collection;

public class RetryUtils {

    private RetryUtils() {}

    public static RetryTemplate createDefaultRetryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(5)
                .exponentialBackoff(Duration.ofMillis(500), 2, Duration.ofSeconds(30))
                .withListener(new LoggingRetryListener())
                .build();
    }

    public static RetryTemplate createDefaultRetryTemplate(final Collection<String> basePackages) {
        return RetryTemplate.builder()
                .maxAttempts(5)
                .exponentialBackoff(Duration.ofMillis(500), 2, Duration.ofSeconds(30))
                .withListener(new LoggingRetryListener(basePackages))
                .build();
    }
}
