package ch.ubique.uetlib.core.spring.converter;

import com.github.zafarkhaja.semver.Version;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToVersionConverter implements Converter<String, Version> {

    @Override
    public Version convert(final String source) {
        return Version.parse(source, false);
    }
}
