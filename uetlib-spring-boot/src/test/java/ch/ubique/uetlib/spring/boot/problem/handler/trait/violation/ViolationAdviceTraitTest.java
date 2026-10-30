package ch.ubique.uetlib.spring.boot.problem.handler.trait.violation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import ch.ubique.uetlib.spring.boot.problem.handler.TestUetlibExceptionHandler;
import ch.ubique.uetlib.spring.boot.problem.handler.config.UetlibExceptionHandlerTestConfig;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.apache.commons.lang3.compare.ComparableUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@SuppressWarnings({"DataFlowIssue", "unchecked", "unused", "java:S1172"})
@ExtendWith(OutputCaptureExtension.class)
@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = UetlibExceptionHandlerTestConfig.class)
@TestPropertySource("classpath:application-test.properties")
class ViolationAdviceTraitTest {

    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired private TestUetlibExceptionHandler handler;

    private WebRequest newRequest() {
        return new ServletWebRequest(new MockHttpServletRequest());
    }

    // ── ConstraintViolationAdviceTrait ────────────────────────────────────────

    @Test
    void handleConstraintViolation_singleViolation_returnsBadRequestWithViolation()
            throws Exception {
        final var violations = VALIDATOR.validate(new TestBean("", 5));
        final var ex = new ConstraintViolationException(violations);

        final var response = handler.handleViolationExceptions(ex, newRequest());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        final var body = assertInstanceOf(ProblemDetail.class, response.getBody());
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getTitle()).isEqualTo("Constraint Violation");
        assertThat(body.getType()).hasToString("urn:ch:uetlib:problem:constraint-violation");

        final var returnedViolations = (List<Violation>) body.getProperties().get("violations");
        assertThat(returnedViolations).hasSize(1);

