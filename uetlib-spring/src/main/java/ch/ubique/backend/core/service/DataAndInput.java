package ch.ubique.backend.core.service;

import ch.ubique.backend.core.file.ByteFile;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public record DataAndInput<T>(@NotNull T data, @NotNull Collection<ByteFile> inputFiles) {
    public DataAndInput(@NotNull final T data) {
        this(data, List.of());
    }

    public DataAndInput(@NotNull final T data, @NotNull final ByteFile inputFile) {
        this(data, List.of(inputFile));
    }

    @NotNull
    public <R> DataAndInput<R> map(@NotNull final Function<T, R> mapper) {
        return new DataAndInput<>(mapper.apply(data), inputFiles);
    }
}
