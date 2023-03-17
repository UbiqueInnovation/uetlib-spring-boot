package ch.ubique.backend.core.http.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        final int id = 1;
        deletedResponse = new DeletedResponse(1);
        assertEquals(id, deletedResponse.ids().get(0).intValue());

        // test single Integer
        final Integer idBoxed = 1;
        deletedResponse = new DeletedResponse(1);
        assertEquals(idBoxed, deletedResponse.ids().get(0));

        // test array of ints with varargs
        final int[] ids = {2, 3};
        deletedResponse = new DeletedResponse(ids[0], ids[1]);
        assertIterableEquals(
                Arrays.stream(ids).boxed().collect(Collectors.toList()), deletedResponse.ids());

        // test list of Integers with varargs
        final List<Integer> idsBoxed = Arrays.asList(2, 3);
        deletedResponse = new DeletedResponse(idsBoxed.get(0), idsBoxed.get(1));
        assertIterableEquals(idsBoxed, deletedResponse.ids());

        // test list of Integers with List interface
        deletedResponse = new DeletedResponse(idsBoxed);
        assertIterableEquals(idsBoxed, deletedResponse.ids());
    }
}
