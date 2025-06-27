package ch.ubique.backend.core.spring.http;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collection;

/**
 * A utility class for converting Java objects (including records) into query parameters. This
 * utility reflects over the fields of the object, adds them to a {@link MultiValueMap}, and handles
 * collections by adding each item in a collection as a separate query parameter.
 *
 * <p>The method inspects all declared fields of the object and checks if the field is a {@link
 * Collection}. If the field is a {@link Collection}, each item is added as a separate query
 * parameter. If the field is not a collection, it is added as a single query parameter.
 *
 * <p>Static fields are ignored during the conversion process.
 *
 * <p>Example:
 *
 * {@snippet :
 *     class SearchParams { private String appKey; private String appVersion; private
 *     List<String> tags;
 *     <p>// Constructor and getter methods... } SearchParams searchParams = new
 *     SearchParams("appKeyValue", "1.0.0", List.of("tag1", "tag2"));
 *     <p>MultiValueMap<String, String> queryParams =
 *     RestClientUtils.getQueryParamsFromClass(searchParams); // queryParams will contain: //
 *     appKey=appKeyValue // appVersion=1.0.0 // tags=tag1 // tags=tag2
 * }
 */
public final class RestClientUtils {

    // Private constructor to prevent instantiation of utility class
    private RestClientUtils() {}

    /**
     * Converts an object's fields to a {@link MultiValueMap} of query parameters. The method
     * inspects the declared fields of the object, checks if the field is a {@link Collection}, and
     * if so, it adds each item as a separate query parameter. Otherwise, the field is added as a
     * single query parameter.
     *
     * <p>Static fields are ignored, and only instance fields are included in the resulting query
     * parameters.
     *
     * @param object the object to convert into query parameters
     * @return a {@link MultiValueMap} containing the query parameters as key-value pairs
     */
    public static MultiValueMap<String, String> getQueryParamsFromRecord(final Record object) {
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // Get all declared fields of the object
        final var fields = object.getClass().getDeclaredFields();

        for (final var field : fields) {
            // Skip static fields
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true); // Make the field accessible

            final Object value; // Get the value of the field
            try {
                value = field.get(object);
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (value != null) {
                // If the field is a Collection (e.g., List, Set), iterate over its elements and add
                // each one separately
                if (value instanceof final Collection<?> collection) {
                    for (final var item : collection) {
                        queryParams.add(field.getName(), item.toString());
                    }
                } else {
                    // Otherwise, add the field as a single query param
                    queryParams.add(field.getName(), value.toString());
                }
            }
        }

        return queryParams;
    }
}
