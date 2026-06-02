package ch.ubique.backend.core.file;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public record ByteFile(
        @Nullable String fileName, String fileHash, String contentType, byte[] bytes) {

    public static ByteFile fromBytes(
            final byte[] bytes,
            final @Nullable String fileName,
            final @Nullable String contentType) {
        final var fileHash = DigestUtils.sha3_512Hex(bytes);
        final var contentTypeOrOctetStream =
                requireNonNull(
                        requireNonNullElse(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE));
        return new ByteFile(fileName, fileHash, contentTypeOrOctetStream, bytes);
    }

    public InputStream inputStream() {
        return new ByteArrayInputStream(bytes);
    }

    public static ByteFileBuilder builder() {
        return new ByteFileBuilder();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof final ByteFile byteFile)) {
            return false;
        }

        return new EqualsBuilder()
                .append(fileName, byteFile.fileName)
                .append(fileHash, byteFile.fileHash)
                .append(contentType, byteFile.contentType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fileName)
                .append(fileHash)
                .append(contentType)
                .toHashCode();
    }

    @Override
    public @NonNull String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fileName", fileName)
                .append("fileHash", fileHash)
                .append("contentType", contentType)
                .toString();
    }

    public static final class ByteFileBuilder {
        private @Nullable String fileName;
        private @Nullable String fileHash;
        private @Nullable String contentType;
        private byte @Nullable [] bytes;

        private ByteFileBuilder() {}

        public ByteFileBuilder withFileName(final @Nullable String fileName) {
            this.fileName = fileName;
            return this;
        }

        public ByteFileBuilder withContentType(final String contentType) {
            this.contentType = contentType;
            return this;
        }

        public ByteFileBuilder withBytes(final byte[] bytes) {
            this.bytes = bytes;
            this.fileHash = DigestUtils.sha3_512Hex(bytes);
            return this;
        }

        public ByteFile build() {
            requireNonNull(fileHash, "fileHash must not be null");
            requireNonNull(contentType, "contentType must not be null");
            requireNonNull(bytes, "bytes must not be null");

            return new ByteFile(fileName, fileHash, contentType, bytes);
        }
    }
}
