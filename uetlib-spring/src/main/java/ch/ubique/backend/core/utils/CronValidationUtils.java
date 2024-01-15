package ch.ubique.backend.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CronValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(CronValidationUtils.class);

    private CronValidationUtils() {}

    /**
     * Tries to get a valid cron expression by removing additional tokens from the end.
     *
     * @param cronExpression the cron expression to validate
     * @return the valid cron expression
     * @throws IllegalArgumentException if the specified cron expression is invalid and could not be
     *     fixed.
     */
    public static String tryToGetValidCronExpression(final String cronExpression) {
        if (CronExpression.isValidExpression(cronExpression)) {
            return cronExpression;
        }

        // Strip additional tokes from the end
        final var limitToSixTokens =
                Arrays.stream(StringUtils.tokenizeToStringArray(cronExpression, " "))
                        .limit(6)
                        .collect(Collectors.joining(" "));
        if (CronExpression.isValidExpression(limitToSixTokens)) {
            logger.warn(
                    "Cron expression {} is invalid. Fixed it by stripping additional tokens from the"
                        + " end. Valid expression is now {}.",
                    cronExpression,
                    limitToSixTokens);
            return limitToSixTokens;
        }

        throw new IllegalArgumentException(
                "The specified cron expression is invalid: " + cronExpression);
    }
}
