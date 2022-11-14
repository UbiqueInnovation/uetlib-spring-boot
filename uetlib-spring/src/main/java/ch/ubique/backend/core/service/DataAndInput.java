package ch.ubique.backend.core.service;

import ch.ubique.backend.core.file.ByteFile;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public record DataAndInput<T>(
        T data,
        Collection<ByteFile> inputFiles
) {
    public DataAndInput(T data) {
        this(data, List.of());
    }

    public DataAndInput(T data, ByteFile inputFile) {
        this(data, List.of(inputFile));
    }

    public <R> DataAndInput<R> map(Function<T, R> mapper) {
        return new DataAndInput<>(mapper.apply(data), inputFiles);
    }
}
