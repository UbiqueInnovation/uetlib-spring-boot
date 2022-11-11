package ch.ubique.backend.core.service;

import java.io.IOException;
import java.util.function.Function;

public class InputDataLoaderServiceMapper<T, R> implements InputDataLoaderService<R> {

    private final InputDataLoaderService<T> service;

    private final Function<T, R> mapper;

    public InputDataLoaderServiceMapper(InputDataLoaderService<T> service, Function<T, R> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public DataAndInput<R> loadData() throws IOException {
        return service.loadData().map(mapper);
    }
}
