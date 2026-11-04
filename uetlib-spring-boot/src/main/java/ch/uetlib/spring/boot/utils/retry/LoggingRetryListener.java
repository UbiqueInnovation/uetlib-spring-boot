package ch.uetlib.spring.boot.utils.retry;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.Retryable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
public class LoggingRetryListener implements RetryListener {

    private static final Logger logger = LoggerFactory.getLogger(LoggingRetryListener.class);

    private final List<String> basePackages;

    @Autowired
    public LoggingRetryListener() {
        this(List.of());
    }

    /** Create a logging retry listener with a custom list of base packages. */
    public LoggingRetryListener(final Collection<String> basePackages) {
        this.basePackages = Stream.concat(Stream.of("ubique"), basePackages.stream()).toList();
    }

    @Override
    public void onRetryFailure(
            final RetryPolicy retryPolicy,
            final Retryable<?> retryable,
            final Throwable throwable) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "Got {} while trying {} on retry: {}",
                    throwable.getClass().getCanonicalName(),
                    getLatestUbiqueStackTraceElement(throwable.getStackTrace()),
                    throwable.getMessage());
        }
    }

    private String getLatestUbiqueStackTraceElement(final StackTraceElement[] stackTraceElements) {
        return Arrays.stream(stackTraceElements)
                .filter(stackTraceElement -> isBasePackage(stackTraceElement.getClassName()))
                .findFirst()
                .map(
                        element ->
                                element.getClassName()
                                        + "#"
                                        + element.getMethodName()
                                        + "#"
                                        + element.getLineNumber())
                .orElse("(unknown)");
    }

    private boolean isBasePackage(final String className) {
        return basePackages.stream().anyMatch(className::contains);
    }

    @Override
    public void onRetryPolicyInterruption(
            final RetryPolicy retryPolicy,
            final Retryable<?> retryable,
            final RetryException exception) {
        logger.info(
                "Retry interrupted during attempt {} while waiting during backoff with cause.",
                exception.getRetryCount(),
                exception.getCause());
    }

    @Override
    public void onRetryPolicyExhaustion(
            final RetryPolicy retryPolicy,
            final Retryable<?> retryable,
            final RetryException exception) {
        logger.info(
                "Could not complete task. Retry policy exhausted after {} retries.",
                exception.getRetryCount(),
                exception);
    }

    @Override
    public void onRetrySuccess(
            final RetryPolicy retryPolicy,
            final Retryable<?> retryable,
            @Nullable final Object result) {
        logger.debug("Successful after retries.");
    }
}
