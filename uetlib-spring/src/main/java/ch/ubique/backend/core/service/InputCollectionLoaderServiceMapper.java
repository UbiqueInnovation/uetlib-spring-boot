package ch.ubique.backend.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

public class InputCollectionLoaderServiceMapper<T, R> implements InputCollectionLoaderService<R> {

    private final InputCollectionLoaderService<T> service;

    private final Function<T, R> mapper;

    public InputCollectionLoaderServiceMapper(InputCollectionLoaderService<T> service, Function<T, R> mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public DataAndInput<Collection<R>> loadData() throws IOException {
        return service.loadData().map(collection -> collection.stream().map(mapper).toList());
    }
}
