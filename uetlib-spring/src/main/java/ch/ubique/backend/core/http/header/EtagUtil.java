package ch.ubique.backend.core.http.header;

import org.apache.commons.codec.binary.Hex;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

public class EtagUtil {

    private static final String WEAK_PREFIX = "W/";
    private static final String SHA_1 = "SHA-1";

    private EtagUtil() {}

    /** generates a weak etag for a list that does not depend on element order */
    public static String getUnsortedListEtag(final boolean asWeakEtag, final List<?> list) {
        final var hash =
                list != null ? list.stream().map(Objects::hash).reduce(0, (a, b) -> a ^ b) : 0;
        final var hashString = String.valueOf(hash);
        return asWeakEtag ? toWeakEtag(hashString) : hashString;
    }

    public static String getSha1HashForFiles(final boolean asWeakEtag, final String... pathToFiles)
            throws IOException, NoSuchAlgorithmException {
        final var sha1 = MessageDigest.getInstance(SHA_1);
        for (final var pathToFile : pathToFiles) {
            final var classpathPrefix = "classpath:";
            try (final var is =
                    pathToFile.startsWith(classpathPrefix)
                            ? new ClassPathResource(pathToFile.replace(classpathPrefix, ""))
                                    .getInputStream()
                            : new FileInputStream(pathToFile)) {

                final var buffer = new byte[8192];
                var len = is.read(buffer);
                while (len != -1) {
                    sha1.update(buffer, 0, len);
                    len = is.read(buffer);
                }
            }
        }
        final var hash = Hex.encodeHexString(sha1.digest());
        return asWeakEtag ? toWeakEtag(hash) : hash;
    }

    public static String getSha1HashForStrings(final boolean asWeakEtag, final String... strings)
            throws NoSuchAlgorithmException {
        final var sha1 = MessageDigest.getInstance(SHA_1);
        for (final var string : strings) {
            final var bytes = string.getBytes(StandardCharsets.UTF_8);
            sha1.update(bytes);
        }
        final var hash = Hex.encodeHexString(sha1.digest());
        return asWeakEtag ? toWeakEtag(hash) : hash;
    }

    public static String toWeakEtag(final String hash) {
        return WEAK_PREFIX + "\"" + hash + "\"";
    }
}
