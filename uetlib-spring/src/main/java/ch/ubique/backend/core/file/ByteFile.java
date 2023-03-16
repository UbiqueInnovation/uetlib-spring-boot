package ch.ubique.backend.core.file;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

public class ByteFile {

    private String fileName;
    private String fileHash;
    private String contentType;
    private byte[] bytes;

    public String getFileName() {
        return fileName;
    }

    public ByteFile setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileHash() {
        return fileHash;
    }

    public String getContentType() {
        return contentType;
    }

    public ByteFile setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public ByteFile setBytes(byte[] bytes) {
        this.bytes = bytes;
        this.fileHash = DigestUtils.sha3_512Hex(bytes);
        return this;
    }

    public InputStream inputStream() {
        return new ByteArrayInputStream(bytes);
    }

    public static ByteFile fromBytes(
            @NotNull final byte[] bytes,
            @Nullable final String fileName,
            @Nullable final String contentType) {

        final var contentTypeOrOctetStream =
                Objects.requireNonNullElse(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return new ByteFile()
                .setBytes(bytes)
                .setFileName(fileName)
                .setContentType(contentTypeOrOctetStream);
    }

    @Override
    public String toString() {
        return "FileDescription{"
                + "fileName='"
                + fileName
                + '\''
                + ", fileHash='"
                + fileHash
                + '\''
                + '}';
    }
}
