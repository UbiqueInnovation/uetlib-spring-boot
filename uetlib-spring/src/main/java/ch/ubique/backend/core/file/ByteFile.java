package ch.ubique.backend.core.file;

import org.apache.commons.codec.digest.DigestUtils;

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
