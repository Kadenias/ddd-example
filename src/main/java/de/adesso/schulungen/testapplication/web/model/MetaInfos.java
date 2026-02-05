package de.adesso.schulungen.testapplication.web.model;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MetaInfos(
        @Parameter(description = "The unique id of the book.")
        Long id,
        @Size(max = 255, message = "The title must contain at most 255 character.")
        @Parameter(description = "The title of the book.")
        String title,
        @Size(max = 255, message = "The author name must contain at most 255 character.")
        @Parameter(description = "The name of the author.")
        String author,
        VerlagIds verlagIds,
        LocalDate dateOfPublish
) {
}
