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
        deletedResponse = DeletedResponse.from();
        assertEquals(Collections.emptyList(), deletedResponse.getIds());

        // test no entries with list interface
        deletedResponse = DeletedResponse.from(Collections.emptyList());
        assertEquals(Collections.emptyList(), deletedResponse.getIds());

        // test single int
        int id = 1;
        deletedResponse = DeletedResponse.from(1);
        assertEquals(id, deletedResponse.getIds().get(0).intValue());

        // test single Integer
        Integer idBoxed = 1;
        deletedResponse = DeletedResponse.from(1);
        assertEquals(idBoxed, deletedResponse.getIds().get(0));

        // test array of ints with varargs
        int[] ids = {2, 3};
        deletedResponse = DeletedResponse.from(ids[0], ids[1]);
        assertIterableEquals(
                Arrays.stream(ids).boxed().collect(Collectors.toList()), deletedResponse.getIds());

        // test list of Integers with varargs
        List<Integer> idsBoxed = Arrays.asList(2, 3);
        deletedResponse = DeletedResponse.from(idsBoxed.get(0), idsBoxed.get(1));
        assertIterableEquals(idsBoxed, deletedResponse.getIds());

        // test list of Integers with List interface
        deletedResponse = DeletedResponse.from(idsBoxed);
        assertIterableEquals(idsBoxed, deletedResponse.getIds());
    }
}
