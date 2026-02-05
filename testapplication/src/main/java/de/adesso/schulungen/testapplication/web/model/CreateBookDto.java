package de.adesso.schulungen.testapplication.web.model;

import jakarta.validation.constraints.Min;

import java.util.List;

public record CreateBookDto(
        ISBN ISBN,
        MetaInfos metaInfos,
        @Min(value = 0, message = "The value must be positive")
        Float price,
        List<String> translations
) {}

