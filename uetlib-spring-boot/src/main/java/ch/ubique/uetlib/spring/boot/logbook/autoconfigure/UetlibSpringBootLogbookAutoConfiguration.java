package ch.ubique.uetlib.spring.boot.logbook.autoconfigure;

import ch.ubique.uetlib.spring.boot.logbook.UetlibSpringBootLogbookProperties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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

        final var defaultValue = BodyFilters.defaultValue();

        final var formUrlEncodedFilter =
                BodyFilters.replaceFormUrlEncodedProperty(
                        formUrlEncodedBodyFields, logbookObfuscateReplacement);

        return Stream.of(formUrlEncodedFilter).reduce(defaultValue, BodyFilter::merge);
    }

    @Bean
    @ConditionalOnMissingBean(ResponseFilter.class)
    ResponseFilter logbookResponseFilter(
            final UetlibSpringBootLogbookProperties uetlibSpringBootLogbookProperties) {
        final var responseFilter = uetlibSpringBootLogbookProperties.getResponseFilter();
        final var bodyReplacers = responseFilter.getBodyReplacers();
        final var contentTypes = bodyReplacers.getContentTypes();

        final var defaultValue = ResponseFilters.replaceBody(BodyReplacers.defaultValue());

        // Conditions::contentType has an awkward signature, so we need to use these three cases
        final ResponseFilter contentTypeFilter;
        if (contentTypes.isEmpty()) {
            contentTypeFilter = response -> response;
        } else if (contentTypes.size() == 1) {
            contentTypeFilter =
                    ResponseFilters.replaceBody(
                            BodyReplacers.replaceBody(
                                    Conditions.contentType(contentTypes.getFirst()), "<binary>"));
        } else {
            contentTypeFilter =
                    ResponseFilters.replaceBody(
                            BodyReplacers.replaceBody(
                                    Conditions.contentType(
                                            contentTypes.getFirst(),
                                            contentTypes.stream().skip(1).toArray(String[]::new)),
                                    "<binary>"));
        }

        return ResponseFilter.merge(defaultValue, contentTypeFilter);
    }
}
