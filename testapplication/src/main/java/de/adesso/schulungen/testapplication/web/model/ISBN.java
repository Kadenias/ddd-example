package de.adesso.schulungen.testapplication.web.model;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Pattern;

public record ISBN(
        @Parameter(description = "The ISBN number. Always exactly 10 or 13 digits.")
        @Pattern(regexp = "^\\d{10}(\\d{3})?$")
        String isbn) {
}
