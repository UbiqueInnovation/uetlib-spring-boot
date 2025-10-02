package ch.ubique.backend.core.spring.converter;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.github.zafarkhaja.semver.Version;

import jakarta.validation.constraints.NotNull;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToVersionConverter implements Converter<String, Version> {

    @Override
    public Version convert(final @NotNull String source) {
        return isBlank(source) ? null : Version.parse(source, false);
    }
}
