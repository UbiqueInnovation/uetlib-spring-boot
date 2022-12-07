package ch.ubique.backend.core.service;

import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.function.Function;

public interface InputDataLoaderService<T> {

    @NotNull
    DataAndInput<T> loadData() throws IOException;

    @NotNull
    default <R> InputDataLoaderService<R> map(@NotNull final Function<T, R> mapper) {
        return new InputDataLoaderServiceMapper<>(this, mapper);
    }
}
