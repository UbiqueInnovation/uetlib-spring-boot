package ch.ubique.backend.core.utils.retry;

import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.util.backoff.ExponentialBackOff;

import java.time.Duration;
import java.util.Collection;

public final class RetryUtils {

    private static final Duration HALF_A_SECOND = Duration.ofMillis(500);
    private static final Duration HALF_A_MINUTE = Duration.ofSeconds(30);

    private RetryUtils() {}

    public static RetryTemplate createDefaultRetryTemplate() {
        final var retryTemplate =
                new RetryTemplate(
                        RetryPolicy.builder()
                                .maxRetries(4)
                                .backOff(new ExponentialBackOff(HALF_A_SECOND.toMillis(), 2))
                                .maxDelay(HALF_A_MINUTE)
                                .build());
        retryTemplate.setRetryListener(new LoggingRetryListener());
        return retryTemplate;
    }

    public static RetryTemplate createDefaultRetryTemplate(final Collection<String> basePackages) {
        final var retryTemplate = createDefaultRetryTemplate();
        retryTemplate.setRetryListener(new LoggingRetryListener(basePackages));
        return retryTemplate;
    }
}
