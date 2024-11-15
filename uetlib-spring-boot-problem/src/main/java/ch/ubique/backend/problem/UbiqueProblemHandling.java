package ch.ubique.backend.problem;

import ch.ubique.backend.problem.traits.NoSuchElementAdviceTrait;

import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

public interface UbiqueProblemHandling
        extends ProblemHandling, SecurityAdviceTrait, NoSuchElementAdviceTrait {}
