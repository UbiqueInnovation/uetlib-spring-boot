package ch.ubique.backend.core.spring.converter;

import com.github.zafarkhaja.semver.Version;

import jakarta.validation.constraints.NotNull;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class VersionToStringConverter implements Converter<Version, String> {

    @Override
    public String convert(final @NotNull Version source) {
        return source == null ? null : source.toString();
    }
}
