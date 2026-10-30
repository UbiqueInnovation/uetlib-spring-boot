package ch.ubique.uetlib.spring.boot.problem.handler;

import static ch.ubique.uetlib.spring.boot.problem.errortype.TestErrorType.ERROR_TEST1;
import static ch.ubique.uetlib.spring.boot.problem.handler.trait.ThrowableProblemAdviceTrait.PROBLEM_CODE_HEADER;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ubique.uetlib.spring.boot.problem.AbstractThrowableProblem;
import ch.ubique.uetlib.spring.boot.problem.TestException;
import ch.ubique.uetlib.spring.boot.problem.TestExceptionWithHeaders;
import ch.ubique.uetlib.spring.boot.problem.errortype.TestErrorType;
import ch.ubique.uetlib.spring.boot.problem.handler.config.UetlibExceptionHandlerTestConfig;
import com.google.common.base.CaseFormat;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = UetlibExceptionHandlerTestConfig.class)
@TestPropertySource("classpath:application-test.properties")
class UetlibExceptionHandlerTest {

    @Autowired private TestUetlibExceptionHandler testUetlibExceptionHandler;

    private WebRequest newRequest() {
        return new ServletWebRequest(new MockHttpServletRequest());
    }

    @BeforeEach
    void beforeEach() {
        LocaleContextHolder.setLocaleContext(new SimpleLocaleContext(Locale.GERMAN));
    }

    @Test
    void handleUetlibThrowableProblem_returnsProblemDetailWithExpectedFields() {
        // test for error test 1
        final var response1 =
                testUetlibExceptionHandler.handleAbstractThrowableProblem(
                        new TestException(ERROR_TEST1), newRequest());

        assertThat(response1.getStatusCode().value()).isEqualTo(400);

        final var pd1 = response1.getBody();
        assertThat(pd1).isNotNull();
        assertThat(pd1.getType())
                .hasToString(
                        TestErrorType.PROBLEM_TYPE.type()
                                + ":"
                                + CaseFormat.UPPER_UNDERSCORE.to(
                                        CaseFormat.LOWER_HYPHEN, ERROR_TEST1.name()));
        assertThat(pd1.getTitle()).isEqualTo("Test Problem");
        assertThat(pd1.getStatus()).isEqualTo(400);
        // Default detail equals reason phrase of BAD_REQUEST in our TestProblem constructor
        assertThat(pd1.getDetail()).isEqualTo("Bad Request");
        assertThat(pd1.getInstance()).isNull();
        assertThat(pd1.getProperties())
                .isNotNull()
                .containsEntry("problemCode", "UT|ET1")
                .doesNotContainKeys("localizedTitle", "localizedDetail");

        // test for error test 2
        final var response2 =
                testUetlibExceptionHandler.handleAbstractThrowableProblem(
                        new TestException(
                                TestErrorType.ERROR_TEST2_WITH_TRANSLATIONS,
                                "Error Test 2 exception"),
                        newRequest());

        assertThat(response2.getStatusCode().value()).isEqualTo(400);

        final var pd2 = response2.getBody();
        assertThat(pd2).isNotNull();
        assertThat(pd2.getDetail()).isEqualTo("Error Test 2 exception");
        assertThat(pd2.getProperties())
                .isNotNull()
                .containsEntry("localizedTitle", "DE: Error Test2 Titel")
                .containsEntry("localizedDetail", "EN: Error Test2 Detail");

        // test for error test 3
        final var response3 =
                testUetlibExceptionHandler.handleAbstractThrowableProblem(
                        new TestException(TestErrorType.ERROR_TEST3_WITH_TITLE_TRANSLATION_ONLY),
                        newRequest());

        assertThat(response3.getStatusCode().value()).isEqualTo(400);

        final var pd3 = response3.getBody();
        assertThat(pd3).isNotNull();
        assertThat(pd3.getProperties())
                .isNotNull()
                .containsEntry("localizedTitle", "DE: Error Test3 Titel")
                .doesNotContainKey("localizedDetail");
    }

