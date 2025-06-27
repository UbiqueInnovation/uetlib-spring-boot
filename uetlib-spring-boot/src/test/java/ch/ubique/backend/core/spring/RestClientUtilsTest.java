package ch.ubique.backend.core.spring;

import static org.junit.jupiter.api.Assertions.*;

import ch.ubique.backend.core.spring.http.RestClientUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RestClientUtilsTest {

    // Test with a record that has Int and List fields
    @Test
    void testGetQueryParamsFromRecordWithIntAndList() {
        // Define a record with fields, including an Int and a List
        record SearchParams(
                String appKey,
                String appVersion,
                String osVersion,
                int retries,
                List<String> tags) {}

        // Instantiate the record
        final var searchParams =
                new SearchParams(
                        "appKeyValue", "1.0.0", "macOS", 3, List.of("tag1", "tag2", "tag3"));

        // Get query parameters
        final var queryParams = RestClientUtils.getQueryParamsFromRecord(searchParams);

        // Assertions to check that the query parameters match the record fields
        assertEquals(5, queryParams.size()); // 3 simple fields + 1 int + 1 list
        assertTrue(queryParams.containsKey("appKey"));
        assertTrue(queryParams.containsKey("appVersion"));
        assertTrue(queryParams.containsKey("osVersion"));
        assertTrue(queryParams.containsKey("retries"));
        assertTrue(queryParams.containsKey("tags"));

        assertEquals("appKeyValue", queryParams.getFirst("appKey"));
        assertEquals("1.0.0", queryParams.getFirst("appVersion"));
        assertEquals("macOS", queryParams.getFirst("osVersion"));
        assertEquals("3", queryParams.getFirst("retries"));
        assertTrue(queryParams.get("tags").containsAll(List.of("tag1", "tag2", "tag3")));
    }


    // Test with null values and List in POJO
    @Test
    void testGetQueryParamsWithNullValuesAndList() {
        // Define a record with a null value and List
        record SearchParams(
                String appKey,
                String appVersion,
                String osVersion,
                int retries,
                List<String> tags) {}

        // Instantiate the record with null and non-null values
        final var searchParams =
                new SearchParams("appKeyValue", null, "macOS", 3, List.of());

        // Get query parameters
        final var queryParams = RestClientUtils.getQueryParamsFromRecord(searchParams);

        // Assertions to check that the query parameters are correctly formed, skipping nulls
        assertEquals(3, queryParams.size()); // appVersion is null, so it shouldn't be included
        assertTrue(queryParams.containsKey("appKey"));
        assertTrue(queryParams.containsKey("osVersion"));
        assertTrue(queryParams.containsKey("retries"));

        assertFalse(queryParams.containsKey("tags")); // tags are empty
        assertFalse(queryParams.containsKey("appVersion")); // appVersion is null

        assertEquals("appKeyValue", queryParams.getFirst("appKey"));
        assertEquals("macOS", queryParams.getFirst("osVersion"));
        assertEquals("3", queryParams.getFirst("retries"));
    }
}
