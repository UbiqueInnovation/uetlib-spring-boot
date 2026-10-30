package ch.ubique.uetlib.core.spring.converter;

import com.github.zafarkhaja.semver.Version;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class VersionToStringConverter implements Converter<Version, String> {

    @Override
    public String convert(final Version source) {
        return source.toString();
    }
}
