package ch.ubique.backend.core.service;

import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.function.Function;

public class InputDataLoaderServiceMapper<T, R> implements InputDataLoaderService<R> {

    @NotNull private final InputDataLoaderService<T> service;

    @NotNull private final Function<T, R> mapper;

    public InputDataLoaderServiceMapper(
            @NotNull final InputDataLoaderService<T> service,
            @NotNull final Function<T, R> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @NotNull
    public DataAndInput<R> loadData() throws IOException {
        return service.loadData().map(mapper);
    }
}
