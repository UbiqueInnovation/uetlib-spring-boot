package ch.ubique.backend.core.service;

import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

public class InputCollectionLoaderServiceMapper<T, R> implements InputCollectionLoaderService<R> {

    @NotNull private final InputCollectionLoaderService<T> service;

    @NotNull private final Function<T, R> mapper;

    public InputCollectionLoaderServiceMapper(
            @NotNull final InputCollectionLoaderService<T> service,
            @NotNull final Function<T, R> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @NotNull
    public DataAndInput<Collection<R>> loadData() throws IOException {
        return service.loadData().map(collection -> collection.stream().map(mapper).toList());
    }
}
