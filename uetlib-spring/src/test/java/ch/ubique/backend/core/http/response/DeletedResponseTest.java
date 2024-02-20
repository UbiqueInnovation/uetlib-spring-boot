package ch.ubique.backend.core.http.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class DeletedResponseTest {

    @Test
    void test() {
        DeletedResponse deletedResponse;

        // test no entries with varargs interface
        deletedResponse = new DeletedResponse();
        assertEquals(Collections.emptyList(), deletedResponse.ids());

        // test no entries with list interface
        deletedResponse = new DeletedResponse(Collections.emptyList());
        assertEquals(Collections.emptyList(), deletedResponse.ids());

        // test single int
        final var id = 1;
        deletedResponse = new DeletedResponse(1);
        assertEquals(id, deletedResponse.ids().getFirst().intValue());

        // test single Integer
        final Integer idBoxed = 1;
        deletedResponse = new DeletedResponse(1);
        assertEquals(idBoxed, deletedResponse.ids().getFirst());

        // test array of ints with varargs
        final var idsArray = new Integer[] {2, 3};
        deletedResponse = new DeletedResponse(idsArray);
        assertIterableEquals(Arrays.stream(idsArray).toList(), deletedResponse.ids());

        // test list of Integers with List interface
        final var idsList = List.of(2, 3);
        deletedResponse = new DeletedResponse(idsList);
        assertIterableEquals(idsList, deletedResponse.ids());
    }
}
