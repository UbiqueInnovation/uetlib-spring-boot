package ch.ubique.backend.core.service;

import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.function.Function;

public interface InputCollectionLoaderService<T> extends InputDataLoaderService<Collection<T>> {

    @NotNull
    default <R> InputCollectionLoaderService<R> mapCollection(
            @NotNull final Function<T, R> mapper) {
        return new InputCollectionLoaderServiceMapper<>(this, mapper);
    }
}