        final var v = returnedViolations.getFirst();
        assertThat(v.fieldName()).isEqualTo("name");
        assertThat(v.message()).isNotNull();
        assertThat(v.rejectedValue()).isEqualTo(""); // rejected value (empty string) is included
    }

    @Test
    void handleConstraintViolation_multipleViolations_returnsSortedAndMultipleDetail()
            throws Exception {
        final var violations = VALIDATOR.validate(new TestBean("", -1));
        // Expect violations for both 'age' and 'name'
        assertThat(violations).hasSizeGreaterThanOrEqualTo(2);
        final var ex = new ConstraintViolationException(violations);

        final var response = handler.handleViolationExceptions(ex, newRequest());

        assertThat(response).isNotNull();
        final var body = assertInstanceOf(ProblemDetail.class, response.getBody());

        final var returnedViolations = (List<Violation>) body.getProperties().get("violations");
        assertThat(returnedViolations).hasSize(violations.size());

        // Violations must be sorted by field name
        for (var i = 0; i < returnedViolations.size() - 1; i++) {
            assertThat(
                            ComparableUtils.is(returnedViolations.get(i))
                                    .lessThanOrEqualTo(returnedViolations.get(i + 1)))
                    .as("violations[%d] should sort before violations[%d]", i, i + 1)
                    .isTrue();
        }

        assertThat(body.getDetail()).isEqualTo("Multiple constraint violations");
    }

    @Test
    void handleConstraintViolation_exactlyOneViolation_detailIsNotMultiple() throws Exception {
        final var violations = VALIDATOR.validate(new TestBean("", 5));
        assertThat(violations).hasSize(1);
        final var ex = new ConstraintViolationException("single violation", violations);

        final var response = handler.handleViolationExceptions(ex, newRequest());

        assertThat(response).isNotNull();
        final var body = assertInstanceOf(ProblemDetail.class, response.getBody());
        assertThat(body.getDetail()).isNotEqualTo("Multiple constraint violations");
    }

    // ── BindAdviceTrait ───────────────────────────────────────────────────────

    @Test
    void handleBindException_withFieldError_returnsBadRequestWithViolation() throws Exception {
        final var bindingResult = new BeanPropertyBindingResult(new Object(), "myBean");
        bindingResult.addError(
                new FieldError("myBean", "email", "invalid@", false, null, null, "invalid email"));
        final var ex = new BindException(bindingResult);

        final var response = handler.handleViolationExceptions(ex, newRequest());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        final var body = assertInstanceOf(ProblemDetail.class, response.getBody());
        assertThat(body.getTitle()).isEqualTo("Constraint Violation");

        final var violations = (List<Violation>) body.getProperties().get("violations");
        assertThat(violations).hasSize(1);

        final var v = violations.getFirst();
        assertThat(v.fieldName()).isEqualTo("email");
        assertThat(v.message()).isEqualTo("invalid email");
        assertThat(v.rejectedValue()).isEqualTo("invalid@");
    }

    @Test
    void handleBindException_withGlobalError_usesObjectNameAsFieldName() throws Exception {
        final var bindingResult = new BeanPropertyBindingResult(new Object(), "myBean");
        bindingResult.addError(new ObjectError("myBean", "global constraint failed"));
        final var ex = new BindException(bindingResult);

        final var response = handler.handleViolationExceptions(ex, newRequest());

        assertThat(response).isNotNull();
        final var body = assertInstanceOf(ProblemDetail.class, response.getBody());

        final var violations = (List<Violation>) body.getProperties().get("violations");
        assertThat(violations).hasSize(1);

        final var v = violations.getFirst();
        assertThat(v.fieldName()).isEqualTo("myBean");
        assertThat(v.message()).isEqualTo("global constraint failed");
        assertThat(v.rejectedValue()).isNull();
    }

    @Test
    void handleBindException_withMixedErrors_returnsAllViolationsSorted() throws Exception {
        final var bindingResult = new BeanPropertyBindingResult(new Object(), "order");
        bindingResult.addError(
                new FieldError("order", "zipCode", "12", false, null, null, "invalid zip"));
        bindingResult.addError(
                new FieldError("order", "amount", -5, false, null, null, "must be positive"));
        bindingResult.addError(new ObjectError("order", "order is invalid"));
        final var ex = new BindException(bindingResult);

        final var response = handler.handleViolationExceptions(ex, newRequest());

        assertThat(response).isNotNull();
        final var body = assertInstanceOf(ProblemDetail.class, response.getBody());
        assertThat(body.getDetail()).isEqualTo("Multiple constraint violations");

        final var violations = (List<Violation>) body.getProperties().get("violations");
        // Sorted alphabetically: amount < order < zipCode
        assertThat(violations)
                .hasSize(3)
                .extracting(Violation::fieldName)
                .containsExactly("amount", "order", "zipCode");
    }

    @Test
    void handleBindException_withNoErrors_returnsEmptyViolations() throws Exception {
        final var bindingResult = new BeanPropertyBindingResult(new Object(), "myBean");
        final var ex = new BindException(bindingResult);

        final var response = handler.handleViolationExceptions(ex, newRequest());

        assertThat(response).isNotNull();
        final var body = assertInstanceOf(ProblemDetail.class, response.getBody());

        final var violations = (List<Violation>) body.getProperties().get("violations");
        assertThat(violations).isEmpty();
    }

    // ── MethodArgumentNotValidAdviceTrait ─────────────────────────────────────

    @Test
    void handleMethodArgumentNotValid_withIncludeConstraintViolationsEnabled_returnsViolations()
            throws Exception {
        final var bindingResult = new BeanPropertyBindingResult(new Object(), "requestDto");
        bindingResult.addError(
                new FieldError(
                        "requestDto", "username", null, false, null, null, "must not be blank"));

        final var method =
                ViolationAdviceTraitTest.class.getDeclaredMethod(
                        "dummyHandlerMethod", String.class);
        final var ex =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        final var response =
                handler.handleMethodArgumentNotValid(
                        ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, newRequest());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        final var body = assertInstanceOf(ProblemDetail.class, response.getBody());
        assertThat(body.getTitle()).isEqualTo("Constraint Violation");
        assertThat(body.getType()).hasToString("urn:ch:uetlib:problem:constraint-violation");

        final var violations = (List<Violation>) body.getProperties().get("violations");
        assertThat(violations)
                .hasSize(1)
                .first()
                .satisfies(
                        v -> {
                            assertThat(v.fieldName()).isEqualTo("username");
                            assertThat(v.message()).isEqualTo("must not be blank");
                        });
    }

    @Test
    void handleMethodArgumentNotValid_logsHandlerMethodAndValidationReason(
            final CapturedOutput output) throws Exception {
        final var bindingResult = new BeanPropertyBindingResult(new Object(), "requestDto");
        bindingResult.addError(
                new FieldError(
                        "requestDto",
                        "username",
                        "sensitive input",
                        false,
                        null,
                        null,
                        "must not be blank"));
        final var method =
                ViolationAdviceTraitTest.class.getDeclaredMethod(
                        "dummyHandlerMethod", String.class);
        final var exception =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        handler.handleMethodArgumentNotValid(
                exception, new HttpHeaders(), HttpStatus.BAD_REQUEST, newRequest());

        assertThat(output.getOut())
                .contains("dummyHandlerMethod", "username: must not be blank")
                .doesNotContain("sensitive input");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Dummy method used only to construct a {@link MethodParameter} in tests. */
    private void dummyHandlerMethod(final String arg) {
        // do nothing
    }

    private record TestBean(@NotBlank String name, @Min(0) int age) {}
}
