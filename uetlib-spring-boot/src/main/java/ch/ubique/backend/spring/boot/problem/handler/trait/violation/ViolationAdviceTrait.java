package ch.ubique.backend.spring.boot.problem.handler.trait.violation;

/** Advice trait to handle any validation exceptions */
public interface ViolationAdviceTrait
        extends ConstraintViolationAdviceTrait,
                BindAdviceTrait,
                MethodArgumentNotValidAdviceTrait {}
