package ch.ubique.backend.core.service;

import java.io.IOException;
import java.util.function.Function;

public interface InputDataLoaderService<T> {

    DataAndInput<T> loadData() throws IOException;

    default <R> InputDataLoaderService<R> map(final Function<T, R> mapper) {
        return new InputDataLoaderServiceMapper<>(this, mapper);
    }
}
