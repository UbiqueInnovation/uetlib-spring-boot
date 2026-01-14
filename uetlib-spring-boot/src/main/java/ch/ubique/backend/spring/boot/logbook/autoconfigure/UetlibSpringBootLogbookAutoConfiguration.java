package ch.ubique.backend.spring.boot.logbook.autoconfigure;

import ch.ubique.backend.spring.boot.logbook.UetlibSpringBootLogbookProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.ResponseFilter;
import org.zalando.logbook.autoconfigure.LogbookProperties;
import org.zalando.logbook.core.BodyFilters;
import org.zalando.logbook.core.BodyReplacers;
import org.zalando.logbook.core.Conditions;
import org.zalando.logbook.core.ResponseFilters;

import java.util.stream.Stream;

@AutoConfiguration
@EnableConfigurationProperties(UetlibSpringBootLogbookProperties.class)
public class UetlibSpringBootLogbookAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BodyFilter.class)
    BodyFilter logbookBodyFilter(
            final UetlibSpringBootLogbookProperties uetlibSpringBootLogbookProperties,
            final LogbookProperties logbookProperties) {
        final var logbookObfuscateReplacement = logbookProperties.getObfuscate().getReplacement();
        final var obfuscate = uetlibSpringBootLogbookProperties.getObfuscate();
        final var formUrlEncodedBodyFields = obfuscate.getFormUrlEncodedBodyFields();

        // No replacements if formUrlEncodedBodyFields is not set
        if (formUrlEncodedBodyFields.isEmpty()) {
            return BodyFilters.replaceFormUrlEncodedProperty(
                    formUrlEncodedBodyFields, logbookObfuscateReplacement);
        }

        final var replaceFormUrlEncodedProperty =
                BodyFilters.replaceFormUrlEncodedProperty(
                        formUrlEncodedBodyFields, logbookObfuscateReplacement);

        return Stream.of(replaceFormUrlEncodedProperty)
                .reduce(BodyFilters.defaultValue(), BodyFilter::merge);
    }

    @Bean
    @ConditionalOnMissingBean(ResponseFilter.class)
    ResponseFilter logbookResponseFilter() {
        final var defaultValue = ResponseFilters.replaceBody(BodyReplacers.defaultValue());

        final var protobufFilter =
                ResponseFilters.replaceBody(
                        BodyReplacers.replaceBody(
                                Conditions.contentType("application/protobuf"),
                                "<binary protobuf>"));
        final var gzipFilter =
                ResponseFilters.replaceBody(
                        BodyReplacers.replaceBody(
                                Conditions.header(HttpHeaders.CONTENT_ENCODING, "gzip"),
                                "<binary gzipped>"));

        return Stream.of(protobufFilter, gzipFilter).reduce(defaultValue, ResponseFilter::merge);
    }
}
