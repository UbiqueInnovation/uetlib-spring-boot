package ch.ubique.uetlib.spring.boot.problem.controller;

import ch.ubique.uetlib.spring.boot.problem.TestException;
import ch.ubique.uetlib.spring.boot.problem.errortype.TestErrorType;
import ch.ubique.uetlib.spring.boot.problem.errortype.TestProblem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class ExceptionThrowingController {

    @GetMapping("/throw")
    @Operation(
            summary = "Throw test exception",
            responses =
                    @ApiResponse(
                            responseCode = "400",
                            content =
                                    @Content(schema = @Schema(implementation = TestProblem.class))))
    public void throwException() {
        throw new TestException(
                TestErrorType.ERROR_TEST3_WITH_TITLE_TRANSLATION_ONLY,
                "Always throw TestErrorType.ERROR_TEST3_WITH_TITLE_ONLY");
    }
}