    @Test
    void handleNoSuchElementException_mapsTo404ProblemDetail() throws Exception {
        final var ex = new NoSuchElementException("Element 17 not found");
        final var response =
                requireNonNull(testUetlibExceptionHandler.handleMoreExceptions(ex, newRequest()));

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody())
                .isInstanceOf(ProblemDetail.class)
                .satisfies(
                        body -> {
                            final var pd = requireNonNull((ProblemDetail) body);
                            assertThat(pd.getStatus()).isEqualTo(404);
                            assertThat(pd.getDetail()).isEqualTo("Element 17 not found");
                        });
    }

    @Test
    void handleOtherException_isRethrown() {
        final var ex = new IllegalArgumentException("boom");
        final var request = newRequest();
        assertThrows(
                IllegalArgumentException.class,
                () -> testUetlibExceptionHandler.handleException(ex, request));
    }

    // ======== ProblemResponseHeadersProvider ========

    @Test
    void
            handleAbstractThrowableProblem_whenExceptionImplementsProblemResponseHeadersProvider_headersAreIncluded() {
        final var response =
                testUetlibExceptionHandler.handleAbstractThrowableProblem(
                        new TestExceptionWithHeaders(), newRequest());

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getHeaders().get(PROBLEM_CODE_HEADER)).containsExactly("UT|ET1");
        assertThat(response.getHeaders().get("x-provider-extra-header"))
                .containsExactly("provider-extra-value");
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(
                        pd ->
                                assertThat(requireNonNull(pd).getProperties())
                                        .containsEntry("problemCode", "UT|ET1"));
    }

    @Test
    void
            handleAbstractThrowableProblem_whenExceptionDoesNotImplementProvider_noProviderHeadersAdded() {
        final var response =
                testUetlibExceptionHandler.handleAbstractThrowableProblem(
                        new TestException(ERROR_TEST1), newRequest());

        assertThat(response.getHeaders().get(PROBLEM_CODE_HEADER)).containsExactly("UT|ET1");
        assertThat(response.getHeaders().get("x-provider-extra-header")).isNull();
    }

    // ======== customizeAbstractThrowableProblemHeaders extension hook ========

    @Test
    void handleAbstractThrowableProblem_whenCustomizeHookOverridden_customHeaderIsAdded() {
        // Anonymous subclass overrides the hook — no Spring context needed for this code path
        final var handler =
                new TestUetlibExceptionHandler() {
                    @Override
                    public void customizeAbstractThrowableProblemHeaders(
                            final AbstractThrowableProblem problem,
                            final WebRequest request,
                            final HttpHeaders headers) {
                        headers.add("x-custom-hook", "hook-was-called");
                    }
                };

        final var response =
                handler.handleAbstractThrowableProblem(
                        new TestException(ERROR_TEST1), newRequest());

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getHeaders().get("x-custom-hook")).containsExactly("hook-was-called");
    }

    @Test
    void
            handleAbstractThrowableProblem_whenBothProviderAndHookPresent_headersFromBothAreIncluded() {
        final var handler =
                new TestUetlibExceptionHandler() {
                    @Override
                    public void customizeAbstractThrowableProblemHeaders(
                            final AbstractThrowableProblem problem,
                            final WebRequest request,
                            final HttpHeaders headers) {
                        headers.add("x-custom-hook", "hook-was-called");
                    }
                };

        final var response =
                handler.handleAbstractThrowableProblem(
                        new TestExceptionWithHeaders(), newRequest());

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getHeaders().get(PROBLEM_CODE_HEADER)).containsExactly("UT|ET1");
        assertThat(response.getHeaders().get("x-provider-extra-header"))
                .containsExactly("provider-extra-value");
        assertThat(response.getHeaders().get("x-custom-hook")).containsExactly("hook-was-called");
    }

    @Test
    void handleAbstractThrowableProblem_defaultHook_addsNoExtraHeaders() {
        final var response =
                testUetlibExceptionHandler.handleAbstractThrowableProblem(
                        new TestException(ERROR_TEST1), newRequest());

        // Default hook is a no-op — only framework-managed headers are present
        assertThat(response.getHeaders().get("x-custom-hook")).isNull();
    }
}
