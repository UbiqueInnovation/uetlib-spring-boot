package ch.ubique.backend.core.utils.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
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
    public <T, E extends Throwable> void onError(
            final RetryContext retryContext,
            final RetryCallback<T, E> callback,
            final Throwable throwable) {
        logger.debug(
                "Got {} while trying {} on retry #{}: {}",
                throwable.getClass().getCanonicalName(),
                logger.isDebugEnabled()
                        ? getLatestUbiqueStackTraceElement(throwable.getStackTrace())
                        : "",
                retryContext.getRetryCount() - 1,
                throwable.getMessage());
    }

    public String getLatestUbiqueStackTraceElement(final StackTraceElement[] stackTraceElements) {
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
    public <T, E extends Throwable> void close(
            final RetryContext retryContext,
            final RetryCallback<T, E> callback,
            final Throwable throwable) {
        if (throwable != null) {
            logger.info("Could not complete task.", throwable);
        }
    }

    @Override
    public <T, E extends Throwable> void onSuccess(
            final RetryContext retryContext, final RetryCallback<T, E> callback, final T result) {
        if (retryContext.getRetryCount() > 0) {
            logger.debug("Successful after {} retries.", retryContext.getRetryCount());
        }
    }
}
