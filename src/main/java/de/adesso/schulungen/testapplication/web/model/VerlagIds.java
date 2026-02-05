package de.adesso.schulungen.testapplication.web.model;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;

public record VerlagIds(
        @Positive
        @Parameter(description = "The unique id of the Verlag.")
        long verlagId
) {
}
